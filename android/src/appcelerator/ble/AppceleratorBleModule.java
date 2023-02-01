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
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import appcelerator.ble.peripheral.TiBLEMutableCharacteristicProxy;
import appcelerator.ble.peripheral.TiBLEPeripheralManagerProxy;
import java.util.ArrayList;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollPromise;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import ti.modules.titanium.BufferProxy;

@SuppressLint("MissingPermission")
@Kroll.module(name = "AppceleratorBleModule", id = "appcelerator.ble")
public class AppceleratorBleModule extends KrollModule
{
	private BluetoothAdapter btAdapter;
	private TiBLECentralManagerProxy centralManagerProxy;
	private TiBLEPeripheralManagerProxy peripheralManagerProxy;

	//constant for Characteristic UUID for UT.
	@Kroll.constant
	public static final String MOCK_UUID_FOR_CHARACTERISTIC_UT = "3b07719f-d2fc-4d09-82f4-806e07702397";

	//Constant for characteristic uuid to enable notifications
	@Kroll.constant
	public static final String CBUUID_CLIENT_CHARACTERISTIC_CONFIGURATION_STRING =
		"00002902-0000-1000-8000-00805f9b34fb";

	// Constants for local bluetooth states
	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_OFF = BluetoothAdapter.STATE_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_POWERED_ON = BluetoothAdapter.STATE_ON;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
	@Kroll.constant
	public static final int MANAGER_STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;

	//Constants for BLE Characteristics properties
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

	//Constants for BLE Characteristics permissions.
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

	//Constants for the BLE connection parameter
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
	public static final String CBUUID_L2CAPPSM_CHARACTERISTIC_STRING = "ABDD3056-28FA-441D-A470-55A75A52553A";

	//Constants for BLE Descriptors permissions
	@Kroll.constant
	public static final int DESCRIPTOR_PERMISSION_READ = BluetoothGattDescriptor.PERMISSION_READ;
	@Kroll.constant
	public static final int DESCRIPTOR_PERMISSION_READ_ENCRYPTED = BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED;
	@Kroll.constant
	public static final int DESCRIPTOR_PERMISSION_WRITE = BluetoothGattDescriptor.PERMISSION_WRITE;
	@Kroll.constant
	public static final int DESCRIPTOR_PERMISSION_WRITE_ENCRYPTED = BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED;

	//Constants for the BLE operations status.
	@Kroll.constant
	public static final int ATT_SUCCESS = BluetoothGatt.GATT_SUCCESS;
	@Kroll.constant
	public static final int ATT_FAILURE = BluetoothGatt.GATT_FAILURE;
	@Kroll.constant
	public static final int ATT_READ_NOT_PERMITTED_ERROR = BluetoothGatt.GATT_READ_NOT_PERMITTED;
	@Kroll.constant
	public static final int ATT_WRITE_NOT_PERMITTED_ERROR = BluetoothGatt.GATT_WRITE_NOT_PERMITTED;
	@Kroll.constant
	public static final int ATT_INSUFFICIENT_AUTHENTICATION_ERROR = BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION;
	@Kroll.constant
	public static final int ATT_INVALID_OFFSET_ERROR = BluetoothGatt.GATT_INVALID_OFFSET;
	@Kroll.constant
	public static final int ATT_INVALID_ATTRIBUTE_VALUE_LENGTH_ERROR = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
	@Kroll.constant
	public static final int ATT_REQUEST_NOT_SUPPORTED_ERROR = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
	@Kroll.constant
	public static final int ATT_INSUFFICIENT_ENCRYPTION_ERROR = BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION;

	@Kroll.constant
	public static final int SCAN_MODE_LOW_POWER = ScanSettings.SCAN_MODE_LOW_POWER;
	public static final int SCAN_MODE_LOW_LATENCY = ScanSettings.SCAN_MODE_LOW_LATENCY;
	public static final int SCAN_MODE_BALANCED = ScanSettings.SCAN_MODE_BALANCED;
	public static final int SCAN_MODE_OPPORTUNISTIC = ScanSettings.SCAN_MODE_OPPORTUNISTIC;



	public AppceleratorBleModule()
	{
		super();

		// Fetch bluetooth adapter.
		final Context context = TiApplication.getInstance();
		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager != null) {
			this.btAdapter = bluetoothManager.getAdapter();
		}

