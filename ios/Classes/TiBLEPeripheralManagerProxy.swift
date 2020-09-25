/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import CoreBluetooth
import TitaniumKit

class TiBLEPeripheralManagerProxy: TiProxy {

    private var _peripheralManager: CBPeripheralManager!
    private var _restoredPeripheralManagerIdentifiers: String?

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, showPowerAlert: Bool?, restoreIdentifier: String?) {
        self.init()
        _init(withPageContext: pageContext)
        var options = [String: Any]()

        if let showPowerAlert = showPowerAlert {
            options[CBPeripheralManagerOptionShowPowerAlertKey] = showPowerAlert
        }

        if let restoreIdentifier = restoreIdentifier {
            _restoredPeripheralManagerIdentifiers = restoreIdentifier
            options[CBPeripheralManagerOptionRestoreIdentifierKey] = restoreIdentifier
        }
        _peripheralManager = CBPeripheralManager(delegate: self, queue: nil, options: options.isEmpty ? nil : options)
    }

    @objc
    func isAdvertising() -> NSNumber {
        return NSNumber(value: _peripheralManager.isAdvertising)
    }

    @objc
    func peripheralManagerState() -> NSNumber {
        return NSNumber(value: _peripheralManager.state.rawValue)
    }

    @objc
    func restoredPeripheralManagerIdentifiers() -> String? {
        return _restoredPeripheralManagerIdentifiers
    }

    @objc(addService:)
    func addService(arg: Any?) -> TiBLEServiceProxy? {
        guard let values = arg as? [Any],
            let options = values.first as? [String: Any],
            let primary = options["primary"] as? Bool,
            let uuid = options["uuid"] as? String else {
                return nil
        }
        let cbUUID = CBUUID(string: uuid)

        let service = CBMutableService(type: cbUUID, primary: primary)
        var characteristicArray = [CBCharacteristic]()

        if let data = options["data"] as? TiBuffer,
            let properties = options["properties"] as? NSNumber,
            let permission = options["permissions"] as? NSNumber {
            let characteristicData = data.data as Data
            let characteristicPermission: CBAttributePermissions = CBAttributePermissions(rawValue: permission.uintValue)
            let characteristicProperties = CBCharacteristicProperties(rawValue: properties.uintValue)
            let characteristic = CBMutableCharacteristic(type: cbUUID, properties: characteristicProperties, value: characteristicData, permissions: characteristicPermission)
            characteristicArray.append(characteristic)
        }
        if let characteristics = options["characteristics"] as? [TiBLECharacteristicProxy] {
            for object in characteristics {
                characteristicArray.append(object.characteristic())
            }
        }

        service.characteristics = characteristicArray

        _peripheralManager?.add(service)
        return TiBLEServiceProxy(pageContext: self.pageContext, service: service)
    }

    @objc(removeAllServices:)
    func removeAllServices(arg: Any?) {
        _peripheralManager?.removeAllServices()
    }

    @objc(removeServices:)
    func removeServices(arg: Any?) {
        guard let options = (arg as? [[String: Any]])?.first,
            let service = options["service"] as? TiBLEServiceProxy else {
                return
        }
        _peripheralManager?.remove(service.mutableService())
    }

    @objc(respondToRequest:)
    func respondToRequest(arg: Any?) {
        guard let options = (arg as? [[String: Any]])?.first,
            let request = options["request"] as? TiBLERequestProxy,
            let code = options["result"] as? NSNumber,
            let result = CBATTError.Code(rawValue: code.intValue) else {
                return
        }
        _peripheralManager.respond(to: request.request(), withResult: result)
    }

    @objc(startAdvertising:)
    func startAdvertising(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first

        var data = [String: Any]()
        if let uuids = options?["serviceUUIDs"] as? [String] {
            data[CBAdvertisementDataServiceUUIDsKey] = TiBLEUtils.toCBUUIDs(from: uuids)
        }
        if let name = options?["localName"] as? String {
            data[CBAdvertisementDataLocalNameKey] = name
        }
        _peripheralManager.startAdvertising(data.isEmpty ? nil : data)
    }

    @objc(stopAdvertising:)
    func stopAdvertising(arg: Any?) {
        _peripheralManager.stopAdvertising()
    }

    @objc(updateValue:)
    func updateValue(arg: Any?) {
        guard let options = (arg as? [[String: Any]])?.first,
            let data = options["data"] as? TiBuffer,
            let characteristic = (options["characteristic"] as? TiBLEMutableCharacteristicProxy)?.mutableCharacteristic() else {
                return
        }
        var cbCentrals = [CBCentral]()
        if let centrals = options["centrals"] as? [TiBLECentralProxy] {
            centrals.forEach({ (proxy) in
                cbCentrals.append(proxy.central())
            })
        }
        _peripheralManager.updateValue(data.data as Data, for: characteristic, onSubscribedCentrals: cbCentrals.isEmpty ? nil : cbCentrals)
    }

    @objc(setDesiredConnectionLatency:)
    func setDesiredConnectionLatency(arg: Any?) {
        guard let options = (arg as? [[String: Any]])?.first,
            let latencyValue = (options["latency"] as? NSNumber)?.intValue,
            let latency = CBPeripheralManagerConnectionLatency(rawValue: latencyValue),
            let central = options["central"] as? TiBLECentralProxy else {
                return
        }
        _peripheralManager.setDesiredConnectionLatency(latency, for: central.central())
    }
}

