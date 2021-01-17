/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothGattCharacteristic;
import appcelerator.ble.TiBLECharacteristicProxy;
import appcelerator.ble.TiBLEDescriptorProxy;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@Kroll.proxy
public class TiBLEMutableCharacteristicProxy extends TiBLECharacteristicProxy
{
	private static final String LCAT = "TiBLEMutableCharacteristicProxy";
	private BluetoothGattCharacteristic characteristic;

	public TiBLEMutableCharacteristicProxy(BluetoothGattCharacteristic characteristic)
	{
		super(characteristic);
		this.characteristic = characteristic;
	}

	public static TiBLEMutableCharacteristicProxy createMutableCharacteristicProxy(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("properties") || !dict.containsKey("permissions")
			|| !dict.containsKey("uuid")) {
			Log.e(
				LCAT,
				"createMutableCharacteristicProxy(): Unable to create characteristic, required parameters not provided");
			return null;
		}
		int properties = (int) dict.get("properties");
		int permissions = (int) dict.get("permissions");
		String uuid = (String) dict.get("uuid");
		BluetoothGattCharacteristic characteristic =
			new BluetoothGattCharacteristic(UUID.fromString(uuid), properties, permissions);
		if (dict.containsKey("descriptors")) {
			Object[] descriptorProxiesObject = (Object[]) dict.get("descriptors");
			if (descriptorProxiesObject != null) {
				TiBLEDescriptorProxy[] descriptorProxies = new TiBLEDescriptorProxy[descriptorProxiesObject.length];
				for (int i = 0; i < descriptorProxiesObject.length; i++) {
					descriptorProxies[i] = (TiBLEDescriptorProxy) descriptorProxiesObject[i];
				}
				for (TiBLEDescriptorProxy descriptorProxy : descriptorProxies) {
					characteristic.addDescriptor(descriptorProxy.getDescriptor());
				}
			}
		}
		return new TiBLEMutableCharacteristicProxy(characteristic);
	}

	@Kroll.getProperty
	public TiBLECentralProxy[] subscribedCentrals()
	{
		return TiBLESubscribedCentrals.getInstance().getSubscribedCentrals(characteristic.getUuid().toString());
	}
}
