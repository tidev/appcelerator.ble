/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@objc
class TiBLEServiceProxy: TiProxy {
    private var _service: CBService

    init(service: CBService) {
        self._service = service
    }

    @objc
    func characteristics() -> [TiBLECharacteristicProxy] {
        guard let characteristics = _service.characteristics else {
            return []
        }
        var objects = [TiBLECharacteristicProxy]()
        for characteristic in characteristics {
            objects.append(TiBLECharacteristicProxy(characteristic: characteristic))
        }
        return objects
    }

    @objc
    func includedServices() -> [TiBLEServiceProxy] {
        guard let includedServices = _service.includedServices else {
            return []
        }
        var objects = [TiBLEServiceProxy]()
        for service in includedServices {
            objects.append(TiBLEServiceProxy(service: service))
        }
        return objects
    }

    @objc
    func peripheral() -> TiBLEPeripheralProxy {
        return TiBLEPeripheralProxy(peripheral: _service.peripheral)
    }

    @objc
    func isPrimary() -> Bool {
        return _service.isPrimary
    }

    @objc
    func uuid() -> String {
        return _service.uuid.uuidString
    }

    func service() -> CBService {
        return _service
    }

    func mutableService() -> CBMutableService {
        if let service = _service as? CBMutableService {
            return service
        }
        let mutableCopy = CBMutableService(type: _service.uuid, primary: _service.isPrimary)
        mutableCopy.characteristics = _service.characteristics
        return mutableCopy
    }

}
