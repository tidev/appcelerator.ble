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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import appcelerator.ble.Receivers.StateBroadcastReceiver;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@SuppressLint("MissingPermission")
@Kroll.module(name = "AppceleratorBleModule", id = "appcelerator.ble")
public class AppceleratorBleModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "BluetoothLowEnergyModule";
	private final BluetoothAdapter btAdapter;
	private StateBroadcastReceiver stateReceiver;
	private final String ACCESS_FINE_LOCATION_ALREADY_GRANTED = "Access fine location permission already been granted";

	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_OFF = BluetoothAdapter.STATE_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_ON = BluetoothAdapter.STATE_ON;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;

	public AppceleratorBleModule()
	{
		super();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateReceiver = new StateBroadcastReceiver(this);
		getActivity().registerReceiver(stateReceiver, intentFilter);
	}

	@Override
	public String getApiName()
	{
		return "appcelerator.ble";
	}

	@Kroll.method
	public boolean isBluetoothAndBluetoothAdminPermissionsGranted()
	{
		return getActivity().getPackageManager().checkPermission(Manifest.permission.BLUETOOTH,
																 getActivity().getPackageName())
			== PackageManager.PERMISSION_GRANTED
			&& getActivity().getPackageManager().checkPermission(Manifest.permission.BLUETOOTH_ADMIN,
																 getActivity().getPackageName())
				   == PackageManager.PERMISSION_GRANTED;
	}

	@Kroll.method
	public boolean isSupported()
	{
		return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	@Kroll.method
	public boolean isEnabled()
	{
		return btAdapter.isEnabled();
	}

	@Kroll.getProperty
	@Kroll.method
	public int getState()
	{
		return btAdapter.getState();
	}

	@Kroll.method
	public boolean enable()
	{
		return btAdapter.enable();
	}

	@Kroll.method
	public boolean disable()
	{
		return btAdapter.disable();
	}

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
			Log.d(LCAT, "requestAccessFineLocationPermission(): " + ACCESS_FINE_LOCATION_ALREADY_GRANTED);
		}
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
}
