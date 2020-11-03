/* eslint-disable no-alert */

function deviceWin(peripheral, centralManager, BLE) {
	var serviceUUID = '12E61727-B41A-436F-B64D-4777B35F2294';
	var characteristicUUID = BLE.CBUUID_L2CAPPSM_CHARACTERISTIC_STRING;
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
		text: 'UUID - ' + peripheral.UUID
	});

	var connectButton = Titanium.UI.createButton({
		top: 200,
		title: 'Connect'
	});
	var disConnectButton = Titanium.UI.createButton({
		top: 250,
		title: 'Disconnect'
	});

	var valueField = Ti.UI.createTextField({
		top: 400,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Value',
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});

	var writeValue = Titanium.UI.createButton({
		top: 450,
		title: 'Write Value'
	});

	var tableView = Titanium.UI.createTableView({
		top: 500,
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

	navDeviceWindow.add(connectButton, backButton, nameLabel, uuidLabel, disConnectButton, tableView, valueField, writeValue);

	// Buttoon click events
	writeValue.addEventListener('click', function () {
		if (channel) {
			var data = valueField.value === '' || valueField.value === null ? 'temp data' : valueField.value;
			var buffer = Ti.createBuffer({ value: data });
			channel.write({
				data: buffer
			});
		} else {
			alert('No Channel open for write value');
		}
	});

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
				logs.push('Value from Peripheral Manager: ' + value);
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
					logs.push('Data Received from channel: ' + e.data);
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
			Ti.API.info('Discovered characteristic ' + characteristic.UUID);
			if (characteristic.uuid === characteristicUUID) {
				global.charactersticObject = characteristic;
				Ti.API.info('Found channel characteristic');
				logs.push('Found channel characteristic!');
				setData(logs);
				requestChannelPSMID(characteristic);
			}
		});
	}
	function requestChannelPSMID(characteristic) {
		peripheral.subscribeToCharacteristic({
			characteristic: global.charactersticObject
		});
		peripheral.readValueForCharacteristic({
			characteristic: characteristic
		});
		logs.push('Request Channel PSM from characteristic');
		setData(logs);
	}
	return navDeviceWindow;
}
exports.deviceWin = deviceWin;
