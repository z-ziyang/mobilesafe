package com.mobile.safe.service;

import java.lang.reflect.Method;



import com.android.internal.telephony.ITelephony;
import com.mobile.safe.CallSmsSafeActivity;
import com.mobile.safe.R;
import com.mobile.safe.db.dao.BlackNumDao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsFireWallService extends Service {
	private final String TAG = "CallSmsFireWallService";
	private BlackNumDao dao;
	private InnerSmsReceiver smsReceiver;
	private TelephonyManager tm;
	private InnerTeleStateListener listener;
	public CallSmsFireWallService() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		dao = new BlackNumDao(this);
		smsReceiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		listener = new InnerTeleStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(smsReceiver, filter);

		super.onCreate();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	private class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				// 获取发信人 号码
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				// 判断sender是否在黑名单数据库里面, 判断拦截模式 是否是1, 3
				String mode = dao.findMode(sender);
				System.out.println("拦截---->" + sender);
				// 号码拦截
				if ("1".equals(mode) || "3".equals(mode)) {
					// TODO: 把黑名单短信内容存储起来
					System.out.println("拦截---->" + sender);
					abortBroadcast();

				}
				String body = smsMessage.getMessageBody();
				// 关键字 拦截
				if (body.contains("fapiao")) {
					abortBroadcast();
				}
			}
		}
	}
	private class InnerTeleStateListener extends PhoneStateListener {
		private long startTime;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING :
					startTime = System.currentTimeMillis();
					String mode = dao.findMode(incomingNumber);
					if ("1".equals(mode) || "2".equals(mode)) {
						// 拦截电话挂断 借助反射
						System.out.println("--->" + mode);
						endCall(incomingNumber);
						// 当呼叫记录产生后 再去移除.
						getContentResolver().registerContentObserver(
								CallLog.Calls.CONTENT_URI, true,
								new MyObserver(new Handler(), incomingNumber));
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE :
					System.out.println("--->");
					//
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;
					if (dTime < 4000) {
						Log.i(TAG, "发现响一声号码");
						// 判断一下当前号码是否已经在黑名单数据库
						String blockmode = dao.findMode(incomingNumber);
						if ("1".equals(blockmode) || "2".equals(blockmode)) {

						} else {
							System.out.println("--->");
							showNotification(incomingNumber);
						}
					}
					break;

				default :
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}
	/**
	 * 显示来电一声响的提醒 title bar
	 * 
	 * @param incomingNumber
	 */
	private void showNotification(String incomingNumber) {
		// TODO Auto-generated method stub
		System.out.println("--->" );
		// 获取系统notification管理器
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 实例化notification
		Notification notification = new Notification(R.drawable.notification,
				"发现响一声号码:" + incomingNumber, System.currentTimeMillis());
		//设置具体参数
		notification.flags = Notification.FLAG_AUTO_CANCEL; //取消掉
		//设置进入应用程序
		Intent intent = new Intent(this,CallSmsSafeActivity.class);
		//拦截一声铃响的号码进入添加到黑名单的界面
		intent.putExtra("blacknumber", incomingNumber);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "手机卫士拦截提醒", "拦截到了一个响一声号码", contentIntent);
		nm.notify(0, notification);

	}
	/**
	 * 挂断电话 反射
	 * 
	 * @param incomingNumber
	 */
	public void endCall(String incomingNumber) {
		try {
			Method method = Class.forName("android.os.ServiceManager")
					.getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null,
					new Object[]{TELEPHONY_SERVICE});
			ITelephony telephony = ITelephony.Stub.asInterface(binder);
			telephony.endCall();// 挂断电话.
			// 接通一个电话
			// telephony.answerRingingCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除呼叫记录
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?",
				new String[]{incomingNumber});
	}
	/**
	 * 观察者
	 * 
	 * @author lenovo
	 * 
	 */
	public class MyObserver extends ContentObserver {
		private String incomingNumber;

		public MyObserver(Handler handler, String incomingNumber) {
			super(handler);
			// TODO Auto-generated constructor stub
			this.incomingNumber = incomingNumber;
		}
		/**
		 * 当观察发生改变的时候调用
		 */
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);
			// 观察之后 移除
			getContentResolver().unregisterContentObserver(this);
		}
	}
}