/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import org.appcelerator.kroll.common.Log;

public class TiBLEManageCentralService extends Service
{
	private static final String LCAT = TiBLEManageCentralService.class.getSimpleName();
	public static final String CHANNEL_ID = "TiBLEForegroundServiceChannel";
	private final IBinder binder = new LocalBinder();

	@Nullable
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
		int notificationId = 7654;
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

	public class LocalBinder extends Binder
	{
		TiBLEManageCentralService getService()
		{
			return TiBLEManageCentralService.this;
		}
	}
}