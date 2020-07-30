let BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
describe('appcelerator.ble', function () {
	it('can be required', () => {
		expect(BLE).toBeDefined();
	});
	if (IOS) {
		describe('constants', () => {
			describe('AUTHORISATION_STATUS_*', () => {
				it('AUTHORISATION_STATUS_NOT_DETERMINED', () => {
					expect(BLE.AUTHORISATION_STATUS_NOT_DETERMINED).toEqual(jasmine.any(Number));
				});

				it('AUTHORISATION_STATUS_RESTRICTED', () => {
					expect(BLE.AUTHORISATION_STATUS_RESTRICTED).toEqual(jasmine.any(Number));
				});

				it('AUTHORISATION_STATUS_DENIED', () => {
					expect(BLE.AUTHORISATION_STATUS_DENIED).toEqual(jasmine.any(Number));
				});

				it('AUTHORISATION_STATUS_ALLOWED_ALWAYS', () => {
					expect(BLE.AUTHORISATION_STATUS_ALLOWED_ALWAYS).toEqual(jasmine.any(Number));
				});
			});
		});
	}
});
