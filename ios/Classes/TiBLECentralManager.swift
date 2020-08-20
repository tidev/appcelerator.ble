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

    @objc(registerForConnectionEvents:)
    func registerForConnectionEvents(arg: Any?) {
        guard let args = arg as? [String: Any] else {
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

    @objc(cancelPeripheralConnection:)
    func cancelPeripheralConnection(arg: Any?) {
        guard let args = arg as? [String: Any],
            let peripheral = args["peripheral"] as? TiBLEPeripheralProxy else {
                return
        }
        _centralManager.cancelPeripheralConnection(peripheral.peripheral())
    }

    @objc(connectPeripheral:)
    func connectPeripheral(arg: Any?) {
        guard let args = arg as? [String: Any],
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
        if !self._hasListeners("peripheral_did_connect") {
            return
        }
        var values = [String: Any]()
        values["peripheral"] = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
        self.fireEvent("peripheral_did_connect", with: values)
    }

    func centralManager(_ central: CBCentralManager, didUpdateANCSAuthorizationFor peripheral: CBPeripheral) {
        if !self._hasListeners("peripheral_did_update_ancs_authorization") {
            return
        }
        var values = [String: Any]()
        values["peripheral"] = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
        self.fireEvent("peripheral_did_update_ancs_authorization", with: values)
    }

    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        if !self._hasListeners("peripheral_failed_to_connect") {
            return
        }
        var values = [String: Any]()
        values["peripheral"] = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
        if let error = error {
            self.fireEvent("peripheral_failed_to_connect", with: values, errorCode: (error as NSError).code, message: error.localizedDescription)
        } else {
            self.fireEvent("peripheral_failed_to_connect", with: values)
        }
    }

    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if !self._hasListeners("peripheral_did_disconnected") {
            return
        }
        var values = [String: Any]()
        values["peripheral"] = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
        if let error = error {
            self.fireEvent("peripheral_did_disconnected", with: values, errorCode: (error as NSError).code, message: error.localizedDescription)
        } else {
            self.fireEvent("peripheral_did_disconnected", with: values)
        }
    }

    func centralManager(_ central: CBCentralManager, connectionEventDidOccur event: CBConnectionEvent, for peripheral: CBPeripheral) {
        if !self._hasListeners("peripheral_connection_event_occur") {
            return
        }
        var values = [String: Any]()
        values["peripheral"] = TiBLEPeripheralProxy(pageContext: pageContext, peripheral: peripheral)
        values["event"] = event.rawValue
        self.fireEvent("peripheral_connection_event_occur", with: values)
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
