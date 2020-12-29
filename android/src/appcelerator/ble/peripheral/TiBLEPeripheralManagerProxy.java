/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import static android.content.Context.BIND_AUTO_CREATE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import appcelerator.ble.receivers.StateBroadcastReceiver;
import java.util.HashMap;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

@Kroll.proxy
public class TiBLEPeripheralManagerProxy extends KrollProxy
{
	private static final String LCAT = "TiBLEPeripheralManager";
	private final BluetoothAdapter btAdapter;
	private final StateBroadcastReceiver stateReceiver;
	private TiBLEManagePeripheralService bleService;
	private Intent serviceIntent = new Intent(TiApplication.getInstance(), TiBLEManagePeripheralService.class);

	public TiBLEPeripheralManagerProxy()
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateReceiver = new StateBroadcastReceiver(this);
		getActivity().registerReceiver(stateReceiver, intentFilter);
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty(name = "peripheralManagerState")
	public int peripheralManagerState()
	{
		return btAdapter.getState();
	}

	private final ServiceConnection bleServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.d(LCAT, "onServiceConnected(): Service is Binded");
			bleService = ((TiBLEManagePeripheralService.LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			bleService = null;
			Log.d(LCAT, "onServiceDisconnected(): Service is unBinded");
		}
	};

	private void startAndBindService()
	{
		TiApplication.getInstance().startService(serviceIntent);
		Log.d(LCAT, "startAndBindService(): starting the service");
		TiApplication.getInstance().bindService(serviceIntent, bleServiceConnection, BIND_AUTO_CREATE);
		Log.d(LCAT, "startAndBindService(): binding the service ");
	}

	private void stopAndUnbindService()
	{
		if (bleService != null) {
			TiApplication.getInstance().unbindService(bleServiceConnection);
			Log.d(LCAT, "stopAndUnbindService(): service unbinding initiated");
			TiApplication.getInstance().stopService(serviceIntent);
			Log.d(LCAT, "stopAndUnbindService(): service stopping..");
			bleService = null;
		}
	}

	public void cleanup()
	{
		try {
			getActivity().unregisterReceiver(stateReceiver);
		} catch (IllegalArgumentException e) {
			Log.e(LCAT, "cleanup(): Error during unregistering the receiver," + e.getMessage());
		}
	}

	public void bluetoothStateChanged(int state)
	{
		HashMap<String, Integer> dict = new HashMap<>();
		dict.put("state", state);
		fireEvent("didUpdateState", dict);
	}
}
