const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	describe('appcelerator.ble.characteristic', function () {
		const characteristic = BLE.addCharacteristic({
			properties: 0,
			permissions: 0,
			descriptors: [],
			value: 'some',
			uuid: BLE.CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING
		});

		describe('.characteristic', function () {
			it('should be defined', () => {
				expect(characteristic).toBeDefined();
			});

			it('should have valid property type', () => {
				expect(characteristic.properties).toBeDefined(jasmine.any(Number));
			});

			it('should have valid descriptor object', () => {
				expect(characteristic.descriptors).toEqual(jasmine.any(Object));
			});

			it('should have valid uuid ', () => {
				expect(characteristic.uuid).toEqual(BLE.CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING);
			});

		});
	});
}
