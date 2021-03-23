/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble.peripheral;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import appcelerator.ble.KeysConstants;
import appcelerator.ble.TiBLEL2CAPChannelProxy;
import java.io.IOException;
import org.appcelerator.kroll.KrollDict;

public class TiBLEPeripheralL2capOperationManager
{

	private static final String TAG = TiBLEPeripheralL2capOperationManager.class.getSimpleName();
	private volatile boolean isPublished = false;
	private BluetoothServerSocket serverSocket;

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void publishL2CAPChannel(boolean encryptionRequired, TiBLEPeripheralManagerProxy proxy)
	{

		if (isPublished) {
			Log.d(
				TAG,
				"publishL2CAPChannel(): unable to publish as l2cap channel already listening for the incoming connection on psm = "
					+ serverSocket.getPsm());
			return;
		}

		isPublished = true;

		int psm;
		KrollDict publishEventDict = new KrollDict();
		try {
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
			serverSocket =
				encryptionRequired ? btAdapter.listenUsingL2capChannel() : btAdapter.listenUsingInsecureL2capChannel();
			psm = serverSocket.getPsm();
			publishEventDict.put(KeysConstants.psm.name(), psm);
			proxy.fireEvent(KeysConstants.didPublishL2CAPChannel.name(), publishEventDict);
		} catch (IOException e) {
			Log.e(TAG, "publishL2CAPChannel(): unable to publish l2cap channel.", e);
			isPublished = false;
			publishEventDict.put(KeysConstants.errorDescription.name(), "" + e.getMessage());
			proxy.fireEvent(KeysConstants.didPublishL2CAPChannel.name(), publishEventDict);
			return;
		}

		new Thread(() -> {
			while (isPublished) {
				KrollDict openChannelEventDict = new KrollDict();
				try {
					BluetoothSocket socket = serverSocket.accept();
					openChannelEventDict.put(KeysConstants.channel.name(), new TiBLEL2CAPChannelProxy(psm, socket));
					proxy.fireEvent(KeysConstants.didOpenL2CAPChannel.name(), openChannelEventDict);
				} catch (IOException e) {
					if (!isPublished) {
						return; // exception occurred due to explicitly closing of the serversocket.
					}
					Log.e(TAG, "publishL2CAPChannel(): Channel unpublished. IOException on serverSocket.accept()", e);
					openChannelEventDict.put(KeysConstants.errorDescription.name(), "" + e.getMessage());
					proxy.fireEvent(KeysConstants.didOpenL2CAPChannel.name(), openChannelEventDict);
					isPublished = false;
					return;
				}
			}
		}).start();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void unpublishL2CAPChannel(TiBLEPeripheralManagerProxy proxy)
	{
		if (!isPublished) {
			Log.e(TAG, "unpublishL2CAPChannel(): unable to unpublish as no l2cap channel has been published.");
			return;
		}

		isPublished = false;

		if (serverSocket != null) {
			KrollDict unpublishEventDict = new KrollDict();
			unpublishEventDict.put(KeysConstants.psm.name(), serverSocket.getPsm());
			try {
				serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "unpublishL2CAPChannel(): exception while closing serversocket.", e);
				unpublishEventDict.put(KeysConstants.errorDescription.name(), "" + e.getMessage());
			}
			proxy.fireEvent(KeysConstants.didUnpublishL2CAPChannel.name(), unpublishEventDict);
		}
	}
}
