const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
if (IOS) {
	const manager = BLE.createRegionManager();

	describe('appcelerator.ble.RegionManager', function () {
		it('should be defined', () => {
			expect(manager).toBeDefined();
		});

		describe('properties', () => {
			it('should have locationManagerAuthorizationStatus', () => {
				expect(manager.locationManagerAuthorizationStatus).toEqual(jasmine.any(Number));
			});

			it('should have maximumRegionMonitoringDistance', () => {
				expect(manager.maximumRegionMonitoringDistance).toEqual(jasmine.any(Number));
			});

			it('should have monitoredRegions', () => {
				expect(manager.monitoredRegions).toEqual(jasmine.any(Object));
			});

			it('should have rangedRegions', () => {
				expect(manager.rangedRegions).toEqual(jasmine.any(Object));
			});

			it('should have rangedBeaconConstraints', () => {
				expect(manager.rangedBeaconConstraints).toEqual(jasmine.any(Object));
			});

			it('should have locationServicesEnabled', () => {
				expect(manager.locationServicesEnabled).toEqual(jasmine.any(Boolean));
			});

			it('should have isRangingAvailable', () => {
				expect(manager.isRangingAvailable).toEqual(jasmine.any(Boolean));
			});

			it('should have isMonitoringAvailable', () => {
				expect(manager.isMonitoringAvailable).toEqual(jasmine.any(Boolean));
			});
		});

		describe('methods', () => {
			it('should have requestAlwaysAuthorization function', () => {
				expect(manager.requestAlwaysAuthorization).toEqual(jasmine.any(Function));
			});

			it('should have requestRegionState function', () => {
				expect(manager.requestRegionState).toEqual(jasmine.any(Function));
			});

			it('should have requestWhenInUseAuthorization function', () => {
				expect(manager.requestWhenInUseAuthorization).toEqual(jasmine.any(Function));
			});

			it('should have startRangingBeaconsInRegion function', () => {
				expect(manager.startRangingBeaconsInRegion).toEqual(jasmine.any(Function));
			});

			it('should have startRangingBeaconsSatisfyingIdentityConstraint function', () => {
				expect(manager.startRangingBeaconsSatisfyingIdentityConstraint).toEqual(jasmine.any(Function));
			});

			it('should have stopRangingBeaconsInRegion function', () => {
				expect(manager.stopRangingBeaconsInRegion).toEqual(jasmine.any(Function));
			});

			it('should have stopRangingBeaconsSatisfyingIdentityConstraint function', () => {
				expect(manager.stopRangingBeaconsSatisfyingIdentityConstraint).toEqual(jasmine.any(Function));
			});

			it('should have startRegionMonitoring function', () => {
				expect(manager.startRegionMonitoring).toEqual(jasmine.any(Function));
			});

			it('should have stopRegionMonitoring function', () => {
				expect(manager.stopRegionMonitoring).toEqual(jasmine.any(Function));
			});

		});
	});
}
