/* eslint-disable no-alert */

function deviceWin(peripheral, centralManager, BLE, serviceUUID, characteristicUUID) {
	var logs = [];
	// Central event for peripheral connection
	var peripheralConnectedListener = (e) => {
		logs.push('Connected to Peripheral');
		setData(logs);
		registerEvents(e.peripheral);
		peripheral.discoverServices();
	};
	var peripheralDisconnectedListener = (e) => {
		Ti.API.info('Disconnected from Peripheral: ' + e.peripheral.name + ' with address: ' + e.peripheral.address);
		unregisterEvents(e.peripheral);

		logs.push('Peripheral Disconnected');
		setData(logs);
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
		top: 40,
		title: 'Go to device list'
	});
	backButton.addEventListener('click', function () {
		navDeviceWindow.close();
	});

	var nameLabel = Ti.UI.createLabel({
		color: 'black',
		top: 95,
		width: 250,
		font: { fontSize: 15 },
		text: 'Name - ' + peripheral.name
	});
	var uuidLabel = Ti.UI.createLabel({
		color: 'blue',
		top: 120,
		width: 250,
		font: { fontSize: 11 },
		text: ('ADDRESS - ' + peripheral.address)
	});

	var connectButton = Titanium.UI.createButton({
		top: 150,
		title: 'Connect'
	});
	var disConnectButton = Titanium.UI.createButton({
		top: 190,
		title: 'Disconnect'
	});
	var subscribeButton = Titanium.UI.createButton({
		top: 230,
		title: 'Subscribe to Heart Rate (2A37)'
	});
	var unsubscribeButton = Titanium.UI.createButton({
		top: 270,
		title: 'Unsubscribe'
	});

	var valueField = Ti.UI.createTextField({
		top: 320,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Value',
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});

	var writeValue = Titanium.UI.createButton({
		top: 360,
		title: 'Write Value'
	});

	var tableView = Titanium.UI.createTableView({
		top: 410,
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

	deviceWindow.add(connectButton);
	deviceWindow.add(backButton);
	deviceWindow.add(nameLabel);
	deviceWindow.add(uuidLabel);
	deviceWindow.add(disConnectButton);
	deviceWindow.add(subscribeButton);
	deviceWindow.add(unsubscribeButton);
	deviceWindow.add(tableView);
	deviceWindow.add(valueField);
	deviceWindow.add(writeValue);

	// Buttoon click events
	writeValue.addEventListener('click', function () {
		if (peripheral.isConnected) {
			if (global.charactersticObject) {
				var data = valueField.value === '' || valueField.value === null ? 'temp data' : valueField.value;
				var buffer = Ti.createBuffer({ value: data });
				// Characteristic needs to have write permission & property
				peripheral.writeValueForCharacteristic({
					data: buffer,
					characteristic: global.charactersticObject,
					type: BLE.CHARACTERISTIC_TYPE_WRITE_WITHOUT_RESPONSE
				});
			} else {
				alert('No heart rate characteristic (2A37) available to write value');
			}
		} else {
			alert('Peripheral is not connected.');
		}
	});

	connectButton.addEventListener('click', function () {
		if (!peripheral) {
			logs.push('No peripheral available to connect');
			setData(logs);
			return;
		}
		if (peripheral.isConnected) {
			logs.push('Peripheral already connect');
			setData(logs);
			return;
		}

		centralManager.connectPeripheral({
			peripheral: peripheral,
			options: { [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true }
		});
	});

	disConnectButton.addEventListener('click', function () {
		if (peripheral) {
			if (peripheral.isConnected) {
				centralManager.cancelPeripheralConnection({ peripheral: peripheral });
			} else {
				alert('Peripheral not connected.');
			}
		} else {
			alert('No peripheral available to disconnect');
		}
	});

	subscribeButton.addEventListener('click', function () {
		if (peripheral) {
			if (peripheral.isConnected) {
				if (global.charactersticObject) {
					var descriptorValue;
					if ((global.charactersticObject.properties & BLE.CHARACTERISTIC_PROPERTIES_INDICATE) !== 0) {
						descriptorValue = BLE.ENABLE_INDICATION_VALUE;
					} else {
						descriptorValue = BLE.ENABLE_NOTIFICATION_VALUE;
					}
					peripheral.subscribeToCharacteristic({
						characteristic: global.charactersticObject,
						descriptorUUID: BLE.CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING,
						descriptorValue: descriptorValue
					});
				} else {
					alert('Heart Rate Characteristic (2A37) Not found');
				}
			} else {
				alert('Peripheral is not connected. Click \'Connect\'');
			}
		} else {
			alert('No peripheral available to discover service');
		}
	});

	unsubscribeButton.addEventListener('click', function () {
		if (peripheral.isConnected) {
			if (global.charactersticObject) {
				peripheral.unsubscribeFromCharacteristic({
					characteristic: global.charactersticObject,
					descriptorUUID: BLE.CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING,
					descriptorValue: BLE.DISABLE_NOTIFICATION_VALUE
				});
			} else {
				alert('No registered characteristic available to unsubscribe');
			}
		} else {
			alert('Peripheral is not connected.');
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
	var characteristicDiscoveredListener = (e) =>  {
		Ti.API.info('didDiscoverCharacteristics');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while discovering characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
		let discoverCharacteristicPeripheral = e.sourcePeripheral;
		discoverHeartRateCharacteristic(discoverCharacteristicPeripheral);// Subscribe To Characteristic
	};
	var descriptorDiscoveredListener = (e) => {
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
	var readRssiListener = (e) => {
		Ti.API.info('didReadRSSI');
		Ti.API.info(e);
		if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
			alert('Error while reading RSSI' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
			return;
		}
	};

	function registerEvents(connectedPeripheral) {
		connectedPeripheral.addEventListener('didDiscoverServices', servicesDiscoveredListener);

		connectedPeripheral.addEventListener('didDiscoverCharacteristics', characteristicDiscoveredListener);

		connectedPeripheral.addEventListener('didUpdateNotificationStateForCharacteristics', function (e) {
			Ti.API.info('didUpdateNotificationStateForCharacteristics');
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
				alert('Error while subscribing characteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
			let characteristic = e.characteristic;
			var subscribe = global.IOS ? characteristic.isNotifying === true : e.isSubscribed === true;
			if (subscribe) {
				logs.push('subscribed for Heart Rate (2A37)');
			} else {
				logs.push('unsubscribed for Heart Rate (2A37)');
			}
			setData(logs);
		});

		connectedPeripheral.addEventListener('didUpdateValueForCharacteristic', function (e) {
			if (typeof e.errorCode !== 'undefined' && e.errorCode !== null) {
				alert('Error while didUpdateValueForCharacteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
			Ti.API.info('ValueForCharacteristic ' + e.value);
			const buffer = e.value;
			if (buffer) {
				var firstBitValue = buffer[0] & 0x01;
				if (firstBitValue === 0) {
					// Heart Rate Value Format is in the 2nd byte
					logs.push('Value from Peripheral Manager: ' + buffer[1]);
				} else {
					// Heart Rate Value Format is in the 2nd and 3rd bytes
					logs.push('Value from Peripheral Manager: ' + ((buffer[1] << 8) +  buffer[2]));
				}
				setData(logs);
			}
		});

		connectedPeripheral.addEventListener('didDiscoverDescriptorsForCharacteristics', descriptorDiscoveredListener);

		connectedPeripheral.addEventListener('didDiscoverIncludedServices', includedServicesDiscoveredListener);

		connectedPeripheral.addEventListener('didReadRSSI', readRssiListener);

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

		connectedPeripheral.addEventListener('didOpenChannel', function (e) {
			Ti.API.info('didOpenChannel');
			Ti.API.info(e);
			if (e.errorCode !== null) {
				alert('Error while opening channel' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
				return;
			}
		});
	}

	function unregisterEvents(peripheral) {
		peripheral.removeEventListener('didDiscoverServices', servicesDiscoveredListener);
		peripheral.removeEventListener('didDiscoverCharacteristics', characteristicDiscoveredListener);
		peripheral.removeEventListener('didDiscoverDescriptorsForCharacteristics', descriptorDiscoveredListener);
		peripheral.removeEventListener('didDiscoverIncludedServices', includedServicesDiscoveredListener);
		peripheral.removeEventListener('didReadRSSI', readRssiListener);
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
				if (sourcePeripheral.isConnected) {
					sourcePeripheral.discoverCharacteristics({
						service: service
					});
				}
			}
		});
	}

	function discoverHeartRateCharacteristic (sourcePeripheral) {
		var characteristics;
		characteristics = global.serviceObject.characteristics;
		Ti.API.info('characteristics ' + characteristics);
		characteristics.forEach(function (characteristic) {
			Ti.API.info('Discovered characteristic ' + characteristic.uuid);
			if (characteristic.uuid.toLowerCase() === characteristicUUID.toLowerCase()) {
				global.charactersticObject = characteristic;
				Ti.API.info('Found heart rate characteristic, will subscribe...');
				logs.push('Found heart rate characteristic!');
				setData(logs);
			}
		});
	}

	deviceWindow.addEventListener('close', function () {
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
