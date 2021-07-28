/* eslint-disable no-alert */
function centralManagerWin(BLE, title, peripheralPage, serviceUUID, characteristicUUID) {
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

			case BLE.MANAGER_STATE_TURNING_ON:
				alert('Bluetooth is turning on');
				break;

			case BLE.MANAGER_STATE_TURNING_OFF:
				alert('Bluetooth is turning off');
				break;

			case BLE.MANAGER_STATE_UNKNOWN:
			default:
				alert('Unknown');
				break;
		}
	});
	centralManager.addEventListener('didDiscoverPeripheral', function (e) {
		Ti.API.info('didDiscoverPeripheral:- Peripheral Name: ' + e.peripheral.name + ' with address: ' + e.peripheral.address);
		setData(centralManager.peripherals);
		activityIndicator.hide();
	});

	if (IOS) {
		centralManager.addEventListener('willRestoreState', function (e) {
			Ti.API.info('willRestoreState');
			Ti.API.info(e);
		});
	}

	var centralDataWin = Ti.UI.createWindow({
		backgroundColor: 'White',
		title: title,
		titleAttributes: { color: 'blue' }
	});

	// permission request button.
	var requestPermissionsButton = Ti.UI.createButton({
		title: 'Request Permissions',
		visible: isAndroid,
		bottom: 150
	});
	centralDataWin.add(requestPermissionsButton);

	// Scan button added to scan peripherals
	var scanButton = Ti.UI.createButton({
		title: 'Scan',
		bottom: 90
	});
	centralDataWin.add(scanButton);

	// Stop Scan button added to stop scaning peripherals
	var stopScanButton = Ti.UI.createButton({
		title: 'Stop Scan',
		left: 5,
		bottom: 30
	});
	centralDataWin.add(stopScanButton);

	// to check Bluetooth connection
	var checkConnection = Ti.UI.createButton({
		title: 'Check Connection',
		right: 5,
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

	// Request permissions from user if not already granted.
	requestPermissionsButton.addEventListener('click', function () {
		if (BLE.isBluetoothAndBluetoothAdminPermissionsGranted() && centralManager.isAccessFineLocationPermissionGranted()) {
			alert('Permissions have already been granted.');
			return;
		}

		BLE.requestBluetoothPermissions((e) => {
			if (!e.success) {
				alert('Bluetooth permissions not granted.');
				return;
			}
			centralManager.requestAccessFineLocationPermission((e) => {
				if (!e.success) {
					alert('Access fine location permission not granted.');
				}
			});
		});
	});

	// scan button functionality
	scanButton.addEventListener('click', function () {
		// android permission checks.
		if (isAndroid) {
			if (!BLE.isBluetoothAndBluetoothAdminPermissionsGranted()) {
				alert('Bluetooth permissions not granted.');
				return;
			} else if (!centralManager.isAccessFineLocationPermissionGranted()) {
				alert('Access fine location permission not granted.');
				return;
			}
		}
		Ti.API.info(centralManager.state);
		if (centralManager.isScanning) {
			alert('Already scanning, please stop scan first!');
			return;
		} else if (centralManager.state !== BLE.MANAGER_STATE_POWERED_ON) {
			alert('Please check device settings to enable bluetooth');
			return;
		}
		tableView.setData(tbl_data);
		centralManager.startScan();
		activityIndicator.show();
	});

	checkConnection.addEventListener('click', function () {
		Ti.API.info('Checking Bluetooth status');
		if (IOS) {
			var connection = BLE.authorizationState;
			if (connection === BLE.AUTHORISATION_STATUS_ALLOWED_ALWAYS) {
				alert('Bluetooth enabled in settings');
			} else {
				alert('Bluetooth is disabled, Please allow it in settings');
			}
		} else {
			// permission for android.
			/* eslint-disable no-lonely-if */
			if (!BLE.isBluetoothAndBluetoothAdminPermissionsGranted()) {
				alert('Bluetooth admin permission not been granted.');
			} else if (!centralManager.isAccessFineLocationPermissionGranted()) {
				alert('Access fine location permission not been granted.');
			} else {
				alert('Bluetooth Permissions have been granted.');
			}
		}
	});

	function setData(list) {
		tbl_data.splice(0, tbl_data.length);
		if (list.length > 0) {
			activityIndicator.hide();
			for (var i = 0; i < list.length; i++) {
				var btDevicesRow = Ti.UI.createTableViewRow({
					height: 70,
					id: list[i].address,
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
				var idLabel = Ti.UI.createLabel({
					left: 10,
					color: 'blue',
					bottom: 0,
					width: 250,
					font: { fontSize: 11 },
					text: ('ADDRESS - ' + list[i].address)
				});
				btDevicesRow.add(nameLabel);
				btDevicesRow.add(idLabel);
				btDevicesRow.addEventListener('click', function (e) {
					var periPheralObject;
					var indexRow = e.row;
					if (indexRow.hasChild) {
						for (var i = 0; i < list.length; i++) {
							if (indexRow.id === list[i].address) {
								periPheralObject = list[i];
								break;
							}
						}
						var devicePage = new peripheralPage.deviceWin(periPheralObject, centralManager, BLE, serviceUUID, characteristicUUID);
						devicePage.open();
						stopScanOperation();
					}
				});
				tbl_data.push(btDevicesRow);
			}
			tableView.setData(tbl_data);
		}
	}

	function stopScanOperation() {
		centralManager.stopScan();
		activityIndicator.hide();
		tbl_data.splice(0, tbl_data.length);
	}

	// Stop scan button functionality
	stopScanButton.addEventListener('click', function () {
		if (!centralManager.isScanning) {
			alert('Not scanning!');
			return;
		}
		stopScanOperation();
	});
	return centralDataWin;
}
exports.centralManagerWin = centralManagerWin;
