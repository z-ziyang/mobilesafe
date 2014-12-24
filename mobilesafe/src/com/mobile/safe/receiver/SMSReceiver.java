package com.mobile.safe.receiver;

import com.mobile.safe.R;
import com.mobile.safe.engine.GPSInfoProvider;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Object[] puds = (Object[]) intent.getExtras().get("pdus");
		for (Object pud : puds) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pud);
			String sender = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			if (body.equals("#*location*#")) {
				System.out.println("SMSReceiver---->" + "#*location*#");
				String location = GPSInfoProvider.getInstance(context)
						.getLastLocation();
				if (!TextUtils.isEmpty(location)) {
					System.out.println("SMSReceiver---->" + location);
					SmsManager sm = SmsManager.getDefault();
					sm.sendTextMessage(sender, null, location, null, null);
				}
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				System.out.println("SMSReceiver---->" + "#*alarm*#");
				MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
				mp.setVolume(1.0f, 1.0f);
				mp.start();
				abortBroadcast();
			} else if (body.equals("#*wipedata*#")) {
				System.out.println("SMSReceiver---->" + "#*wipedata*#");
				//获取权限
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context, MyAdmin.class);
				if (dpm.isAdminActive(who)) {
					dpm.wipeData(0);
				}			
				abortBroadcast();
			} else if (body.equals("#*lockscreen*#")) {
				System.out.println("SMSReceiver---->" + "#*lockscreen*#");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context, MyAdmin.class);
				if (dpm.isAdminActive(who)) {
					dpm.resetPassword("321", 0);
					dpm.lockNow();
				}
				abortBroadcast();
			}
		}

	}

}
