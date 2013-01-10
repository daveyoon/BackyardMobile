package com.backyard.backyard;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import net.sqlcipher.database.SQLiteDatabase;

public class ViewReports extends ListActivity {
	private SQLiteDatabase database;
	 private String[] allColumns = { ReportDataSQLHelper.SECTOR, ReportDataSQLHelper.COMPANY, ReportDataSQLHelper.TIME};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.viewreports);
        //we deal with the database
        SQLiteDatabase.loadLibs(this);
        String password = "foo123";
        ReportDataSQLHelper helper = new ReportDataSQLHelper(this);
        database = helper.getWritableDatabase(password);
        List<Report> values = getAllComments();
      
        Log.d("value",values.toString());
        
        //ArrayAdapter<Report> adapter = new ArrayAdapter<Report>(this,
          //      android.R.layout.simple_list_item_1, values);
           // setListAdapter(adapter);
        ListAdapter customAdapter = new ListAdapter(this, R.layout.reportslist, values);
        
        setListAdapter(customAdapter);
        
    }
    public List<Report> getAllComments() {
        List<Report> reports = new ArrayList<Report>();

        Cursor cursor = database.query(ReportDataSQLHelper.TABLE,allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {	
          Report report = cursorToReport(cursor);
          reports.add(report);
          cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return reports;
      }
    private Report cursorToReport(Cursor cursor) {
        Report report = new Report();
        report.setId(cursor.getInt(0));
        report.setSector(cursor.getString(0));
        return report;
      }
}
