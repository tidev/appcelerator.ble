/**
* Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

import Foundation
import TitaniumKit
import CoreBluetooth

@objc
public class TiBLEPeripheralProviderProxy: NSObject {
    static let sharedPeripheralProvider = TiBLEPeripheralProviderProxy()
    private var _peripherals = [String: TiBLEPeripheralProxy]()
    
    private override init() {
        super.init()
    }
    
    @objc
    func peripherals() -> [TiBLEPeripheralProxy] {
        var list = [TiBLEPeripheralProxy]()
        _peripherals.forEach { (_, value) in
            list.append(value)
        }
        return list
    }
    
    func hasPeripheral(_ peripheral: TiBLEPeripheralProxy?) -> Bool {
        if let peripheral = peripheral {
            return peripherals().contains(peripheral)
        }
        return false
    }
    
    func addPeripheral(_ peripheral: TiBLEPeripheralProxy?) {
        if let peripheral = peripheral {
            _peripherals[peripheral.UUID()] = peripheral
        }
    }
    
    func removePeripheral(_ peripheral: TiBLEPeripheralProxy?) {
        if let peripheral = peripheral {
            if !hasPeripheral(peripheral) {
                print("[ERROR] Trying to remove a peripheral with UUID = \(peripheral.UUID()) that doesn't exist in the provider.")
                return
            }
            _peripherals.removeValue(forKey: peripheral.UUID())
        }
    }
    
    func removeAllPeripheral () {
        _peripherals.removeAll()
    }
    
    func peripheralProxy(from peripheral: CBPeripheral?) -> TiBLEPeripheralProxy? {
        var result: TiBLEPeripheralProxy? = nil
        if let peripheral = peripheral {
            if let availablePPeripheral = _peripherals[peripheral.identifier.uuidString] {
                result = availablePPeripheral
            }
        }
        return result
    }
    
    func checkAddAndGetPeripheralProxy(from peripheral: CBPeripheral, and pageContext: TiEvaluator) -> TiBLEPeripheralProxy? {
        var result = peripheralProxy(from: peripheral)

        if result == nil {
            print("[DEBUG] Could not find cached instance of peripheral proxy. Adding and returning it now.")

            result = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
            addPeripheral(result)
        }

        return result
    }
}

