package beta.delux.ghostscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Technician on 5/24/14.
 */
public class FragmentBluetooth extends Fragment {

   private Button btnConnect;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);

      return rootView;
   }

   public void L007(View view) {
      Toast.makeText(getApplicationContext(), "[L007]",
          Toast.LENGTH_SHORT).show();
   }
}