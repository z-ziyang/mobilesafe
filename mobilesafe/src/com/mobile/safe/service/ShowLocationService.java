package com.mobile.safe.service;



import com.mobile.safe.R;
import com.mobile.safe.db.dao.AddressDao;



import android.app.Service;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class ShowLocationService extends Service {

	private TelephonyManager tm;
	private MyPhoneListener listener;
	private InnerOutCallReceiver innerOutCallReceiver;
    private WindowManager wm;
    private TextView tv;
    private View view;
    private SharedPreferences sp;
    private static final int[] bgs = { R.drawable.call_locate_white,
		R.drawable.call_locate_orange, R.drawable.call_locate_blue,
		R.drawable.call_locate_gray, R.drawable.call_locate_green };
	private int startX=0,startY=0;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		sp=getSharedPreferences("config", MODE_PRIVATE);
		wm=(WindowManager) getSystemService(WINDOW_SERVICE);
		///将广播与服务相互关联
		innerOutCallReceiver=new InnerOutCallReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(innerOutCallReceiver, filter);
		// 监听电话状态的改变
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(innerOutCallReceiver);		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	public class MyPhoneListener extends PhoneStateListener {
		/**
		 * 电话状态改变时的 各种状态
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE : // 空闲
                    if (view!=null) {
						wm.removeView(view);
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING : // 响铃
					showAddressInfo(incomingNumber);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK : // 通话

					break;
			}
		}	
	}
	/**
	 * 通过服务 开启广播查询	地址 
	 * * @author lenovo
	 *
	 */
	private class InnerOutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//获取所播的电话号码
			String number=getResultData();
			showAddressInfo(number);
		}
    	
    }
	/**
	 * 模仿自定义Toast
	 */
	private final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	private void showAddressInfo(String incomingNumber) {
		int which=sp.getInt("which",0);
		view=View.inflate(this,R.layout.ui_toast , null);
		//设置改变toast 位置
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						System.out.println("--->" + "DOWN");
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE :
						//移动toast
						int newX = (int) event.getRawX();
						int newY  = (int) event.getRawY();
						int dx = newX - startX;
						int dy = newY - startY;
						params.x += dx;
						params.y += dy;
						//更改布局位置
						wm.updateViewLayout(view, params);
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP :
						//保存toast的最后位置
						Editor editor = sp.edit();
						editor.putInt("lastX",params.x);
						editor.putInt("lastY",params.y);
						editor.commit();
						break;
					default :
						break;
				}
				return true;
			}
		});
		view.setBackgroundResource(bgs[which]);
		tv=(TextView)view.findViewById(R.id.tv_toast_address);
		tv.setTextColor(Color.RED);
		tv.setText(AddressDao.getAddress(incomingNumber));
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		int lastX = sp.getInt("lastX", 0);
		int lastY = sp.getInt("lastY", 0);
		params.x = lastX;
		params.y = lastY;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		//响应事件 响应类型必须改变 
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.setTitle("Toast");
		wm.addView(view, params);
		
	}
}