		// Release resources when the JS runtime is about to terminate.
		KrollRuntime.addOnDisposingListener(new KrollRuntime.OnDisposingListener() {
			@Override
			public void onDisposing(KrollRuntime runtime)
			{
				KrollRuntime.removeOnDisposingListener(this);
				if (centralManagerProxy != null) {
					centralManagerProxy.cleanup();
					centralManagerProxy = null;
				}
				if (peripheralManagerProxy != null) {
					peripheralManagerProxy.cleanup();
					peripheralManagerProxy = null;
				}
			}
		});
	}

	@Override
	public String getApiName()
	{
		return "appcelerator.ble";
	}

	@Kroll.getProperty(name = "ENABLE_NOTIFICATION_VALUE")
	public BufferProxy getEnableNotificationValue()
	{
		return new BufferProxy(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	}

	@Kroll.getProperty(name = "ENABLE_INDICATION_VALUE")
	public BufferProxy getEnableIndicationValue()
	{
		return new BufferProxy(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
	}

	@Kroll.getProperty(name = "DISABLE_NOTIFICATION_VALUE")
	public BufferProxy getDisableNotificationValue()
	{
		return new BufferProxy(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	}

	@Kroll.method
	public boolean isBluetoothAndBluetoothAdminPermissionsGranted()
	{
		// Create the permission list.
		ArrayList<String> permissionList = new ArrayList<>(3);
		if (Build.VERSION.SDK_INT >= 31) {
			permissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
			permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
			permissionList.add(Manifest.permission.BLUETOOTH_SCAN);
		} else {
			permissionList.add(Manifest.permission.BLUETOOTH);
			permissionList.add(Manifest.permission.BLUETOOTH_ADMIN);
		}

		// Determine if permissions are granted.
		// Note: On OS versions older than Android 6.0, check if permission is defined in manifest.
		TiApplication context = TiApplication.getInstance();
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();
		for (String permissionName : permissionList) {
			if (Build.VERSION.SDK_INT >= 23) {
				if (context.checkSelfPermission(permissionName) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			} else if (packageManager.checkPermission(permissionName, packageName)
					   != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	@Kroll.method
	public KrollPromise<KrollDict>
	requestBluetoothPermissions(@Kroll.argument(optional = true) KrollFunction permissionCallback)
	{
		final KrollObject krollObject = getKrollObject();
		return KrollPromise.create((promise) -> {
			// Do not continue if we already have permission.
			if (isBluetoothAndBluetoothAdminPermissionsGranted()) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(0, null);
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.resolve(responseData);
				return;
			} else if (Build.VERSION.SDK_INT < 31) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(-1, "Bluetooth permissions not defined in manifest.");
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.reject(new Throwable(responseData.getString(TiC.EVENT_PROPERTY_ERROR)));
				return;
			}

			// Do not continue if there is no activity to host the request dialog.
			Activity activity = TiApplication.getInstance().getCurrentActivity();
			if (activity == null) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(-1, "There are no activities to host the permission request dialog.");
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.reject(new Throwable(responseData.getString(TiC.EVENT_PROPERTY_ERROR)));
				return;
			}

			// Show dialog requesting permission.
			String[] permissionsArray = { Manifest.permission.BLUETOOTH_ADVERTISE,
										  Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN };
			TiBaseActivity.registerPermissionRequestCallback(TiC.PERMISSION_CODE_LOCATION, permissionCallback,
															 krollObject, promise);
			activity.requestPermissions(permissionsArray, TiC.PERMISSION_CODE_LOCATION);
		});
	}

	@Kroll.method
	public boolean isSupported()
	{
		return TiApplication.getInstance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	@Kroll.method
	public boolean isEnabled()
	{
		if (btAdapter == null) {
			return false;
		}
		return btAdapter.isEnabled();
	}

	@Kroll.method
	public boolean enable()
	{
		if (btAdapter == null) {
			return false;
		}
		return btAdapter.enable();
	}

	@Kroll.method
	public boolean disable()
	{
		if (btAdapter == null) {
			return false;
		}
		return btAdapter.disable();
	}

	@Kroll.method
	public boolean isAdvertisingSupported()
	{
		if (btAdapter == null) {
			return false;
		}
		return btAdapter.getBluetoothLeAdvertiser() != null;
	}

	@Kroll.method
	public TiBLECentralManagerProxy initCentralManager(@Kroll.argument(optional = true) KrollDict dict)
	{
		return centralManagerProxy = new TiBLECentralManagerProxy(dict);
	}

	@Kroll.method
	public TiBLEPeripheralManagerProxy initPeripheralManager(@Kroll.argument(optional = true) KrollDict dict)
	{
		return peripheralManagerProxy = new TiBLEPeripheralManagerProxy();
	}

	@Kroll.method
	public TiBLEMutableCharacteristicProxy createMutableCharacteristic(KrollDict dict)
	{
		return TiBLEMutableCharacteristicProxy.createMutableCharacteristicProxy(dict);
	}

	@Kroll.method
	public TiBLEDescriptorProxy createDescriptor(KrollDict dict)
	{
		return TiBLEDescriptorProxy.createDescriptorProxy(dict);
	}
}
