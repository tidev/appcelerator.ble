/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import ti.modules.titanium.BufferProxy;

@SuppressLint("LongLogTag")
public class TiBleCentralOperationManager
{

	private static final String LCAT = "TiBleCentralOperationManager";
	private static final String UUID_CLIENT_CHARACTERISTIC_CONFIGURATION = "00002902-0000-1000-8000-00805f9b34fb";

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

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
			{
				super.onCharacteristicRead(gatt, characteristic, status);
				handleOnCharacteristicRead(characteristic, status);
			}

			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
			{
				super.onCharacteristicChanged(gatt, characteristic);
				handleOnCharacteristicRead(characteristic, BluetoothGatt.GATT_SUCCESS);
			}

			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
											  int status)
			{
				super.onCharacteristicWrite(gatt, characteristic, status);
				handleOnCharacteristicWrite(characteristic, status);
			}

			@Override
			public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
			{
				super.onDescriptorRead(gatt, descriptor, status);
				handleOnDescriptorRead(descriptor, status);
			}

			@Override
			public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
			{
				super.onDescriptorWrite(gatt, descriptor, status);
				handleOnDescriptorWrite(descriptor, status);
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

	private void handleOnCharacteristicRead(BluetoothGattCharacteristic characteristic, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", new TiBLECharacteristicProxy(characteristic));
		if (status == BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnCharacteristicRead(): characteristic- " + characteristic.getUuid().toString()
							+ " read successful.");
			dict.put("value", new BufferProxy(characteristic.getValue()));
		} else {
			Log.d(LCAT, "handleOnCharacteristicRead(): characteristic- " + characteristic.getUuid().toString()
							+ " read failed.");
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to read the characteristic for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
		}

		peripheralProxy.fireEvent("didUpdateValueForCharacteristic", dict);
	}

	private void handleOnCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", new TiBLECharacteristicProxy(characteristic));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnCharacteristicWrite(): characteristic- " + characteristic.getUuid().toString()
							+ " write failed.");
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to write value on characteristic for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
		} else {
			Log.d(LCAT, "handleOnCharacteristicWrite(): characteristic- " + characteristic.getUuid().toString()
							+ " write successful.");
		}

		peripheralProxy.fireEvent("didWriteValueForCharacteristic", dict);
	}

	private void handleOnDescriptorRead(BluetoothGattDescriptor descriptor, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("descriptor", new TiBLEDescriptorProxy(descriptor));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnDescriptorRead(): descriptor- " + descriptor.getUuid().toString() + " read failed.");
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to read the descriptor for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
		} else {
			Log.d(LCAT,
				  "handleOnDescriptorRead(): descriptor- " + descriptor.getUuid().toString() + " read successful.");
		}

		peripheralProxy.fireEvent("didUpdateValueForDescriptor", dict);
	}

	private void handleOnDescriptorWrite(BluetoothGattDescriptor descriptor, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("descriptor", new TiBLEDescriptorProxy(descriptor));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnDescriptorWrite(): descriptor- " + descriptor.getUuid().toString() + " write failed.");
			dict.put("errorCode", status);
			dict.put("errorDescription", "failed to write value on descriptor for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
		} else {
			Log.d(LCAT,
				  "handleOnDescriptorWrite(): descriptor- " + descriptor.getUuid().toString() + " write successful.");
		}

		peripheralProxy.fireEvent("didWriteValueForDescriptor", dict);
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

	public void readValueForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
	{
		boolean isReadInitiated = bluetoothGatt.readCharacteristic(characteristicProxy.getCharacteristic());
		Log.d(LCAT, "readValueForCharacteristic(): characteristic- " + characteristicProxy.uuid()
						+ " read initiation status- ." + isReadInitiated);
		if (!isReadInitiated) {
			KrollDict dict = new KrollDict();
			dict.put("sourcePeripheral", peripheralProxy);
			dict.put("characteristic", characteristicProxy);
			dict.put("errorCode", BluetoothGatt.GATT_FAILURE);
			dict.put("errorDescription", "failed to initiate reading characteristic for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
			peripheralProxy.fireEvent("didUpdateValueForCharacteristic", dict);
		}
	}

	public void writeValueForCharacteristic(TiBLECharacteristicProxy charProxy, byte[] buffer, int writeType)
	{
		charProxy.getCharacteristic().setWriteType(writeType);
		charProxy.getCharacteristic().setValue(buffer);
		boolean isWritingInitiated = bluetoothGatt.writeCharacteristic(charProxy.getCharacteristic());
		Log.d(LCAT, "writeValueForCharacteristic(): characteristic- " + charProxy.uuid() + " write initiation status- ."
						+ isWritingInitiated);
		if (!isWritingInitiated) {
			KrollDict dict = new KrollDict();
			dict.put("sourcePeripheral", peripheralProxy);
			dict.put("characteristic", charProxy);
			dict.put("errorCode", BluetoothGatt.GATT_FAILURE);
			dict.put("errorDescription",
					 "failed to initiate writing value on characteristic for peripheral name/address- "
						 + peripheralProxy.name() + " / " + peripheralProxy.address());
			peripheralProxy.fireEvent("didWriteValueForCharacteristic", dict);
		}
	}

	public void readValueForDescriptor(TiBLEDescriptorProxy descriptorProxy)
	{
		boolean isReadInitiated = bluetoothGatt.readDescriptor(descriptorProxy.getDescriptor());
		Log.d(LCAT, "readValueForDescriptor(): descriptor- " + descriptorProxy.uuid() + " read initiation status- ."
						+ isReadInitiated);
		if (!isReadInitiated) {
			KrollDict dict = new KrollDict();
			dict.put("sourcePeripheral", peripheralProxy);
			dict.put("descriptor", descriptorProxy);
			dict.put("errorCode", BluetoothGatt.GATT_FAILURE);
			dict.put("errorDescription", "failed to initiate reading value for descriptor for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
			peripheralProxy.fireEvent("didUpdateValueForDescriptor", dict);
		}
	}

	public void writeValueForDescriptor(TiBLEDescriptorProxy descriptorProxy, byte[] buffer)
	{
		BluetoothGattDescriptor descriptor = descriptorProxy.getDescriptor();
		descriptor.setValue(buffer);
		boolean isWritingInitiated = bluetoothGatt.writeDescriptor(descriptor);
		Log.d(LCAT, "writeValueForDescriptor(): descriptor- " + descriptorProxy.uuid() + " write initiation status- ."
						+ isWritingInitiated);
		if (!isWritingInitiated) {
			KrollDict dict = new KrollDict();
			dict.put("sourcePeripheral", peripheralProxy);
			dict.put("descriptor", descriptorProxy);
			dict.put("errorCode", BluetoothGatt.GATT_FAILURE);
			dict.put("errorDescription", "failed to initiate writing value on descriptor for peripheral name/address- "
											 + peripheralProxy.name() + " / " + peripheralProxy.address());
			peripheralProxy.fireEvent("didWriteValueForDescriptor", dict);
		}
	}

	public void subscribeToCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
										  BufferProxy enableValue)
	{
		int properties = charProxy.getCharacteristic().getProperties();
		if ((properties & PROPERTY_NOTIFY) <= 0 && (properties & PROPERTY_INDICATE) <= 0) {
			String errorDescription = String.format(
				"cannot subscribe as characteristic- %s does not have notify/indicate property.", charProxy.uuid());
			Log.e(LCAT, "subscribeToCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, true);
			return;
		}

		String descUuid = descriptorUUID != null && !descriptorUUID.isEmpty()
							  ? descriptorUUID
							  : UUID_CLIENT_CHARACTERISTIC_CONFIGURATION;
		if (charProxy.getCharacteristic().getDescriptor(UUID.fromString(descUuid)) == null) {
			String errorDescription = String.format("cannot subscribe as CCC descriptor- %s not found", descUuid);
			Log.e(LCAT, "subscribeToCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, true);
			return;
		}

		boolean setCharNotificationSuccessful =
			bluetoothGatt.setCharacteristicNotification(charProxy.getCharacteristic(), true);
		if (!setCharNotificationSuccessful) {
			String errorDescription = String.format(
				"cannot subscribe as setCharacteristicNotification for characteristic- %s failed.", charProxy.uuid());
			Log.e(LCAT, "subscribeToCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, true);
			return;
		}

		if (descriptorUUID != null && !descriptorUUID.isEmpty()) {
			BluetoothGattDescriptor descriptor =
				charProxy.getCharacteristic().getDescriptor(UUID.fromString(descriptorUUID));
			descriptor.setValue(enableValue.getBuffer());
			boolean isWrite = bluetoothGatt.writeDescriptor(descriptor);
			if (!isWrite) {
				String errorDescription =
					String.format("cannot subscribe as writeDescriptor failed for descriptor- %s .", descriptorUUID);
				Log.e(LCAT, "subscribeToCharacteristic(): " + errorDescription);
				fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, true);
				return;
			}
		}
		Log.d(LCAT, "subscribeToCharacteristic(): subscribe successful");

		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", charProxy);
		dict.put("isSubscribed", true);
		peripheralProxy.fireEvent("didUpdateNotificationStateForCharacteristics", dict);
	}

	public void unsubscribeFromCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
											  BufferProxy disableValue)
	{
		int properties = charProxy.getCharacteristic().getProperties();
		if ((properties & PROPERTY_NOTIFY) <= 0 && (properties & PROPERTY_INDICATE) <= 0) {
			String errorDescription = String.format(
				"cannot unsubscribe as Characteristic- %s does not have notify or indicate property", charProxy.uuid());
			Log.e(LCAT, "unsubscribeFromCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, false);
			return;
		}

		String descUuid = descriptorUUID != null && !descriptorUUID.isEmpty()
							  ? descriptorUUID
							  : UUID_CLIENT_CHARACTERISTIC_CONFIGURATION;
		if (charProxy.getCharacteristic().getDescriptor(UUID.fromString(descUuid)) == null) {
			String errorDescription = String.format("cannot unsubscribe as CCC descriptor- %s not found", descUuid);
			Log.e(LCAT, "unsubscribeFromCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, false);
			return;
		}

		boolean setCharNotificationSuccessful =
			bluetoothGatt.setCharacteristicNotification(charProxy.getCharacteristic(), false);
		if (!setCharNotificationSuccessful) {
			String errorDescription = String.format(
				"cannot unsubscribe as setCharacteristicNotification for characteristic- %s failed.", charProxy.uuid());
			Log.e(LCAT, "unsubscribeFromCharacteristic(): " + errorDescription);
			fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, false);
			return;
		}

		if (descriptorUUID != null && !descriptorUUID.isEmpty()) {
			BluetoothGattDescriptor descriptor =
				charProxy.getCharacteristic().getDescriptor(UUID.fromString(descriptorUUID));
			descriptor.setValue(disableValue.getBuffer());
			boolean isWrite = bluetoothGatt.writeDescriptor(descriptor);
			if (!isWrite) {
				String errorDescription =
					String.format("cannot unsubscribe as writeDescriptor failed for descriptor- %s .", descriptorUUID);
				Log.e(LCAT, "unsubscribeFromCharacteristic(): " + errorDescription);
				fireFailedUpdateNotificationStateEvent(charProxy, errorDescription, false);
				return;
			}
		}
		Log.d(LCAT, "unsubscribeToCharacteristic(): unsubscribe successful");

		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", charProxy);
		dict.put("isSubscribed", false);
		peripheralProxy.fireEvent("didUpdateNotificationStateForCharacteristics", dict);
	}

	private void fireFailedUpdateNotificationStateEvent(TiBLECharacteristicProxy charProxy, String errorDescription,
														boolean isSubscribed)
	{
		KrollDict dict = new KrollDict();
		dict.put("sourcePeripheral", peripheralProxy);
		dict.put("characteristic", charProxy);
		dict.put("isSubscribed", isSubscribed);
		dict.put("errorCode", BluetoothGatt.GATT_FAILURE);
		dict.put("errorDescription", errorDescription);
		peripheralProxy.fireEvent("didUpdateNotificationStateForCharacteristics", dict);
	}

	public enum ConnectionState { New, Connecting, Connected, Disconnecting, Disconnected }
}
