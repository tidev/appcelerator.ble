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
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import appcelerator.ble.Receivers.StateBroadcastReceiver;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
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

	//Temp constant for descriptor UUID
	//TODO Address or remove this temp constant in MOD-2689.
	@Kroll.constant
	public static final String MOCK_UUID_FOR_DESCRIPTOR_UT = "4f448481-bf5b-49fb-bb84-794303e3dc33";

	//Temp constant for Characteristic UUID
	//TODO Address or remove this temp constant in MOD-2689.
	@Kroll.constant
	public static final String MOCK_UUID_FOR_CHARACTERISTIC_UT = "3b07719f-d2fc-4d09-82f4-806e07702397";

	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_OFF = BluetoothAdapter.STATE_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_ON = BluetoothAdapter.STATE_ON;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_BROADCAST = BluetoothGattCharacteristic.PROPERTY_BROADCAST;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_EXTENDED_PROPERTIES =
		BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_INDICATE = BluetoothGattCharacteristic.PROPERTY_INDICATE;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_NOTIFY = BluetoothGattCharacteristic.PROPERTY_NOTIFY;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_READ = BluetoothGattCharacteristic.PROPERTY_READ;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_WRITE = BluetoothGattCharacteristic.PROPERTY_WRITE;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_SIGNED_WRITES = BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE;
	@Kroll.constant
	public static final int CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE =
		BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
	@Kroll.constant
	public static final int CHARACTERISTIC_TYPE_WRITE_WITH_RESPONSE = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
	@Kroll.constant
	public static final int CHARACTERISTIC_TYPE_WRITE_WITHOUT_RESPONSE =
		BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
	@Kroll.constant
	public static final int CHARACTERISTIC_PERMISSION_READABLE = BluetoothGattCharacteristic.PERMISSION_READ;
	@Kroll.constant
	public static final int CHARACTERISTIC_PERMISSION_READ_ENCRYPTED =
		BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED;
	@Kroll.constant
	public static final int CHARACTERISTIC_PERMISSION_WRITEABLE = BluetoothGattCharacteristic.PERMISSION_WRITE;
	@Kroll.constant
	public static final int CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED =
		BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED;

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

	//temporary method for descriptor UT.
	//TODO Address or remove this temp method in MOD-2689.
	@Kroll.method
	public TiBLEDescriptorProxy mockDescriptorForUT(KrollDict dict)
	{
		return TiBLEDescriptorProxy.mockDescriptorForUT(dict);
	}

	//temporary method for Characteristic UT.
	//TODO Address or remove this temp method in MOD-2689.
	@Kroll.method
	public TiBLECharacteristicProxy mockCharacteristicForUT(KrollDict dict)
	{
		return TiBLECharacteristicProxy.mockCharacteristicForUT(dict);
	}
}
