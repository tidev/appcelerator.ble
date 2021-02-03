const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
let UUID = BLE.CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING;
const descriptor = BLE.createDescriptor({ value: 1, permission: 1, uuid: UUID });

describe('appcelerator.ble.descriptor', function () {
	describe('.descriptor', () => {
		it('should be defined', () => {
			expect(descriptor).toBeDefined();
		});

		it('should have valid characteristic object', () => {
			expect(descriptor.characteristic).toEqual(jasmine.any(Object));
		});

		it('should have valid value', () => {
			if (IOS) {
				expect(descriptor.value).toEqual(1);
			} else {
				expect(descriptor.value).toEqual(jasmine.any(Object));
			}
		});

		it('should have valid uuid', () => {
			expect(descriptor.uuid).toEqual(UUID);
		});
	});
});
