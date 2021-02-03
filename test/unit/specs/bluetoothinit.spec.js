let BLE;
let peripheralManager;
const isAndroid = Ti.Platform.osname === 'android';

if (isAndroid) {
	describe('Bluetooth init ', function () {
		var originalTimeout;
		beforeAll(function (done) {
			originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
			jasmine.DEFAULT_TIMEOUT_INTERVAL = 15 * 1000;

			BLE = require('appcelerator.ble');
			peripheralManager = BLE.initPeripheralManager();
			if (BLE.isEnabled()) {
				done();
			} else {
				peripheralManager.addEventListener('didUpdateState', function (e) {
					if (e.state === BLE.MANAGER_STATE_POWERED_ON) {
						done();
					}
				});
				BLE.enable();
			}
		});

		it('bluetooth is enabled', () => {
			expect(BLE.isEnabled()).toBeTrue();
		});

		afterAll(function () {
			jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
		});
	});
}
