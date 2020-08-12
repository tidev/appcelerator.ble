/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

class TiBLECentralProxy: TiProxy {
    private var _central: CBCentral
    init(central: CBCentral) {
        self._central = central
    }
    @objc
    func maximumUpdateValueLength() -> NSNumber {
        return NSNumber(value: _central.maximumUpdateValueLength)
    }
    @objc
    func UUID() -> String {
        return _central.identifier.uuidString
    }
    func central() -> CBCentral {
        return _central
    }
}
