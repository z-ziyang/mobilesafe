package com.mobile.safe;

import com.mobile.safe.ui.ActivityUtils;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	// 获取手机相关服务
	private TelephonyManager tm;
	private ImageView iv_lock;
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub
		iv_lock = (ImageView) findViewById(R.id.sim_lock);
		// 根据配置的信息 初始化一下状态
		String sim = sp.getString("sim", "");
		if (TextUtils.isEmpty(sim)) {
			iv_lock.setImageResource(R.drawable.unlock);
		} else {
			iv_lock.setImageResource(R.drawable.lock);
		}
	}

	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		String sim = sp.getString("sim", "");
		//必须先绑定
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "请先绑定SIM卡", Toast.LENGTH_LONG).show();
			return;
		}
		ActivityUtils.startActivityAndFinish(this, Setup3Activity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		ActivityUtils.startActivityAndFinish(this, Setup1Activity.class);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	/**
	 * 绑定SIM卡
	 * @param view
	 */
	public void bindSIM(View view) {
		// 判断sp里面是否保存了sim卡的串号
		String sim = sp.getString("sim", "");
		if (TextUtils.isEmpty(sim)) {
			String simnumber = tm.getSimSerialNumber();
			Editor editor = sp.edit();
			editor.putString("sim", simnumber);
			editor.commit();
			iv_lock.setImageResource(R.drawable.lock);
		} else {
			Editor editor = sp.edit();
			editor.putString("sim", null);
			editor.commit();
			iv_lock.setImageResource(R.drawable.unlock);
	        
		}
	}

}
