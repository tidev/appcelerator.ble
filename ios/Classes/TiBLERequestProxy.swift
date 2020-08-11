/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

class TiBLERequestProxy: TiProxy {
    private var _request: CBATTRequest
    init(request: CBATTRequest) {
        self._request = request
    }
    @objc
    func central() -> TiBLECentralProxy {
        return TiBLECentralProxy(central: _request.central)
    }
    @objc
    func characteristic() -> TiBLECharacteristicProxy {
        return TiBLECharacteristicProxy(characteristic: _request.characteristic) as TiBLECharacteristicProxy
    }
    @objc
    func offset() -> NSNumber {
        return NSNumber(value: _request.offset)
    }
    @objc
    func value() -> TiBuffer? {
        guard let requestValue = _request.value else {
            return nil
        }
        let buffer = TiBuffer()._init(withPageContext: self.pageContext)
        buffer?.data = NSMutableData.init(data: requestValue)
        return buffer
    }
    func request() -> CBATTRequest {
        return _request
    }
}
