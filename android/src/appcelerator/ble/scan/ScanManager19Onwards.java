/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import java.util.UUID;

@SuppressLint({ "MissingPermission" })
@SuppressWarnings("deprecation")
public class ScanManager19Onwards extends ScanManager implements BluetoothAdapter.LeScanCallback
{

	public static final String LCAT = "ScanManager19Onwards";
	protected ScanManager19Onwards(BluetoothAdapter adapter, IScanDeviceFoundListener listener)
	{
		super(adapter, listener);
	}

	@Override
	protected void doScan(String[] servicesUUIDs)
	{
		if (servicesUUIDs == null || servicesUUIDs.length == 0) {
			adapter.startLeScan(this);
		} else {
			UUID[] uuids = new UUID[servicesUUIDs.length];
			for (int i = 0; i < servicesUUIDs.length; i++) {
				uuids[i] = UUID.fromString(servicesUUIDs[i]);
			}
			boolean isScanStarted = adapter.startLeScan(uuids, this);
			if (!isScanStarted) {
				Log.e(LCAT, "doScan(): failed to start the scan.");
			}
			isScanningInProgress = isScanStarted;
		}
	}

	@Override
	public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes)
	{
		onPeripheralFound(bluetoothDevice, i, bytes);
	}

	@Override
	protected void killScan()
	{
		adapter.stopLeScan(this);
	}
}
