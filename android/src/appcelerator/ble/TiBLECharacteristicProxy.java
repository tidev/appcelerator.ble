/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import java.util.List;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLECharacteristicProxy extends KrollProxy
{
	private BluetoothGattCharacteristic characteristic;

	public TiBLECharacteristicProxy(BluetoothGattCharacteristic characteristic)
	{
		this.characteristic = characteristic;
	}

	public BluetoothGattCharacteristic getCharacteristic()
	{
		return characteristic;
	}

	@Kroll.getProperty
	public TiBLEServiceProxy service()
	{
		return new TiBLEServiceProxy(characteristic.getService());
	}

	@Kroll.getProperty
	public String uuid()
	{
		return characteristic.getUuid().toString();
	}

	@Kroll.getProperty
	public BufferProxy value()
	{
		if (characteristic.getValue() == null) {
			return null;
		}
		return new BufferProxy(characteristic.getValue());
	}

	@Kroll.getProperty
	public int permissions()
	{
		return characteristic.getPermissions();
	}

	@Kroll.getProperty
	public int properties()
	{
		return characteristic.getProperties();
	}

	@Kroll.getProperty
	public TiBLEDescriptorProxy[] descriptors()
	{
		List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
		TiBLEDescriptorProxy[] descriptorProxies = new TiBLEDescriptorProxy[descriptors.size()];
		for (int i = 0; i < descriptors.size(); i++) {
			descriptorProxies[i] = new TiBLEDescriptorProxy(descriptors.get(i));
		}
		return descriptorProxies;
	}

	@Kroll.getProperty
	public boolean isMutable()
	{
		return false;
	}
}
