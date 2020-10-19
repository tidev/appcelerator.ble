/* eslint-disable no-alert */

let serviceUUID = '180D';
let characteristicUUID = '2A37';

function deviceWin(peripheral, centralManager, BLE) {
	// Central event for peripheral connection
	centralManager.addEventListener('didConnectPeripheral', function (e) {
		alert('Connected to Peripheral - name' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid);
		registerEvents(e.peripheral);
	});

	centralManager.addEventListener('didDisconnectPeripheral', function (e) {
		Ti.API.info('Disconnected from Peripheral: ' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid);
		alert('Peripheral Disconnected');
		global.charactersticObject = null;
		global.serviceObject = null;
	});

	centralManager.addEventListener('didFailToConnectPeripheral', function (e) {
		Ti.API.info('didFailToConnectPeripheral');
		Ti.API.info(e.peripheral);
		Ti.API.info(e.error.localizedDescription);
		Ti.API.info('Fail to connect with Peripheral - error code ' + e.errorCode + ' error domain: ' + e.errorDomain + ' error description ' + e.errorDescription);
	});

	// Configure UI
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
		title: 'Subscribe to Heart Rate (2A37)'
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

	// Buttoon click events
	connectButton.addEventListener('click', function () {
		if (peripheral) {
			centralManager.connectPeripheral({
				peripheral: peripheral,
				options: { [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true }
			});
		} else {
			alert('No peripheral available to connect');
		}
	});

	disConnectButton.addEventListener('click', function () {
		if (peripheral) {
			centralManager.cancelPeripheralConnection({ peripheral: peripheral });
		} else {
			alert('No peripheral available to disconnect');
		}
	});

	subscribeButton.addEventListener('click', function () {
		if (peripheral) {
			peripheral.discoverServices();
		} else {
			alert('No peripheral available to discover service');
		}
	});

	unsubscribeButton.addEventListener('click', function () {
		if (global.charactersticObject) {
			peripheral.unsubscribeFromCharacteristic({
				characteristic: global.charactersticObject
			});
		} else {
			alert('No registered characteristic available to unsubscribe');
		}
	});

	return navDeviceWindow;
}
exports.deviceWin = deviceWin;

function registerEvents(connectedPeripheral) {
	connectedPeripheral.addEventListener('didDiscoverServices', function (e) {
		Ti.API.info('didDiscoverServices ' + e);
		if (e.errorCode !== null) {
			alert('Error while discovering services' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let discoverServicePeripheral = e.sourcePeripheral;
		discoverHeartRateServices(discoverServicePeripheral);
	});

	connectedPeripheral.addEventListener('didDiscoverCharacteristics', function (e) {
		Ti.API.info('didDiscoverCharacteristics');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while discovering characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let discoverCharacteristicPeripheral = e.sourcePeripheral;
		discoverAndSubscribeToCharacteristic(discoverCharacteristicPeripheral);// Subscribe To Characteristic
	});

	connectedPeripheral.addEventListener('didUpdateNotificationStateForCharacteristics', function (e) {
		Ti.API.info('didUpdateNotificationStateForCharacteristics');
		if (e.errorCode !== null) {
			alert('Error while subscribing characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let characteristic = e.characteristic;
		if (characteristic.isNotifying === true) {
			alert('Successfully subscribed for Heart Rate (2A37)');
		} else {
			alert('Successfully unsubscribed for Heart Rate (2A37)');
		}
	});

	connectedPeripheral.addEventListener('didUpdateValueForCharacteristic', function (e) {
		if (e.errorCode !== null) {
			alert('Error while didUpdateValueForCharacteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		Ti.API.info('ValueForCharacteristic ' + e.value);
		let value = e.value.toString();
		if (value) {
			alert('didUpdateValueForCharacteristic ' + value);
		}
	});

	connectedPeripheral.addEventListener('didDiscoverDescriptorsForCharacteristics', function (e) {
		Ti.API.info('didDiscoverDescriptorsForCharacteristics');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			Ti.API.info('Error while discovering descriptors for characteristics' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didDiscoverIncludedServices', function (e) {
		Ti.API.info('didDiscoverIncludedServices');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while discovering included services' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didReadRSSI', function (e) {
		Ti.API.info('didReadRSSI');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while reading RSSI' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didUpdateValueForDescriptor', function (e) {
		Ti.API.info('didUpdateValueForDescriptor');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while updating value for descriptor' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didWriteValueForCharacteristic', function (e) {
		Ti.API.info('didWriteValueForCharacteristic');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while write value for characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didWriteValueForDescriptor', function (e) {
		Ti.API.info('didWriteValueForDescriptor');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while write value dor descriptor' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});

	connectedPeripheral.addEventListener('didUpdateName', function (e) {
		Ti.API.info('didUpdateName');
		Ti.API.info(e);
	});

	connectedPeripheral.addEventListener('didModifyServices', function (e) {
		Ti.API.info('didModifyServices');
		Ti.API.info(e);
	});

	connectedPeripheral.addEventListener('peripheralIsReadyToSendWriteWithoutResponse', function (e) {
		Ti.API.info('peripheralIsReadyToSendWriteWithoutResponse');
		Ti.API.info(e);
	});

	connectedPeripheral.addEventListener('didOpenChannel', function (e) {
		Ti.API.info('didOpenChannel');
		Ti.API.info(e);
		if (e.errorCode !== null) {
			alert('Error while opening channel' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	});
}

function discoverHeartRateServices (sourcePeripheral) {
	var services;

	// sourcePeripheral is the peripheral sending the didDiscoverServices event
	services = sourcePeripheral.services;
	Ti.API.info('services ' + services);
	services.forEach(function (service) {
		Ti.API.info('Discovered service ' + service.UUID);
		if (service.uuid === serviceUUID) {
			global.serviceObject = service;
			Ti.API.info('Found heart rate service!');
			sourcePeripheral.discoverCharacteristics({
				service: service
			});
		}
	});
}

function discoverAndSubscribeToCharacteristic (sourcePeripheral) {
	var characteristics;

	characteristics = global.serviceObject.characteristics;
	Ti.API.info('characteristics ' + characteristics);
	characteristics.forEach(function (characteristic) {
		Ti.API.info('Discovered characteristic ' + characteristic.UUID);
		if (characteristic.uuid === characteristicUUID) {
			global.charactersticObject = characteristic;
			Ti.API.info('Found heart rate characteristic, will subscribe...');
			sourcePeripheral.subscribeToCharacteristic({
				characteristic: characteristic
			});
		}
	});
}
