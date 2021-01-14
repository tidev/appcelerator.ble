/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import TitaniumKit
import CoreLocation

@objc
class TiBLEBeaconRegionProxy: TiProxy {
    private var _beaconRegion: CLBeaconRegion!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, beaconRegion: CLBeaconRegion) {
        self.init()
        _init(withPageContext: pageContext)
        _beaconRegion = beaconRegion
    }

    @objc
    func identifier() -> String {
        return _beaconRegion.identifier
    }

    @objc
    func major() -> NSNumber? {
        return _beaconRegion.major
    }

    @objc
    func minor() -> NSNumber? {
        return _beaconRegion.minor
    }

    @objc
    var notifyEntryStateOnDisplay: NSNumber {
        get {
            return NSNumber(value: _beaconRegion.notifyEntryStateOnDisplay)
        }
        set {
            _beaconRegion.notifyEntryStateOnDisplay = newValue.boolValue
        }
    }

    @objc
    func uuid() -> String? {
        if #available(iOS 13.0, *) {
            return _beaconRegion.uuid.uuidString
        }
        return _beaconRegion.proximityUUID.uuidString
    }

    func beaconRegion() -> CLBeaconRegion {
        return _beaconRegion
    }
}
