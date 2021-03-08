/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
import appcelerator.ble.KeysConstants;
import appcelerator.ble.TiBLECharacteristicProxy;
import appcelerator.ble.TiBLEServiceProxy;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import org.appcelerator.kroll.KrollDict;
import ti.modules.titanium.BufferProxy;

@SuppressLint("MissingPermission")
public class TiBLEPeripheralOperationManager
{
	private static final String LCAT = TiBLEPeripheralOperationManager.class.getSimpleName();
	private static final String UUID_CLIENT_CHARACTERISTIC_CONFIGURATION = "00002902-0000-1000-8000-00805f9b34fb";
	private static final String ERROR_DESCRIPTION_KEY = "errorDescription";
	private static final String ERROR_CODE_KEY = "errorCode";
	private static final String DID_ADD_SERVICE_KEY = "didAddService";
	private final WeakReference<TiBLEPeripheralManagerProxy> peripheralManagerProxyRef;
	private final Context context;
	private BluetoothGattServer bluetoothGattServer;
	private HashSet<BluetoothDevice> bluetoothDevices = new HashSet<>();

	public TiBLEPeripheralOperationManager(Context context, TiBLEPeripheralManagerProxy peripheralManagerProxy)
	{
		this.context = context;
		this.peripheralManagerProxyRef = new WeakReference<>(peripheralManagerProxy);
	}

