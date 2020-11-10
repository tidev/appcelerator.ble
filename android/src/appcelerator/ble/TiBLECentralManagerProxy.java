/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import appcelerator.ble.receivers.StateBroadcastReceiver;
import appcelerator.ble.scan.ScanManager;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLECentralManagerProxy extends KrollProxy
{

	private final BluetoothAdapter btAdapter;
	private final ScanManager scanManager;
	private final StateBroadcastReceiver stateReceiver;
	private PeripheralProvider peripheralProvider;

	public TiBLECentralManagerProxy()
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		peripheralProvider = new PeripheralProvider();
		scanManager = ScanManager.build(btAdapter, scanListener);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateReceiver = new StateBroadcastReceiver(this);
		getActivity().registerReceiver(stateReceiver, intentFilter);
	}

	private static final String LCAT = "TiBLECentralManager";

	@Kroll.method
	public boolean isAccessFineLocationPermissionGranted()
	{
		return getActivity().getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
																 getActivity().getPackageName())
			== PackageManager.PERMISSION_GRANTED;
	}

	@Kroll.method
	public void requestAccessFineLocationPermission()
	{
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
											  1);
		} else {
			String ACCESS_FINE_LOCATION_ALREADY_GRANTED = "Access fine location permission already been granted";
			Log.d(LCAT, "requestAccessFineLocationPermission(): " + ACCESS_FINE_LOCATION_ALREADY_GRANTED);
		}
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty
	public int getState()
	{
		return btAdapter.getState();
	}

	@Kroll.getProperty(name = "isScanning")
	public boolean isScanning()
	{
		return scanManager.isScanningInProgress;
	}

	@Kroll.method
	public void startScan(@Kroll.argument(optional = true) KrollDict dict)
	{
		peripheralProvider.removeAllPeripheral();

		String[] servicesUUIDs = null;
		if (dict != null && dict.containsKey("services")) {
			servicesUUIDs = dict.getStringArray("services");
		}
		scanManager.startScan(servicesUUIDs);
	}

	@Kroll.method
	public void stopScan()
	{
		scanManager.stopScan();
		peripheralProvider.removeAllPeripheral();
	}

	@Kroll.getProperty
	public TiBLEPeripheralProxy[] peripherals()
	{
		return peripheralProvider.peripherals();
	}

	@Kroll.method
	public TiBLEPeripheralProxy createPeripheral(String address)
	{
		BluetoothDevice bluetoothDevice = btAdapter.getRemoteDevice(address);
		return peripheralProvider.checkAddAndGetPeripheralProxy(bluetoothDevice);
	}

	@Override
	public void onDestroy(Activity activity)
	{
		try {
			getActivity().unregisterReceiver(stateReceiver);
		} catch (IllegalArgumentException e) {
			Log.e(LCAT, "onDestroy(): " + e.getMessage());
		}
		super.onDestroy(activity);
	}

	private ScanManager.IScanDeviceFoundListener scanListener = (bluetoothDevice, i, bytes) ->
	{

		TiBLEPeripheralProxy peripheralProxy = peripheralProvider.checkAddAndGetPeripheralProxy(bluetoothDevice);

		KrollDict dict = new KrollDict();

		dict.put("peripheral", peripheralProxy);
		dict.put("RSSI", i);

		BufferProxy bufferProxy = new BufferProxy();
		bufferProxy.write(0, bytes, 0, bytes.length);
		dict.put("rawAdvertisementData", bufferProxy);

		fireEvent("didDiscoverPeripheral", dict);
	};
}
