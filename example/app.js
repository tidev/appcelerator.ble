/* eslint-disable no-alert */
// require BLE
var BLE = require('appcelerator.ble');
var isAndroid = Ti.Platform.osname === 'android';
if (isAndroid) {
	var win = Ti.UI.createWindow({
		backgroundColor: 'white'
	}).open();
	alert('Android is in progress');
	return;
}
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	var mainWindow = Ti.UI.createWindow({
		backgroundColor: 'White',
		titleAttributes: { color: 'blue' }
	});
	var navCentralWindow = Ti.UI.iOS.createNavigationWindow({
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
		var devicePage = new devices.centralManagerWin(BLE);
		navCentralWindow.openWindow(devicePage, { animated: true });
	});
	mainWindow.add(buttonCentral);

	var buttonPeripheral = Ti.UI.createButton({
		font: { fontSize: 20 },
		title: 'Peripheral',
		top: 240
	});
	buttonPeripheral.addEventListener('click', function () {
		var devices = require('peripheralManager.js');
		var devicePage = new devices.peripheralManagerWin(BLE);
		devicePage.open();
	});
	mainWindow.add(buttonPeripheral);

	navCentralWindow.open();
}
