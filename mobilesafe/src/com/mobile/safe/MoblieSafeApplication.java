package com.mobile.safe;

import com.mobile.safe.enity.BlackNumberInfo;
import android.app.Application;
/**
 * 设置全局静态变量
 * @author lenovo
 *
 */
public class MoblieSafeApplication extends Application {
	public BlackNumberInfo info;
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
