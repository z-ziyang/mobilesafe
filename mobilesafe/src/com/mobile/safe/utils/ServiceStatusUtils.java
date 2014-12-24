package com.mobile.safe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * 判断服务是否处于运行状态
	 * @param servicename
	 * @param mContext
	 * @return
	 */
	public static boolean isServiceRunning(String servicename, Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(mContext.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : infos) {
			if (servicename.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
