/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import org.appcelerator.kroll.KrollDict;

@SuppressLint("LongLogTag")
public class TiBleCentralOperationManager
{

	private static final String LCAT = "TiBleCentralOperationManager";

	private final Context context;
	private final TiBLECentralManagerProxy centralManagerProxy;
	private TiBLEPeripheralProxy peripheralProxy;
	private boolean autoConnect;
	private BluetoothGatt bluetoothGatt;
	private ConnectionState connectionState = ConnectionState.New;

	public TiBleCentralOperationManager(Context context, TiBLECentralManagerProxy centralManagerProxy,
										TiBLEPeripheralProxy peripheralProxy, boolean autoConnect)
	{
		this.context = context;
		this.centralManagerProxy = centralManagerProxy;
		this.peripheralProxy = peripheralProxy;
		this.autoConnect = autoConnect;
	}

	public void initiateConnectionWithPeripheral()
	{
		bluetoothGatt = peripheralProxy.getDevice().connectGatt(context, autoConnect, new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
			{
				super.onConnectionStateChange(gatt, status, newState);
				handleOnConnectionStateChanged(status, newState);
			}

			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
			{
				super.onReadRemoteRssi(gatt, rssi, status);
				handleOnReadRemoteRssi(rssi, status);
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status)
			{
				super.onServicesDiscovered(gatt, status);
				handleOnServicesDiscovered(gatt, status);
			}
		});
		connectionState = ConnectionState.Connecting;
	}

	private void handleOnConnectionStateChanged(int status, int newState)
	{
		switch (newState) {
			case BluetoothProfile.STATE_CONNECTED:
				Log.d(LCAT, "connected to peripheral: name- " + peripheralProxy.name() + ", address- "
								+ peripheralProxy.address());
				connectionState = ConnectionState.Connected;
				KrollDict dict = new KrollDict();
				dict.put("peripheral", peripheralProxy);
				centralManagerProxy.fireEvent("didConnectPeripheral", dict);
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				handleDisconnection(status);
				break;
			default:
				Log.d(LCAT, "handleOnConnectionStateChanged(): central connection state value= " + newState);
		}
	}

	private void handleOnReadRemoteRssi(int rssi, int status)
	{
		Log.d(LCAT, "handleOnReadRemoteRssi(): rssi =  " + rssi
						+ "status = " + (status == BluetoothGatt.GATT_SUCCESS ? "success" : "fail"));
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("RSSI", rssi);
		if (status != BluetoothGatt.GATT_SUCCESS) {
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to read remote rssi");
		}
		peripheralProxy.fireEvent("didReadRSSI", dict);
	}

	private void handleOnServicesDiscovered(BluetoothGatt gatt, int status)
	{
		Log.d(LCAT, "handleOnServicesDiscovered(): discover service operation result = "
						+ (status == BluetoothGatt.GATT_SUCCESS ? "success" : "fail"));

		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);

		if (status == BluetoothGatt.GATT_SUCCESS) {
			peripheralProxy.addServices(gatt.getServices());
		} else {
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to discover services for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
		}

		peripheralProxy.fireEvent("didDiscoverServices", dict);
	}

	public void cancelPeripheralConnection()
	{
		connectionState = ConnectionState.Disconnecting;
		bluetoothGatt.disconnect();
	}

	public boolean isConnected()
	{
		return connectionState == ConnectionState.Connected;
	}

	public void readRSSI()
	{
		bluetoothGatt.readRemoteRssi();
	}

	public void requestConnectionPriority(int priority)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			bluetoothGatt.requestConnectionPriority(priority);
		} else {
			Log.d(
				LCAT,
				"requestConnectionPriority(): This functionality is supported on Android os version LOLLIPOP and onwards.");
		}
	}

	public void discoverServices()
	{
		bluetoothGatt.discoverServices();
	}

	public void discoverIncludedServices(TiBLEServiceProxy serviceProxy)
	{
		// In Android, the all included services have already been discovered as part of the discoverServices operation.
		// so directly fire corresponding result event for it.
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("service", serviceProxy);
		peripheralProxy.fireEvent("didDiscoverIncludedServices", dict);
	}

	public void discoverCharacteristics(TiBLEServiceProxy serviceProxy)
	{
		// In Android, all characteristics have already been discovered as part of the discoverServices operation.
		// so directly fire corresponding result event for it.
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("service", serviceProxy);
		peripheralProxy.fireEvent("didDiscoverCharacteristics", dict);
	}

	public void discoverDescriptorsForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
	{
		// In Android, all descriptors have already been discovered as part of the discoverServices operation.
		// so directly fire corresponding result event for it.
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", characteristicProxy);
		peripheralProxy.fireEvent("didDiscoverDescriptorsForCharacteristics", dict);
	}

	public void handleDisconnection(int status)
	{
		ConnectionState olderState = connectionState;
		connectionState = ConnectionState.Disconnected;
		KrollDict dict = new KrollDict();
		dict.put("peripheral", peripheralProxy);

		if (olderState == ConnectionState.Connected || olderState == ConnectionState.Disconnecting) {
			Log.d(LCAT, "handleDisconnection(): disconnected to peripheral: name- " + peripheralProxy.name()
							+ ", address- " + peripheralProxy.address());
			if (status != BluetoothGatt.GATT_SUCCESS) {
				dict.put("errorCode", status);
				dict.put("errorDescription", "connection disconnected with the peripheral.");
			}
			centralManagerProxy.fireEvent("didDisconnectPeripheral", dict);
		} else {
			Log.d(LCAT, "handleDisconnection(): failed to connect with peripheral: name- " + peripheralProxy.name()
							+ ", address- " + peripheralProxy.address());
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to connect with peripheral.");
			centralManagerProxy.fireEvent("didFailToConnectPeripheral", dict);
		}

		bluetoothGatt.close();
		centralManagerProxy.stopAndUnbindService();
	}

	public enum ConnectionState { New, Connecting, Connected, Disconnecting, Disconnected }
}
