/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
import Foundation
import TitaniumKit
import CoreBluetooth

@objc
class TiBLECharacteristicProxy: TiProxy {
    private var _characteristic: CBCharacteristic!

    override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, characteristic: CBCharacteristic) {
        self.init()
        _init(withPageContext: pageContext)
        setCharacteristic(characteristic: characteristic)
    }

    func setCharacteristic(characteristic: CBCharacteristic) {
        _characteristic = characteristic
    }

    @objc
    func service() -> TiBLEServiceProxy {
        return TiBLEServiceProxy(pageContext: self.pageContext, service: _characteristic.service)
    }
    @objc
    func properties() -> NSNumber {
        return NSNumber(value: _characteristic.properties.rawValue)
    }
    @objc
    func descriptors() -> [TiBLEDescriptorProxy] {
        guard let descriptors = _characteristic.descriptors else {
            return []
        }
        var objects = [TiBLEDescriptorProxy]()
        for descriptor in descriptors {
            objects.append(TiBLEDescriptorProxy(pageContext: pageContext, descriptor: descriptor))
        }
        return objects
    }
    @objc
    func value() -> TiBuffer? {
        guard let requestValue = _characteristic.value else {
            return nil
        }
        return TiBLEUtils.toTiBuffer(from: requestValue)._init(withPageContext: pageContext)
    }
    @objc
    func uuid() -> String {
        return _characteristic.uuid.uuidString
    }
    func characteristic() -> CBCharacteristic {
        return _characteristic
    }

    @objc
    func isNotifying() -> NSNumber {
        return NSNumber(value: _characteristic.isNotifying)
    }

}
