package appcelerator.ble.peripheral;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
import appcelerator.ble.TiBLEServiceProxy;
import java.util.HashSet;
import org.appcelerator.kroll.KrollDict;

@SuppressLint("MissingPermission")
public class TiBLEPeripheralOperationManager
{
	private static final String LCAT = TiBLEPeripheralOperationManager.class.getSimpleName();
	private static final String ERROR_DESCRIPTION_KEY = "errorDescription";
	private static final String ERROR_CODE_KEY = "errorCode";
	private static final String DID_ADD_SERVICE_KEY = "didAddService";
	private TiBLEPeripheralManagerProxy peripheralManagerProxy;
	private Context context;
	private BluetoothGattServer bluetoothGattServer;
	private HashSet<BluetoothDevice> bluetoothDevices = new HashSet<>();

	public TiBLEPeripheralOperationManager(Context context, TiBLEPeripheralManagerProxy peripheralManagerProxy)
	{
		this.context = context;
		this.peripheralManagerProxy = peripheralManagerProxy;
	}

	private BluetoothGattServerCallback serverCallback = new BluetoothGattServerCallback() {
		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status, int newState)
		{
			super.onConnectionStateChange(device, status, newState);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (newState == BluetoothGatt.STATE_CONNECTED) {
					bluetoothDevices.add(device);
					Log.d(LCAT, "onConnectionStateChange(): Connected to the device: " + device.getName());
				} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
					bluetoothDevices.remove(device);
					Log.d(LCAT, "onConnectionStateChange(): Disconnected to the device: " + device.getName());
				}
			} else {
				bluetoothDevices.remove(device);
				Log.d(LCAT, "onConnectionStateChange(): Disconnected to the device: " + device.getName());
			}
		}

		@Override
		public void onServiceAdded(int status, BluetoothGattService service)
		{
			super.onServiceAdded(status, service);
			Log.d(LCAT, "onServiceAdded(): service added successfully");
			KrollDict dict = new KrollDict();
			if (status != BluetoothGatt.GATT_SUCCESS) {
				dict.put(ERROR_CODE_KEY, status);
				dict.put(ERROR_DESCRIPTION_KEY, "Unable to add service");
			} else {
				dict.put("service", new TiBLEServiceProxy(service));
			}
			peripheralManagerProxy.fireEvent(DID_ADD_SERVICE_KEY, dict);
		}
	};

	public void openGattServer()
	{
		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (bluetoothManager == null) {
			Log.e(LCAT, "openGattServer(): Cannot open the GATT server, Bluetooth not available");
			return;
		}
		bluetoothGattServer = bluetoothManager.openGattServer(context, serverCallback);
	}

	public void addService(BluetoothGattService service)
	{
		boolean isAddServiceInitiated = bluetoothGattServer.addService(service);
		if (!isAddServiceInitiated) {
			KrollDict dict = new KrollDict();
			dict.put(ERROR_CODE_KEY, BluetoothGatt.GATT_FAILURE);
			dict.put(ERROR_DESCRIPTION_KEY, "Failed to initiate the request to add service");
			peripheralManagerProxy.fireEvent(DID_ADD_SERVICE_KEY, dict);
		}
	}

	public void removeService(BluetoothGattService service)
	{
		boolean isServiceRemoved = bluetoothGattServer.removeService(service);
		if (isServiceRemoved) {
			Log.d(LCAT, "removeService(): Service removed successfully" + service.getUuid().toString());
		} else {
			Log.e(LCAT, "removeService(): Unable to remove the provided service" + service.getUuid().toString());
		}
	}

	public void clearServices()
	{
		bluetoothGattServer.clearServices();
	}

	public void closeGATTServer()
	{
		bluetoothGattServer.close();
	}
}
