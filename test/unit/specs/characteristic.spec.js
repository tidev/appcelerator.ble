const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
let UUID = IOS ? BLE.CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING : BLE.MOCK_UUID_FOR_CHARACTERISTIC_UT;
const characteristic = IOS ? BLE.createMutableCharacteristic({
	properties: [ BLE.CHARACTERISTIC_PROPERTIES_READ, BLE.CHARACTERISTIC_PROPERTIES_INDICATE ],
	permissions: [ BLE.CHARACTERISTIC_PERMISSION_READABLE ], descriptors: [], uuid: UUID
}) : BLE.mockCharacteristicForUT({ properties: 0, permissions: 0, uuid: UUID });

const charToCompare = IOS ? BLE.createMutableCharacteristic({
	properties: [ BLE.CHARACTERISTIC_PROPERTIES_READ, BLE.CHARACTERISTIC_PROPERTIES_INDICATE ],
	permissions: [ BLE.CHARACTERISTIC_PERMISSION_READABLE ], descriptors: [], uuid: UUID
}) : BLE.mockCharacteristicForUT({ properties: 0, permissions: 0, uuid: UUID });

describe('appcelerator.ble.characteristic', function () {

	describe('.characteristic', function () {
		it('should be defined', () => {
			expect(characteristic).toBeDefined();
		});

		it('should have valid property type', () => {
			expect(characteristic.properties).toBeDefined(jasmine.any(Number));
		});

		it('should have valid descriptor object', () => {
			expect(characteristic.descriptors).toEqual(jasmine.any(Object));
		});

		it('should have valid service object', () => {
			expect(characteristic.service).toEqual(jasmine.any(Object));
		});

		it('should be mutable characteristic', () => {
			expect(characteristic.mutable).toEqual(jasmine.any(Boolean));
		});

		it('should have valid uuid ', () => {
			expect(characteristic.uuid).toEqual(UUID);
		});
	});
	if (IOS) {
		describe('methods', () => {
			it('should have equal function', () => {
				expect(characteristic.equal(charToCompare)).toEqual(false);
			});
		});
	}
});
