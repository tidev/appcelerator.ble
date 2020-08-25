/**
* Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

import Foundation
import TitaniumKit
import CoreBluetooth

class TiBLEUtils {
    static func toTiBuffer(from data:Data) -> TiBuffer {
        let buffer = TiBuffer()
        buffer.data = NSMutableData(data: data)
        return buffer;
    }
    
    static func toStringUUIDs(from uuids:[CBUUID]?) ->[String] {
        guard let uuids = uuids else {
            return []
        }
        var serviceUUIDs = [String]()
        uuids.forEach { (value) in
            serviceUUIDs.append(value.uuidString)
        }
        return serviceUUIDs;
    }
    
    static func toUUIDs(from uuids:[String]) ->[UUID] {
        var ids = [UUID]()
        uuids.forEach { (value) in
            if let id = UUID(uuidString: value) {
                ids.append(id)
            }
        }
        return ids;
    }
    
    static func toCBUUIDs(from uuids:[String]?) ->[CBUUID] {
        guard let uuids = uuids else {
            return []
        }
        var ids = [CBUUID]()
        uuids.forEach { (value) in
            ids.append(CBUUID(string: value))
        }
        return ids;
    }
}
