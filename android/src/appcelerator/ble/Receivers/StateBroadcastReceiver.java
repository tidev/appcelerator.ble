/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.Receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.HashMap;
import org.appcelerator.kroll.KrollModule;

public class StateBroadcastReceiver extends BroadcastReceiver
{

	private KrollModule krollModule;
	private final String EVENT_DID_UPDATE_STATE = "didUpdateState";
	private final String EVENT_DID_UPDATE_STATE_KEY = "state";

	public StateBroadcastReceiver(KrollModule krollModule)
	{
		this.krollModule = krollModule;
	}
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			HashMap<String, Integer> dict = new HashMap<>();
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			dict.put(EVENT_DID_UPDATE_STATE_KEY, state);
			krollModule.fireEvent(EVENT_DID_UPDATE_STATE, dict);
		}
	}
}
