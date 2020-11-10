/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy
public class TiBLEPeripheralProxy extends KrollProxy
{
	private final BluetoothDevice device;

	public TiBLEPeripheralProxy(BluetoothDevice bluetoothDevice)
	{
		this.device = bluetoothDevice;
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty
	public String name()
	{
		return device.getName();
	}

	@Kroll.getProperty
	public String address()
	{
		return device.getAddress();
	}
}
