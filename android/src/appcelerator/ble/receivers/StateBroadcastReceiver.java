/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import appcelerator.ble.TiBLECentralManagerProxy;
import appcelerator.ble.peripheral.TiBLEPeripheralManagerProxy;

public class StateBroadcastReceiver extends BroadcastReceiver
{

	private TiBLECentralManagerProxy centralManagerProxy;
	private TiBLEPeripheralManagerProxy peripheralManagerProxy;

	public StateBroadcastReceiver(TiBLECentralManagerProxy centralManagerProxy)
	{
		this.centralManagerProxy = centralManagerProxy;
	}

	public StateBroadcastReceiver(TiBLEPeripheralManagerProxy peripheralManagerProxy)
	{
		this.peripheralManagerProxy = peripheralManagerProxy;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			if (centralManagerProxy != null) {
				centralManagerProxy.bluetoothStateChanged(state);
			}
			if (peripheralManagerProxy != null) {
				peripheralManagerProxy.bluetoothStateChanged(state);
			}
		}
	}
}
