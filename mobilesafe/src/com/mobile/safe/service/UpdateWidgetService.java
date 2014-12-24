package com.mobile.safe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.mobile.safe.R;
import com.mobile.safe.receiver.MyWidget;
import com.mobile.safe.utils.TaskUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		awm = AppWidgetManager.getInstance(getApplicationContext());
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// 进程间通信 跟新状态 获取class和view
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				// 获取更新布局
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				// 更新数据
				views.setTextViewText(
						R.id.process_count,
						"正在运行的软件:"
								+ TaskUtil
										.getRunningProcessCount(getApplicationContext())
								+ "个");
				views.setTextViewText(
						R.id.process_memory,
						"可用内存:"
								+ Formatter.formatFileSize(
										getApplicationContext(),
										TaskUtil.getAvailRam(getApplicationContext())));
				Intent intent = new Intent();
				intent.setAction("com.mobile.safe.killprocess");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);

			}
		};
		// 定时刷新
		timer.schedule(task, 1000, 2000);
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		task = null;
		timer = null;
		super.onDestroy();
	}

}
