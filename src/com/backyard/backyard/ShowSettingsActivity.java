package com.backyard.backyard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ShowSettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        
     }

}
