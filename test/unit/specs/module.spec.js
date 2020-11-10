const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
const android = Ti.Platform.osname === 'android';

describe('appcelerator.ble', function () {
	it('can be required', () => {
		expect(BLE).toBeDefined();
	});

	it('should have initCentralManager function', () => {
		expect(BLE.initCentralManager).toEqual(jasmine.any(Function));
	});

	if (IOS) {
		describe('methods', function () {
			it('should have createMutableCharacteristic function', () => {
				expect(BLE.createMutableCharacteristic).toEqual(jasmine.any(Function));
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

	if (android) {
		describe('.isEnabled()', () => {
			it('isEnabled is a Function', () => {
				expect(BLE.isEnabled).toEqual(jasmine.any(Function));
			});

			it('isEnabled returns a boolean', () => {
				expect(BLE.isSupported()).toEqual(jasmine.any(Boolean));
			});
		});

		describe('.isSupported()', () => {
			it('isSupported is a Function', () => {
				expect(BLE.isSupported).toEqual(jasmine.any(Function));
			});

			it('isSupported returns a boolean', () => {
				expect(BLE.isSupported()).toEqual(jasmine.any(Boolean));
			});
		});

		describe('.enable()', () => {
			it('enable is a Function', () => {
				expect(BLE.enable).toEqual(jasmine.any(Function));
			});

			it('enable returns a boolean', () => {
				expect(BLE.enable()).toEqual(jasmine.any(Boolean));
			});
		});

		describe('.disable()', () => {
			it('disable is a Function', () => {
				expect(BLE.disable).toEqual(jasmine.any(Function));
			});

			it('disable returns a boolean', () => {
				expect(BLE.disable()).toEqual(jasmine.any(Boolean));
			});
		});

		describe('.isBluetoothAndBluetoothAdminPermissionsGranted', () => {
			it('isBluetoothAndBluetoothAdminPermissionsGranted is a Function', () => {
				expect(BLE.isBluetoothAndBluetoothAdminPermissionsGranted).toEqual(jasmine.any(Function));
			});

			it('isBluetoothAndBluetoothAdminPermissionsGranted returns a boolean', () => {
				expect(BLE.isBluetoothAndBluetoothAdminPermissionsGranted()).toEqual(jasmine.any(Boolean));
			});
		});
	}
});
