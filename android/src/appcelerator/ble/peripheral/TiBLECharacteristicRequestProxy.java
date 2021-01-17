/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import appcelerator.ble.TiBLECharacteristicProxy;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy(name = "TiBLERequestProxy")
public class TiBLECharacteristicRequestProxy extends KrollProxy
{
	private static final String LCAT = "TiBLECharacteristicRequestProxy";
	private BluetoothGattCharacteristic characteristic;
	private BluetoothDevice bluetoothDevice;
	private int offset;
	private int requestId;
	private BufferProxy value;
	private boolean isResponseNeeded;

	public TiBLECharacteristicRequestProxy(BluetoothGattCharacteristic characteristic, BluetoothDevice bluetoothDevice,
										   int requestId, boolean responseNeeded, int offset, byte[] value)
	{
		this.characteristic = characteristic;
		this.bluetoothDevice = bluetoothDevice;
		this.requestId = requestId;
		this.offset = offset;
		this.isResponseNeeded = responseNeeded;
		if (value != null) {
			this.value = new BufferProxy(value);
		}
	}

	@Kroll.getProperty
	public TiBLECharacteristicProxy characteristic()
	{
		return new TiBLECharacteristicProxy(characteristic);
	}

	@Kroll.getProperty
	public TiBLECentralProxy central()
	{
		return new TiBLECentralProxy(bluetoothDevice);
	}

	@Kroll.getProperty
	public int offset()
	{
		return offset;
	}

	@Kroll.getProperty
	public BufferProxy value()
	{
		return value;
	}

	@Kroll.method
	public void updateValue(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("value")) {
			Log.e(LCAT, "updateValue(): Cannot update value, required parameter not provided");
			return;
		}
		value = (BufferProxy) dict.get("value");
		if (value != null) {
			characteristic.setValue(value.getBuffer());
		}
	}

	@Kroll.getProperty
	public boolean responseNeeded()
	{
		return isResponseNeeded;
	}

	public int getRequestId()
	{
		return requestId;
	}

	public BluetoothDevice getBluetoothDevice()
	{
		return bluetoothDevice;
	}
}
