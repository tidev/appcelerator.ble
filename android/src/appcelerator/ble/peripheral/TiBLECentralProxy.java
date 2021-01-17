/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothDevice;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy
public class TiBLECentralProxy extends KrollProxy
{
	private BluetoothDevice bluetoothDevice;
	public TiBLECentralProxy(BluetoothDevice bluetoothDevice)
	{
		this.bluetoothDevice = bluetoothDevice;
	}

	@Kroll.getProperty
	public String address()
	{
		return bluetoothDevice.getAddress();
	}

	public BluetoothDevice getBluetoothDevice()
	{
		return bluetoothDevice;
	}
}
