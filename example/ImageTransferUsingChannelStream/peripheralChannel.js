/* eslint-disable no-alert */

function deviceWin(peripheral, centralManager, BLE, serviceUUID, characteristicUUID) {
	var imageFile = null;
	function createImage() {
		var imageDir = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory,
			'downloaded_images');
		if (!imageDir.exists()) {
			imageDir.createDirectory();
		}
		imageFile = Ti.Filesystem.getFile(imageDir.resolve(), 'image.png');
		if (imageFile.exists()) {
			imageFile.deleteFile();
		}
		if (!imageFile.exists()) {
			imageFile.createFile();
		}
	}
	createImage();
	var channelPSMID = null;
	var channel = null;
	var logs = [];
	// Central event for peripheral connection
	var peripheralConnectedListener = (e) => {
		logs.push('Connected to Peripheral');
		setData(logs);
		registerEvents(e.peripheral);
		peripheral.discoverServices();
	};
	var peripheralDisconnectedListener = (e) => {
		Ti.API.info('Disconnected from Peripheral: ' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid);
		logs.push('Peripheral Disconnected');
		setData(logs);
		unregisterEvents(e.peripheral);
		global.charactersticObject = null;
		global.serviceObject = null;
	};
	var peripheralFailToConnectListener = (e) => {
		Ti.API.info('didFailToConnectPeripheral');
		Ti.API.info(e.peripheral);
		if (global.IOS) {
			Ti.API.info(e.error.localizedDescription);
		}
		Ti.API.info('Fail to connect with Peripheral - error code ' + e.errorCode + ' error domain: ' + e.errorDomain + ' error description ' + e.errorDescription);
		logs.push('did Fail To Connect Peripheral');
		setData(logs);
	};
	centralManager.addEventListener('didConnectPeripheral', peripheralConnectedListener);

	centralManager.addEventListener('didDisconnectPeripheral', peripheralDisconnectedListener);

	centralManager.addEventListener('didFailToConnectPeripheral', peripheralFailToConnectListener);

	// Configure UI
	var deviceWindow = Ti.UI.createWindow({
		backgroundColor: 'white',
		title: 'Device information',
		titleAttributes: { color: 'blue' }
	});
	var navDeviceWindow = Ti.UI.createNavigationWindow({
		window: deviceWindow
	});

	var backButton = Titanium.UI.createButton({
		top: 100,
		title: 'Go to device list'
	});
	backButton.addEventListener('click', function () {
		navDeviceWindow.close();
	});

	var nameLabel = Ti.UI.createLabel({
		color: 'black',
		top: 140,
		width: 250,
		font: { fontSize: 14 },
		text: 'Name - ' + peripheral.name
	});
	var uuidLabel = Ti.UI.createLabel({
		color: 'blue',
		top: 170,
		width: 250,
		font: { fontSize: 11 },
		text: ('ADDRESS - ' + peripheral.address)
	});

	var connectButton = Titanium.UI.createButton({
		top: 200,
		title: 'Connect'
	});
	var disConnectButton = Titanium.UI.createButton({
		top: 250,
		title: 'Disconnect'
	});

	var imageView = Ti.UI.createImageView({
		top: 300,
		width: 100,
		height: 100,
		borderWidth: 1.0
	});
	var showImage = Titanium.UI.createButton({
		top: 430,
		title: 'Show Downloaded Image'
	});
	var deleteImage = Titanium.UI.createButton({
		top: 480,
		title: 'Delete Downloaded Image'
	});
	var tableView = Titanium.UI.createTableView({
		top: 530,
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
					row: i,
					hasChild: true
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

	deviceWindow.add(connectButton);
	deviceWindow.add(backButton);
	deviceWindow.add(nameLabel);
	deviceWindow.add(uuidLabel);
	deviceWindow.add(disConnectButton);
	deviceWindow.add(imageView);
	deviceWindow.add(showImage);
	deviceWindow.add(deleteImage);
	deviceWindow.add(tableView);

	showImage.addEventListener('click', function () {
		var blob = imageFile.read();
		if (imageFile !== null && blob.length !== 0) {
			imageFile.resolve();
			imageView.image = imageFile;
		} else {
			alert('No image found');
		}
	});
	deleteImage.addEventListener('click', function () {
		var blob = imageFile.read();
		if (imageFile !== null && blob.length !== 0) {
			createImage();
			alert('Downloaded image delete');
		} else {
			alert('No image found');
		}
	});
	connectButton.addEventListener('click', function () {
		if (peripheral) {
			if (!peripheral.isConnected) {
				centralManager.connectPeripheral({
					peripheral: peripheral,
					options: { [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true }
				});
			} else {
				alert('Device already connected');
			}
		} else {
			alert('No peripheral available to connect');
		}
	});

	disConnectButton.addEventListener('click', function () {
		if (channel) {
			channel.close();
		}
		if (peripheral) {
			centralManager.cancelPeripheralConnection({ peripheral: peripheral });
		} else {
			alert('No peripheral available to disconnect');
		}
	});

	var servicesDiscoveredListener = (e) => {
		Ti.API.info('didDiscoverServices ' + e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while discovering services' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let discoverServicePeripheral = e.sourcePeripheral;
		discoverHeartRateServices(discoverServicePeripheral);
	};

	var characteristicDiscoveredListener = (e) => {
		Ti.API.info('didDiscoverCharacteristics');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while discovering characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let discoverCharacteristicPeripheral = e.sourcePeripheral;
		discoverChannelCharacteristic(discoverCharacteristicPeripheral);// Subscribe To Characteristic
	};

	var subscribeCharListener = (e) => {
		Ti.API.info('didUpdateNotificationStateForCharacteristics');
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while subscribing characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let characteristic = e.characteristic;
		var subscribe = global.IOS ? characteristic.isNotifying === true : e.isSubscribed === true;
		if (subscribe) {
			logs.push('subscribed for Channel Characteristic');
		} else {
			logs.push('unsubscribed for Channel Characteristic');
		}
		setData(logs);
	};

	var charValueListener = (e) => {
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while didUpdateValueForCharacteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		Ti.API.info('characteristic read value : ' + e.value);
		let value = e.value.toString();
		if (value) {
			logs.push('PSM ID From Peripheral Manager: ' + value);
			setData(logs);
			e.sourcePeripheral.openL2CAPChannel({
				psmIdentifier: Number(e.value.toString())
			});
		}
	};

	var descriptorsDiscoveredListener = (e) => {
		Ti.API.info('didDiscoverDescriptorsForCharacteristics');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			Ti.API.info('Error while discovering descriptors for characteristics' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	};

	var includedServicesDiscoveredListener = (e) => {
		Ti.API.info('didDiscoverIncludedServices');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while discovering included services' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	};

	var channelOpenListener = (e) => {
		Ti.API.info('didOpenChannel');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while opening channel' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		} else {
			logs.push('Peripheral Manager opened L2CAP channel');
			setData(logs);
			channel = e.channel;
			e.channel.addEventListener('onDataReceived', function (e) {
				Ti.API.info('Peripheral Manager received read data from channel');
				logs.push('Data Received from channel (length: ' + e.data.length + ')');
				if (imageFile.write(e.data.toBlob(), true) === false) {
					logs.push('Got error while writting file');
				}
				setData(logs);
			});
			e.channel.addEventListener('onStreamError', function (e) {
				Ti.API.info('Peripheral Manager get error');
				logs.push('Got Stream Error: ' + e.errorDescription);
				if (!global.IOS) {
					Ti.API.info('L2CAP channel closed due to stream error.');
					logs.push('L2CAP channel closed due to Stream Error.');
				}
				setData(logs);
			});
		}
	};

	function registerEvents(connectedPeripheral) {
		connectedPeripheral.addEventListener('didDiscoverServices', servicesDiscoveredListener);

		connectedPeripheral.addEventListener('didDiscoverCharacteristics', characteristicDiscoveredListener);

		connectedPeripheral.addEventListener('didUpdateNotificationStateForCharacteristics', subscribeCharListener);

		connectedPeripheral.addEventListener('didUpdateValueForCharacteristic', charValueListener);

		connectedPeripheral.addEventListener('didDiscoverDescriptorsForCharacteristics', descriptorsDiscoveredListener);

		connectedPeripheral.addEventListener('didDiscoverIncludedServices', includedServicesDiscoveredListener);

		connectedPeripheral.addEventListener('didReadRSSI', function (e) {
			Ti.API.info('didReadRSSI');
			Ti.API.info(e);
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
				alert('Error while reading RSSI' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
		});

		connectedPeripheral.addEventListener('didUpdateValueForDescriptor', function (e) {
			Ti.API.info('didUpdateValueForDescriptor');
			Ti.API.info(e);
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
				alert('Error while updating value for descriptor' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
		});

		connectedPeripheral.addEventListener('didWriteValueForCharacteristic', function (e) {
			Ti.API.info('didWriteValueForCharacteristic');
			Ti.API.info(e);
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
				alert('Error while write value for characteristic ' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
		});

		connectedPeripheral.addEventListener('didWriteValueForDescriptor', function (e) {
			Ti.API.info('didWriteValueForDescriptor');
			Ti.API.info(e);
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
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

		connectedPeripheral.addEventListener('didOpenChannel', channelOpenListener);
	}

	function unregisterEvents(peripheral) {
		peripheral.removeEventListener('didDiscoverServices', servicesDiscoveredListener);
		peripheral.removeEventListener('didDiscoverCharacteristics', characteristicDiscoveredListener);
		peripheral.removeEventListener('didUpdateNotificationStateForCharacteristics', subscribeCharListener);
		peripheral.removeEventListener('didUpdateValueForCharacteristic', charValueListener);
		peripheral.removeEventListener('didDiscoverDescriptorsForCharacteristics', descriptorsDiscoveredListener);
		peripheral.removeEventListener('didDiscoverIncludedServices', includedServicesDiscoveredListener);
		peripheral.removeEventListener('didOpenChannel', channelOpenListener);
	}

	function discoverHeartRateServices (sourcePeripheral) {
		var services;
		// sourcePeripheral is the peripheral sending the didDiscoverServices event
		services = sourcePeripheral.services;
		Ti.API.info('services ' + services);
		services.forEach(function (service) {
			Ti.API.info('Discovered service ' + service.uuid);
			if (service.uuid.toLowerCase() === serviceUUID.toLowerCase()) {
				global.serviceObject = service;
				Ti.API.info('Found heart rate service!');
				logs.push('Found heart rate service!');
				setData(logs);
				sourcePeripheral.discoverCharacteristics({
					service: service
				});
			}
		});
	}

	function discoverChannelCharacteristic (sourcePeripheral) {
		var characteristics;
		characteristics = global.serviceObject.characteristics;
		Ti.API.info('characteristics ' + characteristics);
		characteristics.forEach(function (characteristic) {
			logs.push('Discovered characteristic ' + characteristic.uuid);
			if (characteristic.uuid.toLowerCase() === characteristicUUID.toLowerCase()) {
				global.charactersticObject = characteristic;
				Ti.API.info('Found channel characteristic');
				logs.push('Found channel characteristic!');
				requestChannelPSMID();
			}
		});
		setData(logs);
	}
	function requestChannelPSMID() {
		peripheral.subscribeToCharacteristic({
			characteristic: global.charactersticObject
		});
		peripheral.readValueForCharacteristic({
			characteristic: global.charactersticObject
		});
		logs.push('Request Channel PSM from characteristic');
		setData(logs);
	}

	deviceWindow.addEventListener('close', function () {
		if (channel) {
			channel.close();
		}
		if (peripheral && peripheral.isConnected) {
			unregisterEvents(peripheral);
			centralManager.cancelPeripheralConnection({ peripheral: peripheral });
		}
		centralManager.removeEventListener('didConnectPeripheral', peripheralConnectedListener);
		centralManager.removeEventListener('didDisconnectPeripheral', peripheralDisconnectedListener);
		centralManager.removeEventListener('didFailToConnectPeripheral', peripheralFailToConnectListener);
	});

	return navDeviceWindow;
}
exports.deviceWin = deviceWin;
