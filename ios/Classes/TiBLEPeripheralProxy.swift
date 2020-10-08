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
        peripheral.delegate = self
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

    @objc
    func canSendWriteWithoutResponse() -> Bool {
        if #available(iOS 11.0, *) {
            return _peripheral.canSendWriteWithoutResponse
        } else {
            return true
        }
    }

    @objc
    func ancsAuthorized() -> Bool {
        if #available(iOS 13.0, *) {
            return _peripheral.ancsAuthorized
        } else {
            return true
        }
    }

    func peripheral() -> CBPeripheral {
        return _peripheral
    }

    @objc(maximumWriteValueLength:)
    func maximumWriteValueLength(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let characteristicWriteType = args?["characteristicWriteType"] as? NSNumber,
            let writeType = CBCharacteristicWriteType(rawValue: Int(truncating: characteristicWriteType)) else {
                return
        }
        _peripheral.maximumWriteValueLength(for: writeType)
    }

    @objc(openL2CAPChannel:)
    func openL2CAPChannel(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let psmIdentifier = args?["psmIdentifier"] as? NSNumber else {
            return
        }
        if #available(iOS 11.0, *) {
            let psm: UInt16 = UInt16(truncating: psmIdentifier)
            _peripheral.openL2CAPChannel(CBL2CAPPSM(psm))
        } else {
            return
        }
    }

    @objc(discoverCharacteristics:)
    func discoverCharacteristics(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let service = args?["service"] as? TiBLEServiceProxy else {
            return
        }
        let UUIDs = args?["UUIDs"] as? [String]
        let cBUUID = TiBLEUtils.toCBUUIDs(from: UUIDs)
        _peripheral.discoverCharacteristics(cBUUID.isEmpty ? nil : cBUUID, for: service.service())
    }

    @objc(discoverDescriptorsForCharacteristic:)
    func discoverDescriptorsForCharacteristic(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let characteristic = args?["characteristic"] as? TiBLECharacteristicProxy else {
            return
        }
        _peripheral.discoverDescriptors(for: characteristic.characteristic())
    }

    @objc(discoverIncludedServices:)
    func discoverIncludedServices(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let service = args?["service"] as? TiBLEServiceProxy else {
            return
        }
        let serviceUUIDs = args?["serviceUUIDs"] as? [String]
        let cBUUID = TiBLEUtils.toCBUUIDs(from: serviceUUIDs)
        _peripheral.discoverIncludedServices(cBUUID.isEmpty ? nil : cBUUID, for: service.service())
    }

    @objc(discoverServices:)
    func discoverServices(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        let serviceUUIDStrings = args?["serviceUUIDStrings"] as? [String]
        let cBUUID = TiBLEUtils.toCBUUIDs(from: serviceUUIDStrings)
        _peripheral.discoverServices(cBUUID.isEmpty ? nil : cBUUID)
    }

    @objc(readValueForCharacteristic:)
    func readValueForCharacteristic(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let characteristic = args?["characteristic"] as? TiBLECharacteristicProxy else {
            return
        }
        _peripheral.readValue(for: characteristic.characteristic())
    }

    @objc(readValueForDescriptor:)
    func readValueForDescriptor(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let descriptor = args?["descriptor"] as? TiBLEDescriptorProxy else {
            return
        }
        _peripheral.readValue(for: descriptor.descriptor())
    }

    @objc(writeValueForCharacteristic:)
    func writeValueForCharacteristic(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let data = args?["data"] as? TiBuffer,
            let characteristic = args?["characteristic"] as? TiBLECharacteristicProxy,
            let type = args?["type"] as? NSNumber,
            let writeType = CBCharacteristicWriteType(rawValue: Int(truncating: type)) else {
                return
        }
        let characteristicData = data.data as Data
        _peripheral.writeValue(characteristicData, for: characteristic.characteristic(), type: writeType)
    }

    @objc(writeValueForDescriptor:)
    func writeValueForDescriptor(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let data = args?["data"] as? TiBuffer,
            let descriptor = args?["descriptor"] as? TiBLEDescriptorProxy else {
                return
        }
        let descriptorData = data.data as Data
        _peripheral.writeValue(descriptorData, for: descriptor.descriptor())
    }

    @objc(subscribeToCharacteristic:)
    func subscribeToCharacteristic(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let characteristic = args?["characteristic"] as? TiBLECharacteristicProxy else {
            return
        }
        _peripheral.setNotifyValue(true, for: characteristic.characteristic())
    }

    @objc(unsubscribeFromCharacteristic:)
    func unsubscribeFromCharacteristic(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let characteristic = args?["characteristic"] as? TiBLECharacteristicProxy else {
            return
        }
        _peripheral.setNotifyValue(false, for: characteristic.characteristic())
    }
}
extension TiBLEPeripheralProxy: CBPeripheralDelegate {
    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if !self._hasListeners("didDiscoverCharacteristics") {
            return
        }
        self.fireEvent("didDiscoverCharacteristics", with: [
            "sourcePeripheral": self,
            "service": TiBLEServiceProxy(pageContext: pageContext, service: service),
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverDescriptorsFor characteristic: CBCharacteristic, error: Error?) {
        if !self._hasListeners("didDiscoverDescriptorsForCharacteristics") {
            return
        }
        self.fireEvent("didDiscoverDescriptorsForCharacteristics", with: [
            "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic),
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverIncludedServicesFor service: CBService, error: Error?) {
        if !self._hasListeners("didDiscoverIncludedServices") {
            return
        }
        self.fireEvent("didDiscoverIncludedServices", with: [
            "sourcePeripheral": self,
            "service": TiBLEServiceProxy(pageContext: pageContext, service: service),
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if !self._hasListeners("didDiscoverServices") {
            return
        }
        self.fireEvent("didDiscoverServices", with: [
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didReadRSSI RSSI: NSNumber, error: Error?) {
        if !self._hasListeners("didReadRSSI") {
            return
        }
        self.fireEvent("didReadRSSI", with: [
            "rssi": RSSI,
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if !self._hasListeners("didUpdateValueForCharacteristic") {
            return
        }
        guard let characteristicValue = characteristic.value else {
            return
        }
        let data = TiBLEUtils.toTiBuffer(from: characteristicValue)._init(withPageContext: pageContext)!

        self.fireEvent("didUpdateValueForCharacteristic", with: [
            "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic),
            "sourcePeripheral": self,
            "value": data as TiBuffer,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor descriptor: CBDescriptor, error: Error?) {
        if !self._hasListeners("didUpdateValueForDescriptor") {
            return
        }
        self.fireEvent("didUpdateValueForDescriptor", with: [
            "descriptor": TiBLEDescriptorProxy(pageContext: pageContext, descriptor: descriptor),
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if !self._hasListeners("didWriteValueForCharacteristic") {
            return
        }
        self.fireEvent("didWriteValueForCharacteristic", with: [
            "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic),
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didWriteValueFor descriptor: CBDescriptor, error: Error?) {
        if !self._hasListeners("didWriteValueForDescriptor") {
            return
        }
        self.fireEvent("didWriteValueForDescriptor", with: [
            "descriptor": TiBLEDescriptorProxy(pageContext: pageContext, descriptor: descriptor),
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didUpdateNotificationStateFor characteristic: CBCharacteristic, error: Error?) {
        if !self._hasListeners("didUpdateNotificationStateForCharacteristics") {
            return
        }
        self.fireEvent("didUpdateNotificationStateForCharacteristics", with: [
            "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic),
            "sourcePeripheral": self,
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    public func peripheralDidUpdateName(_ peripheral: CBPeripheral) {
        if !self._hasListeners("didUpdateName") {
            return
        }
        self.fireEvent("didUpdateName", with: [
            "sourcePeripheral": self
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didModifyServices invalidatedServices: [CBService]) {
        if !self._hasListeners("didModifyServices") {
            return
        }
        self.fireEvent("didModifyServices", with: [
            "sourcePeripheral": self,
            "invalidatedServices": invalidatedServices
        ])
    }
    public func peripheralIsReady(toSendWriteWithoutResponse peripheral: CBPeripheral) {
        if !self._hasListeners("peripheralIsReadyToSendWriteWithoutResponse") {
            return
        }
        self.fireEvent("peripheralIsReadyToSendWriteWithoutResponse", with: [
            "sourcePeripheral": self
        ])
    }
    @available(iOS 11.0, *)
    public func peripheral(_ peripheral: CBPeripheral, didOpen channel: CBL2CAPChannel?, error: Error?) {
        if !self._hasListeners("didOpenChannel") {
            return
        }
        self.fireEvent("didOpenChannel", with: [
            "sourcePeripheral": self,
            "channel": TiBLEL2CAPchannelProxy(pageContext: pageContext, L2CapChannel: channel),
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }
}
