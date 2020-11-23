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
	private TiBLEServiceProxy serviceProxy;
	private BluetoothGattCharacteristic characteristic;

	private TiBLEDescriptorProxy[] descriptorProxies;

	public TiBLECharacteristicProxy(BluetoothGattCharacteristic characteristic, TiBLEServiceProxy serviceProxy)
	{
		this.serviceProxy = serviceProxy;
		this.characteristic = characteristic;

		initDescriptorsProxies();
	}

	//temporary method for Characteristic UT.
	//TODO Address or remove this temp method in MOD-2689.
	public static TiBLECharacteristicProxy mockCharacteristicForUT(KrollDict dict, TiBLEServiceProxy serviceProxy)
	{
		if (dict.containsKey("properties") && dict.containsKey("uuid") && dict.containsKey("permissions")) {
			int properties = (int) dict.get("properties");
			String uuid = (String) dict.get("uuid");
			int permissions = (int) dict.get("permissions");

			BluetoothGattCharacteristic characteristic =
				new BluetoothGattCharacteristic(UUID.fromString(uuid), properties, permissions);
			return new TiBLECharacteristicProxy(characteristic, serviceProxy);
		}
		return null;
	}

	@Kroll.getProperty
	public TiBLEServiceProxy service()
	{
		return serviceProxy;
	}

	@Kroll.getProperty
	public String uuid()
	{
		return characteristic.getUuid().toString();
	}

	@Kroll.getProperty
	public BufferProxy value()
	{
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

	private void initDescriptorsProxies()
	{
		List<BluetoothGattDescriptor> bluetoothGattDescriptors = characteristic.getDescriptors();
		this.descriptorProxies = new TiBLEDescriptorProxy[bluetoothGattDescriptors.size()];
		int i = 0;
		for (BluetoothGattDescriptor gattDescriptor : bluetoothGattDescriptors) {
			descriptorProxies[i] = new TiBLEDescriptorProxy(gattDescriptor, this);
			i++;
		}
	}

	@Kroll.getProperty
	public TiBLEDescriptorProxy[] descriptors()
	{
		return descriptorProxies;
	}

	@Kroll.getProperty
	public boolean isMutable()
	{
		//Android doesn't support the peripheral role so this property will always be false.
		return false;
	}
}
