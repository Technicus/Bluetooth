package com.colecago.santa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class BtSPPHelper {

	private final String TAG = getClass().getSimpleName();

	private static final boolean D = true;

	public enum State {
		NONE, LISTEN, CONNECTING, CONNECTED;
	}

	// name for the sdp record when creating server socket
	private static final String NAME = "BluetoothTest";

	// unique UUID for this app
	private static final UUID SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// member fields
	private final BluetoothAdapter mAdapter;

	private final BtHelperHandler mHandler;

	private AcceptThread mAcceptThread;

	private ConnectThread mConnectThread;

	private ConnectedThread mConnectedThread;

	private State mState;

	private Context mContext;

	private String inMessage = "";

	// constructor prepares new bluetooth spp session
	public BtSPPHelper(Context context, BtHelperHandler handler) {

		mContext = context;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = State.NONE;
		mHandler = handler;
	}

	// set the current state of the connection
	private synchronized void setState(State state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// give new state to handler so ui can update
		mHandler.obtainMessage(BtHelperHandler.MessageType.STATE, -1, state)
				.sendToTarget();
	}

	// return conn state
	public synchronized State getState() {
		return mState;
	}

	public synchronized String getMessage() {
		String returnMessage = inMessage;
		inMessage = "";
		return returnMessage;
		/*
		 * byte[] tempBytes = new byte[byteIndex]; for (int i = 0; i <
		 * byteIndex; i++){ tempBytes[i] = inBytes[i]; inBytes[i] = 0; }
		 * byteIndex = 0; return tempBytes;
		 */
	}

	// start session. start acceptthread to begin a session in listening(server)
	// mode.

	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// start the thread to listen
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(State.LISTEN);
	}

	// start the connectthread to initiate a connection
	public synchronized void connect(BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connect to: " + device);

		// cancel any thread attempting to make a connection
		if (mState == State.CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// start the thread to conect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(State.CONNECTING);
	}

	// start connectedThread to begin mananging a bluetooth connection

	private synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");

		// cancel the thread that completed this connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// cancel any thread currently currning a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// cancel the accept thread because we only want to connect to one
		// device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// send the name of the device back to the UI
		mHandler.obtainMessage(BtHelperHandler.MessageType.DEVICE, -1,
				device.getName()).sendToTarget();
		setState(State.CONNECTED);

	}

	// stop all threads
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		setState(State.NONE);
	}

	// write to the connected thread
	public void write(byte[] out) {
		ConnectedThread r;

		// synchronize a copy of connectedthread
		synchronized (this) {
			if (mState != State.CONNECTED)
				return;
			r = mConnectedThread;
		}

		// preform write unsynchronized
		Log.d(TAG, "WRITING " + out);
		r.write(out);
	}

	// write to the connected thread
	public void write(byte[] out, int length) {
		ConnectedThread r;
		byte[] tempBytes = new byte[length];

		for (int i = 0; i < length; i++) {
			tempBytes[i] = out[i];
		}
		// synchronize a copy of connectedthread
		synchronized (this) {
			if (mState != State.CONNECTED)
				return;
			r = mConnectedThread;
		}

		// preform write unsynchronized
		// Log.d(TAG, "WRITING " + out);
		r.write(tempBytes);
	}

	// write string
	public void write(String out) {
		ConnectedThread r;

		// synchronize a copy of connectedthread
		synchronized (this) {
			if (mState != State.CONNECTED)
				return;
			r = mConnectedThread;
		}

		Log.d(TAG, "WRITING " + out);
		r.write(out.getBytes());
	}

	private void sendErrorMessage(int messageId) {
		setState(State.LISTEN);
		mHandler.obtainMessage(BtHelperHandler.MessageType.NOTIFY, -1,
				mContext.getResources().getString(messageId)).sendToTarget();
	}

	// listen for incoming connections
	private class AcceptThread extends Thread {
		// local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			// create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
						SPP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN mAcceptedThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;

			// listen to the server socket if we're not connected
			while (mState != BtSPPHelper.State.CONNECTED) {
				try {
					// this is a blocking call and will only return on a
					// successful
					// connection or exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				// if connection accepted
				if (socket != null) {
					synchronized (BtSPPHelper.this) {
						switch (mState) {
						case LISTEN:
						case CONNECTING:
							// situation normal. start the connected thread.
							connected(socket, socket.getRemoteDevice());
							break;
						case NONE:
						case CONNECTED:
							// either not ready or alread connected
							// terminate new socket
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "cancel " + this);

			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	// this thread runs while attempting to make an outgoing connection with
	// a device
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;

		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// get a bluetoothsocket for a connection with device
			try {
				tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// always cancel discovery because it will slow down a
			// connection
			mAdapter.cancelDiscovery();

			// make a connection
			try {
				// this is a blocking call, will only return on success or
				// exception
				mmSocket.connect();
			} catch (IOException e) {
				sendErrorMessage(R.string.bt_unable);
				// close socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close() socket durring connection failure",
							e2);
				}
				// start service or restart listening
				BtSPPHelper.this.start();
				return;
			}

			// reset the connectthread
			synchronized (BtSPPHelper.this) {
				mConnectThread = null;
			}

			// start the connected thread
			connected(mmSocket, mmDevice);

		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}

	}

	// this thread runs during a connection with a remote device
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;

		private final InputStream mmInStream;

		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// get the bluetoothsocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			int bytes;
			byte[] buffer = new byte[1024];

			// keep listening while connected
			while (true) {
				try {
					// try read
					bytes = mmInStream.read(buffer);
					/*
					 * for (int i = 0; i < bytes; i++){ inBytes[(byteIndex + i)]
					 * = buffer[i]; } byteIndex = byteIndex + bytes;
					 */
					inMessage = inMessage.concat(new String(buffer, 0, bytes));

					// send to UI Activity
					mHandler.obtainMessage(BtHelperHandler.MessageType.READ,
							bytes, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					sendErrorMessage(R.string.bt_connection_lost);
					break;
				}
			}
		}

		// write to output stream
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// share message with UI Activity
				mHandler.obtainMessage(BtHelperHandler.MessageType.WRITE, -1,
						buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}

	}

}
