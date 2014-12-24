package com.mobile.safe;

import com.mobile.safe.ui.ActivityUtils;

import android.content.Intent;

public class Setup1Activity extends BaseSetupActivity{

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		ActivityUtils.startActivityAndFinish(this, Setup2Activity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}


}
