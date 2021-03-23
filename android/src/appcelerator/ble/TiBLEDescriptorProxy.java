/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothGattDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLEDescriptorProxy extends KrollProxy
{
	private final BluetoothGattDescriptor descriptor;
	private static final String LCAT = "TiBLEDescriptorProxy";

	public TiBLEDescriptorProxy(BluetoothGattDescriptor descriptor)
	{
		this.descriptor = descriptor;
	}

	public static TiBLEDescriptorProxy createDescriptorProxy(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.permission.name())
			|| !dict.containsKey(KeysConstants.uuid.name())) {
			Log.e(LCAT, "createDescriptorProxy(): Unable to create Descriptor, required parameters not provided");
			return null;
		}
		String uuid = (String) dict.get(KeysConstants.uuid.name());
		int permission = (int) dict.get(KeysConstants.permission.name());
		BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID.fromString(uuid), permission);
		if (dict.containsKey(KeysConstants.value.name())) {
			Object value = dict.get(KeysConstants.value.name());
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 ObjectOutput out = new ObjectOutputStream(bos)) {
				out.writeObject(value);
				byte[] bytes = bos.toByteArray();
				descriptor.setValue(bytes);
			} catch (IOException e) {
				Log.e(LCAT,
					  "createDescriptorProxy(): Error while converting Object into byte Array: " + e.getMessage());
			}
		}
		return new TiBLEDescriptorProxy(descriptor);
	}

	public BluetoothGattDescriptor getDescriptor()
	{
		return descriptor;
	}

	@Kroll.getProperty
	public String uuid()
	{
		return descriptor.getUuid().toString();
	}

	@Kroll.getProperty
	public BufferProxy value()
	{
		if (descriptor.getValue() == null) {
			return null;
		}
		return new BufferProxy(descriptor.getValue());
	}

	@Kroll.getProperty
	public TiBLECharacteristicProxy characteristic()
	{
		return new TiBLECharacteristicProxy(descriptor.getCharacteristic());
	}
}
