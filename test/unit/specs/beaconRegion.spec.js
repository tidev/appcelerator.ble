const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const beaconUUID = '135C8F13-6A2D-46ED-AA71-FB956FC23742';
	const major = 100;
	const minor = 0;
	const beaconId = 'com.appcelerator.BluetoothLowEnergy.beacon';
	const beaconRegion = BLE.createBeaconRegion({
		uuid: beaconUUID,
		major: major,
		minor: minor,
		identifier: beaconId
	});
	beaconRegion.notifyEntryStateOnDisplay = true;

	describe('appcelerator.ble.beaconRegion', function () {
		describe('.beaconRegion', () => {
			it('should be defined', () => {
				expect(beaconRegion).toBeDefined();
			});

			it('should have valid identifier object', () => {
				expect(beaconRegion.identifier).toEqual(beaconId);
			});

			it('should have valid major', () => {
				expect(beaconRegion.major).toEqual(major);
			});

			it('should have valid minor', () => {
				expect(beaconRegion.minor).toEqual(minor);
			});

			it('should have valid notifyEntryStateOnDisplay', () => {
				expect(beaconRegion.notifyEntryStateOnDisplay).toEqual(true);
			});

			it('should have valid uuid', () => {
				expect(beaconRegion.uuid).toEqual(beaconUUID);
			});
		});
	});

}
