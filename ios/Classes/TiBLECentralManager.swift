/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import CoreBluetooth
import TitaniumKit

@objc
class TiBLECentralManagerProxy: TiProxy {

    private var _centralManager: CBCentralManager!

    let peripheralProvider = TiBLEPeripheralProviderProxy.sharedPeripheralProvider

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
        return peripheralProvider.peripherals()
    }

    @objc(startScan:)
    func startScan(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
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
        peripheralProvider.removeAllPeripheral()

    }

    @objc(registerForConnectionEvents:)
    func registerForConnectionEvents(arg: Any?) {
        guard let args = (arg as? [[String: Any]])?.first else {
            if #available(iOS 13.0, *) {
                _centralManager.registerForConnectionEvents(options: nil)
            }
            return
        }
        var options = [CBConnectionEventMatchingOption: Any]()
        if let peripheralUUIDs = args["peripherals"] as? [String] {
            var uuids = [NSUUID]()
            peripheralUUIDs.forEach { (value) in
                if let uuid = NSUUID(uuidString: value) {
                    uuids.append(uuid)
                }
            }
            if #available(iOS 13.0, *) {
                options[CBConnectionEventMatchingOption.peripheralUUIDs] = uuids
            }
        }

        if let serviceUUIDs = args["services"] as? [String] {
            var uuids = [CBUUID]()
            serviceUUIDs.forEach { (value) in
                uuids.append(CBUUID(string: value))
            }
            if #available(iOS 13.0, *) {
                options[CBConnectionEventMatchingOption.serviceUUIDs] = uuids
            }
        }

        if #available(iOS 13.0, *) {
            if options.isEmpty {
                _centralManager.registerForConnectionEvents(options: nil)
                return
            }
            _centralManager.registerForConnectionEvents(options: [CBConnectionEventMatchingOption.peripheralUUIDs: []])
        }
    }

    @objc(retrievePeripheralsWithIdentifiers:)
    func retrievePeripheralsWithIdentifiers(arg: Any?) -> [TiBLEPeripheralProxy] {
        guard let args = (arg as? [[String: Any]])?.first,
              let uuids = args["UUIDs"] as? [String] else {
            return []
        }
        let ids = TiBLEUtils.toUUIDs(from: uuids)
        let cbPeripherals = _centralManager.retrievePeripherals(withIdentifiers: ids)
        var peripherals = [TiBLEPeripheralProxy]()
        cbPeripherals.forEach { (cbPeripheral) in
            if let peripheral = peripheralProvider.checkAddAndGetPeripheralProxy(from: cbPeripheral, and: self.pageContext) {
                peripherals.append(peripheral)
            }
        }
        return peripherals
    }

    @objc(retrieveConnectedPeripheralsWithServices:)
    func retrieveConnectedPeripheralsWithServices(arg: Any?) -> [TiBLEPeripheralProxy] {
        guard let args = (arg as? [[String: Any]])?.first,
              let uuids = args["UUIDs"] as? [String] else {
            return []
        }
        let ids = TiBLEUtils.toCBUUIDs(from: uuids)
        let cbPeripherals = _centralManager.retrieveConnectedPeripherals(withServices: ids)
        var peripherals = [TiBLEPeripheralProxy]()
        cbPeripherals.forEach { (cbPeripheral) in
            if let peripheral = peripheralProvider.checkAddAndGetPeripheralProxy(from: cbPeripheral, and: self.pageContext) {
                peripherals.append(peripheral)
            }
        }
        return peripherals
    }

    @objc(cancelPeripheralConnection:)
    func cancelPeripheralConnection(arg: Any?) {
        guard let args = (arg as? [[String: Any]])?.first,
              let peripheral = args["peripheral"] as? TiBLEPeripheralProxy else {
            return
        }
        _centralManager.cancelPeripheralConnection(peripheral.peripheral())
    }

    @objc(connectPeripheral:)
    func connectPeripheral(arg: Any?) {
        guard let args = (arg as? [[String: Any]])?.first,
              let peripheral = args["peripheral"] as? TiBLEPeripheralProxy else {
            return
        }
        var options = [String: Any]()
        if let values = args["options"] as? [String: Any] {
            if let optionValue = values[CBConnectPeripheralOptionNotifyOnConnectionKey] as? Bool {
                options[CBConnectPeripheralOptionNotifyOnConnectionKey] = optionValue
            }
            if let optionValue = values[CBConnectPeripheralOptionNotifyOnDisconnectionKey] as? Bool {
                options[CBConnectPeripheralOptionNotifyOnDisconnectionKey] = optionValue
            }
            if let optionValue = values[CBConnectPeripheralOptionNotifyOnNotificationKey] as? Bool {
                options[CBConnectPeripheralOptionNotifyOnNotificationKey] = optionValue
            }
            if let optionValue = values[CBConnectPeripheralOptionStartDelayKey] as? NSNumber {
                options[CBConnectPeripheralOptionStartDelayKey] = optionValue
            }
            if #available(iOS 13.0, *) {
                if let optionValue = values[CBConnectPeripheralOptionEnableTransportBridgingKey] as? Bool {
                    options[CBConnectPeripheralOptionEnableTransportBridgingKey] = optionValue
                }
                if let optionValue = values[CBConnectPeripheralOptionRequiresANCS] as? Bool {
                    options[CBConnectPeripheralOptionRequiresANCS] = optionValue
                }
            }
        }
        _centralManager.connect(peripheral.peripheral(), options: options)
    }
}

