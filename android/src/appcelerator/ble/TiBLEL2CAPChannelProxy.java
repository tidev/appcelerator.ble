/*
  Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
  Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
  Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import ti.modules.titanium.BufferProxy;

@Kroll.proxy
public class TiBLEL2CAPChannelProxy extends KrollProxy
{

	private static final String TAG = "TiBLEL2CAPChannelProxy";
	private final int psm;
	private final BluetoothSocket socketConnected;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private Thread writerThread;
	private volatile BlockingQueue<byte[]> sendDataQueue = new LinkedBlockingQueue<>();
	private volatile boolean isClosed = false;
	private volatile int readBufferSize = 1024;

	public TiBLEL2CAPChannelProxy(int psmIdentifier, BluetoothSocket socket) throws IOException
	{
		this.psm = psmIdentifier;
		this.socketConnected = socket;
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();

		initReader();
		initWriter();
	}

	private void initReader()
	{
		Thread readThread = new Thread(() -> {
			byte[] buffer;
			int bytesRead;
			while (!isClosed) {
				try {
					buffer = new byte[readBufferSize];
					bytesRead = inputStream.read(buffer, 0, readBufferSize);
					BufferProxy bufferProxy = new BufferProxy();
					bufferProxy.write(0, buffer, 0, bytesRead);
					KrollDict dict = new KrollDict();
					dict.put(KeysConstants.data.name(), bufferProxy);
					fireEvent(KeysConstants.onDataReceived.name(), dict);
				} catch (IOException | ArrayIndexOutOfBoundsException e) {
					if (isClosed) {
						return; //exception while reading occurred due to closing the stream.
					}
					Log.e(TAG, "Exception while reading the inputstream.", e);
					close();
					KrollDict dict = new KrollDict();
					dict.put(KeysConstants.errorDescription.name(),
							 "Exception while reading the inputstream. Exception = " + e.getMessage());
					fireEvent(KeysConstants.onStreamError.name(), dict);
					return;
				}
			}
		});
		readThread.start();
	}

	private void initWriter()
	{
		writerThread = new Thread(() -> {
			while (!isClosed) {
				byte[] data;
				try {
					data = sendDataQueue.take();
				} catch (InterruptedException e) {
					Log.d(TAG, "initWriter(): writer thread interrupted.");
					return;
				}

				try {
					Log.d(TAG, "initWriter(): initiating write operation for data size - " + data.length + "bytes");
					outputStream.write(data);
					Log.d(TAG, "initWriter(): write completed for data size - " + data.length + "bytes");
				} catch (IOException e) {
					if (isClosed) {
						return; //exception while writing occurred due to closing the stream.
					}
					Log.e(TAG, "initWriter(): exception while writing data on the output stream.", e);
					close();
					KrollDict dict = new KrollDict();
					dict.put(KeysConstants.errorDescription.name(),
							 "Exception while writing data on the output stream. Exception = " + e.getMessage());
					fireEvent(KeysConstants.onStreamError.name(), dict);
					return;
				}
			}
		});
		writerThread.start();
	}

	@Kroll.getProperty
	public int getPsm()
	{
		return psm;
	}

	@Kroll.method
	public void setReadBufferSize(int newReadBufferSize)
	{
		readBufferSize = newReadBufferSize;
	}

	@Kroll.method
	public int getReadBufferSize()
	{
		return readBufferSize;
	}

	@Kroll.method
	public void write(KrollDict dict)
	{
		if (isClosed) {
			Log.d(TAG, "write(): trying to write on the stream, but streams already been closed.");
			return;
		}

		if (dict == null || !dict.containsKey(KeysConstants.data.name())) {
			Log.d(TAG, "write(): unable to write as the data is not provided.");
			return;
		}

		sendDataQueue.offer(((BufferProxy) dict.get(KeysConstants.data.name())).getBuffer());
	}

	@Kroll.method
	public void close()
	{
		if (isClosed) {
			Log.d(TAG, "close(): trying to close the channel, but channel already closed.");
			return;
		}

		isClosed = true;

		try {
			inputStream.close();
		} catch (IOException exc) {
			Log.e(TAG, "exception while closing inputstream", exc);
		}
		try {
			outputStream.close();
		} catch (IOException exc) {
			Log.e(TAG, "exception while closing outputstream", exc);
		}

		writerThread.interrupt();
		writerThread = null;
		sendDataQueue.clear();
		sendDataQueue = null;

		try {
			socketConnected.close();
		} catch (IOException exc) {
			Log.e(TAG, "exception while closing socket", exc);
		}
	}
}