extension TiBLEPeripheralManagerProxy: CBPeripheralManagerDelegate {
    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        if !self._hasListeners("didUpdateState") {
            return
        }
        self.fireEvent("didUpdateState", with: ["state": NSNumber(value: peripheral.state.rawValue)])
    }

    func peripheralManager(_ peripheral: CBPeripheralManager, willRestoreState dict: [String: Any]) {
        if !self._hasListeners("willRestoreState") {
            return
        }

        let servicesUUID = TiBLEUtils.toStringUUIDs(from: dict[CBPeripheralManagerRestoredStateServicesKey] as? [CBUUID])
        var advertisementData = [String: Any]()
        if let options = dict[CBPeripheralManagerRestoredStateAdvertisementDataKey] as? [String: Any] {
            if let uuids = options[CBAdvertisementDataServiceUUIDsKey] as? [CBUUID] {
                advertisementData["serviceUUIDs"] = TiBLEUtils.toStringUUIDs(from: uuids)
            }
            advertisementData["localName"] = options[CBAdvertisementDataLocalNameKey]
        }

        self.fireEvent("willRestoreState", with: [
            "services": servicesUUID,
            "advertisementData": advertisementData
        ])
    }

    func peripheralManagerDidStartAdvertising(_ peripheral: CBPeripheralManager, error: Error?) {
        if !self._hasListeners("didStartAdvertising") {
            return
        }
        self.fireEvent("didStartAdvertising", with: [
            "errorCode": (error as NSError?)?.code as Any,
            "errorDomain": (error as NSError?)?.domain as Any,
            "errorDescription": error?.localizedDescription as Any
        ])
    }

    func peripheralManager(_ peripheral: CBPeripheralManager, didAdd service: CBService, error: Error?) {
        if !self._hasListeners("didAddService") {
            return
        }
        self.fireEvent("didAddService",
                       with: [
                        "service": TiBLEServiceProxy(pageContext: pageContext, service: service),
                        "errorCode": (error as NSError?)?.code as Any,
                        "errorDomain": (error as NSError?)?.domain as Any,
                        "errorDescription": error?.localizedDescription as Any
        ])
    }

    func peripheralManager(_ peripheral: CBPeripheralManager, central: CBCentral, didSubscribeTo characteristic: CBCharacteristic) {
        if !self._hasListeners("didSubscribeToCharacteristic") {
            return
        }
        self.fireEvent("didSubscribeToCharacteristic",
                       with: [
                        "central": TiBLECentralProxy(pageContext: pageContext, central: central),
                        "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic)
        ])
    }

    func peripheralManager(_ peripheral: CBPeripheralManager, central: CBCentral, didUnsubscribeFrom characteristic: CBCharacteristic) {
        if !self._hasListeners("didUnsubscribeFromCharacteristic") {
            return
        }
        self.fireEvent("didUnsubscribeFromCharacteristic",
                       with: [
                        "central": TiBLECentralProxy(pageContext: pageContext, central: central),
                        "characteristic": TiBLECharacteristicProxy(pageContext: pageContext, characteristic: characteristic)
        ])
    }
    func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveRead request: CBATTRequest) {
        if !self._hasListeners("didReceiveReadRequest") {
            return
        }
        self.fireEvent("didReceiveReadRequest",
                       with: [
                        "request": TiBLERequestProxy(pageContext: pageContext, request: request)
        ])
    }
    func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveWrite requests: [CBATTRequest]) {
        if !self._hasListeners("didReceiveWriteRequests") {
            return
        }
        var proxyRequests = [TiBLERequestProxy]()
        requests.forEach { (request) in
            proxyRequests.append(TiBLERequestProxy(pageContext: pageContext, request: request))
        }
        self.fireEvent("didReceiveWriteRequests",
                       with: [
                        "requestss": proxyRequests
        ])
    }

    func peripheralManagerIsReady(toUpdateSubscribers peripheral: CBPeripheralManager) {
        if !self._hasListeners("readyToUpdateSubscribers") {
            return
        }
        self.fireEvent("readyToUpdateSubscribers")
    }

    func peripheralManager(_ peripheral: CBPeripheralManager, didPublishL2CAPChannel PSM: CBL2CAPPSM, error: Error?) {
        if !self._hasListeners("didPublishL2CAPChannel") {
            return
        }
        self.fireEvent("didPublishL2CAPChannel",
                       with: [
                        "errorCode": (error as NSError?)?.code as Any,
                        "errorDomain": (error as NSError?)?.domain as Any,
                        "errorDescription": error?.localizedDescription as Any,
                        "psm": NSNumber(value: PSM)
        ])
    }
    func peripheralManager(_ peripheral: CBPeripheralManager, didUnpublishL2CAPChannel PSM: CBL2CAPPSM, error: Error?) {

        if !self._hasListeners("didUnpublishL2CAPChannel") {
            return
        }
        self.fireEvent("didUnpublishL2CAPChannel",
                       with: [
                        "errorCode": (error as NSError?)?.code as Any,
                        "errorDomain": (error as NSError?)?.domain as Any,
                        "errorDescription": error?.localizedDescription as Any,
                        "psm": NSNumber(value: PSM)
        ])
    }

    @available(iOS 11.0, *)
    func peripheralManager(_ peripheral: CBPeripheralManager, didOpen channel: CBL2CAPChannel?, error: Error?) {
        if !self._hasListeners("didOpenL2CAPChannel") {
            return
        }
        self.fireEvent("didOpenL2CAPChannel",
                       with: [
                        "errorCode": (error as NSError?)?.code as Any,
                        "errorDomain": (error as NSError?)?.domain as Any,
                        "errorDescription": error?.localizedDescription as Any
                        //                        "channel":NSNumber(value: PSM)
        ])
    }

}
