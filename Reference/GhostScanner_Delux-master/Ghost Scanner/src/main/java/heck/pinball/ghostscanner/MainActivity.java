package heck.pinball.ghostscanner;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
// import heck.pinball.ghostscanner.AboutFragment;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

   public static final String TAG = "MAIN";

   public static final boolean DEBUG = true;

   private static String DEVICE_ADDRESS;

   private BluetoothHandlerSPP btConn;

   private BluetoothHandler btHandler;

   private UpdateThread updateThread;

   private static boolean CONNECTED = false;
   public static boolean OVERRIDE = false;

   public SeekBar sbMeter;
   private ImageView ivNeedle;
   private ImageButton ibConnect;

   public int position = 0;

   SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    // Tab titles
    String[] tabs = { "Bluetooth", "Lights", "Solenoids", "Switches", "Motors", "About" };

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
   /*     for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
*/
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

      /*  @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        } */

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

     /*   @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5).toUpperCase(l);
                case 5:
                    // return getString(R.string.title_section6).toUpperCase(l);
                    // About fragment activity
                    return new AboutFragment();
            }
            return null;
        } */
     @Override
     public Fragment getItem(int index) {

         switch (index) {
             case 0:
                 // Bluetooth fragment activity
                 return new BluetoothFragment();
             case 1:
                 // Lights fragment activity
                 return new LightsFragment();
             case 2:
                 // Solenoids fragment activity
                 return new SolenoidsFragment();
             case 3:
                 // Switches fragment activity
                 return new SwitchesFragment();
             case 4:
                 // Motors fragment activity
                 return new MotorsFragment();
             case 5:
                 // About fragment activity
                 return new AboutFragment();
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
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

   private class UpdateThread extends Thread {
      private boolean running = false;
      private BluetoothHandlerSPP btConn;
      private String tempMSG;

      // private int index;
      // private int[] testing = {90, 90, 110, 180, 90, 90, 70, 00};

      public UpdateThread(BluetoothHandlerSPP btConn, BluetoothHandler btHandler) {
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

      private String makeTwoDig(int checkInt) {
         if (Math.abs(checkInt) < 10)
            return ("0" + checkInt);
         else
            return ("" + checkInt);
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
