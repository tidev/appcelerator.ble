/* eslint-disable no-alert */
/* eslint-disable no-loop-func */
function deviceWin(BLE, beaconUUID, major, minor, beaconId) {
	var logs = [];
	var manager = null;
	var win = Ti.UI.createWindow({
		backgroundColor: 'white',
		title: 'Beacon Advertiser',
		titleAttributes: { color: 'blue' }
	});
	var InitializePeripheralManager = Titanium.UI.createButton({
		top: 40,
		title: 'Initialize Peripheral Manager'
	});
	var InitializePeripheralManagerClick = () => {
		if (manager === null) {
			manager = BLE.initPeripheralManager();
			setPeripheralManagerEventListeners(manager);
		} else {
			Ti.API.info('Peripheral Manager already Initialized');
			alert('Peripheral Manager already Initialized');
		}
	};
	InitializePeripheralManager.addEventListener('click', InitializePeripheralManagerClick);
	win.add(InitializePeripheralManager);

	var startAdvertisingButton = Titanium.UI.createButton({
		top: 90,
		title: 'Start Advertising'
	});
	var startAdvertisingButtonClick = () => {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
			alert('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
		} else {
			if (manager.isAdvertising === true) {
				Ti.API.info('Peripheral Manager is already advertising');
				alert('Peripheral Manager is already advertising');
				return;
			}
			var beaconRegion = BLE.createBeaconRegion({
				uuid: beaconUUID,
				major: major,
				minor: minor,
				identifier: beaconId
			});
			manager.startAdvertisingBeaconRegion({
				beaconRegion: beaconRegion
			});
			logs.push('Beacon uuid: ' + beaconUUID + ' Major: ' + major + ' Minor: ' + minor);
			setData(logs);
		}
	};
	startAdvertisingButton.addEventListener('click', startAdvertisingButtonClick);
	win.add(startAdvertisingButton);

	var stopAdvertisingButton = Titanium.UI.createButton({
		top: 140,
		title: 'Stop Advertising'
	});
	var stopAdvertisingButtonClick = () => {
		if (manager === null) {
			Ti.API.info('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
			alert('Peripheral Manager is Not Initialized. Please click \'Initialize Peripheral Manager\'');
		} else {
			if (manager.isAdvertising === false) {
				Ti.API.info('Peripheral Manager is not advertising');
				alert('Peripheral Manager is not advertising');
				return;
			}
			Ti.API.info('Peripheral Manager has stopped advertising');
			alert('Peripheral Manager has stopped advertising');
			manager.stopAdvertising();
		}
	};
	stopAdvertisingButton.addEventListener('click', stopAdvertisingButtonClick);
	win.add(stopAdvertisingButton);

	var tableView = Titanium.UI.createTableView({
		top: 190,
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

	var didUpdateState = (e) => {
		Ti.API.info('didUpdateState');
		switch (e.state) {
			case BLE.MANAGER_STATE_RESETTING:
				logs.push('Manager state updated to Resetting');
				alert('Resetting');
				break;

			case BLE.MANAGER_STATE_UNSUPPORTED:
				logs.push('Manager state updated to Unsupported');
				alert('Unsupported');
				break;

			case BLE.MANAGER_STATE_UNAUTHORIZED:
				logs.push('Manager state updated to Unauthorized');
				alert('Unauthorized');
				break;

			case BLE.MANAGER_STATE_POWERED_OFF:
				logs.push('Manager state updated to powered Off');
				alert('Peripheral Manager is powered Off');
				break;

			case BLE.MANAGER_STATE_POWERED_ON:
				logs.push('Manager state updated to powered On');
				alert('Peripheral Manager is powered On');
				break;

			case BLE.MANAGER_STATE_UNKNOWN:
			default:
				logs.push('Manager state updated to Unknown');
				alert('Unknown');
				break;
		}
		setData(logs);
	};
	var didStartAdvertising = () => {
		Ti.API.info('Peripheral Manager started advertising');
		logs.push('Peripheral Manager started advertising');
		setData(logs);
	};
	function setPeripheralManagerEventListeners(manager) {
		manager.addEventListener('didUpdateState', didUpdateState);
		manager.addEventListener('didStartAdvertising', didStartAdvertising);
	}
	win.addEventListener('close', function () {
		if (manager === null) {
			return;
		}
		manager.removeEventListener('didUpdateState', didUpdateState);
		manager.removeEventListener('didStartAdvertising', didStartAdvertising);

		InitializePeripheralManager.removeEventListener('click', InitializePeripheralManagerClick);
		startAdvertisingButton.removeEventListener('click', startAdvertisingButtonClick);
		stopAdvertisingButton.removeEventListener('click', stopAdvertisingButtonClick);
	});
	return win;
}
exports.deviceWin = deviceWin;
