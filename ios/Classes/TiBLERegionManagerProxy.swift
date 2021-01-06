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
    convenience init(pageContext: TiEvaluator) {
        self.init()
        _init(withPageContext: pageContext)
        _manager = CLLocationManager()
        _manager.delegate = self
    }

    @objc
    func locationManagerAuthorizationStatus() -> NSNumber {
        if #available(iOS 14.0, *) {
            return NSNumber(value: _manager.authorizationStatus.rawValue)
        }
        return NSNumber(value: CLLocationManager.authorizationStatus().rawValue)
    }

    @objc
    func maximumRegionMonitoringDistance() -> NSNumber? {
        return NSNumber(value: _manager.maximumRegionMonitoringDistance)
    }

    @objc
    func monitoredRegions() -> [TiBLEBeaconRegionProxy] {
        var regions: [TiBLEBeaconRegionProxy] =  [TiBLEBeaconRegionProxy]()
        for region in _manager.monitoredRegions {
            if let region = region as? CLBeaconRegion {
                let proxy = TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
                regions.append(proxy)
            }
        }
        return regions
    }

    @objc
    func rangedRegions() -> [TiBLEBeaconRegionProxy] {
        var regions: [TiBLEBeaconRegionProxy] =  [TiBLEBeaconRegionProxy]()
        for region in _manager.rangedRegions {
            if let region = region as? CLBeaconRegion {
                let proxy = TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
                regions.append(proxy)
            }
        }
        return regions
    }

    @available(iOS 13.0, *)
    @objc
    func rangedBeaconConstraints() -> [TiBeaconIdentityConstraintProxy] {
        let constraints: [TiBeaconIdentityConstraintProxy] =  _manager.rangedBeaconConstraints.map { (constraint) -> TiBeaconIdentityConstraintProxy in
            return TiBeaconIdentityConstraintProxy(pageContext: self.pageContext, beaconIdentityConstraint: constraint)
        }
        return constraints
    }

    @objc
    func locationServicesEnabled() -> NSNumber {
        return NSNumber(value: CLLocationManager.locationServicesEnabled())
    }

    @objc
    func isRangingAvailable() -> NSNumber {
        return NSNumber(value: CLLocationManager.isRangingAvailable())
    }

    @objc
    func isMonitoringAvailable() -> NSNumber {
        return NSNumber(value: CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self))
    }

    @objc(requestAlwaysAuthorization:)
    func requestAlwaysAuthorization(arg: Any?) {
        _manager.requestAlwaysAuthorization()
    }

    @objc(requestRegionState:)
    func requestRegionState(arg: Any) {
        let options = (arg as? [[String: Any]])?.first
        if let beaconRegion = options?["beaconRegion"] as? TiBLEBeaconRegionProxy {
            _manager.requestState(for: beaconRegion.beaconRegion())
        }
    }

    @objc(requestWhenInUseAuthorization:)
    func requestWhenInUseAuthorization(arg: Any?) {
        _manager.requestWhenInUseAuthorization()
    }

    @objc(startRangingBeaconsInRegion:)
    func startRangingBeaconsInRegion(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if let beaconRegion = options?["beaconRegion"] as? TiBLEBeaconRegionProxy {
            _manager.startRangingBeacons(in: beaconRegion.beaconRegion())
        }
    }

    @objc(startRangingBeaconsSatisfyingIdentityConstraint:)
    func startRangingBeaconsSatisfyingIdentityConstraint(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if #available(iOS 13.0, *) {
            if let constraint = options?["identityConstraint"] as? TiBeaconIdentityConstraintProxy {
                _manager.startRangingBeacons(satisfying: constraint.beaconIdentityConstraint())
            }
        }
    }

    @objc(stopRangingBeaconsInRegion:)
    func stopRangingBeaconsInRegion(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if let beaconRegion = options?["beaconRegion"] as? TiBLEBeaconRegionProxy {
            _manager.stopRangingBeacons(in: beaconRegion.beaconRegion())
        }
    }

    @objc(stopRangingBeaconsSatisfyingIdentityConstraint:)
    func stopRangingBeaconsSatisfyingIdentityConstraint(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if #available(iOS 13.0, *) {
            if let constraint = options?["identityConstraint"] as? TiBeaconIdentityConstraintProxy {
                _manager.stopRangingBeacons(satisfying: constraint.beaconIdentityConstraint())
            }
        }
    }

    @objc(startRegionMonitoring:)
    func startRegionMonitoring(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if let beaconRegion = options?["beaconRegion"] as? TiBLEBeaconRegionProxy {
            _manager.startMonitoring(for: beaconRegion.beaconRegion())
        }
    }

    @objc(stopRegionMonitoring:)
    func stopRegionMonitoring(arg: Any?) {
        let options = (arg as? [[String: Any]])?.first
        if let beaconRegion = options?["beaconRegion"] as? TiBLEBeaconRegionProxy {
            _manager.stopMonitoring(for: beaconRegion.beaconRegion())
        }
    }
}

