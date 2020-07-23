/**
* Appcelerator Titanium Mobile - Bluetooth Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

import Foundation
import TitaniumKit

@objc class TiBLECentralProxy: TiProxy {
   @objc var maximumUpdateValueLength: NSNumber?
   @objc var UUID: String?
    //MARK: Initialization
    init?(maximumUpdateValueLength: NSNumber, UUID: String) {
        // The UUID and maximumUpdateValueLength must not be empty
        guard (!UUID.isEmpty) else {
            return nil
        }
        // Initialize stored properties.
        self.maximumUpdateValueLength = maximumUpdateValueLength
        self.UUID = UUID
    }
}
