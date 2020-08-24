/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

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
        return NSNumber(value: _centralManager.isScanning)
    }

    @objc
    func peripherals() -> [TiBLEPeripheralProxy] {
        var list = [TiBLEPeripheralProxy]()
        _peripherals.forEach { (_, value) in
            list.append(value)
        }
        return list
    }

    @objc(startScan:)
    func startScan(arg: Any?) {
        let args = arg as? [String: Any]
        let services = args?["services"] as? [String]
        let options = args?["options"] as? [String: Any]

        let servicesUUID = TiBLEUtils.toCBUUIDs(from: services)
        var scanOptions = [String: Any]()
        if let options = options {
            if let allowDuplicatesKey = options["allowDuplicatesKey"] as? Bool {
                scanOptions[CBCentralManagerScanOptionAllowDuplicatesKey] = allowDuplicatesKey
            }
            if let solicitedServiceUUIDsKey = options["solicitedServiceUUIDsKey"] as? [String] {
                scanOptions[CBCentralManagerScanOptionSolicitedServiceUUIDsKey] = TiBLEUtils.toCBUUIDs(from: solicitedServiceUUIDsKey)
            }
        }
        _centralManager.scanForPeripherals(withServices: servicesUUID.isEmpty ? nil : servicesUUID, options: scanOptions.isEmpty ? nil : scanOptions)
    }

    @objc(stopScan:)
    func stopScan(arg: Any?) {
        _centralManager.stopScan()
        _peripherals = [String: TiBLEPeripheralProxy]()
    }

    @objc(retrievePeripheralsWithIdentifiers:)
    func retrievePeripheralsWithIdentifiers(arg: Any?) -> [TiBLEPeripheralProxy] {
        guard let args = arg as? [String: Any],
            let uuids = args["UUIDs"] as? [String] else {
                return []
        }
        let ids = TiBLEUtils.toUUIDs(from: uuids)
        let cbPeripherals = _centralManager.retrievePeripherals(withIdentifiers: ids)
        var peripherals = [TiBLEPeripheralProxy]()
        cbPeripherals.forEach { (cbPeripheral) in
            peripherals.append(TiBLEPeripheralProxy(pageContext: pageContext, peripheral: cbPeripheral))
        }
        return peripherals
    }

    @objc(retrieveConnectedPeripheralsWithServices:)
    func retrieveConnectedPeripheralsWithServices(arg: Any?) -> [TiBLEPeripheralProxy] {
        guard let args = arg as? [String: Any],
            let uuids = args["UUIDs"] as? [String] else {
                return []
        }
        let ids = TiBLEUtils.toCBUUIDs(from: uuids)
        let cbPeripherals = _centralManager.retrieveConnectedPeripherals(withServices: ids)
        var peripherals = [TiBLEPeripheralProxy]()
        cbPeripherals.forEach { (cbPeripheral) in
            peripherals.append(TiBLEPeripheralProxy(pageContext: pageContext, peripheral: cbPeripheral))
        }
        return peripherals
    }

    // MARK: Events

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if !self._hasListeners("didUpdateState") {
            return
        }
        self.fireEvent("didUpdateState", with: ["state": NSNumber(value: central.state.rawValue)])
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

        if !self._hasListeners("willRestoreState") {
            return
        }
        let servicesUUID = TiBLEUtils.toStringUUIDs(from: dict[CBCentralManagerRestoredStateScanServicesKey] as? [CBUUID])
        var scanOptions = [String: Any]()
        if let options = dict[CBCentralManagerRestoredStateScanOptionsKey] as? [String: Any] {
            if let allowDuplicatesKey = options[CBCentralManagerScanOptionAllowDuplicatesKey] as? NSNumber {
                scanOptions["allowDuplicatesKey"] = allowDuplicatesKey
            }
            scanOptions["solicitedServiceUUIDsKey"] = TiBLEUtils.toStringUUIDs(from: options[CBCentralManagerScanOptionSolicitedServiceUUIDsKey] as? [CBUUID])
        }
        self.fireEvent("willRestoreState", with: [
            "peripherals": objects,
            "services": servicesUUID,
            "options": scanOptions
        ])
    }

    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String: Any], rssi RSSI: NSNumber) {
        let proxy = TiBLEPeripheralProxy(pageContext: self.pageContext, peripheral: peripheral)
        _peripherals[peripheral.identifier.uuidString] = proxy

        if !self._hasListeners("didDiscoverPeripheral") {
            return
        }
        var values = [String: Any]()

        if let optionValue = values[CBAdvertisementDataServiceDataKey] as? [CBUUID: Data] {
            var adsData = [String: TiBuffer]()
            optionValue.forEach { (key, value) in
                adsData[key.uuidString] = TiBLEUtils.toTiBuffer(from: value)._init(withPageContext: pageContext)
            }
            values[CBAdvertisementDataServiceDataKey] = adsData
        }

        if let optionValue = values[CBAdvertisementDataLocalNameKey] as? String {
            values[CBAdvertisementDataLocalNameKey] = optionValue
        }

        if let optionValue = values[CBAdvertisementDataManufacturerDataKey] as? Data {
            values[CBAdvertisementDataManufacturerDataKey] = TiBLEUtils.toTiBuffer(from: optionValue)._init(withPageContext: pageContext)
        }

        if let optionValue = values[CBAdvertisementDataServiceUUIDsKey] as? [CBUUID] {
            values[CBAdvertisementDataServiceUUIDsKey] = TiBLEUtils.toStringUUIDs(from: optionValue)
        }

        if let optionValue = values[CBAdvertisementDataOverflowServiceUUIDsKey] as? [CBUUID] {
            values[CBAdvertisementDataOverflowServiceUUIDsKey] = TiBLEUtils.toStringUUIDs(from: optionValue)
        }

        if let optionValue = values[CBAdvertisementDataTxPowerLevelKey] as? NSNumber {
            values[CBAdvertisementDataTxPowerLevelKey] = optionValue
        }

        if let optionValue = values[CBAdvertisementDataIsConnectable] as? NSNumber {
            values[CBAdvertisementDataIsConnectable] = optionValue
        }

        if let optionValue = values[CBAdvertisementDataSolicitedServiceUUIDsKey] as? [CBUUID] {
            values[CBAdvertisementDataSolicitedServiceUUIDsKey] = TiBLEUtils.toStringUUIDs(from: optionValue)
        }

        self.fireEvent("didDiscoverPeripheral", with: [
            "peripheral": proxy,
            "advertisementData": values,
            "rssi": RSSI
        ])
    }

}
