const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const descriptor = BLE.addDescriptor({
		value: 1,
		uuid: BLE.CBUUID_CHARACTERISTIC_EXTENDED_PROPERTIES_STRING
	});

	describe('appcelerator.ble.descriptor', function () {
		describe('.descriptor', () => {
			it('should be defined', () => {
				expect(descriptor).toBeDefined();
			});

			it('should have valid characteristic object', () => {
				expect(descriptor.characteristic).toEqual(jasmine.any(Object));
			});

			it('should have valid value', () => {
				expect(descriptor.value).toEqual(1);
			});

			it('should have valid uuid', () => {
				expect(descriptor.uuid).toEqual(BLE.CBUUID_CHARACTERISTIC_EXTENDED_PROPERTIES_STRING);
			});
		});
	});
}
