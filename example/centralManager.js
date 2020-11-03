/* eslint-disable no-alert */
function centralManagerWin(BLE, title, peripheralPage) {
	var tbl_data = [];
	var centralManager = BLE.initCentralManager();
	centralManager.addEventListener('didUpdateState', function (e) {
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
				alert('Bluetooth is powered Off');
				break;

			case BLE.MANAGER_STATE_POWERED_ON:
				alert('Bluetooth is powered On');
				break;

			case BLE.MANAGER_STATE_UNKNOWN:
			default:
				alert('Unknown');
				break;
		}
	});
	centralManager.addEventListener('didDiscoverPeripheral', function (e) {
		Ti.API.info('Peripheral Name: ' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid + ' and number of services: ' + e.peripheral.services.length);
		setData(centralManager.peripherals);
		activityIndicator.hide();
	});
	centralManager.addEventListener('willRestoreState', function (e) {
		Ti.API.info('willRestoreState');
		Ti.API.info(e);
	});

	var centralDataWin = Ti.UI.createWindow({
		backgroundColor: 'White',
		title: title,
		titleAttributes: { color: 'blue' }
	});

	// Scan button added to scan peripherals
	var scanButton = Ti.UI.createButton({
		font: { fontSize: 20 },
		title: 'Scan',
		bottom: 90
	});
	centralDataWin.add(scanButton);

	// Stop Scan button added to stop scaning peripherals
	var stopScanButton = Ti.UI.createButton({
		font: { fontSize: 20 },
		title: 'Stop Scan',
		left: 20,
		bottom: 30
	});
	centralDataWin.add(stopScanButton);

	// to check Bluetooth connection
	var checkConnection = Ti.UI.createButton({
		font: { fontSize: 20 },
		title: 'Check Connection',
		right: 20,
		bottom: 30
	});
	centralDataWin.add(checkConnection);

	// Activity indicator
	var activityIndicator = Ti.UI.createActivityIndicator({
		color: 'blue',
		message: 'Loading ...',
		style: Ti.UI.ActivityIndicatorStyle.DARK,
		top: '50%',
		height: Ti.UI.SIZE,
		width: Ti.UI.SIZE
	});
	centralDataWin.add(activityIndicator);

	// Table view will show all the discovered peripheral.
	var tableView = Titanium.UI.createTableView({
		scrollable: true,
		backgroundColor: 'White',
		separatorColor: '#DBE1E2',
		bottom: '20%',
	});
	centralDataWin.add(tableView);

	// scan button functionality
	scanButton.addEventListener('click', function () {
		Ti.API.info(centralManager.state);
		if (centralManager.isScanning) {
			alert('Already scanning, please stop scan first!');
			return;
		} else if (centralManager.state !== BLE.MANAGER_STATE_POWERED_ON) {
			alert('Please check device settings to enable bluetooth');
			return;
		}
		centralManager.startScan();
		activityIndicator.show();
	});

	checkConnection.addEventListener('click', function () {
		Ti.API.info('Checking Bluetooth status');
		var connection = BLE.authorizationState;
		if (connection === BLE.AUTHORISATION_STATUS_ALLOWED_ALWAYS) {
			alert('Bluetooth enabled in settings');
		} else {
			alert('Bluetooth is disabled, Please allow it in settings');
		}
	});

	function setData(list) {
		tbl_data.splice(0, tbl_data.length);
		if (list.length > 0) {
			activityIndicator.hide();
			for (var i = 0; i < list.length; i++) {
				var btDevicesRow = Ti.UI.createTableViewRow({
					height: 70,
					id: list[i].UUID,
					row: i,
					hasChild: true
				});
				var nameLabel = Ti.UI.createLabel({
					left: 10,
					color: 'black',
					top: 10,
					width: 250,
					font: { fontSize: 14 },
					text: 'Name - ' + list[i].name
				});
				var uuidLabel = Ti.UI.createLabel({
					left: 10,
					color: 'blue',
					bottom: 0,
					width: 250,
					font: { fontSize: 11 },
					text: 'UUID - ' + list[i].UUID
				});
				btDevicesRow.add(nameLabel, uuidLabel);
				btDevicesRow.addEventListener('click', function (e) {
					var periPheralObject;
					var indexRow = e.row;
					if (indexRow.hasChild) {
						for (var i = 0; i < list.length; i++) {
							if (indexRow.id === list[i].UUID) {
								periPheralObject = list[i];
								break;
							}
						}
						var devicePage = new peripheralPage.deviceWin(periPheralObject, centralManager, BLE);
						devicePage.open();
					}
				});
				tbl_data.push(btDevicesRow);
			}
			tableView.setData(tbl_data);
		}
	}

	// Stop scan button functionality
	stopScanButton.addEventListener('click', function () {
		if (!centralManager.isScanning) {
			alert('Not scanning!');
			return;
		}
		centralManager.stopScan();
		activityIndicator.hide();
		tbl_data.splice(0, tbl_data.length);
	});
	return centralDataWin;
}
exports.centralManagerWin = centralManagerWin;
