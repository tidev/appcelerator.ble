//
//  TiBLECentralManager.swift
//  AppceleratorBle
//
//  Created by Vikas Goyal on 06/08/20.
//

import Foundation
import CoreBluetooth
import TitaniumKit

@objc
class TiBLECentralManagerProxy: TiProxy, CBCentralManagerDelegate {

    private var _peripherals = [String: TiBLEPeripheralProxy]()

    private var _centralManager: CBCentralManager!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, showPowerAlert: Bool?, restoreIdentifier: String?) {
        self.init()
        _init(withPageContext: pageContext)
        var options = [String: Any]()

        if let showPowerAlert = showPowerAlert {
            options[CBCentralManagerOptionShowPowerAlertKey] = showPowerAlert
        }

        if let restoreIdentifier = restoreIdentifier {
            options[CBCentralManagerOptionRestoreIdentifierKey] = restoreIdentifier
        }
        _centralManager = CBCentralManager(delegate: self, queue: nil, options: options.isEmpty ? nil : options)
    }

    @objc
    func state() -> NSNumber {
        return NSNumber(value: _centralManager.state.rawValue)
    }

    @objc
    func isScanning() -> NSNumber {
        if #available(iOS 9.0, *) {
            return NSNumber(value: _centralManager.isScanning)
        } else {
            return NSNumber(value: 0)
        }
    }

    @objc
    func peripherals() -> [TiBLEPeripheralProxy] {
        var list = [TiBLEPeripheralProxy]()
        _peripherals.forEach { (_, value) in
            list.append(value)
        }
        return list
    }

    @objc
    func startScan(arg: Any?) {
        let args = arg as? [String: Any]
        let services = args?["services"] as? [String]
        let options = args?["options"] as? [String: Any]

        var servicesUUID = [CBUUID]()
        if let services = services {
            services.forEach { (value) in
                servicesUUID.append(CBUUID(string: value))
            }
        }
        var scanOptions = [String: Any]()
        if let options = options {
            if let allowDuplicatesKey = options["allowDuplicatesKey"] as? Bool {
                scanOptions[CBCentralManagerScanOptionAllowDuplicatesKey] = allowDuplicatesKey
            }
            if let solicitedServiceUUIDsKey = options["solicitedServiceUUIDsKey"] as? [String] {
                var solicitedServiceUUIDs = [CBUUID]()
                solicitedServiceUUIDsKey.forEach { (value) in
                    solicitedServiceUUIDs.append(CBUUID(string: value))
                }
                scanOptions[CBCentralManagerScanOptionSolicitedServiceUUIDsKey] = solicitedServiceUUIDs
            }
        }

        _centralManager.scanForPeripherals(withServices: servicesUUID.isEmpty ? nil : servicesUUID, options: scanOptions.isEmpty ? nil : scanOptions)
    }

    @objc
    func stopScan(arg: Any?) {
        _centralManager.stopScan()
        _peripherals = [String: TiBLEPeripheralProxy]()
    }

    // MARK: Events

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if !self._hasListeners("updated") {
            return
        }
        self.fireEvent("updated")
    }

    func centralManager(_ central: CBCentralManager, willRestoreState dict: [String: Any]) {
        var objects = [TiBLEPeripheralProxy]()
        if let resotredPeripherals = dict[CBCentralManagerRestoredStatePeripheralsKey] as? [CBPeripheral] {
            resotredPeripherals.forEach { (peripherals) in
                let proxy = TiBLEPeripheralProxy(pageContext: self.pageContext, peripheral: peripherals)
                objects.append(proxy)
                _peripherals[peripherals.identifier.uuidString] = proxy
            }
        }

        if !self._hasListeners("restore_state") {
            return
        }
        var servicesUUID = [String]()
        if let resotredServices = dict[CBCentralManagerRestoredStateScanServicesKey] as? [CBUUID] {
            resotredServices.forEach { (uuid) in
                servicesUUID.append(uuid.uuidString)
            }
        }
        var scanOptions = [String: Any]()
        if let options = dict[CBCentralManagerRestoredStateScanOptionsKey] as? [String: Any] {
            if let allowDuplicatesKey = options[CBCentralManagerScanOptionAllowDuplicatesKey] as? Bool {
                scanOptions["allowDuplicatesKey"] = allowDuplicatesKey
            }
            if let solicitedServiceUUIDsKey = options[CBCentralManagerScanOptionSolicitedServiceUUIDsKey] as? [CBUUID] {
                var solicitedServiceUUIDs = [String]()
                solicitedServiceUUIDsKey.forEach { (value) in
                    solicitedServiceUUIDs.append(value.uuidString)
                }
                scanOptions["solicitedServiceUUIDsKey"] = solicitedServiceUUIDs
            }
        }
        var restoreState = [String: Any]()
        restoreState["peripherals"] = objects
        restoreState["services"] = servicesUUID
        restoreState["options"] = scanOptions
        self.fireEvent("restore_state", with: restoreState)
    }

    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String: Any], rssi RSSI: NSNumber) {
        let proxy = TiBLEPeripheralProxy(pageContext: self.pageContext, peripheral: peripheral)
        _peripherals[peripheral.identifier.uuidString] = proxy

        if !self._hasListeners("peripheral_discoverd") {
            return
        }
        self.fireEvent("peripheral_discoverd", with: proxy)
    }

}
