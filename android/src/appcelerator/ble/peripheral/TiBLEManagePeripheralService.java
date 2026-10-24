/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import appcelerator.ble.TiBLEForegroundServiceHelper;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

public class TiBLEManagePeripheralService extends Service
{
	private static final String LCAT = TiBLEManagePeripheralService.class.getSimpleName();
	public static final String CHANNEL_ID = TiBLEForegroundServiceHelper.CHANNEL_ID;
	private static final int NOTIFICATION_ID = 7656;
	private WeakReference<TiBLEPeripheralManagerProxy> peripheralManagerProxyRef;
	private TiBLEPeripheralOperationManager peripheralOperationManager;
	private TiBLEPeripheralAdvertiseManager advertiseManager;
	private TiBLEPeripheralL2capOperationManager l2capManager;
	private final IBinder binder = new LocalBinder();

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
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(LCAT, "onCreate(): Service is being created");
		TiBLEForegroundServiceHelper.startForeground(this, NOTIFICATION_ID, "Acting as a Bluetooth peripheral");
	}

	public void initialisePeripheralAndOpenGattServer(TiBLEPeripheralManagerProxy peripheralManagerProxy)
	{
		this.peripheralManagerProxyRef = new WeakReference<>(peripheralManagerProxy);
		peripheralOperationManager = new TiBLEPeripheralOperationManager(this, peripheralManagerProxy);
		peripheralOperationManager.openGattServer();
		if (advertiseManager == null) {
			advertiseManager = new TiBLEPeripheralAdvertiseManager(peripheralManagerProxy);
		}
	}

	private boolean hasOperationManager(String methodName)
	{
		if (peripheralOperationManager == null) {
			Log.e(LCAT, methodName + "(): GATT server is not initialized.");
			return false;
		}
		return true;
	}

	private boolean hasAdvertiseManager(String methodName)
	{
		if (advertiseManager == null) {
			Log.e(LCAT, methodName + "(): Advertise process is not initialized");
			return false;
		}
		return true;
	}

	public void addService(BluetoothGattService service)
	{
		if (hasOperationManager("addService")) {
			peripheralOperationManager.addService(service);
		}
	}

	public void removeServiceFromServer(BluetoothGattService service)
	{
		if (hasOperationManager("removeServiceFromServer")) {
			peripheralOperationManager.removeService(service);
		}
	}

	public void removeAllServicesFromServer()
	{
		if (hasOperationManager("removeAllServicesFromServer")) {
			peripheralOperationManager.clearServices();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void startAdvertising(String[] serviceUUIDs, boolean localName)
	{
		if (hasAdvertiseManager("startAdvertising")) {
			advertiseManager.startAdvertising(serviceUUIDs, localName);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void stopAdvertising()
	{
		if (hasAdvertiseManager("stopAdvertising")) {
			advertiseManager.stopAdvertising();
		}
	}

	public void closeGattServer()
	{
		if (hasOperationManager("closeGattServer")) {
			peripheralOperationManager.closeGATTServer();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public boolean isLEAdvertising()
	{
		if (!hasAdvertiseManager("isLEAdvertising")) {
			return false;
		}
		return advertiseManager.isAdvertising();
	}

	public boolean updateCharacteristicAndNotifySubscribers(BluetoothGattCharacteristic characteristic,
															BufferProxy value, TiBLECentralProxy[] centralProxies)
	{
		if (!hasOperationManager("updateCharacteristicAndNotifySubscribers")) {
			return false;
		}
		return peripheralOperationManager.updateCharacteristicAndNotifySubscribers(characteristic, value,
																				   centralProxies);
	}

	public void sendResponseToCharacteristic(TiBLECharacteristicRequestProxy requestProxy, int result)
	{
		if (hasOperationManager("sendResponseToCharacteristic")) {
			peripheralOperationManager.sendResponseToCharacteristic(requestProxy, result);
		}
	}

	public void sendResponseToDescriptor(TiBLEDescriptorRequestProxy descriptorRequestProxy, int result)
	{
		if (hasOperationManager("sendResponseToDescriptor")) {
			peripheralOperationManager.sendResponseToDescriptor(descriptorRequestProxy, result);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void publishL2CAPChannel(boolean encryptionRequired, TiBLEPeripheralManagerProxy proxy)
	{
		if (l2capManager == null) {
			l2capManager = new TiBLEPeripheralL2capOperationManager();
		}
		l2capManager.publishL2CAPChannel(encryptionRequired, proxy);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void unpublishL2CAPChannel(TiBLEPeripheralManagerProxy proxy)
	{
		if (l2capManager == null) {
			Log.e(LCAT, "unpublishL2CAPChannel(): unable to unpublish as no l2cap channel has been published.");
			return;
		}
		l2capManager.unpublishL2CAPChannel(proxy);
	}

	public class LocalBinder extends Binder
	{
		TiBLEManagePeripheralService getService()
		{
			return TiBLEManagePeripheralService.this;
		}
	}
}
