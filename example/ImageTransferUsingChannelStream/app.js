/* eslint-disable no-alert */
// require BLE
var BLE = require('appcelerator.ble');
let serviceUUID = (global.IOS ? ('180D') : ('0000180D-0000-1000-8000-00805f9b34fb'));
let characteristicUUID = (global.IOS ? ('2A37') : ('00002A37-0000-1000-8000-00805f9b34fb'));
let channelCharacteristicUUID = BLE.CBUUID_L2CAPPSM_CHARACTERISTIC_STRING;

var isAndroid = Ti.Platform.osname === 'android';
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
global.isAndroid = isAndroid;
global.IOS = IOS;
var mainWindow = Ti.UI.createWindow({
	backgroundColor: 'White',
	titleAttributes: { color: 'blue' }
});
var navCentralWindow = Ti.UI.createNavigationWindow({
	window: mainWindow
});

var centralWithChannel = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Central With Channel',
	top: 50
});
centralWithChannel.addEventListener('click', function () {
	var devices = require('centralManager.js');
	var peripheralPage = require('peripheralChannel.js');
	var devicePage = new devices.centralManagerWin(BLE, 'Central With Channel', peripheralPage, serviceUUID, channelCharacteristicUUID);
	navCentralWindow.openWindow(devicePage, { animated: true });
});
mainWindow.add(centralWithChannel);

var buttonPeripheralChannel = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Peripheral With Channel',
	top: 120
});
buttonPeripheralChannel.addEventListener('click', function () {
	var devices = require('peripheralManagerChannel.js');
	var devicePage = new devices.peripheralManagerWin(BLE, serviceUUID, channelCharacteristicUUID);
	devicePage.open();
});
mainWindow.add(buttonPeripheralChannel);

navCentralWindow.open();
