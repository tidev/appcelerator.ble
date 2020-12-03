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
	centralManager.addEventListener('didConnectPeripheral', function (e) {
		logs.push('Connected to Peripheral');
		setData(logs);
		registerEvents(e.peripheral);
		peripheral.discoverServices();
	});

	centralManager.addEventListener('didDisconnectPeripheral', function (e) {
		Ti.API.info('Disconnected from Peripheral: ' + e.peripheral.name + ' with UUID: ' + e.peripheral.uuid);
		logs.push('Peripheral Disconnected');
		setData(logs);
		global.charactersticObject = null;
		global.serviceObject = null;
	});

	centralManager.addEventListener('didFailToConnectPeripheral', function (e) {
		Ti.API.info('didFailToConnectPeripheral');
		Ti.API.info(e.peripheral);
		Ti.API.info(e.error.localizedDescription);
		Ti.API.info('Fail to connect with Peripheral - error code ' + e.errorCode + ' error domain: ' + e.errorDomain + ' error description ' + e.errorDescription);
		logs.push('did Fail To Connect Peripheral');
		setData(logs);
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
		text: 'UUID - ' + peripheral.address
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

	navDeviceWindow.add(connectButton, backButton, nameLabel, uuidLabel, disConnectButton, imageView, showImage, deleteImage, tableView);

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
		if (peripheral) {
			centralManager.cancelPeripheralConnection({ peripheral: peripheral });
		} else {
			alert('No peripheral available to disconnect');
		}
	});

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
			discoverChannelCharacteristic(discoverCharacteristicPeripheral);// Subscribe To Characteristic
		});

		connectedPeripheral.addEventListener('didUpdateNotificationStateForCharacteristics', function (e) {
			Ti.API.info('didUpdateNotificationStateForCharacteristics');
			if (e.errorCode !== null) {
				alert('Error while subscribing characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
			let characteristic = e.characteristic;
			if (characteristic.isNotifying === true) {
				logs.push('subscribed for Channel Characteristic');
			} else {
				logs.push('unsubscribed for Channel Characteristic');
			}
			setData(logs);
		});

		connectedPeripheral.addEventListener('didUpdateValueForCharacteristic', function (e) {
			if (e.errorCode !== null) {
				alert('Error while didUpdateValueForCharacteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
			Ti.API.info('ValueForCharacteristic ' + e.value);
			let value = e.value.toString();
			if (value) {
				logs.push('PSM ID From Peripheral Manager: ' + value);
				setData(logs);
				e.sourcePeripheral.openL2CAPChannel({
					psmIdentifier: Number(e.value.toString())
				});
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
				alert('Error while write value for characteristic ' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
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
					setData(logs);
				});
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
			if (characteristic.uuid === characteristicUUID) {
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
	return navDeviceWindow;
}
exports.deviceWin = deviceWin;
