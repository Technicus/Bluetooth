package beta.ArduinoidBlue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ActivityMain extends Activity {

   private static final int REQUEST_ENABLE_BT = 1;
   private Button onBtn;
   private Button offBtn;
   private Button listBtn;
   private Button findBtn;
   private TextView text;
   private BluetoothAdapter myBluetoothAdapter;
   private Set<BluetoothDevice> pairedDevices;
   private ListView myListView;
   private ArrayAdapter<String> BTArrayAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // take an instance of BluetoothAdapter - Bluetooth radio
      myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if(myBluetoothAdapter == null) {
         onBtn.setEnabled(false);
         offBtn.setEnabled(false);
         listBtn.setEnabled(false);
         findBtn.setEnabled(false);
         text.setText("Status: not supported");

         Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
             Toast.LENGTH_LONG).show();
      } else {
         text = (TextView) findViewById(R.id.text);
         onBtn = (Button)findViewById(R.id.turnOn);
         onBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               // TODO Auto-generated method stub
               on(v);
            }
         });

         offBtn = (Button)findViewById(R.id.turnOff);
         offBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               // TODO Auto-generated method stub
               off(v);
            }
         });

         listBtn = (Button)findViewById(R.id.paired);
         listBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               // TODO Auto-generated method stub
               list(v);
            }
         });

         findBtn = (Button)findViewById(R.id.search);
         findBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               // TODO Auto-generated method stub
               find(v);
            }
         });

         myListView = (ListView)findViewById(R.id.listView1);

         // create the arrayAdapter that contains the BTDevices, and set it to the ListView
         BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
         myListView.setAdapter(BTArrayAdapter);
      }
   }

   public void on(View view){
      if (!myBluetoothAdapter.isEnabled()) {
         Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

         Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
             Toast.LENGTH_LONG).show();
      }
      else{
         Toast.makeText(getApplicationContext(),"Bluetooth is already on",
             Toast.LENGTH_LONG).show();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // TODO Auto-generated method stub
      if(requestCode == REQUEST_ENABLE_BT){
         if(myBluetoothAdapter.isEnabled()) {
            text.setText("Status: Enabled");
         } else {
            text.setText("Status: Disabled");
         }
      }
   }

   public void list(View view){
      // get paired devices
      pairedDevices = myBluetoothAdapter.getBondedDevices();

      // put it's one to the adapter
      for(BluetoothDevice device : pairedDevices)
         BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

      Toast.makeText(getApplicationContext(),"Show Paired Devices",
          Toast.LENGTH_SHORT).show();

   }

   final BroadcastReceiver bReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         // When discovery finds a device
         if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // add the name and the MAC address of the object to the arrayAdapter
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            BTArrayAdapter.notifyDataSetChanged();
         }
      }
   };

   public void find(View view) {
      if (myBluetoothAdapter.isDiscovering()) {
         // the button is pressed when it discovers, so cancel the discovery
         myBluetoothAdapter.cancelDiscovery();
      }
      else {
         BTArrayAdapter.clear();
         myBluetoothAdapter.startDiscovery();

         registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
      }
   }

   public void off(View view){
      myBluetoothAdapter.disable();
      text.setText("Status: Disconnected");

      Toast.makeText(getApplicationContext(),"Bluetooth turned off",
          Toast.LENGTH_LONG).show();
   }

   @Override
   protected void onDestroy() {
      // TODO Auto-generated method stub
      super.onDestroy();
      unregisterReceiver(bReceiver);
   }

}

