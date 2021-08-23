/* eslint-disable no-alert */
/* eslint-disable no-loop-func */
function deviceWin(BLE, beaconUUID, major, minor, beaconId) {
	var logs = [];
	var manager = null;
	var win = Ti.UI.createWindow({
		backgroundColor: 'white',
		title: 'Beacon Scanner',
		titleAttributes: { color: 'blue' }
	});
	var valueField = Ti.UI.createTextField({
		top: 40,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Beacon UUID',
		value: beaconUUID,
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});
	win.add(valueField);

	var majorField = Ti.UI.createTextField({
		top: 90,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Major Value',
		value: major,
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});
	win.add(majorField);

	var minorField = Ti.UI.createTextField({
		top: 140,
		borderStyle: Ti.UI.INPUT_BORDERSTYLE_BEZEL,
		hintText: 'Enter Minor Value',
		value: minor,
		hintTextColor: '#000000',
		backgroundColor: '#fafafa',
		color: 'black',
		width: 250,
		height: 40
	});
	win.add(minorField);

	var initializeManager = Titanium.UI.createButton({
		top: 190,
		title: 'Initialize Manager'
	});
	var initializeManagerClick = () => {
		if (manager === null) {
			manager = BLE.createRegionManager();
			setRegionManagerEventListeners(manager);
			logs.push('Manager initilized');
			setData(logs);
		} else {
			Ti.API.info('Peripheral Manager already Initialized');
			alert('Peripheral Manager already Initialized');
		}
	};
	initializeManager.addEventListener('click', initializeManagerClick);
	win.add(initializeManager);

	var requestAuth = Titanium.UI.createButton({
		top: 240,
		title: 'Request Authorization'
	});
	var requestAuthClick = () => {
		if (manager === null) {
			Ti.API.info('Manager is Not Initialized. Please click \'Initialize Manager\'');
			alert('Manager is Not Initialized. Please click \'Initialize Manager\'');
		} else {
			if (manager.locationManagerAuthorizationStatus !== BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_NOT_DETERMINED) {
				Ti.API.info('Manager already requested authorization');
				alert('Manager already requested authorization. Please check settings and change authorization state');
				return;
			}
			manager.requestWhenInUseAuthorization();
			logs.push('requested authorization');
			setData(logs);
		}
	};
	requestAuth.addEventListener('click', requestAuthClick);
	win.add(requestAuth);

	var startScan = Titanium.UI.createButton({
		top: 290,
		title: 'Start Scan'
	});
	var startScanClick = () => {
		if (manager === null) {
			Ti.API.info('Manager is Not Initialized. Please click \'Initialize Manager\'');
			alert('Manager is Not Initialized. Please click \'Initialize Manager\'');
		} else {
			if (manager.locationManagerAuthorizationStatus !== BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_WHEN_IN_USE) {
				Ti.API.info('Manager is not authorizated');
				alert('Manager is not authorizated. Please check settings and change authorization state');
				return;
			}
			var uuidValue = valueField.value === '' || valueField.value === null ? beaconUUID : valueField.value;
			const majorValue = Number.parseInt(majorField.value, 10);
			const minorValue = Number.parseInt(minorField.value, 10);
			if (majorField.value === '' || majorField.value === null) {
				alert('Please enter Major value');
				return;
			}
			if (minorField.value === '' || minorField.value === null) {
				alert('Please enter Minor value');
				return;
			}
			if (isNaN(majorValue)) {
				alert('Major value should be number only');
				return;
			}
			if (isNaN(minorValue)) {
				alert('Minor value should be number only' + minor);
				return;
			}
			var beaconRegion = BLE.createBeaconRegion({
				uuid: uuidValue,
				major: majorValue,
				minor: minorValue,
				identifier: beaconId
			});
			if (beaconRegion === null) {
				alert('Invalid uuid. Please enter valid uuid');
				return;
			}
			manager.startRegionMonitoring({
				beaconRegion: beaconRegion
			});
			manager.startRangingBeaconsInRegion({
				beaconRegion: beaconRegion
			});
		}
	};
	startScan.addEventListener('click', startScanClick);
	win.add(startScan);

	var stopScan = Titanium.UI.createButton({
		top: 340,
		title: 'Stop Scan'
	});
	var stopScanClick = () => {
		if (manager === null) {
			Ti.API.info('Manager is Not Initialized. Please click \'Initialize Manager\'');
			alert('Manager is Not Initialized. Please click \'Initialize Manager\'');
		} else {
			if (manager.locationManagerAuthorizationStatus !== BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_WHEN_IN_USE) {
				Ti.API.info('Manager is not authorizated');
				alert('Manager is not authorizated. Please check settings and change authorization state');
				return;
			}
			var uuid = valueField.value === '' || valueField.value === null ? beaconUUID : valueField.value;
			var beaconRegion = BLE.createBeaconRegion({
				uuid: uuid,
				major: major,
				minor: minor,
				identifier: beaconId
			});
			if (beaconRegion === null) {
				alert('Invalid uuid. Please enter valid uuid');
				return;
			}
			manager.stopRegionMonitoring({
				beaconRegion: beaconRegion
			});
			manager.stopRangingBeaconsInRegion({
				beaconRegion: beaconRegion
			});
			logs.push('Beacon Stop Scaning For ' + uuid);
			setData(logs);
		}
	};
	stopScan.addEventListener('click', stopScanClick);
	win.add(stopScan);

	var nameLabel = Ti.UI.createLabel({
		color: 'black',
		top: 390,
		width: 250,
		font: { fontSize: 14 }
	});

	function setLocaiontData(value) {
		nameLabel.text =  value;
	}
	setLocaiontData('Beacon Location : UNKNOWN');
	win.add(nameLabel);

	var tableView = Titanium.UI.createTableView({
		top: 440,
		scrollable: true,
		backgroundColor: 'White',
		separatorColor: '#DBE1E2',
		bottom: '5%',
	});
	var tbl_data = [];
	function setData(list) {
		tbl_data.splice(0, tbl_data.length);
		if (list.length > 0) {
			var initalValue = list.length - 1;
			for (var i = initalValue; i >= 0; i--) {
				var btDevicesRow = Ti.UI.createTableViewRow({
					height: 50,
					row: i,
					hasChild: true
				});
				var uuidLabel = Ti.UI.createLabel({
					left: 5,
					right: 5,
					color: 'blue',
					top: 5,
					font: { fontSize: 11 },
					text: list[i]
				});
				btDevicesRow.add(uuidLabel);
				tbl_data.push(btDevicesRow);
			}
		}
		tableView.setData(tbl_data);
	}
	setData(logs);
	win.add(tableView);

	var didChangeAuthorization = (e) => {
		Ti.API.info('didChangeAuthorization');
		switch (e.state) {
			case BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_ALWAYS:
				logs.push('Manager authorization updated to always');
				alert('Manager authorization is always');
				break;

			case BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_AUTHORIZED_WHEN_IN_USE:
				logs.push('Manager authorization updated to when in use');
				alert('Manager authorization is when in use');
				break;

			case BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_DENIED:
				logs.push('Manager authorization updated to denied');
				alert('Manager authorization is denied');
				break;

			case BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_NOT_DETERMINED:
				logs.push('Manager authorization updated to not determined');
				alert('Manager authorization is not determined');
				break;

			case BLE.LOCATION_MANAGER_AUTHORIZATION_STATUS_RESTRICTED:
				logs.push('Manager authorization updated to restricted');
				alert('Manager authorization is powered restricted');
				break;
			default:
				logs.push('Manager state updated to Unknown');
				alert('Unknown');
				break;
		}
		setData(logs);
	};
	var didRangeBeacons = (e) => {
		Ti.API.info('didRangeBeacons');
		var becaons = e.beacons;
		if (becaons.length === 0) {
			setLocaiontData('No beacon in range');
			return;
		}
		var proximity = becaons[0].proximity;
		var accuracy = becaons[0].accuracy;
		switch (proximity) {
			case BLE.BEACON_PROXIMITY_UNKNOWN:
				setLocaiontData('Beacon Location : UNKNOWN');
				break;

			case BLE.BEACON_PROXIMITY_IMMEDIATE:
				setLocaiontData('Beacon Location : IMMEDIATE (approx. ' + accuracy + 'm)');
				break;

			case BLE.BEACON_PROXIMITY_NEAR:
				setLocaiontData('Beacon Location : NEAR (approx. ' + accuracy + 'm)');
				break;

			case BLE.BEACON_PROXIMITY_FAR:
				setLocaiontData('Beacon Location : FAR (approx. ' + accuracy + 'm)');
				break;
			default:
				setLocaiontData('Beacon Location : UNKNOWN');
				break;
		}
	};
	var didDetermineState = (e) => {
		Ti.API.info('didDetermineState');
		var state = e.state;
		switch (state) {
			case BLE.REGION_STATE_UNKNOWN:
				logs.push('Scanner state unknown');
				break;

			case BLE.REGION_STATE_OUTSIDE:
				logs.push('Scanner state outside');
				break;

			case BLE.REGION_STATE_INSIDE:
				logs.push('Scanner state inside');
				break;
			default:
				logs.push('Scanner state unknown');
				break;
		}
		setData(logs);
	};
	var didStartMonitoring = (e) => {
		logs.push('didStartMonitoring:uuid: ' + e.region.uuid + ' Major: ' + e.region.major + ' Minor: ' + e.region.minor);
		setData(logs);
	};
	var didFail = (e) => {
		logs.push('didFail: ' + e.errorDescription);
		setData(logs);
	};
	var didFailRanging = (e) => {
		logs.push('didFailRanging: ' + e.errorDescription);
		setData(logs);
	};
	var rangingBeaconsDidFail = (e) => {
		logs.push('rangingBeaconsDidFail: ' + e.errorDescription);
		setData(logs);
	};
	var monitoringDidFail = (e) => {
		logs.push('monitoringDidFail: ' + e.errorDescription);
		setData(logs);
	};
	function setRegionManagerEventListeners(manager) {
		manager.addEventListener('didChangeAuthorization', didChangeAuthorization);
		manager.addEventListener('didRangeBeacons', didRangeBeacons);
		manager.addEventListener('didDetermineState', didDetermineState);
		manager.addEventListener('didStartMonitoring', didStartMonitoring);
		manager.addEventListener('didFail', didFail);
		manager.addEventListener('didFailRanging', didFailRanging);
		manager.addEventListener('rangingBeaconsDidFail', rangingBeaconsDidFail);
		manager.addEventListener('monitoringDidFail', monitoringDidFail);
	}
	win.addEventListener('close', function () {
		if (manager === null) {
			return;
		}
		manager.removeEventListener('didChangeAuthorization', didChangeAuthorization);
		manager.removeEventListener('didRangeBeacons', didRangeBeacons);
		manager.removeEventListener('didDetermineState', didDetermineState);
		manager.removeEventListener('didStartMonitoring', didStartMonitoring);
		manager.removeEventListener('didFail', didFail);
		manager.removeEventListener('didFailRanging', didFailRanging);
		manager.removeEventListener('rangingBeaconsDidFail', rangingBeaconsDidFail);
		manager.removeEventListener('monitoringDidFail', monitoringDidFail);

		initializeManager.removeEventListener('click', initializeManagerClick);
		requestAuth.removeEventListener('click', requestAuthClick);
		startScan.removeEventListener('click', startScanClick);
		stopScan.removeEventListener('click', stopScanClick);
	});
	return win;
}
exports.deviceWin = deviceWin;
