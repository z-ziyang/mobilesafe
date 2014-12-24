package com.mobile.safe.service;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.EnterPasswrodActivity;
import com.mobile.safe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class WatchDogService extends Service {
	private ActivityManager am;
	private AppLockDao dao;
	private Intent intent;
	private boolean flag;
	private List<String> tempStopProtectPacknames, lockedPackageNames;
	private Thread watchThread;
	private InnerLockScreenReceiver lockScreenReceiver;
	private InnerUnlockScreenReceiver unlockScreenReceiver;
	private MyContentObserver observer;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}
	/**
	 * 绑定服务调用接口方法 进一步调用其他方法
	 * @author lenovo
	 *
	 */
	private class MyBinder extends Binder implements IService {
		@Override
		public void callMethodInService(String packName) {
			tempStopProtect(packName);

		}
	}
	private void tempStopProtect(String packName) {
		tempStopProtectPacknames.add(packName);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		lockScreenReceiver = new InnerLockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver, lockFilter);

		unlockScreenReceiver = new InnerUnlockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(unlockScreenReceiver, unlockFilter);

		tempStopProtectPacknames = new ArrayList<String>();

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(getApplicationContext());
		intent = new Intent(this, EnterPasswrodActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		lockedPackageNames = dao.findAll(); // 放到缓存中

		observer = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.uri, true,
				observer);
		// 开启看门狗
		startWatchDog();
	}
	private void startWatchDog() {
		watchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				flag = true;
				while (flag) {
					// 任务栈
					List<RunningTaskInfo> infos = am.getRunningTasks(100);
					RunningTaskInfo taskInfo = infos.get(0);
					ComponentName topActivity = taskInfo.topActivity;// 得到栈顶的activity
					String packageName = topActivity.getPackageName();
					System.out.println("--->" + packageName);
					// 从缓存中寻找
					if (lockedPackageNames.contains(packageName)) {
						if (tempStopProtectPacknames.contains(packageName)) {

						} else {
							intent.putExtra("packname", packageName);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});
		watchThread.start();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		flag = false;

		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;

		unregisterReceiver(unlockScreenReceiver);
		unlockScreenReceiver = null;

		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		super.onDestroy();
	}

	/**
	 * 锁屏的时候挂掉线程 清空临时数据
	 * 
	 * @author lenovo
	 * 
	 */
	private class InnerLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			tempStopProtectPacknames.clear();
			flag = false;
		}

	}
	/**
	 * 锁屏开启 重新启动监视
	 * 
	 * @author lenovo
	 * 
	 */
	private class InnerUnlockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!flag) {
				startWatchDog();
				System.out.println("--->解锁");
			}
		}
	}
	/**
	 * 观察者 数据库发生变化迅速通知改变
	 * 
	 * @author
	 * 
	 */
	private class MyContentObserver extends ContentObserver {

		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onChange(boolean selfChange) {
			lockedPackageNames = dao.findAll();
			System.out.println("---->" + "数据变化中 ");
			super.onChange(selfChange);
		}
	}
}
