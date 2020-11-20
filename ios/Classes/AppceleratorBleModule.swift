/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import TitaniumKit
import CoreBluetooth

/**

 Titanium Swift Module Requirements
 ---

 1. Use the @objc annotation to expose your class to Objective-C (used by the Titanium core)
 2. Use the @objc annotation to expose your method to Objective-C as well.
 3. Method arguments always have the "[Any]" type, specifying a various number of arguments.
 Unwrap them like you would do in Swift, e.g. "guard let arguments = arguments, let message = arguments.first"
 4. You can use any public Titanium API like before, e.g. TiUtils. Remember the type safety of Swift, like Int vs Int32
 and NSString vs. String.

 */

@objc(AppceleratorBleModule)
class AppceleratorBleModule: TiModule {
    // MARK: Constants
    @objc public let CONNECTION_EVENT_TYPE_PEER_DISCONNECTED = CBConnectionEvent.peerDisconnected.rawValue
    @objc public let CONNECTION_EVENT_TYPE_PEER_CONNECTED =  CBConnectionEvent.peerConnected.rawValue

    @objc public let MANAGER_STATE_UNKNOWN = CBManagerState.unknown.rawValue
    @objc public let MANAGER_STATE_RESETTING = CBManagerState.resetting.rawValue
    @objc public let MANAGER_STATE_UNSUPPORTED = CBManagerState.unsupported.rawValue
    @objc public let MANAGER_STATE_UNAUTHORIZED = CBManagerState.unauthorized.rawValue
    @objc public let MANAGER_STATE_POWERED_OFF = CBManagerState.poweredOff.rawValue
    @objc public let MANAGER_STATE_POWERED_ON = CBManagerState.poweredOn.rawValue

    @objc public let AUTHORISATION_STATUS_NOT_DETERMINED = 0
    @objc public let AUTHORISATION_STATUS_RESTRICTED = 1
    @objc public let AUTHORISATION_STATUS_DENIED = 2
    @objc public let AUTHORISATION_STATUS_ALLOWED_ALWAYS = 3

    @objc public let CHARACTERISTIC_PERMISSION_READABLE = CBAttributePermissions.readable.rawValue
    @objc public let CHARACTERISTIC_PERMISSION_WRITEABLE = CBAttributePermissions.writeable.rawValue
    @objc public let CHARACTERISTIC_PERMISSION_READ_ENCRYPTED = CBAttributePermissions.readEncryptionRequired.rawValue
    @objc public let CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED = CBAttributePermissions.writeEncryptionRequired.rawValue

    @objc public let CHARACTERISTIC_PROPERTIES_BROADCAST = CBCharacteristicProperties.broadcast.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_READ = CBCharacteristicProperties.read.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE = CBCharacteristicProperties.writeWithoutResponse.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_WRITE = CBCharacteristicProperties.write.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_NOTIFY = CBCharacteristicProperties.notify.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_INDICATE = CBCharacteristicProperties.indicate.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_AUTHENTICATED_SIGNED_WRITES = CBCharacteristicProperties.authenticatedSignedWrites.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES = CBCharacteristicProperties.extendedProperties.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_NOTIFY_ENCRYPTION_REQUIRED = CBCharacteristicProperties.notifyEncryptionRequired.rawValue
    @objc public let CHARACTERISTIC_PROPERTIES_INDICATE_ENCRYPTION_REQUIRED = CBCharacteristicProperties.indicateEncryptionRequired.rawValue
    // Descriptor UUID
    @objc public let CBUUID_CHARACTERISTIC_EXTENDED_PROPERTIES_STRING = CBUUIDCharacteristicExtendedPropertiesString
    @objc public let CBUUID_CHARACTERISTIC_USER_DESCRIPTION_STRING = CBUUIDCharacteristicUserDescriptionString
    @objc public let CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING = CBUUIDClientCharacteristicConfigurationString
    @objc public let CBUUID_SERVER_CHARACTERISTIC_CONFIGURATION_STRING = CBUUIDServerCharacteristicConfigurationString
    @objc public let CBUUID_CHARACTERISTIC_FORMAT_STRING = CBUUIDCharacteristicFormatString
    @objc public let CBUUID_CHARACTERISTIC_AGGREGATE_FORMAT_STRING = CBUUIDCharacteristicAggregateFormatString
    @objc public let CBUUID_L2CAPPSM_CHARACTERISTIC_STRING = "ABDD3056-28FA-441D-A470-55A75A52553A"

