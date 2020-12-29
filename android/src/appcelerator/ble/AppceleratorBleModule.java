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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.pm.PackageManager;
import android.os.Build;
import appcelerator.ble.peripheral.TiBLEPeripheralManagerProxy;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import ti.modules.titanium.BufferProxy;

@SuppressLint("MissingPermission")
@Kroll.module(name = "AppceleratorBleModule", id = "appcelerator.ble")
public class AppceleratorBleModule extends KrollModule
{

	private final BluetoothAdapter btAdapter;
	private TiBLECentralManagerProxy centralManagerProxy;
	private TiBLEPeripheralManagerProxy peripheralManagerProxy;

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
	@SuppressLint("InlinedApi")
	@Kroll.constant
	public static final int CONNECTION_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
	@SuppressLint("InlinedApi")
	@Kroll.constant
	public static final int CONNECTION_PRIORITY_BALANCED = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
	@SuppressLint("InlinedApi")
	@Kroll.constant
	public static final int CONNECTION_PRIORITY_LOW_POWER = BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;
	@Kroll.constant
	public static final BufferProxy ENABLE_NOTIFICATION_VALUE =
		new BufferProxy(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	@Kroll.constant
	public static final BufferProxy ENABLE_INDICATION_VALUE =
		new BufferProxy(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
	@Kroll.constant
	public static final BufferProxy DISABLE_NOTIFICATION_VALUE =
		new BufferProxy(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

	public AppceleratorBleModule()
	{
		super();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
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
	public boolean isAdvertisingSupported()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return btAdapter.getBluetoothLeAdvertiser() != null;
		}
		return false;
	}

	@Kroll.method
	public TiBLECentralManagerProxy initCentralManager(@Kroll.argument(optional = true) KrollDict dict)
	{
		return centralManagerProxy = new TiBLECentralManagerProxy();
	}

	@Kroll.method
	public TiBLEPeripheralManagerProxy initPeripheralManager(@Kroll.argument(optional = true) KrollDict dict)
	{
		return peripheralManagerProxy = new TiBLEPeripheralManagerProxy();
	}

	@Override
	public void onDestroy(Activity activity)
	{
		super.onDestroy(activity);
		if (centralManagerProxy != null && activity == TiApplication.getInstance().getRootActivity()) {
			centralManagerProxy.cleanup();
		}
		if (peripheralManagerProxy != null && activity == TiApplication.getInstance().getRootActivity()) {
			peripheralManagerProxy.cleanup();
		}
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
		KrollDict serviceInfo = new KrollDict();
		serviceInfo.put("primary", true);
		serviceInfo.put("uuid", "4b08819f-d2fc-4d09-82f4-806e07702397");

		return TiBLECharacteristicProxy.mockCharacteristicForUT(dict);
	}

	//temporary method for Service UT.
	//TODO: Address or remove this temp method in MOD-2689.
	@Kroll.method
	public TiBLEServiceProxy mockServiceForUT(KrollDict dict)
	{
		return TiBLEServiceProxy.mockServiceForUT(dict);
	}
}
