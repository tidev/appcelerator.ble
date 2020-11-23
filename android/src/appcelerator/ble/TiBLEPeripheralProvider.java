/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothDevice;
import java.util.Hashtable;

public class TiBLEPeripheralProvider
{

	private Hashtable<String, TiBLEPeripheralProxy> peripherals = new Hashtable<>();

	public TiBLEPeripheralProxy[] peripherals()
	{
		return peripherals.values().toArray(new TiBLEPeripheralProxy[0]);
	}

	private boolean hasPeripheral(String address)
	{
		return peripherals.containsKey(address);
	}

	private TiBLEPeripheralProxy getPeripheral(String address)
	{
		return peripherals.get(address);
	}

	private TiBLEPeripheralProxy addPeripheral(BluetoothDevice device, TiBLEPeripheralProxy.IOperationHandler listener)
	{
		TiBLEPeripheralProxy peripheralProxy = new TiBLEPeripheralProxy(device, listener);

		peripherals.put(device.getAddress(), peripheralProxy);

		return peripheralProxy;
	}

	public TiBLEPeripheralProxy checkAddAndGetPeripheralProxy(BluetoothDevice device,
															  TiBLEPeripheralProxy.IOperationHandler listener)
	{
		if (hasPeripheral(device.getAddress())) {
			return getPeripheral(device.getAddress());
		}

		return addPeripheral(device, listener);
	}

	public void removeAllPeripheral()
	{
		peripherals.clear();
	}
}
