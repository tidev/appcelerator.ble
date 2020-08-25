const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const centralManager = BLE.initCentralManager();

	describe('appcelerator.ble.centralManager', function () {
		it('should be defined', () => {
			expect(centralManager).toBeDefined();
		});

		describe('properties', () => {
			it('should have valid state', () => {
				expect(centralManager.state).toEqual(jasmine.any(Number));
			});

			it('should have valid isScanning', () => {
				expect(centralManager.isScanning).toEqual(false);
			});

			it('should have valid peripherals', () => {
				expect(centralManager.peripherals).toEqual(jasmine.any(Object));
			});
		});

		describe('methods', () => {
			it('should have startScan function', () => {
				expect(centralManager.startScan).toEqual(jasmine.any(Function));
			});

			it('should have stopScan function', () => {
				expect(centralManager.stopScan).toEqual(jasmine.any(Function));
			});

			it('should have registerForConnectionEvents function', () => {
				expect(centralManager.registerForConnectionEvents).toEqual(jasmine.any(Function));
			});

			it('should have retrievePeripheralsWithIdentifiers function', () => {
				expect(centralManager.retrievePeripheralsWithIdentifiers).toEqual(jasmine.any(Function));
			});

			it('should have retrieveConnectedPeripheralsWithServices function', () => {
				expect(centralManager.retrieveConnectedPeripheralsWithServices).toEqual(jasmine.any(Function));
			});

			it('should have cancelPeripheralConnection function', () => {
				expect(centralManager.cancelPeripheralConnection).toEqual(jasmine.any(Function));
			});

			it('should have connectPeripheral function', () => {
				expect(centralManager.connectPeripheral).toEqual(jasmine.any(Function));
			});

		});
	});
}
