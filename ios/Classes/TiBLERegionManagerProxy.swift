/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import CoreLocation
import TitaniumKit

class TiBLERegionManagerProxy: TiProxy {
    private var _manager: CLLocationManager!

    private override init() {
        super.init()
    }
    convenience init(pageContext: TiEvaluator, beacon: CLBeacon) {
        self.init()
        _init(withPageContext: pageContext)
        _manager = CLLocationManager()
        _manager.delegate = self
    }
}

extension TiBLERegionManagerProxy: CLLocationManagerDelegate {

}
