/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothGattCharacteristic;
import appcelerator.ble.TiBLECharacteristicProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy
public class TiBLEMutableCharacteristicProxy extends TiBLECharacteristicProxy
{

	public TiBLEMutableCharacteristicProxy(BluetoothGattCharacteristic characteristic)
	{
		super(characteristic);
	}
}
