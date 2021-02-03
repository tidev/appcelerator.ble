const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
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
			expect(peripheralManager.peripheralManagerState).toEqual(jasmine.any(Number));
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

		it('should have stopAdvertising function', () => {
			expect(peripheralManager.stopAdvertising).toEqual(jasmine.any(Function));
		});

		it('should have updateValue function', () => {
			expect(peripheralManager.updateValue).toEqual(jasmine.any(Function));
		});

		if (IOS) {
			it('should have setDesiredConnectionLatency function', () => {
				expect(peripheralManager.setDesiredConnectionLatency).toEqual(jasmine.any(Function));
			});
		} else {
			it('should have closePeripheral function', () => {
				expect(peripheralManager.closePeripheral).toEqual(jasmine.any(Function));
			});

			it('should have respondToDescriptorRequest function', () => {
				expect(peripheralManager.respondToDescriptorRequest).toEqual(jasmine.any(Function));
			});
		}
	});
});
