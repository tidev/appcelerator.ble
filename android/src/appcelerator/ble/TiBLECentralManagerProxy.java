/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import appcelerator.ble.receivers.StateBroadcastReceiver;
import appcelerator.ble.scan.ScanManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollPromise;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLECentralManagerProxy extends KrollProxy
{
	private BluetoothAdapter btAdapter;
	private ScanManager scanManager;
	private final StateBroadcastReceiver stateReceiver;
	private TiBLEPeripheralProvider peripheralProvider;
	private TiBLEManageCentralService bleService;
	private Intent serviceIntent = new Intent(TiApplication.getInstance(), TiBLEManageCentralService.class);
	private TiBLEPeripheralProxy peripheralProxy;
	private boolean autoConnect;

	public TiBLECentralManagerProxy()
	{
		final Context context = TiApplication.getInstance();
		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager != null) {
			btAdapter = bluetoothManager.getAdapter();
		}
		peripheralProvider = new TiBLEPeripheralProvider();
		if (btAdapter != null) {
			scanManager = ScanManager.build(btAdapter, scanListener);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateReceiver = new StateBroadcastReceiver(this);
		context.registerReceiver(stateReceiver, intentFilter);
	}

	private static final String LCAT = "TiBLECentralManager";

	private final ServiceConnection bleServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.d(LCAT, "onServiceConnected(): Service is Binded");
			bleService = ((TiBLEManageCentralService.LocalBinder) service).getService();
			bleService.initiateConnectionWithPeripheral(TiBLECentralManagerProxy.this, peripheralProxy, autoConnect);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			bleService = null;
			peripheralProxy = null;
			autoConnect = false;
			Log.d(LCAT, "onServiceDisconnected(): Service is unBinded");
		}
	};

	@Kroll.method
	public boolean isAccessFineLocationPermissionGranted()
	{
		int result;
		TiApplication context = TiApplication.getInstance();
		if (Build.VERSION.SDK_INT >= 23) {
			result = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
		} else {
			result = context.getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
																 context.getPackageName());
		}
		return (result == PackageManager.PERMISSION_GRANTED);
	}

	@Kroll.method
	public KrollPromise<KrollDict>
	requestAccessFineLocationPermission(@Kroll.argument(optional = true) KrollFunction permissionCallback)
	{
		final KrollObject krollObject = getKrollObject();
		return KrollPromise.create((promise) -> {
			// Do not continue if we already have permission.
			if (isAccessFineLocationPermissionGranted()) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(0, null);
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.resolve(responseData);
				return;
			} else if (Build.VERSION.SDK_INT < 23) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(-1, "Location permission not defined in manifest.");
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.reject(new Throwable(responseData.getString(TiC.EVENT_PROPERTY_ERROR)));
				return;
			}

			// Do not continue if there is no activity to host the request dialog.
			Activity activity = TiApplication.getInstance().getCurrentActivity();
			if (activity == null) {
				KrollDict responseData = new KrollDict();
				responseData.putCodeAndMessage(-1, "There are no activities to host the permission request dialog.");
				if (permissionCallback != null) {
					permissionCallback.callAsync(krollObject, responseData);
				}
				promise.reject(new Throwable(responseData.getString(TiC.EVENT_PROPERTY_ERROR)));
				return;
			}

			// Show dialog requesting permission.
			ArrayList<String> permissionList = new ArrayList<>(2);
			permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
			if (Build.VERSION.SDK_INT >= 31) {
				permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			TiBaseActivity.registerPermissionRequestCallback(TiC.PERMISSION_CODE_LOCATION, permissionCallback,
															 krollObject, promise);
			activity.requestPermissions(permissionList.toArray(new String[0]), TiC.PERMISSION_CODE_LOCATION);
		});
	}

	@SuppressLint("MissingPermission")
	@Kroll.getProperty
	public int getState()
	{
		return btAdapter.getState();
	}

	@Kroll.getProperty(name = "isScanning")
	public boolean isScanning()
	{
		if (scanManager == null) {
			return false;
		}
		return scanManager.isScanningInProgress;
	}

	@Kroll.method
	public void startScan(@Kroll.argument(optional = true) KrollDict dict)
	{
		peripheralProvider.removeAllPeripheral();

		String[] servicesUUIDs = null;
		if (dict != null && dict.containsKey(KeysConstants.services.name())) {
			servicesUUIDs = dict.getStringArray(KeysConstants.services.name());
		}
		if (scanManager != null) {
			scanManager.startScan(servicesUUIDs);
		}
	}

	@Kroll.method
	public void stopScan()
	{
		if (scanManager != null) {
			scanManager.stopScan();
		}
		peripheralProvider.removeAllPeripheral();
	}

	@Kroll.getProperty
	public TiBLEPeripheralProxy[] peripherals()
	{
		return peripheralProvider.peripherals();
	}

	@Kroll.method
	public TiBLEPeripheralProxy createPeripheral(String address)
	{
		BluetoothDevice bluetoothDevice = btAdapter.getRemoteDevice(address);
		return peripheralProvider.checkAddAndGetPeripheralProxy(bluetoothDevice, null);
	}

	@Kroll.method
	public void connectPeripheral(KrollDict dict)
	{
		if (dict == null || !dict.containsKey(KeysConstants.peripheral.name())) {
			Log.e(LCAT, "connectPeripheral(): peripheral object not provided.");
			return;
		}

		TiBLEPeripheralProxy peripheralProxy = (TiBLEPeripheralProxy) dict.get(KeysConstants.peripheral.name());
		boolean autoConnect = false;
		if (dict.containsKey(KeysConstants.autoConnect.name())) {
			autoConnect = dict.getBoolean(KeysConstants.autoConnect.name());
		}
		this.peripheralProxy = peripheralProxy;
		this.autoConnect = autoConnect;
		startAndBindService();
	}

	@Kroll.method
	public void cancelPeripheralConnection(KrollDict dict)
	{
		if ((dict == null || !dict.containsKey(KeysConstants.peripheral.name()))) {
			Log.e(LCAT, "cancelPeripheralConnection(): peripheral object not provided.");
			return;
		}

		TiBLEPeripheralProxy peripheralProxy = (TiBLEPeripheralProxy) dict.get(KeysConstants.peripheral.name());
		bleService.cancelPeripheralConnection(peripheralProxy);
	}

	@Kroll.method
	public void requestMtu(KrollDict dict)
	{
		if (dict != null && dict.containsKey("mtu")) {
			bleService.requestMtu(dict.getInt("mtu"));
		}
	}

	public void cleanup()
	{
		try {
			TiApplication.getInstance().unregisterReceiver(stateReceiver);
		} catch (IllegalArgumentException e) {
			Log.e(LCAT, "cleanup(): " + e.getMessage());
		}
		stopAndUnbindService();
	}

	private void startAndBindService()
	{
		TiApplication.getInstance().startService(serviceIntent);
		Log.d(LCAT, "startAndBindService(): starting the service");
		TiApplication.getInstance().bindService(serviceIntent, bleServiceConnection, Context.BIND_AUTO_CREATE);
		Log.d(LCAT, "startAndBindService(): binding the service ");
	}

	public void stopAndUnbindService()
	{
		if (bleService != null) {
			TiApplication.getInstance().unbindService(bleServiceConnection);
			Log.d(LCAT, "stopAndUnbindService(): service unbinding initiated");
			TiApplication.getInstance().stopService(serviceIntent);
			Log.d(LCAT, "stopAndUnbindService(): service stopping..");
			bleService = null;
		}
		peripheralProxy = null;
		autoConnect = false;
	}

	public void bluetoothStateChanged(int state)
	{
		HashMap<String, Integer> dict = new HashMap<>();
		dict.put(KeysConstants.state.name(), state);
		fireEvent(KeysConstants.didUpdateState.name(), dict);
		if (state == BluetoothAdapter.STATE_OFF && bleService != null) {
			// we have bluetooth turned off and an active connection exist.
			bleService.handleBluetoothTurnedOff();
		}
	}

	private final TiBLEPeripheralProxy.IOperationHandler peripheralOperationListener =
		new TiBLEPeripheralProxy.IOperationHandler() {
			@Override
			public boolean isConnected(TiBLEPeripheralProxy peripheralProxy)
			{
				return bleService != null && bleService.isConnectedWithPeripheral(peripheralProxy);
			}

			@Override
			public void readRSSI()
			{
				bleService.readRSSI();
			}

			@Override
			public void requestConnectionPriority(int priority)
			{
				bleService.requestConnectionPriority(priority);
			}

			@Override
			public void discoverServices(String[] serviceUUIDs)
			{
				bleService.discoverServices();
			}

			@Override
			public void discoverIncludedServices(TiBLEServiceProxy serviceProxy)
			{
				bleService.discoverIncludedServices(serviceProxy);
			}

			@Override
			public void discoverCharacteristics(TiBLEServiceProxy serviceProxy)
			{
				bleService.discoverCharacteristics(serviceProxy);
			}

			@Override
			public void discoverDescriptorsForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
			{
				bleService.discoverDescriptorsForCharacteristic(characteristicProxy);
			}

			@Override
			public void readValueForCharacteristic(TiBLECharacteristicProxy characteristicProxy)
			{
				bleService.readValueForCharacteristic(characteristicProxy);
			}

			@Override
			public void writeValueForCharacteristic(TiBLECharacteristicProxy charProxy, byte[] buffer, int writeType)
			{
				bleService.writeValueForCharacteristic(charProxy, buffer, writeType);
			}

			@Override
			public void readValueForDescriptor(TiBLEDescriptorProxy descriptorProxy)
			{
				bleService.readValueForDescriptor(descriptorProxy);
			}

			@Override
			public void writeValueForDescriptor(TiBLEDescriptorProxy descriptorProxy, byte[] buffer)
			{
				bleService.writeValueForDescriptor(descriptorProxy, buffer);
			}

			@Override
			public void subscribeToCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
												  BufferProxy enableValue)
			{
				bleService.subscribeToCharacteristic(charProxy, descriptorUUID, enableValue);
			}

			@Override
			public void unsubscribeFromCharacteristic(TiBLECharacteristicProxy charProxy, String descriptorUUID,
													  BufferProxy disableValue)
			{
				bleService.unsubscribeFromCharacteristic(charProxy, descriptorUUID, disableValue);
			}

			@RequiresApi(api = Build.VERSION_CODES.Q)
			@Override
			public void openL2CAPChannel(TiBLEPeripheralProxy proxy, int psmIdentifier, boolean isEncryptionRequired)
			{
				new Thread() {
					@Override
					public void run()
					{
						KrollDict dict = new KrollDict();
						dict.put(KeysConstants.sourcePeripheral.name(), proxy);

						try {
							BluetoothSocket socket = isEncryptionRequired
														 ? proxy.getDevice().createL2capChannel(psmIdentifier)
														 : proxy.getDevice().createInsecureL2capChannel(psmIdentifier);
							socket.connect();
							TiBLEL2CAPChannelProxy channelProxy = new TiBLEL2CAPChannelProxy(psmIdentifier, socket);
							dict.put(KeysConstants.channel.name(), channelProxy);
						} catch (IOException e) {
							Log.e(LCAT, "openL2CAPChannel(): IO exception while initiating l2cap connection.", e);
							dict.put(KeysConstants.errorDescription.name(),
									 "IO exception while initiating l2cap connection." + e.getMessage());
						}

						proxy.fireEvent(KeysConstants.didOpenChannel.name(), dict);
					}
				}.start();
			}
		};

	private ScanManager.IScanDeviceFoundListener scanListener = (bluetoothDevice, i, bytes) ->
	{

		TiBLEPeripheralProxy peripheralProxy =
			peripheralProvider.checkAddAndGetPeripheralProxy(bluetoothDevice, peripheralOperationListener);

		KrollDict dict = new KrollDict();

		dict.put(KeysConstants.peripheral.name(), peripheralProxy);
		dict.put(KeysConstants.RSSI.name(), i);

		BufferProxy bufferProxy = new BufferProxy();
		bufferProxy.write(0, bytes, 0, bytes.length);
		dict.put(KeysConstants.rawAdvertisementData.name(), bufferProxy);

		fireEvent(KeysConstants.didDiscoverPeripheral.name(), dict);
	};
}
