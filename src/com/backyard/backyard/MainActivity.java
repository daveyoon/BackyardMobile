package com.backyard.backyard;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	 private LocationManager locationManager;
	 private String provider;

	 private TextView latituteField;
	  private TextView longitudeField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latituteField = (TextView) findViewById(R.id.lat);
        longitudeField = (TextView) findViewById(R.id.lng);
        //setContentView(R.layout.location);
     // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        // Get the string array
        String[] companies = getResources().getStringArray(R.array.company_arrays);
        // Create the adapter and set it to the AutoCompleteTextView 
        ArrayAdapter<String> adapter = 
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, companies);
        textView.setAdapter(adapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
          System.out.println("Provider " + provider + " has been selected.");
          onLocationChanged(location);
        } else {
        	  latituteField.setText("Location not available");
              longitudeField.setText("Location not available");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
      int lat = (int) (location.getLatitude());
      int lng = (int) (location.getLongitude());
      Toast.makeText(this, "Latitude " +lat + "and Longitude"+ lng ,
              Toast.LENGTH_SHORT).show();
      
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
      Toast.makeText(this, "Enabled new provider " + provider,
          Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
      Toast.makeText(this, "Disabled provider " + provider,
          Toast.LENGTH_SHORT).show();
    }
    public void GetLocation(View v)
    {
    	Location location = locationManager.getLastKnownLocation(provider);
    	onLocationChanged(location);
    }
    public void addmultimedia(View v)
    {
    	Intent intent = new Intent(this, AddMultimedia.class);
    	startActivity(intent);
    	
    }
}
