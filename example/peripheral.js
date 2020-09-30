function deviceWin(peripheral, centralManager, BLE) {
	var serviceUUID = '180D';
	var characteristicUUID = '2A37';

	centralManager.addEventListener('didConnectPeripheral', function (e) {
		Ti.API.info('Connected to Peripheral: ' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid);
		var connectedPeripheral = e.peripheral;
		if (e.errorDescription !== null) {
			Ti.API.info('Error while connecting with peripheral' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		connectedPeripheral.addEventListener('didDiscoverServices', function (e) {
			Ti.API.info('didDiscoverServices');
			Ti.API.info(e);
			if (e.errorDescription !== null) {
				Ti.API.info('Error while discovering services' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
			var servicePeripheralObject = e.sourcePeripheral;
			var serviceListArray = servicePeripheralObject.services;
			for (var i = 0; i < serviceListArray.length; i++) {
				if (serviceListArray[i].uuid === serviceUUID) {
					global.serviceObject = serviceListArray[i];
					Ti.API.info(serviceListArray[i].uuid);
					servicePeripheralObject.addEventListener('didDiscoverCharacteristics', function (e) {
						Ti.API.info('didDiscoverCharacteristics');
						Ti.API.info(e);
						if (e.errorDescription !== null) {
							Ti.API.info('Error while discovering characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
							return;
						}
						var charPeripheralObject = e.sourcePeripheral;
						var charListArray = global.serviceObject.characteristics;
						for (var i = 0; i < charListArray.length; i++) {
							if (charListArray[i].uuid === characteristicUUID) {
								global.charactersticObject = charListArray[i];
								Ti.API.info(charListArray[i].uuid);
								charPeripheralObject.addEventListener('didUpdateNotificationStateForCharacteristics', function (e) {
									Ti.API.info('didUpdateNotificationStateForCharacteristics');
									if (e.errorDescription !== null) {
										Ti.API.info('Error while subscribing characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
										return;
									}
								});
								peripheral.subscribeToCharacteristic({
									characteristic: global.charactersticObject
								});
							}
						}
					});
					peripheral.discoverCharacteristics({
						service: global.serviceObject
					});
				}
			}
		});
	});

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
		text: 'Name - ' + peripheral.name
	});
	var uuidLabel = Ti.UI.createLabel({
		color: 'blue',
		top: 140,
		width: 250,
		font: { fontSize: 11 },
		text: 'UUID - ' + peripheral.UUID
	});

	var connectButton = Titanium.UI.createButton({
		top: 200,
		title: 'Connect'
	});
	var disConnectButton = Titanium.UI.createButton({
		top: 300,
		title: 'Disconnect'
	});
	var subscribeButton = Titanium.UI.createButton({
		top: 400,
		title: 'Subscribe'
	});
	var unsubscribeButton = Titanium.UI.createButton({
		top: 500,
		title: 'Unsubscribe'
	});

	var backButton = Titanium.UI.createButton({
		bottom: 50,
		title: 'Go to device list'
	});
	backButton.addEventListener('click', function () {
		navDeviceWindow.close();
	});

	navDeviceWindow.add(connectButton, backButton, nameLabel, uuidLabel, disConnectButton, subscribeButton, unsubscribeButton);
	connectButton.addEventListener('click', function () {
		if (peripheral) {
			centralManager.connectPeripheral({
				peripheral: peripheral,
				options: { [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true }
			});
			Ti.API.info('Peripheral Connected');
		} else {
			Ti.API.info('No peripheral available to connect');
		}
	});
	disConnectButton.addEventListener('click', function () {
		if (peripheral) {
			centralManager.cancelPeripheralConnection({ peripheral: peripheral });
			Ti.API.info('Peripheral Disconnected');
		} else {
			Ti.API.info('No peripheral available to disconnect');
		}
	});
	subscribeButton.addEventListener('click', function () {
		if (peripheral) {
			peripheral.discoverServices();
			Ti.API.info('Subscribed for 2A37 ');
		} else {
			Ti.API.info('No peripheral available to connect');
		}
	});
	unsubscribeButton.addEventListener('click', function () {
		if (peripheral) {
			peripheral.unsubscribeFromCharacteristic({
				characteristic: global.charactersticObject
			});
			Ti.API.info('Unsubscribed for 2A37 ');
		} else {
			Ti.API.info('No peripheral available to disconnect');
		}
	});
	return navDeviceWindow;
}
exports.deviceWin = deviceWin;
