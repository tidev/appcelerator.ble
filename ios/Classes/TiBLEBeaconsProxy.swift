/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import CoreLocation
import TitaniumKit

class TiBLEBeaconsProxy: TiProxy {
    private var _beacon: CLBeacon!

    private override init() {
        super.init()
    }
    convenience init(pageContext: TiEvaluator, beacon: CLBeacon) {
        self.init()
        _init(withPageContext: pageContext)
        _beacon = beacon
    }
}
