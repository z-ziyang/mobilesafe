package com.mobile.safe.test;

import com.mobile.safe.db.BlackNumberDBHelper;
import com.mobile.safe.db.dao.BlackNumDao;
import com.mobile.safe.engine.PackageSizeInfoProvider;
import com.mobile.safe.engine.SmsBackUp;

import android.R.integer;

import android.content.ContentValues;
import android.net.Uri;
import android.test.AndroidTestCase;

public class Test extends AndroidTestCase{

	public void testCaseDB() {
		BlackNumberDBHelper dbHelper=new BlackNumberDBHelper(getContext());
		dbHelper.getWritableDatabase();
	}
    public void setBlackNumber() {
    	BlackNumDao blackNumDao=new BlackNumDao(getContext());
    	int number=1380000000;
    	for (int i = 0; i < 50; i++) {
    		blackNumDao.addBlackNum(number+1+"", "1");
		}
    }
    public void findNumber() {
    	BlackNumDao blackNumDao=new BlackNumDao(getContext());
        System.out.println("--->"+blackNumDao.findBlackNum("110"));
    }
    public void deleteNumber() {
    	BlackNumDao blackNumDao=new BlackNumDao(getContext());
    	System.out.println("--->"+blackNumDao.deleteBlackNum("110"));
    }
    
//    public void testBackupSms() {
//    	SmsBackUp backUp=new SmsBackUp(getContext());
//    	backUp.backUpSms();
//    }
    public void restoreSms() {
    	Uri uri = Uri.parse("content://sms/");
    	ContentValues values= new ContentValues();
    	values.put("address", "hhhhhh");
    	values.put("date", "1231231231");
    	values.put("type", "1");
    	values.put("body", "ccccccccccc");
    	getContext().getContentResolver().insert(uri, values);
    }
    public void test2() {
    	PackageSizeInfoProvider.getAllPackageSize(getContext());
    } 
    
}
