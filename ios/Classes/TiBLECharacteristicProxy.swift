/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
import Foundation
import TitaniumKit
import CoreBluetooth

class TiBLECharacteristicProxy: TiProxy {
    private var _characteristic: CBCharacteristic

    init(characteristic: CBCharacteristic) {
        _characteristic = characteristic
    }

    func characteristic() -> CBCharacteristic {
        return _characteristic
    }

}
