/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
public class ScanManagerAPI21Onwards extends ScanManager
{

	public static final String LCAT = "ScanManagerAPI21Onwards";
	private final ScanSettings scanSetting;

	protected ScanManagerAPI21Onwards(BluetoothAdapter adapter, IScanDeviceFoundListener listener)
	{
		super(adapter, listener);

		ScanSettings.Builder scanBuilder = new ScanSettings.Builder();
		scanBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
		this.scanSetting = scanBuilder.build();
	}

	@Override
	protected void doScan(String[] servicesUUIDs)
	{

		List<ScanFilter> filterList = null;
		if (servicesUUIDs != null && servicesUUIDs.length != 0) {
			filterList = new ArrayList<>(servicesUUIDs.length);
			for (String id : servicesUUIDs) {
				ScanFilter filter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(id)).build();
				filterList.add(filter);
			}
		}

		adapter.getBluetoothLeScanner().startScan(filterList, scanSetting, scanCallback);
		isScanningInProgress = true;
	}

	@Override
	protected void killScan()
	{
		adapter.getBluetoothLeScanner().stopScan(scanCallback);
	}

	private ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanFailed(int errorCode)
		{
			super.onScanFailed(errorCode);
			String errorMsg;
			switch (errorCode) {
				case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
					errorMsg = "Fails to start scan as the BLE scan is already started by the app.";
					break;
				case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
					errorMsg = "Fails to start scan as app cannot be registered.";
					isScanningInProgress = false;
					break;
				case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
					errorMsg = "Fails to start scan as this feature is not supported.";
					isScanningInProgress = false;
					break;
				case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
					errorMsg = "Fails to start scan due an internal error.";
					isScanningInProgress = false;
					break;
				default:
					errorMsg = "Fails to start the scan.";
					isScanningInProgress = false;
					break;
			}
			Log.e(LCAT, "onScanFailed(): errorMessage - " + errorMsg + ", errorCode - " + errorCode);
		}

		@Override
		public void onScanResult(int callbackType, ScanResult result)
		{
			super.onScanResult(callbackType, result);
			onPeripheralFound(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
		}
	};
}
