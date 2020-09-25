/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import Foundation
import TitaniumKit
import CoreBluetooth

@available(iOS 11.0, *)
class TiBLEL2CAPchannelProxy: TiProxy {
    private var _L2CapChannel: CBL2CAPChannel!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, L2CapChannel: CBL2CAPChannel?) {
        self.init()
        _init(withPageContext: pageContext)
        _L2CapChannel = L2CapChannel
    }
    func L2CapChannel() -> CBL2CAPChannel {
        return _L2CapChannel
    }
}
