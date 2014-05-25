package beta.delux.ghostscanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Locale;


public class ActivityMain extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

   /*simple Ghost
   private Button bluetoothOn, bluetoothOff, bluetoothVisible, bluetoothlist, L000, L007, bluetoothConnect, bluetoothDisconnect, bluetoothDiscover;
   private BluetoothAdapter mBluetoothAdapter;
   private Set<BluetoothDevice> pairedDevices;
   private ListView listViewDiscovery;
   private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   private UUID uuid = UUID.fromString("39f7b019-b0da-4a59-ad73-061d4603b514");
   //Simple Ghost*/

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

   // Tab titles
   String[] tabs = { "Bluetooth", "Lights", "Solenoids", "Switches", "Motors", "About" };

   // Thread variables
   public static final String TAG = "MAIN";

   // Bluetooth setup
   private static String DEVICE_ADDRESS;

   private HandlerBluetoothSPP btConn;

   private HandlerBluetooth btHandler;

   private ThreadUpdate_00 updateThread;

   private static boolean CONNECTED = false;
   public static boolean OVERRIDE = false;

   private BluetoothAdapter mBluetoothAdapter;
   private Button btnConnect;

   // Slider positioning
   public int position = 0;

   // Not yet implemented, imported from other meter application
   public SeekBar sbMeter;
   private ImageView ivNeedle;
   private ImageButton ibConnect;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        /*for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }*/
      for (String tab_name : tabs) {
         actionBar.addTab(actionBar.newTab().setText(tab_name)
             .setTabListener(this));
      }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

       @Override
       public int getCount() {
          // Show 6 total pages.
          return 6;
       }

       @Override
       public Fragment getItem(int index) {
          Locale l = Locale.getDefault();
          switch (index) {
             case 0:
                // Bluetooth fragment activity
                return new FragmentBluetooth();
             case 1:
                // Lights fragment activity
                return new FragmentLights();
             case 2:
                // Solenoids fragment activity
                return new FragmentSolenoids();
             case 3:
                // Switches fragment activity
                return new FragmentSwitches();
             case 4:
                // Motors fragment activity
                return new FragmentMotors();
             case 5:
                // About fragment activity
                return new FragmentAbout();
          }
          return null;
       }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

   public void bluetoothOn(View view) {
      if (!mBluetoothAdapter.isEnabled()) {
         Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(turnOn, 0);
         Toast.makeText(getApplicationContext(), "Turned on"
             , Toast.LENGTH_LONG).show();
      } else {
         Toast.makeText(getApplicationContext(), "Already on",
             Toast.LENGTH_LONG).show();
      }
   }

   public class ThreadUpdate extends Thread {
      private boolean running = false;
      private HandlerBluetoothSPP btConn;
      private String tempMSG;

      // private int index;
      // private int[] testing = {90, 90, 110, 180, 90, 90, 70, 00};

      public ThreadUpdate(HandlerBluetoothSPP btConn, HandlerBluetooth btHandler) {
         this.btConn = btConn;

         running = true;
         ActivityMain.this.runOnUiThread(new Runnable() {
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
         ActivityMain.this.runOnUiThread(new Runnable() {
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
         ActivityMain.CONNECTED = false;
         ActivityMain.this.runOnUiThread(new Runnable() {
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
