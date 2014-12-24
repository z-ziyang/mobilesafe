package com.mobile.safe.ui;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtils {

	/**
	 * 开启新的activity并且关掉
	 * @param mContext
	 * @param T
	 */
	public static void startActivityAndFinish(Context mContext,Class<?> T) {
		Intent intent=new Intent(mContext,T);
		mContext.startActivity(intent);
		((Activity) mContext).finish();
	}
	/**
	 * 开启新的activity
	 * @param mContext
	 * @param T
	 */
	public static void startActivity(Context mContext,Class<?> T) {
		Intent intent=new Intent(mContext,T);
		mContext.startActivity(intent);
	}
}
