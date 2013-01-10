package com.backyard.backyard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class ShowSettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
     }

}
