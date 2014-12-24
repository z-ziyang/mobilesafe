package com.mobile.safe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class TaskUtil {

	/**
	 * 获取进程个数
	 * @param mContext
	 * @return
	 */
	public static int getRunningProcessCount(Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(mContext.ACTIVITY_SERVICE);

		return am.getRunningAppProcesses().size();
	}
	/**
	 * 获取可用空间
	 * @param mContext
	 * @return
	 */
	public static long getAvailRam(Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(mContext.ACTIVITY_SERVICE);
		MemoryInfo outInfo=new MemoryInfo();
		am.getMemoryInfo(outInfo);		
		return outInfo.availMem;
	}
	/**
	 * 获取手机的总内存
	 * 
	 * @return
	 */
	public static long getTotalRam() {
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			// MemTotal
			String result = br.readLine();
			StringBuffer sb = new StringBuffer();
			char[] chars = result.toCharArray();
			for (char c : chars) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;

		}
	}
}
