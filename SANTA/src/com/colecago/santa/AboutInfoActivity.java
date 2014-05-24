
package com.colecago.santa;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AboutInfoActivity extends Activity {

    private static final String TAG = "AboutInfoActivty";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about); 
        Log.d(TAG, "onCreate AboutInfo");
    }
}
