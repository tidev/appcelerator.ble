/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import static android.content.Context.BIND_AUTO_CREATE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import appcelerator.ble.TiBLECharacteristicProxy;
import appcelerator.ble.TiBLEServiceProxy;
import appcelerator.ble.receivers.StateBroadcastReceiver;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLEPeripheralManagerProxy extends KrollProxy
{
	private static final String LCAT = "TiBLEPeripheralManager";
	private final BluetoothAdapter btAdapter;
	private final StateBroadcastReceiver stateReceiver;
	private TiBLEManagePeripheralService bleService;
	private BluetoothGattService gattServiceToAddInServer;
	private Intent serviceIntent = new Intent(TiApplication.getInstance(), TiBLEManagePeripheralService.class);

	public TiBLEPeripheralManagerProxy()
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateReceiver = new StateBroadcastReceiver(this);
		getActivity().registerReceiver(stateReceiver, intentFilter);
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty(name = "peripheralManagerState")
	public int peripheralManagerState()
	{
		return btAdapter.getState();
	}

	private final ServiceConnection bleServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.d(LCAT, "onServiceConnected(): Service is Binded");
			bleService = ((TiBLEManagePeripheralService.LocalBinder) service).getService();
			bleService.initialisePeripheralAndOpenGattServer(TiBLEPeripheralManagerProxy.this);
			bleService.addService(gattServiceToAddInServer);
			gattServiceToAddInServer = null;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			bleService = null;
			Log.d(LCAT, "onServiceDisconnected(): Service is unBinded");
		}
	};

	private void startAndBindService()
	{
		TiApplication.getInstance().startService(serviceIntent);
		Log.d(LCAT, "startAndBindService(): starting the service");
		TiApplication.getInstance().bindService(serviceIntent, bleServiceConnection, BIND_AUTO_CREATE);
		Log.d(LCAT, "startAndBindService(): binding the service ");
	}

	private void stopAndUnbindService()
	{
		if (bleService != null) {
			TiApplication.getInstance().unbindService(bleServiceConnection);
			Log.d(LCAT, "stopAndUnbindService(): service unbinding initiated");
			TiApplication.getInstance().stopService(serviceIntent);
			Log.d(LCAT, "stopAndUnbindService(): service stopping..");
			bleService = null;
		}
	}

	@Kroll.method
	public TiBLEServiceProxy addService(KrollDict dict)
	{
		TiBLEServiceProxy serviceProxy = TiBLEServiceProxy.createServiceProxy(dict);
		if (serviceProxy == null) {
			Log.e(LCAT, "addService(): Unable to add service");
			return null;
		}
		if (bleService == null) {
			startAndBindService();
			gattServiceToAddInServer = serviceProxy.getService();
			return serviceProxy;
		}
		bleService.addService(serviceProxy.getService());
		return serviceProxy;
	}

	@Kroll.method
	public void removeService(TiBLEServiceProxy serviceProxy)
	{
		if (bleService == null) {
			Log.e(LCAT, "removeService(): Cannot remove service, GATT server not opened");
			return;
		}
		bleService.removeServiceFromServer(serviceProxy.getService());
	}

	@Kroll.method
	public void removeAllServices()
	{
		if (bleService == null) {
			Log.e(LCAT, "removeAllServices(): Cannot remove all service, GATT server not opened");
			return;
		}
		bleService.removeAllServicesFromServer();
	}

	@Kroll.method
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void startAdvertising(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("serviceUUIDs") || !dict.containsKey("localName")) {
			Log.e(LCAT, "startAdvertising(): Cannot start Advertising, required parameters not provided");
			return;
		}
		if (bleService == null) {
			Log.e(LCAT, "startAdvertising(): Cannot start Advertising, GATT server not opened");
			return;
		}
		Object[] serviceUUIDObjects = (Object[]) dict.get("serviceUUIDs");
		boolean name = (boolean) dict.get("localName");
		if (serviceUUIDObjects != null) {
			String[] uuids = new String[serviceUUIDObjects.length];
			for (int i = 0; i < serviceUUIDObjects.length; i++) {
				uuids[i] = (String) serviceUUIDObjects[i];
			}
			bleService.startAdvertising(uuids, name);
		}
	}

	@Kroll.method
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void stopAdvertising()
	{
		if (bleService == null) {
			Log.e(LCAT, "stopAdvertising(): Cannot stop Advertising, GATT server not opened");
			return;
		}
		bleService.stopAdvertising();
	}

	@Kroll.getProperty(name = "isAdvertising")
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public boolean isAdvertising()
	{
		if (bleService == null) {
			Log.e(LCAT, "isAdvertising(): Advertising is not going on, GATT server not opened");
			return false;
		}
		return bleService.isLEAdvertising();
	}

	@Kroll.method(name = "updateValue")
	public boolean updateCharacteristicAndNotifySubscribers(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("characteristic") || !dict.containsKey("data")) {
			Log.e(LCAT,
				  "updateCharacteristicAndNotifySubscribers(): Cannot update value, required parameters not provided");
			return false;
		}
		if (bleService == null) {
			Log.e(LCAT, "updateCharacteristicAndNotifySubscribers(): Cannot update value, GATT server not opened");
			return false;
		}
		TiBLECharacteristicProxy characteristicProxy = (TiBLECharacteristicProxy) dict.get("characteristic");
		BluetoothGattCharacteristic characteristic = characteristicProxy.getCharacteristic();
		BufferProxy value = (BufferProxy) dict.get("data");
		if (dict.containsKey("centrals")) {
			Object[] centralsObjects = (Object[]) dict.get("centrals");
			if (centralsObjects != null) {
				TiBLECentralProxy[] centralProxies = new TiBLECentralProxy[centralsObjects.length];
				for (int i = 0; i < centralsObjects.length; i++) {
					centralProxies[i] = (TiBLECentralProxy) centralsObjects[i];
				}
				return bleService.updateCharacteristicAndNotifySubscribers(characteristic, value, centralProxies);
			}

		} else {
			return bleService.updateCharacteristicAndNotifySubscribers(characteristic, value, null);
		}
		return false;
	}

	@Kroll.method(name = "respondToRequest")
	public void respondToCharacteristicRequest(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("request") || !dict.containsKey("result")) {
			Log.e(
				LCAT,
				"respondToCharacteristicRequest(): Cannot respond to characteristic request, required parameters not provided");
			return;
		}
		if (bleService == null) {
			Log.e(LCAT,
				  "respondToCharacteristicRequest(): Cannot respond to characteristic request, GATT server not opened");
			return;
		}
		TiBLECharacteristicRequestProxy requestProxy = (TiBLECharacteristicRequestProxy) dict.get("request");
		int result = (int) dict.get("result");
		bleService.sendResponseToCharacteristic(requestProxy, result);
	}

	@Kroll.method
	public void respondToDescriptorRequest(KrollDict dict)
	{
		if (dict == null || !dict.containsKey("descriptorRequest") || !dict.containsKey("result")) {
			Log.e(
				LCAT,
				"respondToDescriptorRequest(): Cannot respond to descriptor request, required parameters not provided");
			return;
		}
		if (bleService == null) {
			Log.e(LCAT, "respondToDescriptorRequest(): Cannot respond to descriptor request, GATT server not opened");
			return;
		}
		TiBLEDescriptorRequestProxy descriptorRequestProxy =
			(TiBLEDescriptorRequestProxy) dict.get("descriptorRequest");
		int result = (int) dict.get("result");
		bleService.sendResponseToDescriptor(descriptorRequestProxy, result);
	}

	public void cleanup()
	{
		try {
			getActivity().unregisterReceiver(stateReceiver);
		} catch (IllegalArgumentException e) {
			Log.e(LCAT, "cleanup(): Error during unregistering the receiver," + e.getMessage());
		}
		if (bleService != null) {
			closePeripheral();
		}
	}

	@Kroll.method
	public void closePeripheral()
	{
		if (bleService == null) {
			Log.e(LCAT, "closePeripheral(): Cannot close peripheral as GATT server already not opened.");
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (isAdvertising()) {
				bleService.stopAdvertising();
			}
		}
		bleService.closeGattServer();
		stopAndUnbindService();
	}

	public void bluetoothStateChanged(int state)
	{
		HashMap<String, Integer> dict = new HashMap<>();
		dict.put("state", state);
		fireEvent("didUpdateState", dict);
		if (state == BluetoothAdapter.STATE_OFF && bleService != null) {
			closePeripheral();
		}
	}
}
