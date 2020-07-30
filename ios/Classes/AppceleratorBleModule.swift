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
    
    @objc
    func authorizationState() -> NSNumber {
        if #available(iOS 13.1, *) {
            return NSNumber(value: CBCentralManager.authorization.rawValue)
        } else {
            return NSNumber(value: AUTHORISATION_STATUS_NOT_DETERMINED)
        };
    }
    
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
}
