package com.backyard.backyard;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;
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

	public ReportDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + "( " + BaseColumns._ID
				+ " integer primary key autoincrement, " + TIME + " integer, "
				+ SECTOR + " text not null);";
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