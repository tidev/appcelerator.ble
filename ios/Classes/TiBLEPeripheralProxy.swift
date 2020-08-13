/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@objc
public class TiBLEPeripheralProxy: TiProxy {
    private var _peripheral: CBPeripheral!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, peripheral: CBPeripheral) {
        self.init()
        _init(withPageContext: pageContext)
        _peripheral = peripheral
    }

    @objc
    func isConnected() -> Bool {
        return _peripheral.state == CBPeripheralState.connected
    }

    @objc
    func name() -> String? {
        return _peripheral.name
    }

    @objc
    func services() -> [TiBLEServiceProxy] {
        guard let services = _peripheral.services else {
            return []
        }
        var list = [TiBLEServiceProxy]()
        services.forEach { (service) in
            list.append(TiBLEServiceProxy(pageContext: self.pageContext, service: service))
        }
        return list
    }

    @objc
    func state() -> NSNumber {
        return NSNumber(value: _peripheral.state.rawValue)
    }

    @objc
    func UUID() -> String {
        return _peripheral.identifier.uuidString
    }

    @objc(readRSSI:)
    func readRSSI(arg: Any?) {
        _peripheral.readRSSI()
    }

}
