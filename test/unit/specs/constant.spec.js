const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');

describe('appcelerator.ble', function () {
	if (IOS) {
		describe('ATTRIBUTE_PERMISSION_*', function () {
			it('ATTRIBUTE_PERMISSION_READABLE', () => {
				expect(BLE.ATTRIBUTE_PERMISSION_READABLE).toEqual(jasmine.any(Number));
			});

			it('ATTRIBUTE_PERMISSION_WRITEABLE', () => {
				expect(BLE.ATTRIBUTE_PERMISSION_WRITEABLE).toEqual(jasmine.any(Number));
			});

			it('ATTRIBUTE_PERMISSION_READ_ENCRYPTION_REQUIRED', () => {
				expect(BLE.ATTRIBUTE_PERMISSION_READ_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});

			it('ATTRIBUTE_PERMISSION_WRITE_ENCRYPTION_REQUIRED', () => {
				expect(BLE.ATTRIBUTE_PERMISSION_WRITE_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});
		});

		describe('CHARACTERISTIC_PROPERTIES_*', function () {
			it('CHARACTERISTIC_PROPERTIES_BROADCAST', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_BROADCAST).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_READ', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_READ).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_WRITE', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_WRITE).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_NOTIFY', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_NOTIFY).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_INDICATE', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_INDICATE).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_AUTHENTICATED_SIGNED_WRITES', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_AUTHENTICATED_SIGNED_WRITES).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_NOTIFY_ENCRYPTION_REQUIRED', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_NOTIFY_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_INDICATE_ENCRYPTION_REQUIRED', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_INDICATE_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});
		});
	}
});