/*import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ActivityMain extends Activity
{
   TextView myLabel;
   EditText myTextbox;
   BluetoothAdapter mBluetoothAdapter;
   BluetoothSocket mmSocket;
   BluetoothDevice mmDevice;
   OutputStream mmOutputStream;
   InputStream mmInputStream;
   Thread workerThread;
   byte[] readBuffer;
   int readBufferPosition;
   int counter;
   volatile boolean stopWorker;

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Button openButton = (Button)findViewById(R.id.open);
      Button sendButton = (Button)findViewById(R.id.send);
      Button closeButton = (Button)findViewById(R.id.close);
      myLabel = (TextView)findViewById(R.id.label);
      myTextbox = (EditText)findViewById(R.id.entry);

      //Open Button
      openButton.setOnClickListener(new View.OnClickListener()
      {
         public void onClick(View v)
         {
            try
            {
               findBT();
               openBT();
            }
            catch (IOException ex) { }
         }
      });

      //Send Button
      sendButton.setOnClickListener(new View.OnClickListener()
      {
         public void onClick(View v)
         {
            try
            {
               sendData();
            }
            catch (IOException ex) { }
         }
      });

      //Close button
      closeButton.setOnClickListener(new View.OnClickListener()
      {
         public void onClick(View v)
         {
            try
            {
               closeBT();
            }
            catch (IOException ex) { }
         }
      });
   }

   void findBT()
   {
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if(mBluetoothAdapter == null)
      {
         myLabel.setText("No bluetooth adapter available");
      }

      if(!mBluetoothAdapter.isEnabled())
      {
         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBluetooth, 0);
      }

      Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
      if(pairedDevices.size() > 0)
      {
         for(BluetoothDevice device : pairedDevices)
         {
            if(device.getName().equals("MattsBlueTooth"))
            {
               mmDevice = device;
               break;
            }
         }
      }
      myLabel.setText("Bluetooth Device Found");
   }

   void openBT() throws IOException
   {
      UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
      mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
      mmSocket.connect();
      mmOutputStream = mmSocket.getOutputStream();
      mmInputStream = mmSocket.getInputStream();

      beginListenForData();

      myLabel.setText("Bluetooth Opened");
   }

   void beginListenForData()
   {
      final Handler handler = new Handler();
      final byte delimiter = 10; //This is the ASCII code for a newline character

      stopWorker = false;
      readBufferPosition = 0;
      readBuffer = new byte[1024];
      workerThread = new Thread(new Runnable()
      {
         public void run()
         {
            while(!Thread.currentThread().isInterrupted() && !stopWorker)
            {
               try
               {
                  int bytesAvailable = mmInputStream.available();
                  if(bytesAvailable > 0)
                  {
                     byte[] packetBytes = new byte[bytesAvailable];
                     mmInputStream.read(packetBytes);
                     for(int i=0;i<bytesAvailable;i++)
                     {
                        byte b = packetBytes[i];
                        if(b == delimiter)
                        {
                           byte[] encodedBytes = new byte[readBufferPosition];
                           System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                           final String data = new String(encodedBytes, "US-ASCII");
                           readBufferPosition = 0;

                           handler.post(new Runnable()
                           {
                              public void run()
                              {
                                 myLabel.setText(data);
                              }
                           });
                        }
                        else
                        {
                           readBuffer[readBufferPosition++] = b;
                        }
                     }
                  }
               }
               catch (IOException ex)
               {
                  stopWorker = true;
               }
            }
         }
      });

      workerThread.start();
   }

   void sendData() throws IOException
   {
      String msg = myTextbox.getText().toString();
      msg += "\n";
      mmOutputStream.write(msg.getBytes());
      myLabel.setText("Data Sent");
   }

   void closeBT() throws IOException
   {
      stopWorker = true;
      mmOutputStream.close();
      mmInputStream.close();
      mmSocket.close();
      myLabel.setText("Bluetooth Closed");
   }
}*/

/*import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ActivityMain extends ActionBarActivity implements ActionBar.TabListener {

    *//**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     *//*
    SectionsPagerAdapter mSectionsPagerAdapter;

    *//**
     * The {@link ViewPager} that will host the section contents.
     *//*
    ViewPager mViewPager;

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
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
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

    *//**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     *//*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    *//**
     * A placeholder fragment containing a simple view.
     *//*
    public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
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
            View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);
            return rootView;
        }
    }

}*/
