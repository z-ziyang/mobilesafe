package com.mobile.safe.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public abstract class MyAsyncTask {

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			onPostExcute();
		}
	};
	/**
	 * 耗时开启之前调用的方法
	 */
	public abstract void onPreExcute();
	/**
	 * 后台执行的任务 在子线程内运行
	 */
	public abstract void doInBackground();
	/**
	 * 耗时任务完成之后调用的方法
	 */
	public abstract void onPostExcute();
	/**
	 * 执行一个耗时任务
	 */
	public void excute() {
		onPreExcute();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				doInBackground();
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
}
