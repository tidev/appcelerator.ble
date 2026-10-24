/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import org.appcelerator.kroll.common.Log;

/**
 * Puts a BLE service into the foreground with a valid notification.
 * Shared by the central and the peripheral service.
 */
public final class TiBLEForegroundServiceHelper
{
	private static final String LCAT = "TiBLEForegroundServiceHelper";
	public static final String CHANNEL_ID = "TiBLEForegroundServiceChannel";
	private static final String CHANNEL_NAME = "Bluetooth";

	private TiBLEForegroundServiceHelper()
	{
	}

	public static void startForeground(Service service, int notificationId, String contentText)
	{
		createNotificationChannel(service);

		ApplicationInfo appInfo = service.getApplicationInfo();
		CharSequence appLabel = appInfo.loadLabel(service.getPackageManager());
		int smallIcon = (appInfo.icon != 0) ? appInfo.icon : android.R.drawable.stat_sys_data_bluetooth;

		Notification notification = new NotificationCompat.Builder(service, CHANNEL_ID)
										.setSmallIcon(smallIcon)
										.setContentTitle(appLabel)
										.setContentText(contentText)
										.setOngoing(true)
										.setPriority(NotificationCompat.PRIORITY_LOW)
										.setCategory(NotificationCompat.CATEGORY_SERVICE)
										.build();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			service.startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
		} else {
			service.startForeground(notificationId, notification);
		}
	}

	private static void createNotificationChannel(Service service)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return;
		}
		NotificationManager manager = service.getSystemService(NotificationManager.class);
		if (manager == null) {
			Log.e(LCAT, "createNotificationChannel(): NotificationManager not available.");
			return;
		}
		NotificationChannel channel =
			new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
		manager.createNotificationChannel(channel);
	}
}
