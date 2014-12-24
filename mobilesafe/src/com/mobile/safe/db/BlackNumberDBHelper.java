package com.mobile.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBHelper extends SQLiteOpenHelper{

	
	public BlackNumberDBHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String blacknumberSql="CREATE TABLE IF NOT EXISTS blacknumber (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"number TEXT, "+"mode TEXT "+")";
		db.execSQL(blacknumberSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
