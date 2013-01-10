package com.backyard.backyard;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;

public class ReportDataSQLHelper extends SQLiteOpenHelper {
	public ReportDataSQLHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	private static final String DATABASE_NAME = "backyard.db";
	private static final int DATABASE_VERSION = 1;

	// Table name
	public static final String TABLE = "reports";

	// Columns
	public static final String TIME = "time";
	public static final String SECTOR = "sector";
	public static final String ISSUE = "issue";
	public static final String COMPANY = "company";
	public static final String DESC = "desc";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
	public static final String PHOTO = "photo";
	public static final String VIDEO = "video";
	public static final String SYNC = "sync";

	public ReportDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql = "create table " + TABLE + "( " + BaseColumns._ID
				+ " integer primary key autoincrement, " + TIME + " integer, "
				+ SECTOR + " text not null,"
				+ ISSUE + " text not null,"
				+ COMPANY + " text not null,"
				+ DESC + " text not null," 
				+ LATITUDE + " text not null,"
				+ LONGITUDE + " text not null,"
				+ PHOTO + " text not null,"
				+ VIDEO + " text not null,"
				+ SYNC + " integer not null DEFAULT '0');";
		Log.d("ReportData", "onCreate: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion)
			return;

		String sql = null;
		if (oldVersion == 1) 
			sql = "alter table " + TABLE + " add note text;";
		if (oldVersion == 2)
			sql = "";

		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
	}

}