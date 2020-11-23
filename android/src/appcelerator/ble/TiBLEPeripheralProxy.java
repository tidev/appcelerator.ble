/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy
public class TiBLEPeripheralProxy extends KrollProxy
{
	private static final String LCAT = "TiBLEPeripheralProxy";

	private final BluetoothDevice device;
	private final IOperationHandler handler;
	private TiBLEServiceProxy[] serviceProxies = new TiBLEServiceProxy[0];

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
		return serviceProxies;
	}

	public void addServices(List<BluetoothGattService> services)
	{
		serviceProxies = new TiBLEServiceProxy[services.size()];
		for (int i = 0; i < services.size(); i++) {
			serviceProxies[i] = new TiBLEServiceProxy(services.get(i), this);
		}
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
		if (dict == null || !dict.containsKey("service")) {
			Log.e(LCAT,
				  "discoverIncludedServices(): unable to discover includedServices as service proxy not provided.");
			return;
		}
		TiBLEServiceProxy serviceProxy = (TiBLEServiceProxy) dict.get("service");
		handler.discoverIncludedServices(serviceProxy);
	}

	@Kroll.method
	public void discoverCharacteristics(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("service")) {
			Log.e(LCAT, "discoverCharacteristics(): unable to discover characteristics as service proxy not provided.");
			return;
		}
		TiBLEServiceProxy serviceProxy = (TiBLEServiceProxy) dict.get("service");
		handler.discoverCharacteristics(serviceProxy);
	}

	@Kroll.method
	public void discoverDescriptorsForCharacteristic(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("characteristic")) {
			Log.e(
				LCAT,
				"discoverDescriptorsForCharacteristic(): unable to discover descriptors as characteristic proxy not provided.");
			return;
		}
		TiBLECharacteristicProxy characteristicProxy = (TiBLECharacteristicProxy) dict.get("characteristic");
		handler.discoverDescriptorsForCharacteristic(characteristicProxy);
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
	}
}
