const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');

var buffer = Ti.createBuffer({ value: 'hello world' });

describe('appcelerator.service', function () {
	var service;
	const serviceInfo = {
		data: buffer,
		properties: 0,
		characteristics: [],
		permission: 0,
		primary: true,
		uuid: '0C50D390-DC8E-436B-8AD0-A36D1B304B18'
	};

	if (IOS) {
		const peripheralManager = BLE.initPeripheralManager();
		service = peripheralManager.addService(serviceInfo);
	} else {
		// service object creation for android.
		service = BLE.mockServiceForUT(serviceInfo);
	}

	describe('property', function () {
		it('should be defined', () => {
			expect(service).toBeDefined();
		});

		it('should have valid primary value', () => {
			expect(service.isPrimary).toEqual(true);
		});

		it('should have valid includedServices value', () => {
			expect(service.includedServices).toEqual(jasmine.any(Object));
		});

		it('should have valid uuid value', () => {
			expect(service.uuid.toLowerCase()).toEqual('0C50D390-DC8E-436B-8AD0-A36D1B304B18'.toLowerCase());
		});

		it('should have valid characteristics value', () => {
			expect(service.characteristics).toEqual(jasmine.any(Object));
		});
	});

});
