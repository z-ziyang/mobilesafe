package com.mobile.safe;

import com.mobile.safe.db.dao.BlackNumDao;
import com.mobile.safe.enity.BlackNumberInfo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EditBlackNumberDialog extends Activity {

	private RadioButton rb_all, rb_phone, rb_sms;
	private BlackNumDao dao;
	private BlackNumberInfo info;
	private RadioGroup rg;
	private TextView tv_title_name;
	private EditText et_blacknumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_add_blacknumber);
		initView();
		setUpData();
		findViewById(R.id.bt_blacknumber_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = rg.getCheckedRadioButtonId();
						String mode = "1";
						switch (id) {
							case R.id.ra_all :
								mode = "1";
								break;
							case R.id.ra_phone :
								mode = "2";
								break;
							case R.id.ra_sms :
								mode = "3";
								break;
						}
						dao.updateBlackNum(info.getNumber(), mode);
						// 设置新的拦截模式到info对象.
						info.setMode(mode);
						setResult(200);
						finish();

					}
				});
		findViewById(R.id.bt_blacknumber_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(404);
						finish();
					}
				});
	}

	public void setUpData() {
		tv_title_name.setText("更改黑名单拦截模式");
		tv_title_name.setTextColor(Color.BLACK);
		et_blacknumber.setEnabled(false);
		MoblieSafeApplication app = (MoblieSafeApplication) getApplication();
		info = app.info;
		app.info = null;
		String blacknumber = info.getNumber();
		et_blacknumber.setText(blacknumber);
		String mode = info.getMode();
		System.out.println(mode);
		if ("1".equals(mode)) {
			rb_all.setChecked(true);
		} else if ("2".equals(mode)) {
			rb_phone.setChecked(true);
		} else if ("3".equals(mode)) {
			rb_sms.setChecked(true);
		}
	}
	public void initView() {
		tv_title_name = (TextView) findViewById(R.id.tv_title_name);
		et_blacknumber = (EditText) findViewById(R.id.et_blacknumber);
		rg = (RadioGroup) findViewById(R.id.ra_mode);
		rb_all = (RadioButton) findViewById(R.id.ra_all);
		rb_phone = (RadioButton) findViewById(R.id.ra_phone);
		rb_sms = (RadioButton) findViewById(R.id.ra_sms);
		dao=new BlackNumDao(this);
	}

}
