/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.util.Log;
import java.util.Hashtable;

public class TiBLESubscribedCentrals
{
	private static TiBLESubscribedCentrals subscribedCentrals;
	private static final String LCAT = "TiBLESubscribedCentrals";
	private Hashtable<String, Hashtable<String, TiBLECentralProxy>> subscribedCentralsProxies = new Hashtable<>();
	private TiBLESubscribedCentrals()
	{
	}

	public static TiBLESubscribedCentrals getInstance()
	{
		if (subscribedCentrals == null) {
			synchronized (TiBLESubscribedCentrals.class)
			{
				if (subscribedCentrals == null) {
					subscribedCentrals = new TiBLESubscribedCentrals();
				}
			}
		}
		return subscribedCentrals;
	}

	public TiBLECentralProxy[] getSubscribedCentrals(String uuid)
	{
		TiBLECentralProxy[] centralProxies = null;
		if (subscribedCentralsProxies.isEmpty()) {
			Log.w(LCAT, "getSubscribedCentrals(): No subscribed central found");
		} else {
			Hashtable<String, TiBLECentralProxy> hashtable = subscribedCentralsProxies.get(uuid);
			if (hashtable != null && hashtable.size() != 0) {
				centralProxies = hashtable.values().toArray(new TiBLECentralProxy[0]);
			}
		}
		return centralProxies;
	}

	public void setSubscribedCentrals(boolean isSubscribe, String uuid, TiBLECentralProxy centralProxy)
	{
		Hashtable<String, TiBLECentralProxy> innerMap = subscribedCentralsProxies.get(uuid);
		if (isSubscribe) {
			if (innerMap == null) {
				subscribedCentralsProxies.put(uuid, new Hashtable<String, TiBLECentralProxy>() {
					{
						put(centralProxy.address(), centralProxy);
					}
				});
			} else {
				innerMap.put(centralProxy.address(), centralProxy);
			}
		} else {
			if (innerMap != null) {
				innerMap.remove(centralProxy.address());
			}
		}
	}

	public void releaseSubscribedCentrals()
	{
		subscribedCentralsProxies.clear();
		subscribedCentrals = null;
	}
}
