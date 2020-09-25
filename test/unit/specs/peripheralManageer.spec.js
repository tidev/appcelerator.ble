const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const peripheralManager = BLE.initPeripheralManager();

	describe('appcelerator.ble.peripheralManager', function () {
		it('should be defined', () => {
			expect(peripheralManager).toBeDefined();
		});

		describe('methods', () => {
			it('should have addService function', () => {
				expect(peripheralManager.addService).toEqual(jasmine.any(Function));
			});

			it('should have removeAllServices function', () => {
				expect(peripheralManager.removeAllServices).toEqual(jasmine.any(Function));
			});

			it('should have removeServices function', () => {
				expect(peripheralManager.removeServices).toEqual(jasmine.any(Function));
			});
		});
	});
}
