/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.common.Log;
import ti.modules.titanium.BufferProxy;

public class TiBLEManagePeripheralService extends Service
{
	private static final String LCAT = TiBLEManagePeripheralService.class.getSimpleName();
	public static final String CHANNEL_ID = "TiBLEForegroundServiceChannel";
	private WeakReference<TiBLEPeripheralManagerProxy> peripheralManagerProxyRef;
	private TiBLEPeripheralOperationManager peripheralOperationManager;
	private TiBLEPeripheralAdvertiseManager advertiseManager;
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
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
		createNotificationChannelForOreoOnwards();
		int notificationId = 7656;
		startForeground(notificationId, notificationBuilder.build());
	}

	private void createNotificationChannelForOreoOnwards()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel serviceChannel =
				new NotificationChannel(CHANNEL_ID, "TiBLEServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);

			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(serviceChannel);
		}
	}

	public void initialisePeripheralAndOpenGattServer(TiBLEPeripheralManagerProxy peripheralManagerProxy)
	{
		this.peripheralManagerProxyRef = new WeakReference<>(peripheralManagerProxy);
		peripheralOperationManager = new TiBLEPeripheralOperationManager(this, peripheralManagerProxy);
		peripheralOperationManager.openGattServer();
	}

	public void addService(BluetoothGattService service)
	{
		peripheralOperationManager.addService(service);
	}

	public void removeServiceFromServer(BluetoothGattService service)
	{
		peripheralOperationManager.removeService(service);
	}

	public void removeAllServicesFromServer()
	{
		peripheralOperationManager.clearServices();
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void startAdvertising(String[] serviceUUIDs, boolean localName)
	{
		if (advertiseManager == null) {
			advertiseManager = new TiBLEPeripheralAdvertiseManager(peripheralManagerProxyRef.get());
		}
		advertiseManager.startAdvertising(serviceUUIDs, localName);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void stopAdvertising()
	{
		advertiseManager.stopAdvertising();
	}

	public void closeGattServer()
	{
		peripheralOperationManager.closeGATTServer();
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public boolean isLEAdvertising()
	{
		if (advertiseManager == null) {
			Log.e(LCAT, "isLEAdvertising(): Advertise process is not initialized");
			return false;
		}
		return advertiseManager.isAdvertising();
	}

	public boolean updateCharacteristicAndNotifySubscribers(BluetoothGattCharacteristic characteristic,
															BufferProxy value, TiBLECentralProxy[] centralProxies)
	{
		return peripheralOperationManager.updateCharacteristicAndNotifySubscribers(characteristic, value,
																				   centralProxies);
	}

	public void sendResponseToCharacteristic(TiBLECharacteristicRequestProxy requestProxy, int result)
	{
		peripheralOperationManager.sendResponseToCharacteristic(requestProxy, result);
	}

	public void sendResponseToDescriptor(TiBLEDescriptorRequestProxy descriptorRequestProxy, int result)
	{
		peripheralOperationManager.sendResponseToDescriptor(descriptorRequestProxy, result);
	}

	public class LocalBinder extends Binder
	{
		TiBLEManagePeripheralService getService()
		{
			return TiBLEManagePeripheralService.this;
		}
	}
}
