const Ble = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	var central = Ble.createCentral();
	describe('appcelerator.ble', function () {
		describe('property', () => {
			it('should be defined', () => {
				expect(central).toBeDefined();
			});

			it('should be number', () => {
				expect(central.maximumUpdateValueLength).toEqual(123);
			});

			it('should have UDID', () => {
				expect(central.UUID).toEqual('some');
			});
		});
	});
}
