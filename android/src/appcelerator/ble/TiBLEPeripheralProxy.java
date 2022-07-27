/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLEPeripheralProxy extends KrollProxy
{
	private static final String LCAT = "TiBLEPeripheralProxy";

	private final BluetoothDevice device;
	private final IOperationHandler handler;
	private List<BluetoothGattService> services;

	public TiBLEPeripheralProxy(BluetoothDevice bluetoothDevice, IOperationHandler handler)
	{
		this.device = bluetoothDevice;
		this.handler = handler;
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty
	public String name()
	{
		return device.getName();
	}

	@Kroll.getProperty
	public String address()
	{
		return device.getAddress();
	}

	@Kroll.getProperty(name = "isConnected")
	public boolean isConnected()
	{
		return handler.isConnected(this);
	}

	@Kroll.getProperty
	public TiBLEServiceProxy[] services()
	{
		if (services == null) {
			return null;
		}
		TiBLEServiceProxy[] serviceProxies = new TiBLEServiceProxy[services.size()];
		for (int i = 0; i < services.size(); i++) {
			serviceProxies[i] = new TiBLEServiceProxy(services.get(i));
		}
		return serviceProxies;
	}

	public void addServices(List<BluetoothGattService> services)
	{
		this.services = services;
	}

	@Kroll.method
	public void readRSSI()
	{
		handler.readRSSI();
	}

	@Kroll.method
	public void requestConnectionPriority(int priority)
	{
		handler.requestConnectionPriority(priority);
	}

	@Kroll.method
	public void discoverServices(@Kroll.argument(optional = true) String[] serviceUUIDStrings)
	{
		handler.discoverServices(serviceUUIDStrings);
	}

	@Kroll.method
	public void discoverIncludedServices(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.service.name())) {
			Log.e(LCAT,
				  "discoverIncludedServices(): unable to discover includedServices as service proxy not provided.");
			return;
		}
		TiBLEServiceProxy serviceProxy = (TiBLEServiceProxy) dict.get(KeysConstants.service.name());
		handler.discoverIncludedServices(serviceProxy);
	}

	@Kroll.method
	public void discoverCharacteristics(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.service.name())) {
			Log.e(LCAT, "discoverCharacteristics(): unable to discover characteristics as service proxy not provided.");
			return;
		}
		TiBLEServiceProxy serviceProxy = (TiBLEServiceProxy) dict.get(KeysConstants.service.name());
		handler.discoverCharacteristics(serviceProxy);
	}

	@Kroll.method
	public void discoverDescriptorsForCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.characteristic.name())) {
			Log.e(
				LCAT,
				"discoverDescriptorsForCharacteristic(): unable to discover descriptors as characteristic proxy not provided.");
			return;
		}
		TiBLECharacteristicProxy characteristicProxy =
			(TiBLECharacteristicProxy) dict.get(KeysConstants.characteristic.name());
		handler.discoverDescriptorsForCharacteristic(characteristicProxy);
	}

	@Kroll.method
	public void readValueForCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.characteristic.name())) {
			Log.e(LCAT, "readValueForCharacteristic(): unable to read value as characteristic proxy not provided.");
			return;
		}
		TiBLECharacteristicProxy characteristicProxy =
			(TiBLECharacteristicProxy) dict.get(KeysConstants.characteristic.name());
		handler.readValueForCharacteristic(characteristicProxy);
	}

	@Kroll.method
	public void writeValueForCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.characteristic.name())) {
			Log.e(LCAT, "writeValueForCharacteristic(): unable to write value as characteristic proxy not provided.");
			return;
		}
		if (!dict.containsKey(KeysConstants.data.name())) {
			Log.e(LCAT, "writeValueForCharacteristic(): unable to write value as buffer proxy not provided.");
			return;
		}

		TiBLECharacteristicProxy charProxy = (TiBLECharacteristicProxy) dict.get(KeysConstants.characteristic.name());
		BufferProxy bufferProxy = (BufferProxy) dict.get(KeysConstants.data.name());

		int writeType = AppceleratorBleModule.CHARACTERISTIC_TYPE_WRITE_WITH_RESPONSE;
		if (dict.containsKey(KeysConstants.type.name())) {
			writeType = dict.getInt(KeysConstants.type.name());
		}

		handler.writeValueForCharacteristic(charProxy, bufferProxy.getBuffer(), writeType);
	}

	@Kroll.method
	public void readValueForDescriptor(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.descriptor.name())) {
			Log.e(LCAT, "readValueForDescriptor(): unable to read value as descriptor proxy not provided.");
			return;
		}

		TiBLEDescriptorProxy descriptorProxy = (TiBLEDescriptorProxy) dict.get(KeysConstants.descriptor.name());

		handler.readValueForDescriptor(descriptorProxy);
	}

	@Kroll.method
	public void writeValueForDescriptor(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.descriptor.name())) {
			Log.e(LCAT, "writeValueForDescriptor(): unable to write value as descriptor proxy not provided.");
			return;
		}
		if (!dict.containsKey(KeysConstants.data.name())) {
			Log.e(LCAT, "writeValueForDescriptor(): unable to write value as buffer proxy not provided.");
			return;
		}

		TiBLEDescriptorProxy descriptorProxy = (TiBLEDescriptorProxy) dict.get(KeysConstants.descriptor.name());
		BufferProxy bufferProxy = (BufferProxy) dict.get(KeysConstants.data.name());

		handler.writeValueForDescriptor(descriptorProxy, bufferProxy.getBuffer());
	}

	@Kroll.method
	public void subscribeToCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.characteristic.name())) {
			Log.e(LCAT, "subscribeToCharacteristic(): unable to subscribe as characteristic proxy not provided.");
			return;
		}

		TiBLECharacteristicProxy charProxy = (TiBLECharacteristicProxy) dict.get(KeysConstants.characteristic.name());

		String descriptorUUID = null;
		if (dict.containsKey(KeysConstants.descriptorUUID.name())) {
			descriptorUUID = dict.getString(KeysConstants.descriptorUUID.name());
		}

		BufferProxy enableValue = null;
		if (descriptorUUID != null && !descriptorUUID.isEmpty()) {
			enableValue = new BufferProxy(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			if (dict.containsKey(KeysConstants.descriptorValue.name())) {
				enableValue = (BufferProxy) dict.get(KeysConstants.descriptorValue.name());
			}
		}

		handler.subscribeToCharacteristic(charProxy, descriptorUUID, enableValue);
	}

	@Kroll.method
	public void unsubscribeFromCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.characteristic.name())) {
			Log.e(
				LCAT,
				"unsubscribeFromCharacteristic(): unable to unsubscribeFromCharacteristic as characteristic proxy not provided.");
			return;
		}

		TiBLECharacteristicProxy charProxy = (TiBLECharacteristicProxy) dict.get(KeysConstants.characteristic.name());

		String descriptorUUID = null;
		if (dict.containsKey(KeysConstants.descriptorUUID.name())) {
			descriptorUUID = dict.getString(KeysConstants.descriptorUUID.name());
		}

		BufferProxy disableValue = null;
		if (descriptorUUID != null && !descriptorUUID.isEmpty()) {
			disableValue = new BufferProxy(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			if (dict.containsKey(KeysConstants.descriptorValue.name())) {
				disableValue = (BufferProxy) dict.get(KeysConstants.descriptorValue.name());
			}
		}

		handler.unsubscribeFromCharacteristic(charProxy, descriptorUUID, disableValue);
	}

	@Kroll.method
	public void openL2CAPChannel(KrollDict dict)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			Log.e(
				LCAT,
				"openL2CAPChannel(): unable to open the channel. This feature is supported on Android OS Version 'Q' and above.");
			return;
		}
		if (dict == null || !dict.containsKey(KeysConstants.psmIdentifier.name())) {
			Log.e(LCAT, "openL2CAPChannel(): unable to open l2cap channel as psmIdentifier not provided.");
			return;
		}

		int psmIdentifier = dict.getInt(KeysConstants.psmIdentifier.name());
		boolean isEncryptionRequired = dict.containsKey(KeysConstants.encryptionRequired.name())
									   && dict.getBoolean(KeysConstants.encryptionRequired.name());
		handler.openL2CAPChannel(this, psmIdentifier, isEncryptionRequired);
	}

	public BluetoothDevice getDevice()
	{
		return device;
	}

	public interface IOperationHandler {
		boolean isConnected(TiBLEPeripheralProxy peripheralProxy);
		void readRSSI();
		void requestConnectionPriority(int priority);
		void discoverServices(String[] serviceUUIDs);
		void discoverIncludedServices(TiBLEServiceProxy serviceProxy);
		void discoverCharacteristics(TiBLEServiceProxy serviceProxy);
		void discoverDescriptorsForCharacteristic(TiBLECharacteristicProxy characteristicProxy);
		void readValueForCharacteristic(TiBLECharacteristicProxy characteristicProxy);
		void writeValueForCharacteristic(TiBLECharacteristicProxy characteristicProxy, byte[] buffer, int writeType);
		void readValueForDescriptor(TiBLEDescriptorProxy descriptorProxy);
		void writeValueForDescriptor(TiBLEDescriptorProxy descriptorProxy, byte[] buffer);
		void subscribeToCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
									   BufferProxy enableValue);
		void unsubscribeFromCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
										   BufferProxy disableValue);

		void openL2CAPChannel(TiBLEPeripheralProxy peripheralProxy, int psmIdentifier, boolean isEncryptionRequired);
	}
}
