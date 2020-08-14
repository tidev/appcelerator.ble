/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@objc
class TiBLEDescriptorProxy: TiProxy {
    private var _descriptor: CBDescriptor

    init(descriptor: CBDescriptor) {
        self._descriptor = descriptor
    }
    @objc
    func characteristic() -> TiBLECharacteristicProxy {
        let characteristic = _descriptor.characteristic
        return TiBLECharacteristicProxy(characteristic: characteristic)
    }
    @objc
    func value() -> Any? {
        if _descriptor.value is String || _descriptor.value is NSNumber {
            return _descriptor.value
        }
        if let value = _descriptor.value as? Data {
            let buffer = TiBuffer()._init(withPageContext: self.pageContext)
            buffer?.data = NSMutableData.init(data: value)
        }
        return nil
    }
    @objc
    func uuid() -> String {
        return _descriptor.uuid.uuidString
    }
    func descriptor() -> CBDescriptor {
        return _descriptor
    }
}
