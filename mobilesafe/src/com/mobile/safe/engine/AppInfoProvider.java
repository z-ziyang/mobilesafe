package com.mobile.safe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.mobile.safe.enity.AppInfo;

/**
 * 获取手机应用信息
 * 
 * @author
 * 
 */
public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		//和获取Activity类似 必须加上标记才可以获取的到permissions 
		List<PackageInfo> packageInfos = pm
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for (PackageInfo packageInfo : packageInfos) {
			AppInfo appInfo = new AppInfo();
			String version = packageInfo.versionName;
			appInfo.setVersion(version);
			// 获取到应用的权限信息
			String[] permissions = packageInfo.requestedPermissions;
			if (permissions != null && permissions.length > 0) {
				for (String permission : permissions) {
				       
					if ("android.permission.INTERNET".equals(permission)) {
						appInfo.setUsenet(true);
					} else if ("android.permission.READ_CONTACTS"
							.equals(permission)) {
						appInfo.setUsecontact(true);
					} else if ("android.permission.ACCESS_FINE_LOCATION"
							.equals(permission)) {
						appInfo.setUsegps(true);
					} else if ("android.permission.SEND_SMS".equals(permission)) {
						appInfo.setUsesms(true);
					}

				}
			}
			String packagename = packageInfo.packageName;
			appInfo.setPackname(packagename);

			String appname = packageInfo.applicationInfo.loadLabel(pm)
					.toString();

			Drawable appicon = packageInfo.applicationInfo.loadIcon(pm);

			appInfo.setAppicon(appicon);
			appInfo.setAppname(appname);

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setUserapp(true);
			} else {
				appInfo.setUserapp(false);
			}
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				appInfo.setInRom(true);
			} else {
				appInfo.setInRom(false);
			}
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
