package com.mobile.safe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobile.safe.enity.*;
import com.mobile.safe.db.BlackNumberDBHelper;

public class BlackNumDao {

	private BlackNumberDBHelper helper;
	public BlackNumDao(Context mContext) {
		// TODO Auto-generated constructor stub
		helper = new BlackNumberDBHelper(mContext);
	}

	public boolean findBlackNum(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?",
				new String[]{number});
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询全部的数据
	 * 
	 * @return
	 */
	public List<BlackNumberInfo> findAllBlackNum() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
			info = null;
		}
		cursor.close();
		db.close();
		return infos;
	}
	/**
	 * 分页查询数据
	 * @param maxnumber
	 * @param offset
	 * @return
	 */
	public List<BlackNumberInfo> findByPage(int maxnumber,int offset) {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",
				new String[]{maxnumber+"",offset+""});
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
			info = null;
		}
		cursor.close();
		db.close();
		return infos;
	}
	/**
	 * 查询一共有多少条的记录
	 */
	public int getMaxBlackNum() {
		int maxnumber = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber",
				null);
		maxnumber = cursor.getCount();
		cursor.close();
		db.close();
		return maxnumber;
	}
	/**
	 * 添加
	 * 
	 * @param number
	 * @param mode
	 */
	public void addBlackNum(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into blacknumber (number,mode) values (?,?)",
				new Object[]{number, mode});
		db.close();
	}
	public void updateBlackNum(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update blacknumber set mode =? where number=?",
				new Object[]{newmode, number});
		db.close();
	}
	public boolean deleteBlackNum(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
    /**
     * 找到一条mode
     * @param number
     * @return
     */
	public String findMode(String number) {
		String result = "-1";
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?",
				new String[]{number});
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}

}
