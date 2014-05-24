package com.colecago.santa;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "MAIN";

	public static final boolean DEBUG = true;

	private static String DEVICE_ADDRESS;

	private BtSPPHelper btConn;

	private BtHelperHandler btHandler;

	private UpdateThread updateThread;

	private static boolean CONNECTED = false;
	public static boolean OVERRIDE = false;

	private Button btnConnect;

	private TextView tvDevID;
	private TextView tvMvStatus;
	private TextView tvErrStatus;

	public SeekBar sbMeter;
	private ImageView ivNeedle;
	private ImageButton ibConnect;

	public int position = 0;

	private EditText etAccess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ibConnect = (ImageButton) findViewById(R.id.ibConnect);
		ibConnect.setOnClickListener(new ButtonListener());

		sbMeter = (SeekBar) findViewById(R.id.sbMeter);
		sbMeter.setOnSeekBarChangeListener(new SlideListener());

		ivNeedle = (ImageView) findViewById(R.id.ivNeedle);

		ivNeedle.setPivotY(444);
		ivNeedle.setPivotX(86);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CONNECTED == false) {
			ibConnect.setImageResource(R.drawable.button_pressed_small);
			// btnConnect.setText(R.string.btnConnectConnect);
			// tvDevID.setText("None");

		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (updateThread != null) {
			updateThread.cancel();
			updateThread = null;
		}

		if (CONNECTED == true) {
			if (btConn != null)
				btConn.stop();
			CONNECTED = false;
			ibConnect.setImageResource(R.drawable.button_pressed_small);
			// btnConnect.setText(R.string.btnConnectConnect);
			// tvDevID.setText("None");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			DEVICE_ADDRESS = data
					.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
			Log.d(TAG, "Device Chosen: " + DEVICE_ADDRESS);
			btHandler = new BtHelperHandler();
			btConn = new BtSPPHelper(this, btHandler);
			BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(DEVICE_ADDRESS);
			btConn.start();
			btConn.connect(btDevice);
			CONNECTED = true;
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// TextView tvDevID = (TextView) findViewById(R.id.tvDevID);
					// tvDevID.setText(DEVICE_ADDRESS);
					// Button btnConnect = (Button)
					// findViewById(R.id.btnConnect);
					// btnConnect.setText(R.string.btnConnectDisconnect);
					ibConnect.setImageResource(R.drawable.button_normal_small);
				}
			});
			// makePacket();

			if (updateThread == null) {
				updateThread = new UpdateThread(btConn, btHandler);
				updateThread.start();
			} else {
				updateThread.interrupt();
				updateThread = new UpdateThread(btConn, btHandler);
				updateThread.start();
			}

		} catch (Exception e) {
			Log.e(TAG, "Error in Result", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Displaying options menu");
		super.getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu");
		menu.findItem(R.id.menu_settings).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about_info:
			Log.d(TAG, "menu about");
			super.startActivity(new Intent(this, AboutInfoActivity.class));
			return true;

		case R.id.menu_settings:
			Log.d(TAG, "menu settings");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private String getPacket() {
		String message;

		try {
			;
			// devIDInt = Integer.parseInt(etAccess.getText().toString());
		} catch (Exception e) {
			;
			// devIDInt = 0;
		}

		message = "!";
		// message += makeTwoDig(devIDInt);
		// message += command;
		message += makeTwoDig(sbMeter.getProgress() + 1);
		// if ((sbMeter.getProgress() + 1) >= 10)
		// message += 0;
		// else
		// message += sbMeter.getProgress() + 1;
		message += (char) (0x0D);

		// Log.d(TAG, "Packet is : " + packet);

		return message;
	}

	private void sendPacket(String packet) {
		btConn.write(packet);
	}

	private String makeTwoDig(int checkInt) {
		if (Math.abs(checkInt) < 10)
			return ("0" + checkInt);
		else
			return ("" + checkInt);
	}

	private class SlideListener implements OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			if (CONNECTED) {
				MainActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						// SeekBar sbMeter = (SeekBar)
						// findViewById(R.id.sbMeter);
						// Log.d("MAIN", "position " + sbMeter.getProgress());
						// ivNeedle.setRotation((sbMeter.getProgress() - 11)*6);
					}
				});

				sendPacket(getPacket());
			}
		}

		public void onStartTrackingTouch(SeekBar arg0) {
			OVERRIDE = true;
		}

		public void onStopTrackingTouch(SeekBar arg0) {
			OVERRIDE = false;
			// SeekBar skPosition = (SeekBar) findViewById(R.id.sbSpeed);
		}

	}

	private class ButtonListener implements OnClickListener {
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case (R.id.ibConnect):
				// default:
				if (DEBUG)
					Log.d(TAG, "Connect Pressed");
				if (!CONNECTED) {
					CONNECTED = true;
					Intent intent = new Intent(MainActivity.this,
							DeviceListActivity.class);
					startActivityForResult(intent, 1);
				} else {
					btConn.stop();
					CONNECTED = false;
					ibConnect.setImageResource(R.drawable.button_pressed_small);
					// tvDevID.setText("None");
				}
				break;

			}
			// sendPacket(getPacket());
		}
	}

	private class UpdateThread extends Thread {
		private boolean running = false;
		private BtSPPHelper btConn;
		private String tempMSG;

		// private int index;
		// private int[] testing = {90, 90, 110, 180, 90, 90, 70, 00};

		public UpdateThread(BtSPPHelper btConn, BtHelperHandler btHandler) {
			this.btConn = btConn;

			running = true;
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// Button btnConnect = (Button)
					// findViewById(R.id.btnConnect);
					// btnConnect.setText(R.string.button_disconnect);
				}
			});
		}

		public void run() {
			while (running)
				try {
					Thread.sleep(250);
					tempMSG = btConn.getMessage();
					if (!tempMSG.isEmpty()) {
						Log.d(TAG, "RCVD MESSAGE: " + tempMSG);
						decodeMessage(tempMSG);
					}

				} catch (InterruptedException interrupt) {
					Log.e(TAG, "sleep interrupted", interrupt);
					this.cancel();
				} catch (Exception e) {
					Log.e(TAG, "other thread exception", e);
					this.cancel();
				}

		}

		public void decodeMessage(final String inString) {
			MainActivity.this.runOnUiThread(new Runnable() {
				int tempIndex = -1;
				char tempchar1, tempchar2;

				public void run() {
					try {
						tempIndex = inString.indexOf(0x21);
						if (tempIndex != -1) {
							tempchar1 = inString.charAt(tempIndex + 1);
							tempchar2 = inString.charAt(tempIndex + 2);
							
							position = Integer.parseInt(Character.toString(tempchar1) + Character.toString(tempchar2)) - 1;
							if (!OVERRIDE){
								sbMeter.setProgress(position);
							}
							else{
								if (position != sbMeter.getProgress())
									sendPacket(getPacket());
							}
							ivNeedle.setRotation((position - 11)*6);
						}
					} catch (Exception e) {

					}

				}
			});
		}

		public void cancel() {
			running = false;
			MainActivity.CONNECTED = false;
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// Button btnConnect = (Button)
					// findViewById(R.id.btnConnect);
					// btnConnect.setText(R.string.button_connect);
				}
			});
			// btConn.stop();
		}
	}
}
