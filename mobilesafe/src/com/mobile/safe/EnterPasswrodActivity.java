package com.mobile.safe;
import com.mobile.safe.service.IService;
import com.mobile.safe.service.WatchDogService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPasswrodActivity extends Activity {
	private String packname;
	private EditText et_password;
	private IService myIService;
	private MyConn conn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
		et_password = (EditText) findViewById(R.id.et_password);
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");		
		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			Drawable icon = info.loadIcon(pm);
			CharSequence name = info.loadLabel(pm);
			TextView tv = (TextView) findViewById(R.id.tv_name);
			tv.setText(name);
			ImageView iv = (ImageView) findViewById(R.id.iv_icon);
			iv.setImageDrawable(icon);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		conn=new MyConn();
		Intent service=new Intent(this,WatchDogService.class);
	    bindService(service, conn, BIND_AUTO_CREATE);
		
	}
	public void enter(View view){
		String password = et_password.getText().toString().trim();
		if("123".equals(password)){
			finish(); 
			//告诉看门狗 ,密码正确了 ,这个是熟人 放行.
			//调用服务方法 停止保护
            myIService.callMethodInService(packname);
		}else{
			Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
		}
	}
	private class MyConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			myIService=(IService) service;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub	
		}
	}
	/**
	 * 屏蔽 直接回到桌面
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addCategory(Intent.CATEGORY_MONKEY);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(conn);
		super.onDestroy();
	}
}