    @objc public let PERIPHERAL_STATE_CONNECTED = CBPeripheralState.connected.rawValue
    @objc public let PERIPHERAL_STATE_CONNECTING = CBPeripheralState.connecting.rawValue
    @objc public let PERIPHERAL_STATE_DISCONNECTED = CBPeripheralState.disconnected.rawValue
    @objc public let PERIPHERAL_STATE_DISCONNECTING = CBPeripheralState.disconnecting.rawValue

    @objc public let ADVERT_DATA_KEY_SERVICE_DATA = CBAdvertisementDataServiceDataKey
    @objc public let ADVERT_DATA_KEY_LOCAL_NAME = CBAdvertisementDataLocalNameKey
    @objc public let ADVERT_DATA_KEY_MANUFACTURER_DATA = CBAdvertisementDataManufacturerDataKey
    @objc public let ADVERT_DATA_KEY_SERVICE_UUIDS = CBAdvertisementDataServiceUUIDsKey
    @objc public let ADVERT_DATA_KEY_OVERFLOW_SERVICE_UUIDS = CBAdvertisementDataOverflowServiceUUIDsKey
    @objc public let ADVERT_DATA_KEY_TX_POWER_LEVEL = CBAdvertisementDataTxPowerLevelKey
    @objc public let ADVERT_DATA_KEY_IS_CONNECTABLE = CBAdvertisementDataIsConnectable
    @objc public let ADVERT_DATA_KEY_SOLICITED_SERVICE_UUIDS = CBAdvertisementDataSolicitedServiceUUIDsKey

    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION = CBConnectPeripheralOptionNotifyOnConnectionKey
    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION = CBConnectPeripheralOptionNotifyOnDisconnectionKey
    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_NOTIFICATION = CBConnectPeripheralOptionNotifyOnNotificationKey
    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_START_DELAY = CBConnectPeripheralOptionStartDelayKey
    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_ENABLE_TRANSPORT_BRIDGING = "kCBConnectOptionEnableTransportBridging"
    @objc public let CONNECT_PERIPHERAL_OPTIONS_KEY_REQUIRES_ANCS = "kCBConnectOptionRequiresANCS"

    @objc public let CHARACTERISTIC_TYPE_WRITE_WITH_RESPONSE  = CBCharacteristicWriteType.withResponse.rawValue
    @objc public let CHARACTERISTIC_TYPE_WRITE_WITHOUT_RESPONSE  = CBCharacteristicWriteType.withoutResponse.rawValue

    @objc public let ATT_SUCCESS = CBATTError.success.rawValue
    @objc public let ATT_INVALID_HANDLE_ERROR = CBATTError.invalidHandle.rawValue
    @objc public let ATT_READ_NOT_PERMITTED_ERROR = CBATTError.readNotPermitted.rawValue
    @objc public let ATT_WRITE_NOT_PERMITTED_ERROR = CBATTError.writeNotPermitted.rawValue
    @objc public let ATT_INVALID_PDU_ERROR = CBATTError.invalidPdu.rawValue
    @objc public let ATT_INSUFFICIENT_AUTHENTICATION_ERROR = CBATTError.insufficientAuthentication.rawValue
    @objc public let ATT_REQUEST_NOT_SUPPORTED_ERROR = CBATTError.requestNotSupported.rawValue
    @objc public let ATT_INVALID_OFFSET_ERROR = CBATTError.invalidOffset.rawValue
    @objc public let ATT_INSUFFICIENT_AUTHORIZATION_ERROR = CBATTError.insufficientAuthorization.rawValue
    @objc public let ATT_PREPARE_QUEUE_FULL_ERROR = CBATTError.prepareQueueFull.rawValue
    @objc public let ATT_ATTRIBUTE_NOT_FOUND_ERROR = CBATTError.attributeNotFound.rawValue
    @objc public let ATT_ATTRIBUTE_NOT_LONG_ERROR = CBATTError.attributeNotLong.rawValue
    @objc public let ATT_INSUFFICIENT_ENCRYPTION_KEY_SIZE_ERROR = CBATTError.insufficientEncryptionKeySize.rawValue
    @objc public let ATT_INVALID_ATTRIBUTE_VALUE_LENGTH_ERROR = CBATTError.invalidAttributeValueLength.rawValue
    @objc public let ATT_UNLIKELY_ERROR = CBATTError.unlikelyError.rawValue
    @objc public let ATT_INSUFFICIENT_ENCRYPTION_ERROR = CBATTError.insufficientEncryption.rawValue
    @objc public let ATT_UNSUPPORTED_GROUP_TYPE_ERROR = CBATTError.unsupportedGroupType.rawValue
    @objc public let ATT_INSUFFICIENT_RESOURCES_ERROR = CBATTError.insufficientResources.rawValue

