package com.mobile.safe.receiver;

import com.mobile.safe.LostFindActivity;
import com.mobile.safe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {
	private static final String TAG="OutCallReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "外拨电话");
		String number=getResultData();
		if (number.equals("666")) {
			Intent lostIntent=new Intent(context,LostFindActivity.class);
			//FLAG必须加的内容 
			lostIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostIntent);
			setResultData(null);
		}
	}
}
