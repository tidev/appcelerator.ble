/* eslint-disable no-alert */
// require BLE
var isAndroid = Ti.Platform.osname === 'android';
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
global.isAndroid = isAndroid;
global.IOS = IOS;

var BLE = require('appcelerator.ble');
let serviceUUID = (global.IOS ? ('180D') : ('0000180D-0000-1000-8000-00805f9b34fb'));
let characteristicUUID = (global.IOS ? ('2A37') : ('00002A37-0000-1000-8000-00805f9b34fb'));
let channelCharacteristicUUID = BLE.CBUUID_L2CAPPSM_CHARACTERISTIC_STRING;

var mainWindow = Ti.UI.createWindow({
	backgroundColor: 'White',
	titleAttributes: { color: 'blue' }
});
var navCentralWindow = Ti.UI.createNavigationWindow({
	window: mainWindow
});
var buttonCentral = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Central',
	top: 50
});
// Central button will  open central section
buttonCentral.addEventListener('click', function () {
	var devices = require('centralManager.js');
	var peripheralPage = require('peripheral.js');
	var devicePage = new devices.centralManagerWin(BLE, 'Central', peripheralPage, serviceUUID, characteristicUUID);
	navCentralWindow.openWindow(devicePage, { animated: true });
});
mainWindow.add(buttonCentral);

var centralWithChannel = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Central With Channel',
	top: 140,
	visible: IOS
});
centralWithChannel.addEventListener('click', function () {
	var devices = require('centralManager.js');
	var peripheralPage = require('peripheralChannel.js');
	var devicePage = new devices.centralManagerWin(BLE, 'Central With Channel', peripheralPage, serviceUUID, channelCharacteristicUUID);
	navCentralWindow.openWindow(devicePage, { animated: true });
});
mainWindow.add(centralWithChannel);

var buttonPeripheral = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Peripheral',
	top: 240
});
buttonPeripheral.addEventListener('click', function () {
	if (isAndroid) {
		if (!BLE.isEnabled()) {
			alert('Please Enable Bluetooth');
			Ti.API.info('Please Enable Bluetooth');
			return;
		}
		if (!BLE.isAdvertisingSupported()) {
			alert('Bluetooth Advertising is not supported in this device');
			Ti.API.info('Bluetooth Advertising is not supported in this device');
			return;
		}
	}
	var devices = require('peripheralManager.js');
	var devicePage = new devices.peripheralManagerWin(BLE, serviceUUID, characteristicUUID);
	devicePage.open();
});
mainWindow.add(buttonPeripheral);

var buttonPeripheralChannel = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Peripheral With Channel',
	top: 300,
	visible: IOS
});
buttonPeripheralChannel.addEventListener('click', function () {
	var devices = require('peripheralManagerChannel.js');
	var devicePage = new devices.peripheralManagerWin(BLE, serviceUUID, channelCharacteristicUUID);
	devicePage.open();
});
mainWindow.add(buttonPeripheralChannel);

navCentralWindow.open();
