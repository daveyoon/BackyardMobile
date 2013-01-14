package com.backyard.backyard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import net.sqlcipher.database.SQLiteDatabase;
import com.backyard.backyard.AddMultimedia;

public class MainActivity extends Activity implements LocationListener{
	 private LocationManager locationManager;
	 private String provider;
	 ReportDataSQLHelper reportdata;

	 private TextView latituteField;
	  private TextView longitudeField;
	  //private TextView sector;
	  private TextView desc;
	  private TextView company;
	  private String sector;
	  private String issue;
	  private double lat;
	  private double lng;
	  private Spinner spinner,spinnertwo;
	  private String photopath="";
	  private String videopath="";
	  //for the camera functions
	  
		private Uri fileUri;
		public static final int MEDIA_TYPE_IMAGE = 1;
		public static final int MEDIA_TYPE_VIDEO = 2;
		private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
		private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //we deal with the database
        SQLiteDatabase.loadLibs(this);
        String password = "foo123";
        reportdata = new ReportDataSQLHelper(this);
        SQLiteDatabase db = reportdata.getWritableDatabase(password);
        
        //latituteField = (TextView) findViewById(R.id.lat);
        //longitudeField = (TextView) findViewById(R.id.lng);
        //sector = (TextView) findViewById(R.id.sector);
        spinner = (Spinner) findViewById(R.id.sectorvalue);
        spinnertwo = (Spinner) findViewById(R.id.spinner2);
        
        desc = (TextView) findViewById(R.id.reportfield);
        
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        // Get the string array
        String[] companies = getResources().getStringArray(R.array.company_arrays);
        // Create the adapter and set it to the AutoCompleteTextView 
        ArrayAdapter<String> adapter = 
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, companies);
        textView.setAdapter(adapter);
        company = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
          System.out.println("Provider " + provider + " has been selected.");
          onLocationChanged(location);
        } else {
        	 // latituteField.setText("Location not available");
              //longitudeField.setText("Location not available");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, ShowSettingsActivity.class));
                return true;

        }

        return false;
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
      lat = location.getLatitude();
      lng = location.getLongitude();
      //lat = (int) (location.getLatitude());
      //lng = (int) (location.getLongitude());
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
    	if (location == null)
    	{
    		LocationManager locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            provider = locmgr.getBestProvider(criteria, false);
            Location mylocation = locationManager.getLastKnownLocation(provider);
            lat = mylocation.getLatitude();
            lng = mylocation.getLongitude();
            Toast.makeText(this, "Latitude " +lat + "and Longitude"+ lng ,
                    Toast.LENGTH_SHORT).show();
    	}else {
    		onLocationChanged(location);
    	}
    	
    }
    public void addmultimedia(View v)
    {
    	Intent intent = new Intent(this, AddMultimedia.class);
    	startActivity(intent);
    	
    }
    private void addReport(String sector,String issue,String company,String desc,double lat,double lon,String photo,String video, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Log.d("sector: ", sector.toString());
        Log.d("description",desc.toString());
        values.put(ReportDataSQLHelper.TIME, System.currentTimeMillis());
        values.put(ReportDataSQLHelper.SECTOR, sector);
        values.put(ReportDataSQLHelper.ISSUE,issue);
        values.put(ReportDataSQLHelper.COMPANY, company);
        values.put(ReportDataSQLHelper.DESC, desc);
        values.put(ReportDataSQLHelper.LATITUDE, lat);
        values.put(ReportDataSQLHelper.LONGITUDE, lon);
        values.put(ReportDataSQLHelper.PHOTO,photo);
        values.put(ReportDataSQLHelper.VIDEO,video);
        Log.d("sector: ", values.toString());
        db.insert(ReportDataSQLHelper.TABLE, null, values);
        db.close();
      }

    public void submit(View V)
    {
     //we get the database connection
    Log.d("Insert: ", "Inserting ..");
    String password = "foo123";
    reportdata = new ReportDataSQLHelper(this);
    SQLiteDatabase db = reportdata.getWritableDatabase(password);
    sector = spinner.getSelectedItem().toString();
    issue = spinnertwo.getSelectedItem().toString();
    String comp = company.getText().toString();
    String description = desc.getText().toString();

    
    //Log.d("sector: ", sector);
    //Log.d("photo path", photopath);
    //Log.d("video path",videopath);
    addReport(sector,issue,comp,description,lat,lng,photopath,videopath,db);
    
    Log.d("Inserted: ", "Inseted");
    Toast.makeText(this, "Record Saved Successfully",Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(this, Backyardhome.class);
    startActivity(intent);
    	
    }
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "Backyard");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Backyard", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        Log.d("My file",String.valueOf(type));
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        //we encrypt the file
        return mediaFile;
    }
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    public void takephoto(View v)
    {
    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        File f = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Log.d("file", f.toURI().toString());
        
    	//cameraIntent.putExtra("output",f.toURI());
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,fileUri);
    	// set the image file name
    	//photopath = f.toURI().toString();
        try {
			encrypt(photopath);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	photopath = f.getAbsolutePath();
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);  
    }
    static void encrypt(String filepath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(filepath);
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(filepath);

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("BackYard2012".getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }
    
    public void takevideo(View v)
    {
    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
        File f = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        Log.d("video file", f.toURI().toString());
        
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, f.toURI());
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,fileUri);
        
    	//cameraIntent.putExtra("output",); // set the image file name
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //videopath = f.toURI().toString();
        videopath = f.getAbsolutePath();
        
    	
        startActivityForResult(cameraIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);  
    }
}
