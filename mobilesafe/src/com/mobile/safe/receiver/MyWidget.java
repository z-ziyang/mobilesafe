package com.mobile.safe.receiver;

import com.mobile.safe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
/**
 * 学习Widget应用 生命周期
 * @author 
 *
 */
public class MyWidget extends AppWidgetProvider{

	//每次状态变化的时候 都会调用
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent mintent=new Intent(context,UpdateWidgetService.class);
		context.startService(mintent);
		super.onReceive(context, intent);
	}

	//根据设置的时间 进行刷新 
	//每次创建的时候会调用
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
    //每次删除的时候都会调用
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}
    //第一次出现的时候 调用 最初 出现的时候 会调用
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}
   //最后完全消除的 完全移除的时候 会调用
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

	
}
