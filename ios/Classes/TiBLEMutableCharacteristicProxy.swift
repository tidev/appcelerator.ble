/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import TitaniumKit
import CoreBluetooth

@objc
class TiBLEMutableCharacteristicProxy: TiBLECharacteristicProxy {

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, characteristic: CBMutableCharacteristic) {
        self.init()
        _init(withPageContext: pageContext)
        setCharacteristic(characteristic: characteristic)
    }

    @objc
    func subscribedCentrals() -> [TiBLECentralProxy] {
        guard let _mutableCharacteristic = characteristic() as? CBMutableCharacteristic,
            let centrals = _mutableCharacteristic.subscribedCentrals else {
                return []
        }
        var objects = [TiBLECentralProxy]()
        for central in centrals {
            objects.append(TiBLECentralProxy(pageContext: pageContext, central: central))
        }
        return objects
    }

    @objc
    func isNotifying() -> NSNumber? {
        guard let _mutableCharacteristic = characteristic() as? CBMutableCharacteristic else {
            return nil
        }
        return NSNumber(value: _mutableCharacteristic.isNotifying)
    }
    @objc
    func permissions() -> NSNumber? {
        guard let _mutableCharacteristic = characteristic() as? CBMutableCharacteristic else {
            return nil
        }
        return NSNumber(value: _mutableCharacteristic.permissions.rawValue)
    }
    func mutableCharacteristic() -> CBMutableCharacteristic? {
        guard let _mutableCharacteristic = characteristic() as? CBMutableCharacteristic else {
            return nil
        }
        return _mutableCharacteristic
    }
}