    @objc public let PERIPHERAL_MANAGER_CONNECTION_LATENCY_LOW = CBPeripheralManagerConnectionLatency.low.rawValue
    @objc public let PERIPHERAL_MANAGER_CONNECTION_LATENCY_MEDIUM = CBPeripheralManagerConnectionLatency.medium.rawValue
    @objc public let PERIPHERAL_MANAGER_CONNECTION_LATENCY_HIGH = CBPeripheralManagerConnectionLatency.high.rawValue

    func moduleGUID() -> String {
        return "8d0b486f-27ff-4029-a989-56e4a6755e6f"
    }

    override func moduleId() -> String! {
        return "appcelerator.ble"
    }

    override func startup() {
        super.startup()
        debugPrint("[DEBUG] \(self) loaded")
    }

    @objc
    func authorizationState() -> NSNumber {
        if #available(iOS 13.1, *) {
            return NSNumber(value: CBCentralManager.authorization.rawValue)
        } else {
            return NSNumber(value: AUTHORISATION_STATUS_NOT_DETERMINED)
        }
    }

    @objc(initCentralManager:)
    func initCentralManager(arg: Any?) -> TiBLECentralManagerProxy? {
        let options = (arg as? [[String: Any]])?.first
        let showPowerAlert = options?["showPowerAlert"] as? Bool
        let restoreIdentifier = options?["restoreIdentifier"] as? String
        let centralManager = TiBLECentralManagerProxy(pageContext: self.pageContext, showPowerAlert: showPowerAlert, restoreIdentifier: restoreIdentifier)
        return centralManager
    }

    @objc(initPeripheralManager:)
    func initPeripheralManager(arg: Any?) -> TiBLEPeripheralManagerProxy? {
        let options = (arg as? [[String: Any]])?.first
        let showPowerAlert = options?["showPowerAlert"] as? Bool
        let restoreIdentifier = options?["restoreIdentifier"] as? String
        let peripheralManager = TiBLEPeripheralManagerProxy(pageContext: pageContext, showPowerAlert: showPowerAlert, restoreIdentifier: restoreIdentifier)
        return peripheralManager
    }

    @objc(createDescriptor:)
    func createDescriptor(arg: Any?) -> TiBLEDescriptorProxy? {
        guard let values = arg as? [Any],
              let options = values.first as? [String: Any],
              let value = options["value"],
              let uuid = options["uuid"] as? String else {
            return nil
        }
        var descriptorValue: Any?
        if value is TiBuffer, let data = (value as? TiBuffer)?.data {
            descriptorValue = data
        } else {
            descriptorValue = value
        }
        let cbUUID = CBUUID(string: uuid)
        let mutableDescriptor = CBMutableDescriptor(type: cbUUID, value: descriptorValue)
        return TiBLEDescriptorProxy(pageContext: pageContext, descriptor: mutableDescriptor)
    }

    @objc(createMutableCharacteristic:)
    func createMutableCharacteristic(arg: Any?) -> TiBLEMutableCharacteristicProxy? {
        if let options = (arg as? [[String: Any]])?.first,
           let properties = options["properties"] as? [NSNumber],
           let permission = options["permissions"] as? [NSNumber],
           let uuid = options["uuid"] as? String {
            let cbUUID = CBUUID(string: uuid)
            var characteristicPermission: CBAttributePermissions?
            for value in permission {
                if characteristicPermission == nil {
                    characteristicPermission = CBAttributePermissions(rawValue: value.uintValue)
                    continue
                }
                characteristicPermission?.insert(CBAttributePermissions(rawValue: value.uintValue))
            }
            var characteristicProperties: CBCharacteristicProperties?
            for value in properties {
                if characteristicProperties == nil {
                    characteristicProperties = CBCharacteristicProperties(rawValue: value.uintValue)
                    continue
                }
                characteristicProperties?.insert(CBCharacteristicProperties(rawValue: value.uintValue))
            }
            let data = options["data"] as? TiBuffer
            let characteristicData = data?.data as Data?
            if let characteristicProperties = characteristicProperties,
               let characteristicPermission = characteristicPermission {
                let characteristic = CBMutableCharacteristic(type: cbUUID, properties: characteristicProperties, value: characteristicData, permissions: characteristicPermission)
                var descriptorArray = [CBDescriptor]()
                if let descriptors = options["descriptors"] as? [TiBLEDescriptorProxy] {
                    for descriptor in descriptors {
                        descriptorArray.append(descriptor.descriptor())
                    }
                }
                characteristic.descriptors = descriptorArray
                return TiBLEMutableCharacteristicProxy(pageContext: pageContext, characteristic: characteristic)
            }
        }
        return nil
    }
}
