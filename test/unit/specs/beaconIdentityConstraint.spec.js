const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const beaconUUID = '135C8F13-6A2D-46ED-AA71-FB956FC23742';
	const major = 100;
	const minor = 0;
	const beaconIdentityConstraint = BLE.createBeaconIdentityConstraint({
		uuid: beaconUUID,
		major: major,
		minor: minor
	});

	describe('appcelerator.ble.BeaconIdentityConstraint', function () {
		describe('.BeaconIdentityConstraint', () => {
			it('should be defined', () => {
				expect(beaconIdentityConstraint).toBeDefined();
			});

			it('should have valid major value', () => {
				expect(beaconIdentityConstraint.major).toEqual(major);
			});

			it('should have valid minor value', () => {
				expect(beaconIdentityConstraint.minor).toEqual(minor);
			});

			it('should have valid uuid', () => {
				expect(beaconIdentityConstraint.uuid).toEqual(beaconUUID);
			});
		});
	});
}
