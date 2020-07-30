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
    @objc public let AUTHORISATION_STATUS_NOT_DETERMINED = 0
    @objc public let AUTHORISATION_STATUS_RESTRICTED = 1
    @objc public let AUTHORISATION_STATUS_DENIED = 2
    @objc public let AUTHORISATION_STATUS_ALLOWED_ALWAYS = 3
    
    @objc public let ATTRIBUTE_PERMISSION_READABLE = CBAttributePermissions.readable.rawValue
    @objc public let ATTRIBUTE_PERMISSION_WRITEABLE = CBAttributePermissions.writeable.rawValue
    @objc public let ATTRIBUTE_PERMISSION_READ_ENCRYPTION_REQUIRED = CBAttributePermissions.readEncryptionRequired.rawValue
    @objc public let ATTRIBUTE_PERMISSION_WRITE_ENCRYPTION_REQUIRED = CBAttributePermissions.writeEncryptionRequired.rawValue
    
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

    


    var _peripheralManager:CBPeripheralManager?
    
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
        };
    }
    
    @objc(addService:)
    func addService(arg: Any?) -> TiBLEServiceProxy? {
        guard let values = arg as? [Any],
            let options = values.first as? [String:Any],
            let primary = options["primary"] as? Bool,
            let uuid = options["uuid"] as? String else {
                return nil;
        }
        let cbUUID = CBUUID(string: uuid)
        
        let service = CBMutableService(type: cbUUID, primary: primary)
        var characteristicArray = [CBCharacteristic]()
        
        if let data = options["data"] as? TiBuffer,
            let properties = options["properties"] as? NSNumber,
            let permission = options["permissions"] as? NSNumber {
            let characteristicData = data.data as Data
            let characteristicPermission:CBAttributePermissions = CBAttributePermissions(rawValue: permission.uintValue)
            let characteristicProperties = CBCharacteristicProperties(rawValue: properties.uintValue)
            let characteristic = CBMutableCharacteristic(type: cbUUID, properties: characteristicProperties, value: characteristicData, permissions: characteristicPermission)
            characteristicArray.append(characteristic)
        }
        if let characteristics = options["characteristics"] as? [TiBLECharacteristicProxy] {
            for object in characteristics {
                characteristicArray.append(object.characteristic())
            }
        }
        
        service.characteristics = characteristicArray
    
        _peripheralManager?.add(service)
        return TiBLEServiceProxy(service: service)
    }
    
    @objc(removeAllServices:)
    func removeAllServices(arg:Any?){
        _peripheralManager?.removeAllServices()
    }
    
    @objc(removeServices:)
    func removeServices(arg: Any?){
        guard let options = arg as? [String:Any],
            let service = options["service"] as? TiBLEServiceProxy else {
            return
        }
        _peripheralManager?.remove(service.mutableService())
    }
    
}

