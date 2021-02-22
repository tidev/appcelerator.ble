const BLE = require('appcelerator.ble');
const android = Ti.Platform.osname === 'android';
if (android) {
	const centralManager = BLE.initCentralManager();
	const peripheralAddress = '00:11:22:AA:BB:CC';
	const peripheral = centralManager.createPeripheral(peripheralAddress);
	describe('appcelerator.ble.peripheral', function () {
		it('should be defined', () => {
			expect(peripheral).toBeDefined();
		});

		describe('address is a property', () => {
			it('should return String', () => {
				expect(peripheral.address).toEqual(jasmine.any(String));
			});

			it('should have valid address', () => {
				expect(peripheral.address).toEqual(peripheralAddress);
			});
		});

		describe('methods', () => {
			it('should have readRSSI function', () => {
				expect(peripheral.readRSSI).toEqual(jasmine.any(Function));
			});

			it('should have requestConnectionPriority function', () => {
				expect(peripheral.requestConnectionPriority).toEqual(jasmine.any(Function));
			});

			it('should have discoverServices function', () => {
				expect(peripheral.discoverServices).toEqual(jasmine.any(Function));
			});

			it('should have discoverIncludedServices function', () => {
				expect(peripheral.discoverIncludedServices).toEqual(jasmine.any(Function));
			});

			it('should have discoverCharacteristics function', () => {
				expect(peripheral.discoverCharacteristics).toEqual(jasmine.any(Function));
			});

			it('should have discoverDescriptorsForCharacteristic function', () => {
				expect(peripheral.discoverDescriptorsForCharacteristic).toEqual(jasmine.any(Function));
			});

			it('should have readValueForCharacteristic function', () => {
				expect(peripheral.readValueForCharacteristic).toEqual(jasmine.any(Function));
			});

			it('should have writeValueForCharacteristic function', () => {
				expect(peripheral.writeValueForCharacteristic).toEqual(jasmine.any(Function));
			});

			it('should have readValueForDescriptor function', () => {
				expect(peripheral.readValueForDescriptor).toEqual(jasmine.any(Function));
			});

			it('should have writeValueForDescriptor function', () => {
				expect(peripheral.writeValueForDescriptor).toEqual(jasmine.any(Function));
			});

			it('should have subscribeToCharacteristic function', () => {
				expect(peripheral.subscribeToCharacteristic).toEqual(jasmine.any(Function));
			});

			it('should have unsubscribeFromCharacteristic function', () => {
				expect(peripheral.unsubscribeFromCharacteristic).toEqual(jasmine.any(Function));
			});

			it('should have openL2CAPChannel function', () => {
				expect(peripheral.openL2CAPChannel).toEqual(jasmine.any(Function));
			});
		});
	});
}
