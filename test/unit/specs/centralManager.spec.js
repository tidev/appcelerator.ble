const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
const android = Ti.Platform.osname === 'android';
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

		it('should have cancelPeripheralConnection function', () => {
			expect(centralManager.cancelPeripheralConnection).toEqual(jasmine.any(Function));
		});

		it('should have connectPeripheral function', () => {
			expect(centralManager.connectPeripheral).toEqual(jasmine.any(Function));
		});

	});

	if (IOS) {
		it('should have registerForConnectionEvents function', () => {
			expect(centralManager.registerForConnectionEvents).toEqual(jasmine.any(Function));
		});

		it('should have retrievePeripheralsWithIdentifiers function', () => {
			expect(centralManager.retrievePeripheralsWithIdentifiers).toEqual(jasmine.any(Function));
		});

		it('should have retrieveConnectedPeripheralsWithServices function', () => {
			expect(centralManager.retrieveConnectedPeripheralsWithServices).toEqual(jasmine.any(Function));
		});
	}

	if (android) {
		describe('.isAccessFineLocationPermissionGranted', () => {
			it('isAccessFineLocationPermissionGranted is a Function', () => {
				expect(centralManager.isAccessFineLocationPermissionGranted).toEqual(jasmine.any(Function));
			});

			it('isAccessFineLocationPermissionGranted returns a boolean', () => {
				expect(centralManager.isAccessFineLocationPermissionGranted()).toEqual(jasmine.any(Boolean));
			});
		});

		describe('.requestAccessFineLocationPermission()', () => {
			it('is a function', () => {
				expect(centralManager.requestAccessFineLocationPermission).toEqual(jasmine.any(Function));
			});
		});

		describe('.createPeripheral()', () => {
			it('createPeripheral is a function', () => {
				expect(centralManager.createPeripheral).toEqual(jasmine.any(Function));
			});

			it('createPeripheral returns peripheralproxy object', () => {
				expect(centralManager.createPeripheral('00:11:22:AA:BB:CC')).toEqual(jasmine.any(Object));
			});
		});
	}
});