	private final BluetoothGattServerCallback serverCallback = new BluetoothGattServerCallback() {
		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status, int newState)
		{
			super.onConnectionStateChange(device, status, newState);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (newState == BluetoothGatt.STATE_CONNECTED) {
					bluetoothDevices.add(device);
					Log.d(LCAT, "onConnectionStateChange(): Connected to the device: " + device.getName());
				} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
					bluetoothDevices.remove(device);
					Log.d(LCAT, "onConnectionStateChange(): Disconnected to the device: " + device.getName());
				}
			} else {
				bluetoothDevices.remove(device);
				Log.d(LCAT, "onConnectionStateChange(): Disconnected to the device: " + device.getName());
			}
		}

		@Override
		public void onServiceAdded(int status, BluetoothGattService service)
		{
			super.onServiceAdded(status, service);
			Log.d(LCAT, "onServiceAdded(): service added successfully");
			KrollDict dict = new KrollDict();
			if (status != BluetoothGatt.GATT_SUCCESS) {
				dict.put(ERROR_CODE_KEY, status);
				dict.put(ERROR_DESCRIPTION_KEY, "Unable to add service");
			} else {
				dict.put(KeysConstants.service.name(), new TiBLEServiceProxy(service));
			}
			peripheralManagerProxyRef.get().fireEvent(DID_ADD_SERVICE_KEY, dict);
		}

		@Override
		public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
												BluetoothGattCharacteristic characteristic)
		{
			super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
			Log.d(LCAT, "onCharacteristicReadRequest(): Characteristic read request" + characteristic.getUuid());
			TiBLECharacteristicRequestProxy characteristicRequestProxy =
				new TiBLECharacteristicRequestProxy(characteristic, device, requestId, true, offset, null);
			KrollDict dict = new KrollDict();
			dict.put(KeysConstants.request.name(), characteristicRequestProxy);
			peripheralManagerProxyRef.get().fireEvent(KeysConstants.didReceiveReadRequest.name(), dict);
		}

		@Override
		public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
												 BluetoothGattCharacteristic characteristic, boolean preparedWrite,
												 boolean responseNeeded, int offset, byte[] value)
		{
			super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset,
											   value);
			Log.d(LCAT, "onCharacteristicWriteRequest(): Characteristic write request" + characteristic.getUuid());
			TiBLECharacteristicRequestProxy characteristicRequestProxy =
				new TiBLECharacteristicRequestProxy(characteristic, device, requestId, responseNeeded, offset, value);
			characteristic.setValue(value);
			KrollDict dict = new KrollDict();
			dict.put(KeysConstants.requests.name(),
					 new TiBLECharacteristicRequestProxy[] { characteristicRequestProxy });
			peripheralManagerProxyRef.get().fireEvent(KeysConstants.didReceiveWriteRequests.name(), dict);
		}

		@Override
		public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
											BluetoothGattDescriptor descriptor)
		{
			super.onDescriptorReadRequest(device, requestId, offset, descriptor);
			Log.d(LCAT, "onDescriptorReadRequest(): Descriptor read request" + descriptor.getUuid());
			TiBLEDescriptorRequestProxy descriptorRequestProxy =
				new TiBLEDescriptorRequestProxy(descriptor, device, requestId, offset, true, null);
			KrollDict dict = new KrollDict();
			dict.put(KeysConstants.descriptorRequest.name(), descriptorRequestProxy);
			peripheralManagerProxyRef.get().fireEvent(KeysConstants.didReceiveDescriptorReadRequest.name(), dict);
		}

		@Override
		public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor,
											 boolean preparedWrite, boolean responseNeeded, int offset, byte[] value)
		{
			super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
			Log.d(LCAT, "onDescriptorWriteRequest(): Descriptor Write Request " + descriptor.getUuid());
			if (descriptor.getUuid().toString().equalsIgnoreCase(UUID_CLIENT_CHARACTERISTIC_CONFIGURATION)) {
				handleSubscribeAndUnsubscribeEvents(descriptor, value, device, requestId, responseNeeded);
				return;
			}
			KrollDict dict = new KrollDict();
			TiBLEDescriptorRequestProxy descriptorRequestProxy =
				new TiBLEDescriptorRequestProxy(descriptor, device, requestId, offset, responseNeeded, value);
			descriptor.setValue(value);
			dict.put(KeysConstants.descriptorRequests.name(),
					 new TiBLEDescriptorRequestProxy[] { descriptorRequestProxy });
			peripheralManagerProxyRef.get().fireEvent(KeysConstants.didReceiveDescriptorWriteRequests.name(), dict);
		}
	};

	public void openGattServer()
	{
		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager == null) {
			Log.e(LCAT, "openGattServer(): Cannot open the GATT server, Bluetooth not available");
			return;
		}
		bluetoothGattServer = bluetoothManager.openGattServer(context, serverCallback);
	}

	public void addService(BluetoothGattService service)
	{
		boolean isAddServiceInitiated = bluetoothGattServer.addService(service);
		if (!isAddServiceInitiated) {
			KrollDict dict = new KrollDict();
			dict.put(ERROR_CODE_KEY, BluetoothGatt.GATT_FAILURE);
			dict.put(ERROR_DESCRIPTION_KEY, "Failed to initiate the request to add service");
			peripheralManagerProxyRef.get().fireEvent(DID_ADD_SERVICE_KEY, dict);
		}
	}

	public void removeService(BluetoothGattService service)
	{
		boolean isServiceRemoved = bluetoothGattServer.removeService(service);
		if (isServiceRemoved) {
			Log.d(LCAT, "removeService(): Service removed successfully: " + service.getUuid().toString());
		} else {
			Log.e(LCAT, "removeService(): Unable to remove the provided service: " + service.getUuid().toString());
		}
	}

	private void handleSubscribeAndUnsubscribeEvents(BluetoothGattDescriptor descriptor, byte[] value,
													 BluetoothDevice device, int requestId, boolean responseNeeded)
	{
		TiBLECentralProxy centralProxy = new TiBLECentralProxy(device);
		BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
		TiBLECharacteristicProxy characteristicProxy = new TiBLECharacteristicProxy(characteristic);
		boolean isSupportsNotificationsOrIndications =
			(characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
			|| (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;
		int status;
		if (!isSupportsNotificationsOrIndications) {
			status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
		} else if (value.length != 2) {
			status = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
		} else if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
			status = BluetoothGatt.GATT_SUCCESS;
			descriptor.setValue(value);
			setSubsAndUnsubsEventKeys(centralProxy, characteristicProxy, false);
		} else if (Arrays.equals(value, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
				   || Arrays.equals(value, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
			status = BluetoothGatt.GATT_SUCCESS;
			descriptor.setValue(value);
			setSubsAndUnsubsEventKeys(centralProxy, characteristicProxy, true);
		} else {
			status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
		}
		if (responseNeeded) {
			bluetoothGattServer.sendResponse(device, requestId, status, 0, null);
		}
	}

	private void setSubsAndUnsubsEventKeys(TiBLECentralProxy centralProxy, TiBLECharacteristicProxy characteristicProxy,
										   boolean isSubscribe)
	{
		TiBLESubscribedCentrals.getInstance().setSubscribedCentrals(isSubscribe, characteristicProxy.uuid(),
																	centralProxy);
		KrollDict dict = new KrollDict();
		dict.put(KeysConstants.central.name(), centralProxy);
		dict.put(KeysConstants.characteristic.name(), characteristicProxy);
		String subscribeEventKey = (isSubscribe) ? "didSubscribeToCharacteristic" : "didUnsubscribeFromCharacteristic";
		peripheralManagerProxyRef.get().fireEvent(subscribeEventKey, dict);
	}

	public boolean updateCharacteristicAndNotifySubscribers(BluetoothGattCharacteristic characteristic,
															BufferProxy value, TiBLECentralProxy[] centralProxies)
	{
		if (value != null) {
			characteristic.setValue(value.getBuffer());
		} else {
			Log.e(LCAT, "updateCharacteristicAndNotifySubscribers(): Cannot update value, as value provided is null");
			return false;
		}
		HashSet<BluetoothDevice> bluetoothDevicesForUpdate = new HashSet<>();
		if (centralProxies != null) {
			for (TiBLECentralProxy centralProxy : centralProxies) {
				bluetoothDevicesForUpdate.add(centralProxy.getBluetoothDevice());
			}
		} else {
			TiBLECentralProxy[] allSubscribedCentrals =
				TiBLESubscribedCentrals.getInstance().getSubscribedCentrals(characteristic.getUuid().toString());
			if (allSubscribedCentrals == null) {
				Log.e(LCAT, "updateCharacteristicAndNotifySubscribers(): No subscribed Central found to update value");
				return false;
			}
			for (TiBLECentralProxy centralProxy : allSubscribedCentrals) {
				bluetoothDevicesForUpdate.add(centralProxy.getBluetoothDevice());
			}
		}
		boolean isIndicate = characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_INDICATE;

		try {
			boolean isNotifyInitiated = bluetoothDevicesForUpdate.size() > 0;
			for (BluetoothDevice bluetoothDevice : bluetoothDevicesForUpdate) {
				isNotifyInitiated =
					isNotifyInitiated
					& bluetoothGattServer.notifyCharacteristicChanged(bluetoothDevice, characteristic, isIndicate);
			}
			return isNotifyInitiated;
		} catch (IllegalArgumentException e) {
			Log.e(LCAT, "updateCharacteristicAndNotifySubscribers(): Cannot update value" + e.getMessage());
		}
		return false;
	}

	public void sendResponseToCharacteristic(TiBLECharacteristicRequestProxy requestProxy, int result)
	{
		BluetoothDevice device = requestProxy.getBluetoothDevice();
		int requestId = requestProxy.getRequestId();
		int offset = requestProxy.offset();
		byte[] value = requestProxy.value().getBuffer();
		boolean isSendResponseInitiated = bluetoothGattServer.sendResponse(device, requestId, result, offset, value);
		if (!isSendResponseInitiated) {
			Log.e(LCAT, "sendResponseToCharacteristic(): Failed to send response to the characteristic request");
		}
	}

	public void sendResponseToDescriptor(TiBLEDescriptorRequestProxy descriptorRequestProxy, int result)
	{
		BluetoothDevice device = descriptorRequestProxy.getBluetoothDevice();
		int requestId = descriptorRequestProxy.getRequestId();
		int offset = descriptorRequestProxy.offset();
		byte[] value = descriptorRequestProxy.value().getBuffer();
		boolean isSendResponseInitiated = bluetoothGattServer.sendResponse(device, requestId, result, offset, value);
		if (!isSendResponseInitiated) {
			Log.e(LCAT, "sendResponseToDescriptor(): Failed to send response to the descriptor request");
		}
	}

	public void clearServices()
	{
		bluetoothGattServer.clearServices();
	}

	public void closeGATTServer()
	{
		TiBLESubscribedCentrals.getInstance().releaseSubscribedCentrals();
		bluetoothGattServer.close();
	}
}
