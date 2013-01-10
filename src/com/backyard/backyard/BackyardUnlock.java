package com.backyard.backyard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class BackyardUnlock extends Activity {
	private TextView unlockcode;
	SharedPreferences sharedPrefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock_screen);
        unlockcode = (TextView) findViewById(R.id.unlockcode);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //userunlockcode = sharedPrefs.getInt("userunlockcode",5555);
                                
    }
    public void unlockapp(View v)
    {
    	//Intent intent = new Intent(this, BackyardUnlock.class);
    	//startActivity(intent);
    	String userunlockcode = sharedPrefs.getString("userunlockcode","5555");
    	System.out.println("userunlockcode " + userunlockcode + " has been selected.");
    	if (unlockcode != null)
    	{
    		int unlock = Integer.parseInt(unlockcode.getText().toString());
    		int usercode = Integer.parseInt(userunlockcode);
    		//System.out.println("userunlockcode " + userunlockcode + " has been selected.");
    		if (unlock == 5555){
    		//initial set up of the app
    	    Intent intent = new Intent(this, Backyardhome.class);
    	    startActivity(intent);
    		//System.out.println("unlock code " + unlockcode.getText().toString() + " has been selected.");
    		} else if( unlock == usercode){
    			
        	    Intent intent = new Intent(this, Backyardhome.class);
        	    startActivity(intent);
    		}    	
    	}
    	
    }
}
