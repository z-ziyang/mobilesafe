package com.mobile.safe;

import com.mobile.safe.ui.ActivityUtils;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity{

	private EditText ed_contacText;
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setup3);
		ed_contacText=(EditText)findViewById(R.id.ed_contact);
	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub
		ed_contacText.setText(sp.getString("safenumber", ""));
	}

	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		String safenumber=ed_contacText.getText().toString().trim();
		if (TextUtils.isEmpty(safenumber)) {
			Toast.makeText(this, "安全号码不能为空！！", Toast.LENGTH_LONG).show();
			return;
		}
		Editor editor=sp.edit();
		editor.putString("safenumber", safenumber);
		editor.commit();
		
		ActivityUtils.startActivityAndFinish(this, Setup4Activity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		ActivityUtils.startActivityAndFinish(this, Setup2Activity.class);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data != null) {
			String phone=data.getStringExtra("phone");
			ed_contacText.setText(phone);
		}
	}
	/**
	 * 联系人选择点击事件
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent=new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
		
	}


}
