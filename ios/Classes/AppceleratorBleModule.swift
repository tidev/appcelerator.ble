/**
* Appcelerator Titanium Mobile - Bluetooth Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

import UIKit
import TitaniumKit

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
    @objc(createCentral:)
    func createCentral(args:Any?) -> TiBLECentralProxy {
        let central = TiBLECentralProxy(maximumUpdateValueLength: 123, UUID: "some")
        return central!
    }
}
