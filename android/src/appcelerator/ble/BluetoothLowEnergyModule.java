/**
* Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
* Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
* Proprietary and Confidential - This source code is not for redistribution
*/

package appcelerator.ble;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

@Kroll.module(name = "BluetoothLowEnergy", id = "appcelerator.ble")
public class BluetoothLowEnergyModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "BluetoothLowEnergyModule";

	public BluetoothLowEnergyModule()
	{
		super();
	}

	@Override
	public String getApiName()
	{
		return "appcelerator.ble";
	}
}
