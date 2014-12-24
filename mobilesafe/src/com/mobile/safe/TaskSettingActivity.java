package com.mobile.safe;

import com.mobile.safe.service.AutoKillService;
import com.mobile.safe.ui.SettingView;
import com.mobile.safe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {

	private CheckBox cb_task_show_system;
	private SharedPreferences sp;
	private SettingView sv_task_setting_autokill;
	private Intent autokillIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sv_task_setting_autokill=(SettingView)findViewById(R.id.ch_task_autokill);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		boolean showsystem = sp.getBoolean("showsystem", false);
		autokillIntent = new Intent(this,AutoKillService.class);
		cb_task_show_system=(CheckBox)findViewById(R.id.cb_task_setting_showsystem);
		cb_task_show_system.setChecked(showsystem);
		cb_task_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor editor=sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
				//返回结果码
				setResult(100);
			}
		});
		sv_task_setting_autokill.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sv_task_setting_autokill.isChecked()){
                    sv_task_setting_autokill.setChecked(false);
                    stopService(autokillIntent);
				}else{
					sv_task_setting_autokill.setChecked(true);
					startService(autokillIntent);
				}
			}
		});
	}
	/**
	 * 检查是否处于运行状态
	 */
	@Override
	protected void onStart() {
		if(ServiceStatusUtils.isServiceRunning("com.mobile.safe.service.AutoKillService", this)){
			sv_task_setting_autokill.setChecked(true);
		}else{
			sv_task_setting_autokill.setChecked(false);
		}
		super.onStart();
	}
}
