/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy
public class TiBLEServiceProxy extends KrollProxy
{

	private final BluetoothGattService service;
	private final TiBLEPeripheralProxy peripheralProxy;

	private TiBLEServiceProxy[] includedServiceProxyArr;
	private TiBLECharacteristicProxy[] characteristicProxyArr;

	public TiBLEServiceProxy(BluetoothGattService service, TiBLEPeripheralProxy peripheralProxy)
	{
		this.service = service;
		this.peripheralProxy = peripheralProxy;

		initIncludedServicesProxies();
		initCharacteristicsProxies();
	}

	public static TiBLEServiceProxy mockServiceForUT(KrollDict dict, TiBLEPeripheralProxy peripheralProxy)
	{
		//temporary method for service UT.
		//TODO: Address or remove this temp method in MOD-2689.
		if (dict.containsKey("uuid") && dict.containsKey("primary")) {
			String uuid = (String) dict.get("uuid");
			UUID id = UUID.fromString(uuid);
			boolean isPrimary = dict.getBoolean("primary");
			int serviceType =
				isPrimary ? BluetoothGattService.SERVICE_TYPE_PRIMARY : BluetoothGattService.SERVICE_TYPE_SECONDARY;

			return new TiBLEServiceProxy(new BluetoothGattService(id, serviceType), peripheralProxy);
		}
		return null;
	}

	private void initCharacteristicsProxies()
	{
		List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
		characteristicProxyArr = new TiBLECharacteristicProxy[characteristics.size()];

		for (int i = 0; i < characteristics.size(); i++) {
			characteristicProxyArr[i] = new TiBLECharacteristicProxy(characteristics.get(i), this);
		}
	}

	@Kroll.getProperty
	public TiBLECharacteristicProxy[] characteristics()
	{
		return characteristicProxyArr;
	}

	private void initIncludedServicesProxies()
	{
		List<BluetoothGattService> includedServices = service.getIncludedServices();
		includedServiceProxyArr = new TiBLEServiceProxy[includedServices.size()];

		for (int i = 0; i < includedServices.size(); i++) {
			includedServiceProxyArr[i] = new TiBLEServiceProxy(includedServices.get(i), peripheralProxy);
		}
	}

	@Kroll.getProperty
	public TiBLEServiceProxy[] includedServices()
	{
		return includedServiceProxyArr;
	}

	@Kroll.getProperty
	public boolean isPrimary()
	{
		return service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY;
	}

	@Kroll.getProperty
	public TiBLEPeripheralProxy peripheral()
	{
		return peripheralProxy;
	}

	@Kroll.getProperty
	public String uuid()
	{
		return service.getUuid().toString();
	}
}
