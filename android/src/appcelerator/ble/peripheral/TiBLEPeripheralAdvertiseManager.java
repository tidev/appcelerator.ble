/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.os.ParcelUuid;
import androidx.annotation.RequiresApi;
import java.lang.ref.WeakReference;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TiBLEPeripheralAdvertiseManager
{

	private final WeakReference<TiBLEPeripheralManagerProxy> managerProxyRef;
	private BluetoothLeAdvertiser bluetoothLeAdvertiser;
	private static final String DID_START_ADVERTISING_KEY = "didStartAdvertising";
	private static final String LCAT = "TiBLEPeripheralAdvertiseManager";
	private AdvertisingState advertisingState = AdvertisingState.New;

	public TiBLEPeripheralAdvertiseManager(TiBLEPeripheralManagerProxy managerProxy)
	{
		this.managerProxyRef = new WeakReference<>(managerProxy);
	}

	private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
		@Override
		public void onStartSuccess(AdvertiseSettings settingsInEffect)
		{
			super.onStartSuccess(settingsInEffect);
			Log.d(LCAT, "onStartSuccess(): Advertising is started successfully");
			advertisingState = AdvertisingState.Started;
			managerProxyRef.get().fireEvent(DID_START_ADVERTISING_KEY, "advertising is started");
		}

		@Override
		public void onStartFailure(int errorCode)
		{
			super.onStartFailure(errorCode);
			KrollDict dict = new KrollDict();
			dict.put("errorCode", errorCode);
			String errorDescription;
			switch (errorCode) {
				case ADVERTISE_FAILED_ALREADY_STARTED:
					errorDescription = "Cannot start advertising," + advertisingState;
					break;
				case ADVERTISE_FAILED_DATA_TOO_LARGE:
					errorDescription = "Cannot start advertising, Data is too large";
					advertisingState = AdvertisingState.Stopped;
					break;
				case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
					errorDescription = "Cannot start advertising, Feature Unsupported";
					advertisingState = AdvertisingState.Stopped;
					break;
				case ADVERTISE_FAILED_INTERNAL_ERROR:
					errorDescription = "Cannot start advertising, internal error";
					advertisingState = AdvertisingState.Stopped;
					break;
				case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
					errorDescription = "Cannot start advertising, too many advertisers";
					advertisingState = AdvertisingState.Stopped;
					break;
				default:
					errorDescription = "Failed to start Advertising";
					advertisingState = AdvertisingState.Stopped;
					break;
			}
			dict.put("errorDescription", errorDescription);
			managerProxyRef.get().fireEvent(DID_START_ADVERTISING_KEY, dict);
			Log.e(LCAT, "onStartFailure: Cannot start advertising, errorDescription: " + errorDescription
							+ "errorCode: " + errorCode);
		}
	};

	public void startAdvertising(String[] serviceUUIDs, boolean localName)
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothLeAdvertiser = btAdapter.getBluetoothLeAdvertiser();
		if (bluetoothLeAdvertiser == null) {
			Log.e(LCAT, "startAdvertising(): Cannot start Advertising, Bluetooth LE Advertising is not supported");
			return;
		}
		if (advertisingState == AdvertisingState.Starting || advertisingState == AdvertisingState.Started) {
			Log.e(LCAT, "startAdvertising(): Cannot start Advertising, advertising state: " + advertisingState);
			return;
		}
		ParcelUuid[] parcelUuids = new ParcelUuid[serviceUUIDs.length];
		for (int i = 0; i < serviceUUIDs.length; i++) {
			parcelUuids[i] = new ParcelUuid(UUID.fromString(serviceUUIDs[i]));
		}
		AdvertiseData.Builder builder = new AdvertiseData.Builder();
		for (ParcelUuid parcelUuid : parcelUuids) {
			builder.addServiceUuid(parcelUuid);
		}
		AdvertiseData advertiseData = builder.build();
		AdvertiseData scanCallback = new AdvertiseData.Builder().setIncludeDeviceName(localName).build();
		AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
												  .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
												  .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
												  .build();
		bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, scanCallback, advertiseCallback);
		advertisingState = AdvertisingState.Starting;
	}

	public void stopAdvertising()
	{
		try {
			bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
			advertisingState = AdvertisingState.Stopped;
			Log.d(LCAT, "stopAdvertising(): Advertising has stopped");
		} catch (Exception e) {
			Log.e(LCAT, "stopAdvertising(): Error on stopping the advertise" + e.getMessage());
		}
	}

	public boolean isAdvertising()
	{
		return advertisingState == AdvertisingState.Started;
	}

	public enum AdvertisingState { New, Starting, Started, Stopped }
}
