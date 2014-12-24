package com.mobile.safe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

	public static final String dbPath = "/data/data/com.mobile.safe/files/address.db";
	public AddressDao() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取归属地
	 * 
	 * @param number
	 * @return
	 */
	public static String getAddress(String number) {
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READONLY);
		// 正则 表达式
		if (number.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[]{number.substring(0, 7)});
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (number.length()) {
				case 3 :
					address = "特殊号码";
					break;
				case 4 :
					address = "模拟器";
					break;
				case 5 :
					address = "特殊号码";
					break;
				case 7 :
					address = "本地号码";
					break;
				case 8 :
					address = "本地号码";
					break;
				default :
					if (number.length() >= 10 && number.startsWith("0")) {
						// 去查前三位
						Cursor cursor = db.rawQuery(
								"select location from data2 where area =?",
								new String[]{number.substring(1, 3)});
						if (cursor.moveToNext()) {
							String text=cursor.getString(0);
							address=text.substring(0, text.length()-2);
						}
						cursor.close();
					}
					
					break;
			}
		}
		System.out.println("address---->" + address);
		db.close();
		return address;
		
	}

}
