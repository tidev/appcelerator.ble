/*
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

import androidx.annotation.RequiresApi;

import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import ti.modules.titanium.BufferProxy;

@SuppressLint({"LongLogTag", "MissingPermission"})
public class TiBleCentralOperationManager
{

	private static final String LCAT = "TiBleCentralOperationManager";
	private static final String UUID_CLIENT_CHARACTERISTIC_CONFIGURATION = "00002902-0000-1000-8000-00805f9b34fb";
	private final Context context;
	private final TiBLECentralManagerProxy centralManagerProxy;
	private final TiBLEPeripheralProxy peripheralProxy;
	private final boolean autoConnect;
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

			@Override
			public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
				super.onMtuChanged(gatt, mtu, status);
				handleOnMtuChanged(mtu, status);
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
				dict.put(KeysConstants.peripheral.name(), peripheralProxy);
				centralManagerProxy.fireEvent(KeysConstants.didConnectPeripheral.name(), dict);
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
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.rssi.name(), rssi);
		if (status != BluetoothGatt.GATT_SUCCESS) {
			String errorMessage = "failed to read remote rssi";
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		}
		peripheralProxy.fireEvent(KeysConstants.didReadRSSI.name(), dict);
	}

	private void handleOnServicesDiscovered(BluetoothGatt gatt, int status)
	{
		Log.d(LCAT, "handleOnServicesDiscovered(): discover service operation result = "
						+ (status == BluetoothGatt.GATT_SUCCESS ? "success" : "fail"));

		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);

		if (status == BluetoothGatt.GATT_SUCCESS) {
			peripheralProxy.addServices(gatt.getServices());
		} else {
			String errorMessage = "failed to discover services for peripheral name/address- " + peripheralProxy.name()
								  + " / " + peripheralProxy.address();
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		}

		peripheralProxy.fireEvent(KeysConstants.didDiscoverServices.name(), dict);
	}

	private void handleOnCharacteristicRead(BluetoothGattCharacteristic characteristic, int status)
	{
		TiBLECharacteristicProxy characteristicProxy = new TiBLECharacteristicProxy(characteristic);
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), characteristicProxy);
		if (status == BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnCharacteristicRead(): characteristic- " + characteristic.getUuid().toString()
							+ " read successful.");
			dict.put(KeysConstants.value.name(), characteristicProxy.value());
		} else {
			Log.d(LCAT, "handleOnCharacteristicRead(): characteristic- " + characteristic.getUuid().toString()
							+ " read failed.");
			String errorMessage = "failed to read the characteristic for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		}

		peripheralProxy.fireEvent(KeysConstants.didUpdateValueForCharacteristic.name(), dict);
	}

	private void handleOnCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), new TiBLECharacteristicProxy(characteristic));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnCharacteristicWrite(): characteristic- " + characteristic.getUuid().toString()
							+ " write failed.");
			String errorMessage = "failed to write value on characteristic for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		} else {
			Log.d(LCAT, "handleOnCharacteristicWrite(): characteristic- " + characteristic.getUuid().toString()
							+ " write successful.");
		}

		peripheralProxy.fireEvent(KeysConstants.didWriteValueForCharacteristic.name(), dict);
	}

	private void handleOnDescriptorRead(BluetoothGattDescriptor descriptor, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.descriptor.name(), new TiBLEDescriptorProxy(descriptor));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnDescriptorRead(): descriptor- " + descriptor.getUuid().toString() + " read failed.");
			String errorMessage = "failed to read the descriptor for peripheral name/address- " + peripheralProxy.name()
								  + " / " + peripheralProxy.address();
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		} else {
			Log.d(LCAT,
				  "handleOnDescriptorRead(): descriptor- " + descriptor.getUuid().toString() + " read successful.");
		}

		peripheralProxy.fireEvent(KeysConstants.didUpdateValueForDescriptor.name(), dict);
	}

	private void handleOnDescriptorWrite(BluetoothGattDescriptor descriptor, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.descriptor.name(), new TiBLEDescriptorProxy(descriptor));
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(LCAT, "handleOnDescriptorWrite(): descriptor- " + descriptor.getUuid().toString() + " write failed.");
			String errorMessage = "failed to write value on descriptor for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
		} else {
			Log.d(LCAT,
				  "handleOnDescriptorWrite(): descriptor- " + descriptor.getUuid().toString() + " write successful.");
		}

		peripheralProxy.fireEvent(KeysConstants.didWriteValueForDescriptor.name(), dict);
	}

	private void handleOnMtuChanged(int mtu, int status)
	{
		KrollDict dict = new KrollDict();
		dict.put("mtu", mtu);
		peripheralProxy.fireEvent("mtuChanged", dict);
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

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void requestMtu(int value)
	{
		bluetoothGatt.requestMtu(value);
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
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.service.name(), serviceProxy);
		peripheralProxy.fireEvent(KeysConstants.didDiscoverIncludedServices.name(), dict);
	}

	public void discoverCharacteristics(TiBLEServiceProxy serviceProxy)
	{
		// In Android, all characteristics have already been discovered as part of the discoverServices operation.
		// so directly fire corresponding result event for it.
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.service.name(), serviceProxy);
		peripheralProxy.fireEvent(KeysConstants.didDiscoverCharacteristics.name(), dict);
	}

	public void discoverDescriptorsForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
	{
		// In Android, all descriptors have already been discovered as part of the discoverServices operation.
		// so directly fire corresponding result event for it.
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), characteristicProxy);
		peripheralProxy.fireEvent(KeysConstants.didDiscoverDescriptorsForCharacteristics.name(), dict);
	}

	public void handleDisconnection(int status)
	{
		ConnectionState olderState = connectionState;
		connectionState = ConnectionState.Disconnected;
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.peripheral.name(), peripheralProxy);

		if (olderState == ConnectionState.Connected || olderState == ConnectionState.Disconnecting) {
			Log.d(LCAT, "handleDisconnection(): disconnected to peripheral: name- " + peripheralProxy.name()
							+ ", address- " + peripheralProxy.address());
			if (status != BluetoothGatt.GATT_SUCCESS) {
				String errorMessage = "connection disconnected with the peripheral.";
				dict.put(KeysConstants.errorCode.name(), status);
				dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
			}
			centralManagerProxy.fireEvent(KeysConstants.didDisconnectPeripheral.name(), dict);
		} else {
			Log.d(LCAT, "handleDisconnection(): failed to connect with peripheral: name- " + peripheralProxy.name()
							+ ", address- " + peripheralProxy.address());
			String errorMessage = "failed to connect with peripheral.";
			dict.put(KeysConstants.errorCode.name(), status);
			dict.put(KeysConstants.errorDescription.name(), getErrorDescriptionMessage(status, errorMessage));
			centralManagerProxy.fireEvent(KeysConstants.didFailToConnectPeripheral.name(), dict);
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
			String errorMessage = "failed to initiate reading characteristic for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
			dict.put(KeysConstants.characteristic.name(), characteristicProxy);
			dict.put(KeysConstants.errorCode.name(), BluetoothGatt.GATT_FAILURE);
			dict.put(KeysConstants.errorDescription.name(),
					 getErrorDescriptionMessage(BluetoothGatt.GATT_FAILURE, errorMessage));
			peripheralProxy.fireEvent(KeysConstants.didUpdateValueForCharacteristic.name(), dict);
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
			String errorMessage = "failed to initiate writing value on characteristic for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
			dict.put(KeysConstants.characteristic.name(), charProxy);
			dict.put(KeysConstants.errorCode.name(), BluetoothGatt.GATT_FAILURE);
			dict.put(KeysConstants.errorDescription.name(),
					 getErrorDescriptionMessage(BluetoothGatt.GATT_FAILURE, errorMessage));
			peripheralProxy.fireEvent(KeysConstants.didWriteValueForCharacteristic.name(), dict);
		}
	}

	public void readValueForDescriptor(TiBLEDescriptorProxy descriptorProxy)
	{
		boolean isReadInitiated = bluetoothGatt.readDescriptor(descriptorProxy.getDescriptor());
		Log.d(LCAT, "readValueForDescriptor(): descriptor- " + descriptorProxy.uuid() + " read initiation status- ."
						+ isReadInitiated);
		if (!isReadInitiated) {
			KrollDict dict = new KrollDict();
			String errorMessage = "failed to initiate reading value on descriptor for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
			dict.put(KeysConstants.descriptor.name(), descriptorProxy);
			dict.put(KeysConstants.errorCode.name(), BluetoothGatt.GATT_FAILURE);
			dict.put(KeysConstants.errorDescription.name(),
					 getErrorDescriptionMessage(BluetoothGatt.GATT_FAILURE, errorMessage));
			peripheralProxy.fireEvent(KeysConstants.didUpdateValueForDescriptor.name(), dict);
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
			String errorMessage = "failed to initiate writing value on descriptor for peripheral name/address- "
								  + peripheralProxy.name() + " / " + peripheralProxy.address();
			dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
			dict.put(KeysConstants.descriptor.name(), descriptorProxy);
			dict.put(KeysConstants.errorCode.name(), BluetoothGatt.GATT_FAILURE);
			dict.put(KeysConstants.errorDescription.name(),
					 getErrorDescriptionMessage(BluetoothGatt.GATT_FAILURE, errorMessage));
			peripheralProxy.fireEvent(KeysConstants.didWriteValueForDescriptor.name(), dict);
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
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), charProxy);
		dict.put(KeysConstants.isSubscribed.name(), true);
		peripheralProxy.fireEvent(KeysConstants.didUpdateNotificationStateForCharacteristics.name(), dict);
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
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), charProxy);
		dict.put(KeysConstants.isSubscribed.name(), false);
		peripheralProxy.fireEvent(KeysConstants.didUpdateNotificationStateForCharacteristics.name(), dict);
	}

	private void fireFailedUpdateNotificationStateEvent(TiBLECharacteristicProxy charProxy, String errorDescription,
														boolean isSubscribed)
	{
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.sourcePeripheral.name(), peripheralProxy);
		dict.put(KeysConstants.characteristic.name(), charProxy);
		dict.put(KeysConstants.isSubscribed.name(), isSubscribed);
		dict.put(KeysConstants.errorCode.name(), BluetoothGatt.GATT_FAILURE);
		dict.put(KeysConstants.errorDescription.name(),
				 getErrorDescriptionMessage(BluetoothGatt.GATT_FAILURE, errorDescription));
		peripheralProxy.fireEvent(KeysConstants.didUpdateNotificationStateForCharacteristics.name(), dict);
	}

	private String getErrorDescriptionMessage(int status, String message)
	{
		String errorMessage;
		switch (status) {
			case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
				errorMessage = "Insufficient authentication for a given operation.";
				break;
			case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
				errorMessage = "The write operation exceeds the maximum length of the attribute.";
				break;
			case BluetoothGatt.GATT_INVALID_OFFSET:
				errorMessage = "The read or write operation was requested with an invalid offset.";
				break;
			case BluetoothGatt.GATT_READ_NOT_PERMITTED:
				errorMessage = "The read operation is not permitted.";
				break;
			case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
				errorMessage = "The given request is not supported.";
				break;
			case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
				errorMessage = "The write operation is not permitted.";
				break;
			case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
				errorMessage = "Insufficient encryption for a given operation.";
				break;
			case BluetoothGatt.GATT_FAILURE:
			default:
				if (message == null) {
					errorMessage = "Failed to perform this operation.";
				} else {
					errorMessage = message;
				}
				break;
		}
		return errorMessage;
	}

	public enum ConnectionState { New, Connecting, Connected, Disconnecting, Disconnected }
}
