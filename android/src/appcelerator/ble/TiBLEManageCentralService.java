/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

public class TiBLEManageCentralService extends Service
{
	private static final String LCAT = TiBLEManageCentralService.class.getSimpleName();
	public static final String CHANNEL_ID = TiBLEForegroundServiceHelper.CHANNEL_ID;
	private static final int NOTIFICATION_ID = 7654;
	private final IBinder binder = new LocalBinder();

	private TiBleCentralOperationManager centralOperationManager;
	private TiBLEPeripheralProxy peripheralProxy;

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(LCAT, "onStartCommand(): Service is Started");

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(LCAT, "onDestroy(): Service is being destroyed");

		peripheralProxy = null;
		centralOperationManager = null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(LCAT, "onCreate(): Service is being created");
		TiBLEForegroundServiceHelper.startForeground(this, NOTIFICATION_ID, "Connected to a Bluetooth device");
	}

	public void initiateConnectionWithPeripheral(TiBLECentralManagerProxy centralManagerProxy,
												 TiBLEPeripheralProxy peripheralProxy, boolean autoConnect)
	{
		this.peripheralProxy = peripheralProxy;
		centralOperationManager =
			new TiBleCentralOperationManager(this, centralManagerProxy, this.peripheralProxy, autoConnect);
		centralOperationManager.initiateConnectionWithPeripheral();
	}

	private boolean hasConnection(String methodName)
	{
		if (centralOperationManager == null || this.peripheralProxy == null) {
			Log.e(LCAT, methodName + "(): no peripheral connection has been initiated.");
			return false;
		}
		return true;
	}

	public void cancelPeripheralConnection(TiBLEPeripheralProxy peripheralProxy)
	{
		if (!hasConnection("cancelPeripheralConnection")) {
			return;
		}
		if (!peripheralProxy.address().equals(this.peripheralProxy.address())) {
			Log.e(LCAT,
				  "cancelPeripheralConnection(): unable to cancel the connection. Peripheral provided name/address- "
					  + peripheralProxy.name() + " / " + peripheralProxy.address()
					  + ", but existing peripheral connection name/address - " + this.peripheralProxy.name() + " / "
					  + this.peripheralProxy.address());
			return;
		}

		centralOperationManager.cancelPeripheralConnection();
	}

	public boolean isConnectedWithPeripheral(TiBLEPeripheralProxy peripheralProxy)
	{
		if (centralOperationManager == null || this.peripheralProxy == null) {
			return false;
		}
		if (!peripheralProxy.address().equals(this.peripheralProxy.address())) {
			return false;
		}
		return centralOperationManager.isConnected();
	}

	public void readRSSI()
	{
		if (hasConnection("readRSSI")) {
			centralOperationManager.readRSSI();
		}
	}

	public void requestConnectionPriority(int priority)
	{
		if (hasConnection("requestConnectionPriority")) {
			centralOperationManager.requestConnectionPriority(priority);
		}
	}

	public void discoverServices()
	{
		if (hasConnection("discoverServices")) {
			centralOperationManager.discoverServices();
		}
	}

	public void discoverIncludedServices(TiBLEServiceProxy serviceProxy)
	{
		if (hasConnection("discoverIncludedServices")) {
			centralOperationManager.discoverIncludedServices(serviceProxy);
		}
	}

	public void discoverCharacteristics(TiBLEServiceProxy serviceProxy)
	{
		if (hasConnection("discoverCharacteristics")) {
			centralOperationManager.discoverCharacteristics(serviceProxy);
		}
	}

	public void discoverDescriptorsForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
	{
		if (hasConnection("discoverDescriptorsForCharacteristic")) {
			centralOperationManager.discoverDescriptorsForCharacteristic(characteristicProxy);
		}
	}

	public void handleBluetoothTurnedOff()
	{
		if (hasConnection("handleBluetoothTurnedOff")) {
			centralOperationManager.handleDisconnection(BluetoothGatt.GATT_FAILURE);
		}
	}

	public void readValueForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
	{
		if (hasConnection("readValueForCharacteristic")) {
			centralOperationManager.readValueForCharacteristic(characteristicProxy);
		}
	}

	public void writeValueForCharacteristic(TiBLECharacteristicProxy charProxy, byte[] buffer, int writeType)
	{
		if (hasConnection("writeValueForCharacteristic")) {
			centralOperationManager.writeValueForCharacteristic(charProxy, buffer, writeType);
		}
	}

	public void readValueForDescriptor(TiBLEDescriptorProxy descriptorProxy)
	{
		if (hasConnection("readValueForDescriptor")) {
			centralOperationManager.readValueForDescriptor(descriptorProxy);
		}
	}

	public void writeValueForDescriptor(TiBLEDescriptorProxy descriptorProxy, byte[] buffer)
	{
		if (hasConnection("writeValueForDescriptor")) {
			centralOperationManager.writeValueForDescriptor(descriptorProxy, buffer);
		}
	}

	public void subscribeToCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
										  BufferProxy enableValue)
	{
		if (hasConnection("subscribeToCharacteristic")) {
			centralOperationManager.subscribeToCharacteristic(charProxy, descriptorUUID, enableValue);
		}
	}

	public void unsubscribeFromCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
											  BufferProxy disableValue)
	{
		if (hasConnection("unsubscribeFromCharacteristic")) {
			centralOperationManager.unsubscribeFromCharacteristic(charProxy, descriptorUUID, disableValue);
		}
	}

	public class LocalBinder extends Binder
	{
		TiBLEManageCentralService getService()
		{
			return TiBLEManageCentralService.this;
		}
	}
}
