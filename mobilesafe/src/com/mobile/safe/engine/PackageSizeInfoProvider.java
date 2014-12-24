package com.mobile.safe.engine;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;

import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.text.format.Formatter;

public class PackageSizeInfoProvider {
	private static PackageManager pm;
	private static Context mContext;
	public static void getAllPackageSize(Context context) {
		mContext=context;
		pm = mContext.getPackageManager();
//		List<PackageInfo> infos = pm.getInstalledPackages(0);
//    	for(PackageInfo info : infos){
//    		String packname = info.packageName;
//    		getPackageSize(packname);
//    		System.out.println("--->"+packname);
//    	}	 
		getPackageSize(mContext.getPackageName());
	}
	/**
	 * 获取手机code cache 等大小
	 * @param mContext
	 */
	private static void getPackageSize(String packname) {
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[] { String.class,
							IPackageStatsObserver.class });
			System.out.println("--->"+method.getName());
			method.invoke(pm,
					new Object[] { packname, new MyObserver(packname,mContext) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class MyObserver extends IPackageStatsObserver.Stub {
	private String packname;
	private Context mContext;
	public MyObserver(String packname,Context mContext) {
		this.packname = packname;
		this.mContext=mContext;
	}
	@Override
	public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
			throws RemoteException {
		// TODO Auto-generated method stub
		long cachesize = pStats.cacheSize;
		long codesize = pStats.codeSize;
		long datasize = pStats.dataSize;
		System.out.println("--->"+Formatter.formatFileSize(mContext, cachesize));
		System.out.println("--->"+Formatter.formatFileSize(mContext,codesize));
		System.out.println("--->"+Formatter.formatFileSize(mContext, datasize));
	}
}