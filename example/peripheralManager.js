/* eslint-disable no-alert */
/* eslint-disable no-loop-func */

function peripheralManagerWin(BLE) {
	var services = [];
	var manager = null;
	var deviceWindow = Ti.UI.createWindow({
		backgroundColor: 'white',
		title: 'Peripheral Manager',
		titleAttributes: { color: 'blue' }
	});
	var win = Ti.UI.iOS.createNavigationWindow({
		window: deviceWindow
	});
	var closeButton = Titanium.UI.createButton({
		top: 100,
		title: 'Close'
	});
	closeButton.addEventListener('click', function () {
		win.close();
	});

	win.add(closeButton);
	var InitializePeripheralManager = Titanium.UI.createButton({
		top: 140,
		title: 'Initialize Peripheral Manager'
	});
	InitializePeripheralManager.addEventListener('click', function () {
		if (manager === null) {
			manager = BLE.initPeripheralManager();
			manager.addEventListener('didUpdateState', function (e) {
				Ti.API.info('didUpdateState');
				switch (e.state) {
					case BLE.MANAGER_STATE_RESETTING:
						alert('Resetting');
						break;

					case BLE.MANAGER_STATE_UNSUPPORTED:
						alert('Unsupported');
						break;

					case BLE.MANAGER_STATE_UNAUTHORIZED:
						alert('Unauthorized');
						break;

					case BLE.MANAGER_STATE_POWERED_OFF:
						alert('Peripheral Manager is powered Off');
						break;

					case BLE.MANAGER_STATE_POWERED_ON:
						alert('Peripheral Manager is powered On');
						break;

					case BLE.MANAGER_STATE_UNKNOWN:
					default:
						alert('Unknown');
						break;
				}
			});
			manager.addEventListener('didStartAdvertising', function (e) {
				Ti.API.info('Peripheral Manager started advertising');
				alert('Peripheral Manager started advertising');
			});
			manager.addEventListener('willRestoreState', function (e) {
				Ti.API.info('Peripheral Manager will restore state');
				alert('Peripheral Manager will restore state');
			});
			manager.addEventListener('didAddService', function (e) {
				if (e.errorCode !== null || e.errorDomain !== null) {
					Ti.API.info('Error while adding service');
					if (e.errorDescription !== null) {
						alert('Error while adding service (error: ' + e.errorDescription + ')');
						return;
					}
					alert('Error while adding service');
					return;
				}
				Ti.API.info('Peripheral Manager added service');
				alert('Peripheral Manager added service (uuid:' + e.service.uuid + ')');
				services.push(e.service);
				setData(services);
			});
			manager.addEventListener('didSubscribeToCharacteristic', function (e) {
				Ti.API.info('Peripheral Manager subscribed to characteristic');
				alert('Peripheral Manager subscribed to characteristic');
			});

			manager.addEventListener('didUnsubscribeFromCharacteristic', function (e) {
				Ti.API.info('Peripheral Manager unsubscribed to characteristic');
				alert('Peripheral Manager unsubscribed to characteristic');
			});

			manager.addEventListener('didReceiveReadRequest', function (e) {
				Ti.API.info('Peripheral Manager received read request');
				alert('Peripheral Manager received read request');
			});
			manager.addEventListener('didReceiveWriteRequests', function (e) {
				Ti.API.info('Peripheral Manager received write request');
				alert('Peripheral Manager received write request');
			});

			manager.addEventListener('readyToUpdateSubscribers', function (e) {
				Ti.API.info('Peripheral Manager ready to update subscribers');
				alert('Peripheral Managerready to update subscribers');
			});

			manager.addEventListener('didPublishL2CAPChannel', function (e) {
				Ti.API.info('Peripheral Manager published L2CAP channel');
				alert('Peripheral Managerready published L2CAP channel');
			});
			manager.addEventListener('didUnpublishL2CAPChannel', function (e) {
				Ti.API.info('Peripheral Manager unpublished L2CAP channel');
				alert('Peripheral Managerready unpublished L2CAP channel');
			});
			manager.addEventListener('didOpenL2CAPChannel', function (e) {
				Ti.API.info('Peripheral Manager opened L2CAP channel');
				alert('Peripheral Managerready opened L2CAP channel');
			});
		} else {
			Ti.API.info('Peripheral Manager already Initialized');
			alert('Peripheral Manager already Initialized');
		}
	});

	win.add(InitializePeripheralManager);

	var addServiceButton = Titanium.UI.createButton({
		top: 180,
		title: 'Add Service'
	});
	addServiceButton.addEventListener('click', function () {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is not Initialized');
			alert('Peripheral Manager is not Initialized');
		} else {
			var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
				var r = Math.random() * 16 | 0,
					v = c === 'x' ? r : (r & 0x3 | 0x8);
				return v.toString(16);
			});
			manager.addService({
				uuid: uuid,
				primary: true
			});
		}
	});
	win.add(addServiceButton);

	var removeAllServices = Titanium.UI.createButton({
		top: 220,
		title: 'Remove All Service'
	});
	removeAllServices.addEventListener('click', function () {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is not Initialized');
			alert('Peripheral Manager is not Initialized');
		} else {
			manager.removeAllServices();
			services.splice(0, services.length);
			setData(services);
		}
	});
	win.add(removeAllServices);

	var nameTextField = Ti.UI.createTextField({
		top: 260,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Peripheral Name',
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});
	win.add(nameTextField);

	var startAdvertisingButton = Titanium.UI.createButton({
		top: 320,
		title: 'Start Advertising'
	});
	startAdvertisingButton.addEventListener('click', function () {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
			alert('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
		} else {
			if (manager.isAdvertising === true) {
				Ti.API.info('Peripheral Manager is already advertising');
				alert('Peripheral Manager is already advertising');
				return;
			}
			var name = nameTextField.value === null || nameTextField.value === '' ? 'Appcelerator.BLE' : nameTextField.value;
			manager.startAdvertising({
				localName: name
			});
		}
	});
	win.add(startAdvertisingButton);

	var stopAdvertisingButton = Titanium.UI.createButton({
		top: 360,
		title: 'Stop Advertising'
	});
	stopAdvertisingButton.addEventListener('click', function () {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
			alert('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
		} else {
			if (manager.isAdvertising === false) {
				Ti.API.info('Peripheral Manager is not advertising');
				alert('Peripheral Manager is not advertising');
				return;
			}
			Ti.API.info('Peripheral Manager has stopped advertising');
			alert('Peripheral Manager has stopped advertising');
			manager.stopAdvertising();
		}
	});
	win.add(stopAdvertisingButton);

	var tableView = Titanium.UI.createTableView({
		top: 410,
		scrollable: true,
		backgroundColor: 'White',
		separatorColor: '#DBE1E2',
		bottom: '20%',
	});
	var tbl_data = [];
	function setData(list) {
		tbl_data.splice(0, tbl_data.length);
		if (list.length > 0) {
			for (var i = 0; i < list.length; i++) {
				var btDevicesRow = Ti.UI.createTableViewRow({
					height: 44,
					row: i,
					hasChild: true
				});
				var uuidLabel = Ti.UI.createLabel({
					left: 10,
					color: 'blue',
					top: 18,
					width: 300,
					font: { fontSize: 11 },
					text: 'UUID - ' + list[i].uuid
				});
				btDevicesRow.add(uuidLabel);
				var deleteButton = Titanium.UI.createButton({
					right: 0,
					top: 10,
					title: 'Delete',
					id: i
				});
				deleteButton.addEventListener('click', function (e) {
					var newArray = [];
					var index = e.source.id;
					for (var j = 0; j < list.length; j++) {
						if (j !== index) {
							newArray.push(list[j]);
							continue;
						}
						manager.removeServices({
							service: list[j]
						});
					}
					services.splice(0, services.length);
					for (var k = 0; k < newArray.length; k++) {
						services.push(newArray[k]);
					}
					setData(services);
				});
				btDevicesRow.add(deleteButton);
				tbl_data.push(btDevicesRow);
			}
		}
		tableView.setData(tbl_data);
	}
	setData(services);
	win.add(tableView);

	return win;
}
exports.peripheralManagerWin = peripheralManagerWin;
