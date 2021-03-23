/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit
import CoreLocation
import TitaniumKit

@available(iOS 13.0, *)
@objc
class TiBeaconIdentityConstraintProxy: TiProxy {
    private var _beaconIdentityConstraint: CLBeaconIdentityConstraint!

    private override init() {
        super.init()
    }

    convenience init(pageContext: TiEvaluator, beaconIdentityConstraint: CLBeaconIdentityConstraint) {
        self.init()
        _init(withPageContext: pageContext)
        _beaconIdentityConstraint = beaconIdentityConstraint
    }

    @objc
    func major() -> NSNumber? {
        if let major = _beaconIdentityConstraint.major {
            return NSNumber(value: major)
        }
        return nil
    }

    @objc
    func minor() -> NSNumber? {
        if let minor = _beaconIdentityConstraint.minor {
            return NSNumber(value: minor)
        }
        return nil
    }

    @objc
    func uuid() -> String? {
        return _beaconIdentityConstraint.uuid.uuidString
    }

    func beaconIdentityConstraint() -> CLBeaconIdentityConstraint {
        return _beaconIdentityConstraint
    }
}
