/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import CoreLocation
import TitaniumKit

@objc
class TiBLEBeaconProxy: TiProxy {
    private var _beacon: CLBeacon!

    private override init() {
        super.init()
    }
    convenience init(pageContext: TiEvaluator, beacon: CLBeacon) {
        self.init()
        _init(withPageContext: pageContext)
        _beacon = beacon
    }

    @objc
    func accuracy() -> NSNumber {
        return NSNumber(value: _beacon.accuracy)
    }

    @objc
    func major() -> NSNumber {
        return _beacon.major
    }

    @objc
    func minor() -> NSNumber {
        return _beacon.minor
    }

    @objc
    func proximity() -> NSNumber {
        return NSNumber(value: _beacon.proximity.rawValue)
    }

    @objc
    func rssi() -> NSNumber {
        return NSNumber(value: _beacon.rssi)
    }

    @objc
    func uuid() -> String? {
        if #available(iOS 13.0, *) {
            return _beacon.uuid.uuidString
        }
        return nil
    }

    @objc
    func timestamp() -> NSNumber {
        if #available(iOS 13.0, *) {
            return NSNumber(value: _beacon.timestamp.timeIntervalSince1970)
        }
        return NSNumber(value: -1)
    }

    func beacon() -> CLBeacon {
        return _beacon
    }
}