extension TiBLERegionManagerProxy: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion) {
        if !self._hasListeners("didEnterRegion") {
            return
        }
        if let region = region as? CLBeaconRegion {
            self.fireEvent("didEnterRegion",
                           with: [
                            "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
            ])
        }
    }
    func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        if !self._hasListeners("didExitRegion") {
            return
        }
        if let region = region as? CLBeaconRegion {
            self.fireEvent("didExitRegion",
                           with: [
                            "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
            ])
        }
    }

    func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        if !self._hasListeners("didRangeBeacons") {
            return
        }
        let beaconProxies = beacons.map { (beacon) -> TiBLEBeaconProxy in
            return TiBLEBeaconProxy(pageContext: self.pageContext, beacon: beacon)
        }
        self.fireEvent("didRangeBeacons",
                       with: [
                        "beacons": beaconProxies,
                        "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
        ])
    }

    @available(iOS 13.0, *)
    func locationManager(_ manager: CLLocationManager, didRange beacons: [CLBeacon], satisfying beaconConstraint: CLBeaconIdentityConstraint) {
        if !self._hasListeners("didRange") {
            return
        }
        let beaconProxies = beacons.map { (beacon) -> TiBLEBeaconProxy in
            return TiBLEBeaconProxy(pageContext: self.pageContext, beacon: beacon)
        }
        self.fireEvent("didRange",
                       with: [
                        "beacons": beaconProxies,
                        "beaconConstraint": TiBeaconIdentityConstraintProxy(pageContext: self.pageContext, beaconIdentityConstraint: beaconConstraint)
        ])
    }

    @available(iOS 13.0, *)
    func locationManager(_ manager: CLLocationManager, didFailRangingFor beaconConstraint: CLBeaconIdentityConstraint, error: Error) {
        if !self._hasListeners("didFailRanging") {
            return
        }
        self.fireEvent("didFailRanging",
                       with: [
                        "beaconConstraint": TiBeaconIdentityConstraintProxy(pageContext: self.pageContext, beaconIdentityConstraint: beaconConstraint),
                        "errorCode": (error as NSError).code ,
                        "errorDomain": (error as NSError).domain,
                        "errorDescription": error.localizedDescription
        ])
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        if !self._hasListeners("didFail") {
            return
        }
        self.fireEvent("didFail",
                       with: [
                        "errorCode": (error as NSError).code ,
                        "errorDomain": (error as NSError).domain,
                        "errorDescription": error.localizedDescription
        ])
    }

    func locationManager(_ manager: CLLocationManager, rangingBeaconsDidFailFor region: CLBeaconRegion, withError error: Error) {
        if !self._hasListeners("rangingBeaconsDidFail") {
            return
        }
        self.fireEvent("rangingBeaconsDidFail",
                       with: [
                        "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region),
                        "errorCode": (error as NSError).code ,
                        "errorDomain": (error as NSError).domain,
                        "errorDescription": error.localizedDescription
        ])
    }

    func locationManager(_ manager: CLLocationManager, didDetermineState state: CLRegionState, for region: CLRegion) {
        if !self._hasListeners("didDetermineState") {
            return
        }
        if let region = region as? CLBeaconRegion {
            self.fireEvent("didDetermineState",
                           with: [
                            "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region),
                            "state": NSNumber(value: state.rawValue)
            ])
        }
    }

    func locationManager(_ manager: CLLocationManager, didStartMonitoringFor region: CLRegion) {
        if !self._hasListeners("didStartMonitoring") {
            return
        }
        if let region = region as? CLBeaconRegion {
            self.fireEvent("didStartMonitoring",
                           with: [
                            "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region)
            ])
        }
    }

    func locationManager(_ manager: CLLocationManager, monitoringDidFailFor region: CLRegion?, withError error: Error) {
        if !self._hasListeners("monitoringDidFail") {
            return
        }
        if let region = region as? CLBeaconRegion {
            self.fireEvent("monitoringDidFail",
                           with: [
                            "region": TiBLEBeaconRegionProxy(pageContext: self.pageContext, beaconRegion: region),
                            "errorCode": (error as NSError).code ,
                            "errorDomain": (error as NSError).domain,
                            "errorDescription": error.localizedDescription
            ])
        }
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if !self._hasListeners("didChangeAuthorization") {
            return
        }
        self.fireEvent("didChangeAuthorization",
                       with: [
                        "state": NSNumber(value: status.rawValue)
        ])
    }

    @available(iOS 14.0, *)
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        if !self._hasListeners("didChangeAuthorization") {
            return
        }
        self.fireEvent("didChangeAuthorization",
                       with: [
                        "state": self.locationManagerAuthorizationStatus()
        ])
    }
}
