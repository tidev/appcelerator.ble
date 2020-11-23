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

public class StateBroadcastReceiver extends BroadcastReceiver
{

	private TiBLECentralManagerProxy proxy;

	public StateBroadcastReceiver(TiBLECentralManagerProxy proxy)
	{
		this.proxy = proxy;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			proxy.bluetoothStateChanged(state);
		}
	}
}
