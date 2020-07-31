let BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	describe('appcelerator.ble', function () {
		describe('property', () => {
			describe('authorizationState', () => {
				it('should be number', () => {
					expect(BLE.authorizationState).toEqual(jasmine.any(Number));
				});
			});
		});
	});
}

