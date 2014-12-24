package com.mobile.safe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mobile.safe.receiver.MyAdmin;
import com.mobile.safe.ui.ActivityUtils;

public class Setup4Activity extends BaseSetupActivity{

	private CheckBox cb_check_state;
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setup4);
		cb_check_state=(CheckBox)findViewById(R.id.cb_setup4_state);
	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub
		boolean protecting = sp.getBoolean("proctect", false);
		if(protecting){
			cb_check_state.setChecked(true);
			cb_check_state.setText("防盗保护已经开启");
		}else{
			cb_check_state.setChecked(false);
			cb_check_state.setText("防盗保护没有开启");
		}
		cb_check_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor editor=sp.edit();
				if (isChecked) {
					cb_check_state.setText("防盗保护已经开启");
				}else {
					cb_check_state.setText("防盗保护没有开启");
				}
				editor.putBoolean("proctect", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		Editor editor = sp.edit();
		editor.putBoolean("setup",true);
		editor.commit();
		ActivityUtils.startActivityAndFinish(this, LostFindActivity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		ActivityUtils.startActivityAndFinish(this, Setup3Activity.class);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	public void activeAdmin(View v) {
		Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName mComponentName=new ComponentName(this,MyAdmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活我可以远程锁屏,清除数据....");
		startActivity(intent);
				
	}


}
