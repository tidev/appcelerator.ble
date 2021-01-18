const BLE = require('appcelerator.ble');
const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');

describe('appcelerator.ble', function () {
	if (IOS) {

		describe('CONNECTION_EVENT_TYPE_*', () => {
			it('CONNECTION_EVENT_TYPE_PEER_DISCONNECTED', () => {
				expect(BLE.CONNECTION_EVENT_TYPE_PEER_DISCONNECTED).toEqual(jasmine.any(Number));
			});

			it('CONNECTION_EVENT_TYPE_PEER_CONNECTED', () => {
				expect(BLE.CONNECTION_EVENT_TYPE_PEER_CONNECTED).toEqual(jasmine.any(Number));
			});
		});

		describe('MANAGER_STATE_*', () => {
			it('MANAGER_STATE_UNKNOWN', () => {
				expect(BLE.MANAGER_STATE_UNKNOWN).toEqual(jasmine.any(Number));
			});

			it('MANAGER_STATE_RESETTING', () => {
				expect(BLE.MANAGER_STATE_RESETTING).toEqual(jasmine.any(Number));
			});

			it('MANAGER_STATE_UNSUPPORTED', () => {
				expect(BLE.MANAGER_STATE_UNSUPPORTED).toEqual(jasmine.any(Number));
			});

			it('MANAGER_STATE_UNAUTHORIZED', () => {
				expect(BLE.MANAGER_STATE_UNAUTHORIZED).toEqual(jasmine.any(Number));
			});
		});

		describe('AUTHORISATION_STATUS_*', () => {
			it('AUTHORISATION_STATUS_NOT_DETERMINED', () => {
				expect(BLE.AUTHORISATION_STATUS_NOT_DETERMINED).toEqual(jasmine.any(Number));
			});

			it('AUTHORISATION_STATUS_RESTRICTED', () => {
				expect(BLE.AUTHORISATION_STATUS_RESTRICTED).toEqual(jasmine.any(Number));
			});

			it('AUTHORISATION_STATUS_DENIED', () => {
				expect(BLE.AUTHORISATION_STATUS_DENIED).toEqual(jasmine.any(Number));
			});

			it('AUTHORISATION_STATUS_ALLOWED_ALWAYS', () => {
				expect(BLE.AUTHORISATION_STATUS_ALLOWED_ALWAYS).toEqual(jasmine.any(Number));
			});
		});

		describe('CHARACTERISTIC_PROPERTIES_*', function () {
			it('CHARACTERISTIC_PROPERTIES_AUTHENTICATED_SIGNED_WRITES', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_AUTHENTICATED_SIGNED_WRITES).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_NOTIFY_ENCRYPTION_REQUIRED', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_NOTIFY_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});

			it('CHARACTERISTIC_PROPERTIES_INDICATE_ENCRYPTION_REQUIRED', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_INDICATE_ENCRYPTION_REQUIRED).toEqual(jasmine.any(Number));
			});
		});

		describe('CBUUID_*', function () {
			it('CBUUID_CHARACTERISTIC_EXTENDED_PROPERTIES_STRING', () => {
				expect(BLE.CBUUID_CHARACTERISTIC_EXTENDED_PROPERTIES_STRING).toEqual('2900');
			});

			it('CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING', () => {
				expect(BLE.CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING).toEqual('2901');
			});

			it('CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING', () => {
				expect(BLE.CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING).toEqual('2902');
			});

			it('CBUUID_SERVER_CHARACTERISTIC_CONFIGURATION_STRING', () => {
				expect(BLE.CBUUID_SERVER_CHARACTERISTIC_CONFIGURATION_STRING).toEqual('2903');
			});

			it('CBUUID_CHARACTERISTIC_FORMAT_STRING', () => {
				expect(BLE.CBUUID_CHARACTERISTIC_FORMAT_STRING).toEqual('2904');
			});

			it('CBUUID_CHARACTERISTIC_AGGREGATE_FORMAT_STRING', () => {
				expect(BLE.CBUUID_CHARACTERISTIC_AGGREGATE_FORMAT_STRING).toEqual('2905');
			});

			it('CBUUID_L2CAPPSM_CHARACTERISTIC_STRING', () => {
				expect(BLE.CBUUID_L2CAPPSM_CHARACTERISTIC_STRING).toEqual('ABDD3056-28FA-441D-A470-55A75A52553A');
			});
		});

		describe('PERIPHERAL_STATE_*', () => {
			it('PERIPHERAL_STATE_CONNECTED', () => {
				expect(BLE.PERIPHERAL_STATE_CONNECTED).toEqual(jasmine.any(Number));
			});

			it('PERIPHERAL_STATE_CONNECTING', () => {
				expect(BLE.PERIPHERAL_STATE_CONNECTING).toEqual(jasmine.any(Number));
			});

			it('PERIPHERAL_STATE_DISCONNECTED', () => {
				expect(BLE.PERIPHERAL_STATE_DISCONNECTED).toEqual(jasmine.any(Number));
			});

			it('PERIPHERAL_STATE_DISCONNECTING', () => {
				expect(BLE.PERIPHERAL_STATE_DISCONNECTING).toEqual(jasmine.any(Number));
			});
		});
		describe('ADVERT_DATA_KEY_*', () => {
			it('ADVERT_DATA_KEY_SERVICE_DATA', () => {
				expect(BLE.ADVERT_DATA_KEY_SERVICE_DATA).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_LOCAL_NAME', () => {
				expect(BLE.ADVERT_DATA_KEY_LOCAL_NAME).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_MANUFACTURER_DATA', () => {
				expect(BLE.ADVERT_DATA_KEY_MANUFACTURER_DATA).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_SERVICE_UUIDS', () => {
				expect(BLE.ADVERT_DATA_KEY_SERVICE_UUIDS).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_OVERFLOW_SERVICE_UUIDS', () => {
				expect(BLE.ADVERT_DATA_KEY_OVERFLOW_SERVICE_UUIDS).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_TX_POWER_LEVEL', () => {
				expect(BLE.ADVERT_DATA_KEY_TX_POWER_LEVEL).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_IS_CONNECTABLE', () => {
				expect(BLE.ADVERT_DATA_KEY_IS_CONNECTABLE).toEqual(jasmine.any(String));
			});

			it('ADVERT_DATA_KEY_SOLICITED_SERVICE_UUIDS', () => {
				expect(BLE.ADVERT_DATA_KEY_SOLICITED_SERVICE_UUIDS).toEqual(jasmine.any(String));
			});
		});
		describe('CONNECT_PERIPHERAL_OPTIONS_KEY_*', () => {
			it('CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION).toEqual(jasmine.any(String));
			});

			it('CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION).toEqual(jasmine.any(String));
			});

			it('CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_NOTIFICATION', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_NOTIFICATION).toEqual(jasmine.any(String));
			});

			it('CONNECT_PERIPHERAL_OPTIONS_KEY_START_DELAY', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_START_DELAY).toEqual(jasmine.any(String));
			});

			it('CONNECT_PERIPHERAL_OPTIONS_KEY_ENABLE_TRANSPORT_BRIDGING', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_ENABLE_TRANSPORT_BRIDGING).toEqual(jasmine.any(String));
			});

			it('CONNECT_PERIPHERAL_OPTIONS_KEY_REQUIRES_ANCS', () => {
				expect(BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_REQUIRES_ANCS).toEqual(jasmine.any(String));
			});
		});

		describe('ATT_*', () => {
			it('ATT_SUCCESS', () => {
				expect(BLE.ATT_SUCCESS).toEqual(jasmine.any(Number));
			});

			it('ATT_INVALID_HANDLE_ERROR', () => {
				expect(BLE.ATT_INVALID_HANDLE_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_READ_NOT_PERMITTED_ERROR', () => {
				expect(BLE.ATT_READ_NOT_PERMITTED_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_WRITE_NOT_PERMITTED_ERROR', () => {
				expect(BLE.ATT_WRITE_NOT_PERMITTED_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INVALID_PDU_ERROR', () => {
				expect(BLE.ATT_INVALID_PDU_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INSUFFICIENT_AUTHENTICATION_ERROR', () => {
				expect(BLE.ATT_INSUFFICIENT_AUTHENTICATION_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_REQUEST_NOT_SUPPORTED_ERROR', () => {
				expect(BLE.ATT_REQUEST_NOT_SUPPORTED_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INVALID_OFFSET_ERROR', () => {
				expect(BLE.ATT_INVALID_OFFSET_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INSUFFICIENT_AUTHORIZATION_ERROR', () => {
				expect(BLE.ATT_INSUFFICIENT_AUTHORIZATION_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_PREPARE_QUEUE_FULL_ERROR', () => {
				expect(BLE.ATT_PREPARE_QUEUE_FULL_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_ATTRIBUTE_NOT_FOUND_ERROR', () => {
				expect(BLE.ATT_ATTRIBUTE_NOT_FOUND_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_ATTRIBUTE_NOT_LONG_ERROR', () => {
				expect(BLE.ATT_ATTRIBUTE_NOT_LONG_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INSUFFICIENT_ENCRYPTION_KEY_SIZE_ERROR', () => {
				expect(BLE.ATT_INSUFFICIENT_ENCRYPTION_KEY_SIZE_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INVALID_ATTRIBUTE_VALUE_LENGTH_ERROR', () => {
				expect(BLE.ATT_INVALID_ATTRIBUTE_VALUE_LENGTH_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_UNLIKELY_ERROR', () => {
				expect(BLE.ATT_UNLIKELY_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INSUFFICIENT_ENCRYPTION_ERROR', () => {
				expect(BLE.ATT_INSUFFICIENT_ENCRYPTION_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_UNSUPPORTED_GROUP_TYPE_ERROR', () => {
				expect(BLE.ATT_UNSUPPORTED_GROUP_TYPE_ERROR).toEqual(jasmine.any(Number));
			});

			it('ATT_INSUFFICIENT_RESOURCES_ERROR', () => {
				expect(BLE.ATT_INSUFFICIENT_RESOURCES_ERROR).toEqual(jasmine.any(Number));
			});
		});

		describe('PERIPHERAL_MANAGER_CONNECTION_LATENCY_*', () => {
			it('PERIPHERAL_MANAGER_CONNECTION_LATENCY_LOW', () => {
				expect(BLE.PERIPHERAL_MANAGER_CONNECTION_LATENCY_LOW).toEqual(jasmine.any(Number));
			});

			it('PERIPHERAL_MANAGER_CONNECTION_LATENCY_MEDIUM', () => {
				expect(BLE.PERIPHERAL_MANAGER_CONNECTION_LATENCY_MEDIUM).toEqual(jasmine.any(Number));
			});

			it('PERIPHERAL_MANAGER_CONNECTION_LATENCY_HIGH', () => {
				expect(BLE.PERIPHERAL_MANAGER_CONNECTION_LATENCY_HIGH).toEqual(jasmine.any(Number));
			});
		});

		describe('LOCATION_MANAGER_AUTHORIZATION_STATUS_*', () => {
			it('LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_ALWAYS', () => {
				expect(BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_ALWAYS).toEqual(jasmine.any(Number));
			});

			it('LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_WHEN_IN_USE', () => {
				expect(BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_WHEN_IN_USE).toEqual(jasmine.any(Number));
			});

			it('LOCATION_MANAGER_AUTHORIZATION_STATUS_DENIED', () => {
				expect(BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_DENIED).toEqual(jasmine.any(Number));
			});

			it('LOCATION_MANAGER_AUTHORIZATION_STATUS_NOT_DETERMINED', () => {
				expect(BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_NOT_DETERMINED).toEqual(jasmine.any(Number));
			});

			it('LOCATION_MANAGER_AUTHORIZATION_STATUS_RESTRICTED', () => {
				expect(BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_RESTRICTED).toEqual(jasmine.any(Number));
			});
		});

		describe('REGION_STATE_*', () => {
			it('REGION_STATE_UNKNOWN', () => {
				expect(BLE.REGION_STATE_UNKNOWN).toEqual(jasmine.any(Number));
			});

			it('REGION_STATE_INSIDE', () => {
				expect(BLE.REGION_STATE_INSIDE).toEqual(jasmine.any(Number));
			});

			it('REGION_STATE_OUTSIDE', () => {
				expect(BLE.REGION_STATE_OUTSIDE).toEqual(jasmine.any(Number));
			});
		});

		describe('BEACON_PROXIMITY_*', () => {
			it('BEACON_PROXIMITY_UNKNOWN', () => {
				expect(BLE.BEACON_PROXIMITY_UNKNOWN).toEqual(jasmine.any(Number));
			});

			it('BEACON_PROXIMITY_IMMEDIATE', () => {
				expect(BLE.BEACON_PROXIMITY_IMMEDIATE).toEqual(jasmine.any(Number));
			});

			it('BEACON_PROXIMITY_NEAR', () => {
				expect(BLE.BEACON_PROXIMITY_NEAR).toEqual(jasmine.any(Number));
			});

			it('BEACON_PROXIMITY_FAR', () => {
				expect(BLE.BEACON_PROXIMITY_FAR).toEqual(jasmine.any(Number));
			});
		});
	}

	describe('CHARACTERISTIC_PERMISSION_*', function () {
		it('CHARACTERISTIC_PERMISSION_READABLE', () => {
			expect(BLE.CHARACTERISTIC_PERMISSION_READABLE).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PERMISSION_WRITEABLE', () => {
			expect(BLE.CHARACTERISTIC_PERMISSION_WRITEABLE).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PERMISSION_READ_ENCRYPTED', () => {
			expect(BLE.CHARACTERISTIC_PERMISSION_READ_ENCRYPTED).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED', () => {
			expect(BLE.CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED).toEqual(jasmine.any(Number));
		});
	});

	describe('CHARACTERISTIC_PROPERTIES_* are constants', function () {
		it('CHARACTERISTIC_PROPERTIES_BROADCAST', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_BROADCAST).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_READ', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_READ).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_WRITE', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_WRITE).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_NOTIFY', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_NOTIFY).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_INDICATE', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_INDICATE).toEqual(jasmine.any(Number));
		});

		it('CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES', () => {
			expect(BLE.CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES).toEqual(jasmine.any(Number));
		});

		if (!IOS) {
			it('CHARACTERISTIC_PROPERTIES_SIGNED_WRITES', () => {
				expect(BLE.CHARACTERISTIC_PROPERTIES_SIGNED_WRITES).toEqual(jasmine.any(Number));
			});

			describe('CHARACTERISTIC_TYPE_WRITE_*', () => {
				it('CHARACTERISTIC_TYPE_WRITE_WITH_RESPONSE', () => {
					expect(BLE.CHARACTERISTIC_TYPE_WRITE_WITH_RESPONSE).toEqual(jasmine.any(Number));
				});

				it('CHARACTERISTIC_TYPE_WRITE_WITHOUT_RESPONSE', () => {
					expect(BLE.CHARACTERISTIC_TYPE_WRITE_WITHOUT_RESPONSE).toEqual(jasmine.any(Number));
				});
			});
		}
	});

	describe('MANAGER_STATE_* are constants', () => {
		it('MANAGER_STATE_POWERED_OFF', () => {
			expect(BLE.MANAGER_STATE_POWERED_OFF).toEqual(jasmine.any(Number));
		});

		it('MANAGER_STATE_POWERED_ON', () => {
			expect(BLE.MANAGER_STATE_POWERED_ON).toEqual(jasmine.any(Number));
		});

		if (!IOS) {
			it('MANAGER_STATE_TURNING_OFF', () => {
				expect(BLE.MANAGER_STATE_TURNING_OFF).toEqual(jasmine.any(Number));
			});

			it('MANAGER_STATE_TURNING_ON', () => {
				expect(BLE.MANAGER_STATE_TURNING_ON).toEqual(jasmine.any(Number));
			});
		}
	});
});

