/* eslint-disable no-alert */
// require BLE
var BLE = require('appcelerator.ble');
let beaconUUID = '135C8F13-6A2D-46ED-AA71-FB956FC23742';
let major = 100;
let minor = 0;
let beaconId = 'com.appcelerator.BluetoothLowEnergy.beacon';

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
var buttonAdvertiser = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Advertise Beacon',
	top: 50
});
buttonAdvertiser.addEventListener('click', function () {
	var devices = require('beaconAdvertiser.js');
	var devicePage = new devices.deviceWin(BLE, beaconUUID, major, minor, beaconId);
	navCentralWindow.openWindow(devicePage, { animated: true });
});
mainWindow.add(buttonAdvertiser);

var buttonScanner = Ti.UI.createButton({
	font: { fontSize: 20 },
	title: 'Scan Beacon',
	top: 100,
	visible: IOS
});
buttonScanner.addEventListener('click', function () {
	alert('Beacon Scanner is not implemented yet');
});
mainWindow.add(buttonScanner);

navCentralWindow.open();
