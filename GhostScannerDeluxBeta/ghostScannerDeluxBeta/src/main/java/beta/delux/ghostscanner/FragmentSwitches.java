package beta.delux.ghostscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Created by Technician on 5/24/14.
 */
public class FragmentSwitches extends Fragment {
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_switches, container, false);

      return rootView;
   }
}