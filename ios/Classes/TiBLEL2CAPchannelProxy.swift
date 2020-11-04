/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@available(iOS 11.0, *)
@objc
class TiBLEL2CAPchannelProxy: TiProxy {
    private var _L2CapChannel: CBL2CAPChannel!
    private var _ioStream: TiBLEIOStream!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, L2CapChannel: CBL2CAPChannel) {
        self.init()
        _init(withPageContext: pageContext)
        _L2CapChannel = L2CapChannel
        _ioStream = TiBLEIOStream(inputStream: L2CapChannel.inputStream, outputStream: L2CapChannel.outputStream, streamListener: self)
    }

    func L2CapChannel() -> CBL2CAPChannel {
        return _L2CapChannel
    }

    @objc
    func peer() -> TiBLEPeerProxy {
        return TiBLEPeerProxy(pageContext: self.pageContext, peer: _L2CapChannel.peer)
    }

    @objc
    func psm() -> NSNumber {
        return NSNumber(value: _L2CapChannel.psm)
    }

    @objc(write:)
    func write(arg: Any?) {
        let args = (arg as? [[String: Any]])?.first
        guard let data = args?["data"] as? TiBuffer else {
            return
        }
        let writeData = data.data as Data
        _ioStream.write(data: writeData)
    }

    @objc(close:)
    func close(arg: Any?) {
        _ioStream.close()
    }
}

@available(iOS 11.0, *)
extension TiBLEL2CAPchannelProxy: IOStreamListener {

    func onStreamEndEncountered() {
        if !self._hasListeners("onStreamEndEncountered") {
            return
        }
        self.fireEvent("onStreamEndEncountered")
    }

    func onDataReceived(data: Data) {
        if !self._hasListeners("onDataReceived") {
            return
        }
        self.fireEvent("onDataReceived", with: [
            "data": TiBLEUtils.toTiBuffer(from: data)._init(withPageContext: self.pageContext)
        ])
    }

    func onStreamError(error: Error) {
        if !self._hasListeners("onStreamError") {
            return
        }
        self.fireEvent("onStreamError", with: [
            "sourceChannel": self,
            "errorCode": (error as NSError).code as Any,
            "errorDomain": (error as NSError).domain as Any,
            "errorDescription": error.localizedDescription as Any
        ])
    }
}
