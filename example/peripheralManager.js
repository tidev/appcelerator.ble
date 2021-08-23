/* eslint-disable no-alert */
/* eslint-disable no-loop-func */

function peripheralManagerWin(BLE, serviceUUID, heartRateCharacteristicUUID) {
	var central = null;
	var logs = [];
	var charProperties = [ BLE.CHARACTERISTIC_PROPERTIES_READ, BLE.CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE, BLE.CHARACTERISTIC_PROPERTIES_NOTIFY ];
	var charPermissions = [ BLE.CHARACTERISTIC_PERMISSION_READABLE, BLE.CHARACTERISTIC_PERMISSION_WRITEABLE ];
	var descriptor = BLE.createDescriptor({
		uuid: BLE.CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING,
		permission: BLE.DESCRIPTOR_PERMISSION_WRITE
	});
	var heartRateCharacteristic = BLE.createMutableCharacteristic({
		uuid: heartRateCharacteristicUUID,
		properties: charProperties,
		permissions: charPermissions,
		descriptors: [ descriptor ]
	});
	var heartRateService = null;

	var manager = null;
	var win = Ti.UI.createWindow({
		backgroundColor: 'white',
		title: 'Peripheral Manager',
		titleAttributes: { color: 'blue' }
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
			manager.addEventListener('didUpdateState', didUpdateStateListener);
			manager.addEventListener('didStartAdvertising', didStartAdvertisingListener);
			manager.addEventListener('didAddService', didAddServiceListener);
			manager.addEventListener('didSubscribeToCharacteristic', didSubscribeToCharacteristicListener);
			manager.addEventListener('didUnsubscribeFromCharacteristic', didUnsubscribeFromCharacteristicListener);
			manager.addEventListener('didReceiveReadRequest', didReceiveReadRequestListener);
			manager.addEventListener('didReceiveWriteRequests', didReceiveWriteRequestsListener);

			if (IOS) {
				manager.addEventListener('willRestoreState', function () {
					Ti.API.info('Peripheral Manager will restore state');
					logs.push('Peripheral Manager will restore state');
					setData(logs);
				});
				manager.addEventListener('readyToUpdateSubscribers', function () {
					Ti.API.info('Peripheral Manager ready to update subscribers');
					logs.push('readyToUpdateSubscribers');
					setData(logs);
				});
			}

			logs.push('Peripheral Manager initialized');
			setData(logs);

		} else {
			Ti.API.info('Peripheral Manager already Initialized');
			alert('Peripheral Manager already Initialized');
		}
	});

	win.add(InitializePeripheralManager);

	var didAddServiceListener = (e) => {
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			Ti.API.info('Error while adding service');
			if (e.errorDescription !== null) {
				alert('Error while adding service (error: ' + e.errorDescription + ')');
				return;
			}
			alert('Error while adding service');
			return;
		}
		heartRateService = e.service;
		Ti.API.info('Peripheral Manager added service');
		logs.push('Did Added Heart Rate Service (uuid: 180D) with characteristic (uuid: 2A37)');
		setData(logs);
	};

	var didStartAdvertisingListener = (e) => {
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			Ti.API.info('Error while starting advertising');
			if (e.errorDescription !== null) {
				alert('Error while starting advertising (error: ' + e.errorDescription + ')');
				return;
			}
			alert('Error while starting advertising');
			return;
		}
		Ti.API.info('Peripheral Manager started advertising');
		logs.push('Peripheral Manager started advertising');
		setData(logs);
	};

	var didSubscribeToCharacteristicListener = (e) => {
		Ti.API.info('Peripheral Manager subscribed to characteristic');
		central = e.central;
		logs.push('Central Manager Subscribed to ' + e.characteristic.uuid);
		setData(logs);
	};

	var didUnsubscribeFromCharacteristicListener = (e) => {
		Ti.API.info('Peripheral Manager unsubscribed to characteristic');
		central = null;
		logs.push('Central Manager Unsubscribed to ' + e.characteristic.uuid);
		setData(logs);
	};

	var didReceiveReadRequestListener = (e) => {
		Ti.API.info('Peripheral Manager received read request');
		logs.push('Received Read Request from Central Manager');
		var buffer = Ti.createBuffer({ length: 4 });
		buffer[0] = 0;
		buffer[1] = 50;
		var req = e.request;
		req.updateValue({
			value: buffer
		});
		manager.respondToRequest({
			request: req,
			result: BLE.ATT_SUCCESS
		});
		setData(logs);
	};

	var didReceiveWriteRequestsListener = (e) => {
		Ti.API.info('Peripheral Manager received write request');
		var requests = e.requests;
		if (requests.length !== 0) {
			for (var i = 0; i < requests.length; i++) {
				logs.push('Value from Central Manager: ' + requests[i].value);
			}
		} else {
			logs.push('Received Write Request from Central Manager');
		}
		setData(logs);
	};

	var didUpdateStateListener = (e) => {
		Ti.API.info('didUpdateState');
		switch (e.state) {
			case BLE.MANAGER_STATE_RESETTING:
				logs.push('Manager state updated to Resetting');
				alert('Resetting');
				break;

			case BLE.MANAGER_STATE_UNSUPPORTED:
				logs.push('Manager state updated to Unsupported');
				alert('Unsupported');
				break;

			case BLE.MANAGER_STATE_UNAUTHORIZED:
				logs.push('Manager state updated to Unauthorized');
				alert('Unauthorized');
				break;

			case BLE.MANAGER_STATE_POWERED_OFF:
				logs.push('Manager state updated to powered Off');
				alert('Peripheral Manager is powered Off');
				heartRateService = null;
				break;

			case BLE.MANAGER_STATE_POWERED_ON:
				logs.push('Manager state updated to powered On');
				alert('Peripheral Manager is powered On');
				break;

			case BLE.MANAGER_STATE_TURNING_ON:
				logs.push('Manager state updated to powered Turning On');
				alert('Peripheral Manager is Turning On');
				break;

			case BLE.MANAGER_STATE_TURNING_OFF:
				logs.push('Manager state updated to powered Turning Off');
				alert('Peripheral Manager is Turning Off');
				break;

			case BLE.MANAGER_STATE_UNKNOWN:
			default:
				logs.push('Manager state updated to Unknown');
				alert('Unknown');
				break;
		}
		setData(logs);
	};

	var addServiceButton  = Titanium.UI.createButton({
		top: 180,
		title: 'Add Heart Rate Service'
	});

	addServiceButton.addEventListener('click', function () {
		if (heartRateService !== null) {
			Ti.API.info('Heart Rate service already added');
			alert('Heart Rate service already added');
			return;
		}
		if (manager.peripheralManagerState !== BLE.MANAGER_STATE_POWERED_ON) {
			Ti.API.info('Bluetooth is not enabled');
			alert('Bluetooth is not enabled');
			return;
		}
		heartRateService = manager.addService({
			uuid: serviceUUID,
			primary: true,
			characteristics: [ heartRateCharacteristic ]
		});
		logs.push('Adding Heart Rate Service (uuid: 180D) with characteristic (uuid: 2A37)');
		setData(logs);
	});

	win.add(addServiceButton);

	var startAdvertisingButton = Titanium.UI.createButton({
		top: 220,
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
			var name = IOS ? 'BLE-Sample' : true;
			var servicesUUIDs = [];
			if (heartRateService !== null) {
				servicesUUIDs.push(heartRateService.uuid);
			} else {
				Ti.API.info('Heart Rate service not added');
				alert('Heart Rate service not added');
				return;
			}
			manager.startAdvertising({
				localName: name,
				serviceUUIDs: servicesUUIDs
			});
		}
	});
	win.add(startAdvertisingButton);

	var stopAdvertisingButton = Titanium.UI.createButton({
		top: 260,
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
			manager.stopAdvertising();
			Ti.API.info('Peripheral Manager has stopped advertising');
			logs.push('Peripheral Manager has stopped advertising');
			setData(logs);
		}
	});
	win.add(stopAdvertisingButton);

	var valueField = Ti.UI.createTextField({
		top: 310,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Value',
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});
	win.add(valueField);

	var updateValue = Titanium.UI.createButton({
		top: 350,
		title: 'Update Value'
	});
	updateValue.addEventListener('click', function () {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
			alert('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
		} else {
			var data = valueField.value === '' || valueField.value === null ? '60' : valueField.value;
			if (isNaN(data)) {
				alert('Value should be number only');
				return;
			}
			const parsed = Number.parseInt(data, 10);
			if (parsed > 255 || parsed < 0) {
				alert('Value should be between 0 to 255 only');
				return;
			}
			var buffer = Ti.createBuffer({ length: 4 });
			buffer[0] = 0;
			buffer[1] = parsed;

			var centrals = [];
			if (central !== null) {
				centrals.push(central);
			}
			manager.updateValue({
				characteristic: heartRateCharacteristic,
				data: buffer,
				centrals: centrals
			});
		}
	});
	win.add(updateValue);

	var tableView = Titanium.UI.createTableView({
		top: 400,
		scrollable: true,
		backgroundColor: 'White',
		separatorColor: '#DBE1E2',
		bottom: '5%',
	});
	var tbl_data = [];
	function setData(list) {
		tbl_data.splice(0, tbl_data.length);
		if (list.length > 0) {
			var initalValue = list.length - 1;
			for (var i = initalValue; i >= 0; i--) {
				var btDevicesRow = Ti.UI.createTableViewRow({
					height: 50,
					row: i
				});
				var uuidLabel = Ti.UI.createLabel({
					left: 5,
					right: 5,
					color: 'blue',
					top: 5,
					font: { fontSize: 11 },
					text: list[i]
				});
				btDevicesRow.add(uuidLabel);
				tbl_data.push(btDevicesRow);
			}
		}
		tableView.setData(tbl_data);
	}
	setData(logs);
	win.add(tableView);

	win.addEventListener('close', function () {
		if (manager === null) {
			return;
		}
		if (isAndroid) {
			manager.closePeripheral();
		}
		manager.removeEventListener('didAddService', didAddServiceListener);
		manager.removeEventListener('didStartAdvertising', didStartAdvertisingListener);
		manager.removeEventListener('didUpdateState', didUpdateStateListener);
		manager.removeEventListener('didSubscribeToCharacteristic', didSubscribeToCharacteristicListener);
		manager.removeEventListener('didUnsubscribeToCharacteristic', didUnsubscribeFromCharacteristicListener);
		manager.removeEventListener('didReceiveReadRequest', didReceiveReadRequestListener);
		manager.removeEventListener('didReceiveWriteRequests', didReceiveWriteRequestsListener);
	});

	return win;
}
exports.peripheralManagerWin = peripheralManagerWin;
