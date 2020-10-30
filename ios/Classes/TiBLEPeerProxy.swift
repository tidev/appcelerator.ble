/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@objc
class TiBLEPeerProxy: TiProxy {
    private var _peer: CBPeer!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, peer: CBPeer) {
        self.init()
        _init(withPageContext: pageContext)
        _peer = peer
    }

    func peer() -> CBPeer {
        return _peer
    }

    @objc
    func identifier() -> String {
        return _peer.identifier.uuidString
    }

}
