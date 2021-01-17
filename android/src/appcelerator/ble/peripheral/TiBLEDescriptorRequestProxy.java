/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import appcelerator.ble.TiBLEDescriptorProxy;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLEDescriptorRequestProxy extends KrollProxy
{

	private static final String LCAT = "TiBLEDescriptorRequestProxy";
	private BluetoothGattDescriptor descriptor;
	private BluetoothDevice bluetoothDevice;
	private int offset;
	private int requestId;
	private BufferProxy descriptorValue;
	private boolean isResponseNeeded;

	public TiBLEDescriptorRequestProxy(BluetoothGattDescriptor descriptor, BluetoothDevice bluetoothDevice,
									   int requestId, int offset, boolean isResponseNeeded, byte[] value)
	{
		this.descriptor = descriptor;
		this.bluetoothDevice = bluetoothDevice;
		this.requestId = requestId;
		this.offset = offset;
		this.isResponseNeeded = isResponseNeeded;
		if (value != null) {
			this.descriptorValue = new BufferProxy(value);
		}
	}

	@Kroll.getProperty
	public TiBLEDescriptorProxy descriptor()
	{
		return new TiBLEDescriptorProxy(descriptor);
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
		return descriptorValue;
	}

	@Kroll.getProperty
	public boolean isResponseNeeded()
	{
		return isResponseNeeded;
	}

	@Kroll.method
	public void updateDescriptorValue(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("value")) {
			Log.e(LCAT, "updateDescriptorValue(): Cannot update value, required parameter not provided");
			return;
		}
		descriptorValue = (BufferProxy) dict.get("value");
		if (descriptorValue != null) {
			descriptor.setValue(descriptorValue.getBuffer());
		}
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
