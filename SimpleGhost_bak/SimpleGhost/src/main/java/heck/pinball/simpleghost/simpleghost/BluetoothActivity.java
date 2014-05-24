package heck.pinball.simpleghost.simpleghost;

/**
 * BluetoothActivity is an activity that monitors a Bluetooth
 * accessory device and uses the input from that device to report status
 * updates to the MainAcitivity.
 */


import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//More imports
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/*
public class BluetoothActivity extends Activity implements Runnable {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }



            public void run() {}}
*/
/*
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();

                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception
                    mmSocket.connect();
                } catch (IOException connectException) {
                    // Unable to connect; close the socket and get out
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) { }
                    return;
                }

                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(mmSocket);
            }

            /** Will cancel an in-progress connection, and close the socket */
      //      public void cancel() {
    //            try {
      //              mmSocket.close();
        //        } catch (IOException e) { }
   //         }
   //     }

        /*
         * This Handler receives messages from the polling thread and
         * injects them into the GameActivity methods on the main thread.
         *
         * It also responds to connect events posted from the connect thread
         * in order to update the UI state.
         */
 //       Handler mHandler = new Handler() {
 //           @Override
  //          public void handleMessage(Message msg) {
             /*   switch (msg.what) {
                    case MESSAGE_CONNECTED:
                        beginGame();
                    case MESSAGE_CONNECTFAIL:
                        if (mProgress != null) {
                            mProgress.dismiss();
                            mProgress = null;
                        }
                        break;

                    case MESSAGE_SWITCH:
                        SwitchMsg o = (SwitchMsg) msg.obj;
                        handleSwitchMessage(o);
                        break;

                    case MESSAGE_JOY:
                        JoyMsg j = (JoyMsg) msg.obj;
                        handleJoyMessage(j);
                        break;

                    case MESSAGE_VIBE:
                        try {
                            byte[] v = (byte[]) msg.obj;
                            mOutputStream.write(v);
                            mOutputStream.flush();
                        } catch (IOException e) {
                            Log.w("AccessoryController", "Error writing vibe output");
                        }
                        break;
                } */
