package com.mobile.safe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KillProcessReceiver extends BroadcastReceiver {

	private static final String TAG = "MyKillProcessReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : infos){
			am.killBackgroundProcesses(info.processName);
		}
		System.out.println("--->"+TAG);
	}

}
