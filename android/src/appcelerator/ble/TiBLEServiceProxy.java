/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import java.util.List;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@Kroll.proxy
public class TiBLEServiceProxy extends KrollProxy
{
	private final BluetoothGattService service;
	private static final String LCAT = "TiBLEServiceProxy";

	public TiBLEServiceProxy(BluetoothGattService service)
	{
		this.service = service;
	}

	public static TiBLEServiceProxy createServiceProxy(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("uuid") || !dict.containsKey("primary")) {
			Log.e(LCAT, "createServiceProxy(): Cannot create service, required parameters not provided");
			return null;
		}
		String uuid = (String) dict.get("uuid");
		boolean isPrimary = (boolean) dict.get("primary");
		int primary =
			(isPrimary) ? BluetoothGattService.SERVICE_TYPE_PRIMARY : BluetoothGattService.SERVICE_TYPE_SECONDARY;
		BluetoothGattService service = new BluetoothGattService(UUID.fromString(uuid), primary);
		if (dict.containsKey("characteristics")) {
			Object[] characteristicObject = (Object[]) dict.get("characteristics");
			if (characteristicObject != null) {
				TiBLECharacteristicProxy[] characteristicProxies =
					new TiBLECharacteristicProxy[characteristicObject.length];
				for (int i = 0; i < characteristicObject.length; i++) {
					characteristicProxies[i] = (TiBLECharacteristicProxy) characteristicObject[i];
				}
				for (TiBLECharacteristicProxy characteristicProxy : characteristicProxies) {
					service.addCharacteristic(characteristicProxy.getCharacteristic());
				}
			}
		}
		return new TiBLEServiceProxy(service);
	}

	@Kroll.getProperty
	public TiBLECharacteristicProxy[] characteristics()
	{
		List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
		TiBLECharacteristicProxy[] characteristicProxyArr = new TiBLECharacteristicProxy[characteristics.size()];

		for (int i = 0; i < characteristics.size(); i++) {
			characteristicProxyArr[i] = new TiBLECharacteristicProxy(characteristics.get(i));
		}
		return characteristicProxyArr;
	}

	@Kroll.getProperty
	public TiBLEServiceProxy[] includedServices()
	{
		List<BluetoothGattService> includedServices = service.getIncludedServices();
		TiBLEServiceProxy[] includedServiceProxyArr = new TiBLEServiceProxy[includedServices.size()];

		for (int i = 0; i < includedServices.size(); i++) {
			includedServiceProxyArr[i] = new TiBLEServiceProxy(includedServices.get(i));
		}
		return includedServiceProxyArr;
	}

	@Kroll.getProperty(name = "isPrimary")
	public boolean isPrimary()
	{
		return service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY;
	}

	@Kroll.getProperty
	public String uuid()
	{
		return service.getUuid().toString();
	}

	public BluetoothGattService getService()
	{
		return service;
	}
}
