const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');

describe('appcelerator.ble', function () {
	it('can be required', () => {
		expect(BLE).toBeDefined();
	});
	if (IOS) {
		describe('methods', function () {
			it('should have initCentralManager function', () => {
				expect(BLE.initCentralManager).toEqual(jasmine.any(Function));
			});
		});

		describe('property', () => {
			describe('authorizationState', () => {
				it('should be number', () => {
					expect(BLE.authorizationState).toEqual(jasmine.any(Number));
				});
			});
		});
	}
});
