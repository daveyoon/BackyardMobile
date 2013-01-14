package com.backyard.backyard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

public class Backyardhome extends Activity {
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	//database 
	ReportDataSQLHelper reportdata;
	private String[] allColumns = { ReportDataSQLHelper.SECTOR, ReportDataSQLHelper.ISSUE,ReportDataSQLHelper.DESC,ReportDataSQLHelper.LATITUDE,ReportDataSQLHelper.LONGITUDE,ReportDataSQLHelper.PHOTO , ReportDataSQLHelper.VIDEO , ReportDataSQLHelper.TIME};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
        startActivity(new Intent(this, ShowSettingsActivity.class));
        return true;
        default:
        return super.onOptionsItemSelected(item);
        }
    }
    public void newreport(View v)
    {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    	
    }
    public void viewreports(View v)
    {
    	Intent intent = new Intent(this, ViewReports.class);
    	startActivity(intent);
    	
    }
    public void help(View v)
    {
    	Intent intent = new Intent(this, Help.class);
    	startActivity(intent);
    	
    }
    public void Sync(View v)
    {
    	// prepare for a progress bar dialog
		progressBar = new ProgressDialog(v.getContext());
		progressBar.setCancelable(true);
		progressBar.setMessage("Sourcemap Syncing ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();
		//reset progress bar status
		progressBarStatus = 0;
		new Thread(new Runnable() {
			  public void run() {
				while (progressBarStatus < 100) {

				  // process some tasks
				  progressBarStatus = doSync();

				  // your computer is too fast, sleep 1 second
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
					e.printStackTrace();
				  }

				  // Update the progress bar
				  progressBarHandler.post(new Runnable() {
					public void run() {
					  progressBar.setProgress(progressBarStatus);
					}
				  });
				}

				// ok, file is downloaded,
				if (progressBarStatus >= 100) {

					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// close the progress bar dialog
					progressBar.dismiss();
				}
				// ok, kill thread,
				if (progressBarStatus == 1000) {
					Thread.currentThread().interrupt();
					// close the progress bar dialog
					progressBar.dismiss();
					
					
					
		    		 
				}
			  }
		       }).start();
    }
    
    public int doSync()
    {
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	String username = sharedPref.getString("sourcemapusername",null);
    	String sourcemappass = sharedPref.getString("sourcemappassword",null);
    	int percentage = 0;
    	if ((username != null) && (sourcemappass != null)){
    	//we get records from the database
        Log.d("fetch: ", "Syncing ..");
        String password = "foo123";
        reportdata = new ReportDataSQLHelper(this);
        SQLiteDatabase.loadLibs(this);
        SQLiteDatabase db = reportdata.getWritableDatabase(password);
    	long totalrecords = fetchrecords(db);
    	Log.d("records: ", " "+ totalrecords +"");
    	Integer records = (int) (long) totalrecords;
    	String selectQuery = "SELECT  * FROM " + ReportDataSQLHelper.TABLE + " WHERE SYNC = '0'";
    	//Cursor cursor = db.query(ReportDataSQLHelper.TABLE,allColumns, null, null, null, null, null);
    	Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int record = 1;

    	if (records == 0)
    	{
    		percentage = 101;
    	}
        while (!cursor.isAfterLast() && records != 0) {
          Report report = cursorToReport(cursor);
          Log.d("sector", report._sector);
          Log.d("desc",report._desc);
          Log.d("lat",report._latitude);
          Log.d("lng",report._longitude);
          Log.d("issue",report._issue);
          Log.d("photo",report._photo);
          Log.d("video",report._video);
          
          //we now push the report to sourcemap
          sourcemappush(report,username,sourcemappass);
          updateReport(report);
          record = record + 1;
          percentage = (int) (record/totalrecords) * 100;
          cursor.moveToNext();
          //Log.d("percent:",""+percentage+"");
          progressBarStatus = percentage;
          //Log.d("progress status",""+mProgressStatus+"");
        }
        // Make sure to close the cursor
        cursor.close();
    	
    	} else {
    		progressBarStatus = 1000;
    		progressBar.dismiss();
    
    		
    		 
    	}
    	return percentage;
    }
    public int updateReport(Report report) {
        String password = "foo123";
        reportdata = new ReportDataSQLHelper(this);
        SQLiteDatabase.loadLibs(this);
        SQLiteDatabase db = reportdata.getWritableDatabase(password);
     
        ContentValues values = new ContentValues();
        values.put(ReportDataSQLHelper.SYNC, "1");
        Log.d("id",""+report.getID()+"");
        // updating row
        return db.update(ReportDataSQLHelper.TABLE, values, BaseColumns._ID + " = ?",
                new String[] { String.valueOf(report.getID()) });
        
        
    }
    public void sourcemappush(Report report,String username, String pass)
    {
    	Random random = new Random();
    	HttpClient httpClient = new DefaultHttpClient();
    	String map_set = null;
    	HttpPost request = new HttpPost("http://beta.mysourcemap.com/member/login?format=json");
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("username", "jmwenda"));  
    	nameValuePairs.add(new BasicNameValuePair("password", "DaYu2005"));
    	nameValuePairs.add(new BasicNameValuePair("submit", "Login"));
    	nameValuePairs.add(new BasicNameValuePair("api_key", random.toString()));
    	HttpParams params = new BasicHttpParams();
    	params.setParameter("username", username);
    	params.setParameter("password", pass);
    	params.setParameter("api_key", random.toString());
    	params.setParameter("submit", "Login");
    	
    	request.setParams(params);
    	
    	if (report._sector.equals("Forestry"))
    	{
    		map_set = "50f264a03988cc163c00000e";
    	}
    	if (report._sector.equals("Mining"))
    	{
    		map_set = "50f2648b3988cc3d4000000f";
    	}
    	if (report._sector.equals("Agriculture"))
    	{
    		map_set =  "50f264283988cc533e00000c";
    		
    	}
    	
    	try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			request.setParams(params);
			try {
				//String responseBody = EntityUtils.toString(request.getEntity());
				//Log.d("request body", responseBody);
				HttpResponse response = httpClient.execute(request);
				HttpEntity resEntityGet;
				String objectid;
				String object;
				
				Log.d("sector", report._sector);

				
				Log.d("response: ", " "+ response.getStatusLine().getStatusCode() +"");
				
				Log.d("request user", request.getParams().getParameter("username").toString());
				Log.d("request user", request.getParams().getParameter("password").toString());
				ArrayList<NameValuePair> attpairs = new ArrayList<NameValuePair>();
				attpairs.add(new BasicNameValuePair("issue", "test issue")); 
				HttpPost requestpost = new HttpPost("http://beta.mysourcemap.com/thing/update/null?format=json");
				ArrayList<NameValuePair> postValuePairs = new ArrayList<NameValuePair>();
				postValuePairs.add(new BasicNameValuePair("name", report._sector)); 
		    	postValuePairs.add(new BasicNameValuePair("description",report._desc));
		    	postValuePairs.add(new BasicNameValuePair("address",report._latitude +", " + report._longitude ));
		    	postValuePairs.add(new BasicNameValuePair("type", "site"));
		    	postValuePairs.add(new BasicNameValuePair("attributes[issue]", report._issue));
		    	postValuePairs.add(new BasicNameValuePair("set",map_set));
		    	Log.d("postvalues",""+postValuePairs+"");
		    	requestpost.setEntity(new UrlEncodedFormEntity(postValuePairs));
		    	HttpResponse responsepost = httpClient.execute(requestpost);
		    	resEntityGet = responsepost.getEntity();
		    	object = EntityUtils.toString(resEntityGet);
		    	
		    	try {
		    		JSONObject jObject = new JSONObject(object);
		    		JSONObject object_id = jObject.getJSONObject("_id");
					objectid = object_id.getString("$id");
					
					Log.d("report photo",report._photo);
					File videofile = new File(report._video);
					//FileBody photobin = new FileBody(p);
					Log.d("filephoto", videofile.toURI().toString());
					pushfiles(httpClient,report._video,"video",objectid);
					pushfiles(httpClient,report._photo,"photo",objectid);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	//Log.d("request user", request.getParams().getParameter("api").toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void pushfiles(HttpClient httpClient,String path, String type,String object)
    {
    	HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost("http://beta.mysourcemap.com/file/accept_upload?format=json");
		try 
        {
            MultipartEntity entity = new MultipartEntity();
            ContentBody cbFile = new FileBody(new File(path), "image/jpeg");
            //entity.addPart("file", new FileBody(videofile));
            entity.addPart("uploaded_file", new FileBody(new File(path)));
            entity.addPart("related_id", new StringBody(object));
            entity.addPart("related_to", new StringBody("thing"));
            entity.addPart("type", new StringBody(type));
            entity.addPart("userfile", cbFile);
            entity.addPart("video[file]", new FileBody(new File(path)));
            httpPost.setEntity(entity);


            HttpResponse responsefile = httpClient.execute(httpPost);
            HttpEntity resEntity = responsefile.getEntity();  
            //Log.d("response file",""+EntityUtils.toString(resEntity)+"");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    public long fetchrecords(SQLiteDatabase db){
    	String sql = "SELECT COUNT(*) FROM reports where SYNC = '0'";
    	SQLiteStatement statement = db.compileStatement(sql);
    	long count = statement.simpleQueryForLong();
    	return count;
    	
    }
    private Report cursorToReport(Cursor cursor) {
        Report report = new Report();
        
        report.setId(cursor.getInt(0));
        report.setSector(cursor.getString(2));
        report.setDesc(cursor.getString(5));
        report.setIssue(cursor.getString(3));
        report.setLat(cursor.getString(6));
        report.setLon(cursor.getString(7));
        report.setPhoto(cursor.getString(8));
        report.setVideo(cursor.getString(9));
        return report;
      }
}
