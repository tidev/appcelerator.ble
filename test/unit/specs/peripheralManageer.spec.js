const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const peripheralManager = BLE.initPeripheralManager();

	describe('appcelerator.ble.peripheralManager', function () {
		it('should be defined', () => {
			expect(peripheralManager).toBeDefined();
		});

		describe('properties', () => {
			it('should adverties data', () => {
				expect(peripheralManager.isAdvertising).toEqual(false);
			});

			it('should have valid state', () => {
				expect(peripheralManager.peripheralManagerState).toEqual(2);
			});
		});

		describe('methods', () => {
			it('should have addService function', () => {
				expect(peripheralManager.addService).toEqual(jasmine.any(Function));
			});

			it('should have removeAllServices function', () => {
				expect(peripheralManager.removeAllServices).toEqual(jasmine.any(Function));
			});

			it('should have removeService function', () => {
				expect(peripheralManager.removeService).toEqual(jasmine.any(Function));
			});

			it('should have respondToRequest function', () => {
				expect(peripheralManager.respondToRequest).toEqual(jasmine.any(Function));
			});

			it('should have startAdvertising function', () => {
				expect(peripheralManager.startAdvertising).toEqual(jasmine.any(Function));
			});

			it('should have startAdvertisingBeaconRegion function', () => {
				expect(peripheralManager.startAdvertisingBeaconRegion).toEqual(jasmine.any(Function));
			});

			it('should have stopAdvertising function', () => {
				expect(peripheralManager.stopAdvertising).toEqual(jasmine.any(Function));
			});

			it('should have updateValue function', () => {
				expect(peripheralManager.updateValue).toEqual(jasmine.any(Function));
			});

			it('should have setDesiredConnectionLatency function', () => {
				expect(peripheralManager.setDesiredConnectionLatency).toEqual(jasmine.any(Function));
			});

		});
	});
}
