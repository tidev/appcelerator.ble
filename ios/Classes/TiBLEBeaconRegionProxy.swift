/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import TitaniumKit
import CoreLocation

class TiBLEBeaconRegionProxy: TiProxy {
    private var _beaconRegion: CLBeaconRegion!

    private override init() {
        super.init()
    }
    convenience init(pageContext: TiEvaluator, beaconRegion: CLBeaconRegion) {
        self.init()
        _init(withPageContext: pageContext)
        _beaconRegion = _beaconRegion
    }
}
