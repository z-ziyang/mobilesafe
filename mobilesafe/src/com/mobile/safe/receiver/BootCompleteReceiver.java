package com.mobile.safe.receiver;

import com.mobile.safe.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "手机开启");
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean protect = sp.getBoolean("proctect", false);
		if (protect) {
			// 先获取当前SIM信息
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String realSim = tm.getSimSerialNumber();
			// 获取绑定的SIM卡
            String saveSim=sp.getString("sim", "");
            //比对 不一样发送短信
            if (! saveSim.equals(realSim)) {
				//发送报警短息
            	SmsManager sm=SmsManager.getDefault();
            	sm.sendTextMessage(sp.getString("safenumber", ""), null, "sim changed", null, null);
			}
		} else {

		}

	}

}
