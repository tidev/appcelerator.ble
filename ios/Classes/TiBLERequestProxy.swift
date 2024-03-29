/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

class TiBLERequestProxy: TiProxy {
    private var _request: CBATTRequest!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, request: CBATTRequest) {
        self.init()
        _init(withPageContext: pageContext)
        _request = request
    }

    @objc
    func central() -> TiBLECentralProxy {
        return TiBLECentralProxy(pageContext: self.pageContext, central: _request.central)
    }

    @objc
    func characteristic() -> TiBLECharacteristicProxy {
        return TiBLECharacteristicProxy(pageContext: self.pageContext, characteristic: _request.characteristic) as TiBLECharacteristicProxy
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
        return TiBLEUtils.toTiBuffer(from: requestValue)._init(withPageContext: pageContext)
    }

    @objc(updateValue:)
    func updateValue(arg: Any?) {
        guard let options = (arg as? [[String: Any]])?.first,
              let value = options["value"] as? TiBuffer else {
            return
        }
        _request.value = value.data as Data?
    }

    func request() -> CBATTRequest {
        return _request
    }
}