extension TiBLECentralManagerProxy: CBCentralManagerDelegate {
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        if !self._hasListeners("didConnectPeripheral") {
            return
        }
        self.fireEvent("didConnectPeripheral", with: [
            "peripheral": peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext)
        ])
    }

    func centralManager(_ central: CBCentralManager, didUpdateANCSAuthorizationFor peripheral: CBPeripheral) {
        if !self._hasListeners("didUpdateANCSAuthorization") {
            return
        }
        self.fireEvent("didUpdateANCSAuthorization", with: [
            "peripheral": peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext)
        ])
    }

    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        if !self._hasListeners("didFailToConnectPeripheral") {
            return
        }

        self.fireEvent("didFailToConnectPeripheral", with: [
            "peripheral": peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext) as Any,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if !self._hasListeners("didDisconnectPeripheral") {
            return
        }

        self.fireEvent("didDisconnectPeripheral", with: [
            "peripheral": peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext) as Any,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    func centralManager(_ central: CBCentralManager, connectionEventDidOccur event: CBConnectionEvent, for peripheral: CBPeripheral) {
        if !self._hasListeners("connectionEventDidOccur") {
            return
        }

        self.fireEvent("connectionEventDidOccur", with: [
            "peripheral": peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext) as Any,
            "event": NSNumber(value: event.rawValue)
        ])
    }

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
                if let proxy = peripheralProvider.checkAddAndGetPeripheralProxy(from: peripherals, and: self.pageContext) {
                    objects.append(proxy)
                }
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
        let proxy = peripheralProvider.checkAddAndGetPeripheralProxy(from: peripheral, and: self.pageContext)

        if !self._hasListeners("didDiscoverPeripheral") {
            return
        }
        var values = [String: Any]()

        if let optionValue = values[CBAdvertisementDataServiceDataKey] as? [CBUUID: Data] {
            var adsData = [String: TiBuffer]()
            optionValue.forEach { (key, value) in
                adsData[key.uuidString] = TiBLEUtils.toTiBuffer(from: value)._init(withPageContext: self.pageContext)
            }
            values[CBAdvertisementDataServiceDataKey] = adsData
        }

        if let optionValue = values[CBAdvertisementDataLocalNameKey] as? String {
            values[CBAdvertisementDataLocalNameKey] = optionValue
        }

        if let optionValue = values[CBAdvertisementDataManufacturerDataKey] as? Data {
            values[CBAdvertisementDataManufacturerDataKey] = TiBLEUtils.toTiBuffer(from: optionValue)._init(withPageContext: self.pageContext)
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
            "peripheral": proxy as Any,
            "advertisementData": values,
            "rssi": RSSI
        ])
    }
}
