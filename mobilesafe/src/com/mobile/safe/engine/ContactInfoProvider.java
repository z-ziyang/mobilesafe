package com.mobile.safe.engine;


import java.util.ArrayList;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.mobile.safe.enity.ContactInfo;

public class ContactInfoProvider {

	/**
	 * 获取手机里面所有联系人的信息
	 * @param mContext
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static List<ContactInfo> getContactInfo(Context mContext) {
	     List<ContactInfo> contactInfos=new ArrayList<ContactInfo>();
		ContentResolver resolver=mContext.getContentResolver();
		Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri=Uri.parse("content://com.android.contacts/data");
		Cursor cursor=resolver.query(uri, new String[]{"contact_id"}, null, null, null);
		while(cursor.moveToNext()) {
			String contact_id=cursor.getString(0);
			if (contact_id != null) {
				ContactInfo contactInfo=new ContactInfo();
				Cursor dataCursor=resolver.query(datauri, new String[]{"mimetype", "data1"},
						"raw_contact_id=?" ,new String[]{contact_id}, null);
				//游标遍历每一条记录
				while(dataCursor.moveToNext()) {
					String mime=dataCursor.getString(0);
					String data=dataCursor.getString(1);
					System.out.println("------>"+data);
					if("vnd.android.cursor.item/name".equals(mime)){
						contactInfo.setName(data);
						
					}else if("vnd.android.cursor.item/phone_v2".equals(mime)){
						contactInfo.setNumber(data);
					}					
				}
				contactInfos.add(contactInfo);
				dataCursor.close();
			}
		}
		try {
			new Thread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		return contactInfos;
	}
}
