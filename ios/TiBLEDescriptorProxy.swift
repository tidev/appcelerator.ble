/**
* Appcelerator Titanium Mobile - Bluetooth Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

import Foundation
import TitaniumKit

@objc class TiBLEDescriptorProxy : TiProxy {
    
   @objc var characterstic : TiBLECharacteristicProxy?
   @objc var value: NSNumber?
   @objc var UUID: String?
    
    //MARK: Initialization
    init?(characterstic: TiBLECharacteristicProxy!, value: NSNumber, UUID: String) {
        
        // The UUID must not be empty
        guard !UUID.isEmpty else {
            return nil
        }
        
        // Initialize stored properties.
        self.characterstic = characterstic
        self.value = value
        self.UUID = UUID
    }
}
