// require BLE
var BLE = require('appcelerator.ble');
var tbl_data = [];
var centralManager = BLE.initCentralManager();
centralManager.addEventListener('didUpdateState', function (e) {
	Ti.API.info('didUpdateState');
	switch (e.state) {
		case BLE.CENTRAL_MANAGER_STATE_RESETTING:
			alert('Resetting');
			break;

		case BLE.CENTRAL_MANAGER_STATE_UNSUPPORTED:
			alert('Unsupported');
			break;

		case BLE.CENTRAL_MANAGER_STATE_UNAUTHORIZED:
			alert('Unauthorized');
			break;

		case BLE.CENTRAL_MANAGER_STATE_POWERED_OFF:
			alert('Powered Off');
			break;

		case BLE.CENTRAL_MANAGER_STATE_POWERED_ON:
			alert('Powered On');
			break;

		case BLE.CENTRAL_MANAGER_STATE_UNKNOWN:
		default:
			alert('Unknown');
			break;
	}
});
centralManager.addEventListener('didDiscoverPeripheral', function (e) {
	Ti.API.info('didDiscoverPeripheral');
	Ti.API.info(e);
	setData(centralManager.peripherals);
	activityIndicator.hide();
});

centralManager.addEventListener('didDisconnectPeripheral', function (e) {
	Ti.API.info('didDisconnectPeripheral');
	Ti.API.info(e);
});

centralManager.addEventListener('didConnectPeripheral', function (e) {
	Ti.API.info('didConnectPeripheral');
	Ti.API.info(e);
});

centralManager.addEventListener('didFailToConnectPeripheral', function (e) {
	Ti.API.info('didFailToConnectPeripheral');
	Ti.API.info(e.peripheral);
});

centralManager.addEventListener('willRestoreState', function (e) {
	Ti.API.info('willRestoreState');
	Ti.API.info(e);
});

var mainWindow = Ti.UI.createWindow({
	backgroundColor: 'White',
	titleAttributes: { color: 'blue' }
});

var navCentralWindow = Ti.UI.iOS.createNavigationWindow({
	window: mainWindow
});

var centralDataWin = Ti.UI.createWindow({
	backgroundColor: 'White',
	title: 'Central',
	titleAttributes: { color: 'blue' }
});

var buttonCentral = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Central',
	top: 50
});

// Scan button added to scan peripherals
var scanButton = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Scan',
	bottom: 90
});
// Stop Scan button added to stop scaning peripherals
var stopScanButton = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Stop Scan',
	left: 20,
	bottom: 30
});

// to check Bluetooth connection
var checkConnection = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Check Connection',
	right: 20,
	bottom: 30
});

// Activity indicator
var activityIndicator = Ti.UI.createActivityIndicator({
	color: 'blue',
	message: 'Loading ...',
	style: Ti.UI.ActivityIndicatorStyle.DARK,
	top: '50%',
	height: Ti.UI.SIZE,
	width: Ti.UI.SIZE
});
// Central button will  open central section
buttonCentral.addEventListener('click', function () {
	alert('Please scan to fetch BLE devices');
	navCentralWindow.openWindow(centralDataWin, { animated: true });
});

// scan button functionality
scanButton.addEventListener('click', function () {
	Ti.API.info(centralManager.state);
	if (centralManager.isScanning) {
		alert('Already scanning, please stop scan first!');
		return;
	} else if (centralManager.state != BLE.CENTRAL_MANAGER_STATE_POWERED_ON) {
		alert('Please check device settings to enable bluetooth');
		return;
	}
	centralManager.startScan();
	activityIndicator.show();
});

checkConnection.addEventListener('click', function () {
	Ti.API.info('Checking Bluetooth status');
	var connection = BLE.authorizationState;
	if (connection == BLE.AUTHORISATION_STATUS_ALLOWED_ALWAYS) {
		alert('Bluetooth is enabled');
		Ti.API.info('Bluetooth enabled');
	} else {
		alert('Bluetooth is disabled');
		Ti.API.info('Bluetooth disabled');
	}
});

// Table view will show all the discovered peripheral.
var tableView = Titanium.UI.createTableView({
	scrollable: true,
	backgroundColor: 'White',
	separatorColor: '#DBE1E2',
	bottom: '20%',
});

function setData(list) {
	tbl_data.splice(0, tbl_data.length);
	if (list.length > 0) {
		var btDevicesListSection = Ti.UI.createTableViewSection();
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
				var indexRow = e.source;
				if (indexRow.hasChild) {
					for (var i = 0; i < list.length; i++) {
						if (indexRow.id == list[i].UUID) {
							periPheralObject = list[i];
							break;
						}
					}
					var deviceWindow = Ti.UI.createWindow({
						backgroundColor: 'white',
						title: 'Device information',
						titleAttributes: { color: 'blue' }
					});

					var navDeviceWindow = Ti.UI.iOS.createNavigationWindow({
						window: deviceWindow
					});

					var nameLabel = Ti.UI.createLabel({
						color: 'black',
						top: 100,
						width: 250,
						font: { fontSize: 14 },
						text: 'Name - ' + periPheralObject.name
					});
					var uuidLabel = Ti.UI.createLabel({
						color: 'blue',
						top: 140,
						width: 250,
						font: { fontSize: 11 },
						text: 'UUID - ' + periPheralObject.UUID
					});

					var connectButton = Titanium.UI.createButton({
						top: 200,
						title: 'Connect'
					});
					var disConnectButton = Titanium.UI.createButton({
						top: 300,
						title: 'Disconnect'
					});

					var backButton = Titanium.UI.createButton({
						bottom: 100,
						title: 'Go to device list'
					});
					backButton.addEventListener('click', function () {
						navDeviceWindow.close();
					});
					connectButton.addEventListener('click', function () {
						if (periPheralObject) {
							centralManager.connectPeripheral({
								peripheral: periPheralObject,
								options: { [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true }
							});
							alert('Peripheral Connected');
						} else {
							alert('No peripheral available to connect');
						}
					});
					disConnectButton.addEventListener('click', function () {
						if (periPheralObject) {
							centralManager.cancelPeripheralConnection({ peripheral: periPheralObject });
							alert('Peripheral Disconnected');
						} else {
							alert('No peripheral available to disconnect');
						}
					});

					navDeviceWindow.add(connectButton, backButton, nameLabel, uuidLabel, disConnectButton)
					navDeviceWindow.open();
				}
			});
			tbl_data.push(btDevicesRow)
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

centralDataWin.add(scanButton, stopScanButton, checkConnection, tableView, activityIndicator);
mainWindow.add(buttonCentral);
navCentralWindow.open();

// todo: When add sample for peripheral
var peripheralDataWin = Ti.UI.createWindow({
	backgroundColor: 'White',
	title: 'Peripheral'
});

var buttonPeripheral = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Peripheral',
	bottom: 75
});
buttonPeripheral.addEventListener('click', function () {
	navCentralWindow.openWindow(peripheralDataWin, { animated: true });
});

mainWindow.add(buttonPeripheral);
