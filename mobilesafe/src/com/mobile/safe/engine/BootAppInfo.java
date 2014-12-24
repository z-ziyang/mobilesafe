package com.mobile.safe.engine;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.enity.AppInfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class BootAppInfo {

	public static List<AppInfo> getBootApps(Context mContext)
			throws NameNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
		List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent,
				PackageManager.GET_INTENT_FILTERS);
		for (ResolveInfo info : infos) {

			AppInfo appInfo = new AppInfo();

			String packname = info.activityInfo.packageName;
			//去除相同的应用
			if (appInfos.size() != 0
					&& appInfos.get(appInfos.size() - 1).getPackname()
							.equals(packname)) {
				continue;
			}
			appInfo.setPackname(packname);
			PackageInfo packageInfo;

			packageInfo = pm.getPackageInfo(packname, 0);

			Drawable appicon = packageInfo.applicationInfo.loadIcon(pm);
			appInfo.setAppicon(appicon);

			String appname = packageInfo.applicationInfo.loadLabel(pm)
					.toString();
			appInfo.setAppname(appname);

			appInfo.setVersion(packageInfo.versionName);
			System.out.println("--->" + appname);
			// 判断系统还是用户
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setUserapp(true);
			} else {
				appInfo.setUserapp(false);
			}
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
