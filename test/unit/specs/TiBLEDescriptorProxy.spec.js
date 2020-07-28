const Ble = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	var central = Ble.createDescriptor();
	describe('TiBLEDescriptorProxy', function () {
		describe('property', () => {
			it('should be defined', () => {
				expect(central).toBeDefined();
			});

			it('should be a class object', () => {
				expect(central.characterstic).toEqual(jasmine.any(Object));
			});

			it('should be number', () => {
				expect(central.value).toEqual(123);
			});

			it('should have UDID', () => {
				expect(central.UUID).toEqual('some');
			});
		});
	});
}
