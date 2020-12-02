/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

public abstract class ScanManager
{

	private static final String LCAT = "ScanManager";

	protected final BluetoothAdapter adapter;
	protected final IScanDeviceFoundListener listener;
	public boolean isScanningInProgress;

	public ScanManager(BluetoothAdapter adapter, IScanDeviceFoundListener listener)
	{
		this.adapter = adapter;
		this.listener = listener;
	}

	public void startScan(String[] servicesUUIDs)
	{

		if (isScanningInProgress) {
			Log.d(LCAT, "startScan(): Scanning already in progress. So, Cannot start the scan again.");
			return;
		}

		doScan(servicesUUIDs);
	}

	public void stopScan()
	{

		if (!isScanningInProgress) {
			Log.d(LCAT, "stopScan(): Scanning not in progress. So, Cannot stop any scan.");
			return;
		}

		isScanningInProgress = false;
		killScan();
	}

	public void onPeripheralFound(BluetoothDevice bluetoothDevice, int i, byte[] bytes)
	{
		listener.onScanDeviceFound(bluetoothDevice, i, bytes);
	}

	public static ScanManager build(BluetoothAdapter btAdapter, IScanDeviceFoundListener listener)
	{
		ScanManager scanManager;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			scanManager = new ScanManagerAPI21Onwards(btAdapter, listener);
		} else {
			scanManager = new ScanManager19Onwards(btAdapter, listener);
		}
		return scanManager;
	}

	abstract protected void doScan(String[] servicesUUIDs);
	abstract protected void killScan();

	public interface IScanDeviceFoundListener {
		void onScanDeviceFound(BluetoothDevice bluetoothDevice, int i, byte[] bytes);
	}
}
