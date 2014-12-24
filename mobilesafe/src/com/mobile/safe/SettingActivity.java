package com.mobile.safe;

import com.mobile.safe.service.CallSmsFireWallService;
import com.mobile.safe.service.ShowLocationService;
import com.mobile.safe.service.WatchDogService;
import com.mobile.safe.ui.ActivityUtils;
import com.mobile.safe.ui.SettingView;
import com.mobile.safe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener {
	private SettingView sv_setting_update;
	private SettingView sv_setting_show_address;
	private SharedPreferences sp;
	private Intent showAddressIntent, appLockServiceIntent;
	private RelativeLayout reLayout, drgLayout;
	private TextView tv_setting_addressbg_color;
	private static String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
	private SettingView sv_setting_callsmsfirewall, sv_setting_applock;
	private Intent firewallIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_show_address = (SettingView) findViewById(R.id.sv_setting_show_address);
		sv_setting_applock = (SettingView) findViewById(R.id.sv_setting_applock);
		// 设置开机更新 检测版本下载
		boolean isupdate = sp.getBoolean("update", true);
		sv_setting_update.setChecked(isupdate);
		sv_setting_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				if (sv_setting_update.isChecked()) {
					sv_setting_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					sv_setting_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		// 号码归属地服务开启
		showAddressIntent = new Intent(this, ShowLocationService.class);
		sv_setting_show_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sv_setting_show_address.isChecked()) {
					sv_setting_show_address.setChecked(false);
					stopService(showAddressIntent);
				} else {
					sv_setting_show_address.setChecked(true);
					startService(showAddressIntent);
				}
			}
		});
		// 设置toast更改风格
		reLayout = (RelativeLayout) findViewById(R.id.rl_setting_changebg);
		tv_setting_addressbg_color = (TextView) findViewById(R.id.tv_setting_addressbg_color);
		int which = sp.getInt("which", 0);
		tv_setting_addressbg_color.setText(items[which]);
		reLayout.setOnClickListener(this);
		// 更改归属地位置
		drgLayout = (RelativeLayout) findViewById(R.id.rl_setting_change_location);
		drgLayout.setOnClickListener(this);

		// 短信防火墙服务设置
		sv_setting_callsmsfirewall = (SettingView) findViewById(R.id.sv_setting_callsmsfirewall);
		firewallIntent = new Intent(this, CallSmsFireWallService.class);
		sv_setting_callsmsfirewall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (sv_setting_callsmsfirewall.isChecked()) {
					sv_setting_callsmsfirewall.setChecked(false);
					stopService(firewallIntent);

				} else {
					sv_setting_callsmsfirewall.setChecked(true);
					startService(firewallIntent);

				}

			}
		});
		appLockServiceIntent = new Intent(this, WatchDogService.class);
		sv_setting_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sv_setting_applock.isChecked()) {
					sv_setting_applock.setChecked(false);
					stopService(appLockServiceIntent);
				} else {
					sv_setting_applock.setChecked(true);
					startService(appLockServiceIntent);

				}
			}
		});

	}

	// 当界面重新用户可见的时候
	@Override
	protected void onStart() {
		// 判断线程是否开启
		if (ServiceStatusUtils.isServiceRunning(
				"com.mobile.safe.service.ShowLocationService", this)) {
			sv_setting_show_address.setChecked(true);
		} else {
			sv_setting_show_address.setChecked(false);
		}
		if (ServiceStatusUtils.isServiceRunning(
				"com.mobile.safe.service.CallSmsFireWallService", this)) {
			sv_setting_callsmsfirewall.setChecked(true);
		} else {
			sv_setting_callsmsfirewall.setChecked(false);
		}
		if (ServiceStatusUtils.isServiceRunning(
				"com.mobile.safe.service.WatchDogService", this)) {
			sv_setting_applock.setChecked(true);
		} else {
			sv_setting_applock.setChecked(false);
		}
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.rl_setting_changebg :
				showChangeBgDialog();
				break;
			case R.id.rl_setting_change_location :
				ActivityUtils.startActivity(this, DragViewActivity.class);
				break;
			default :
				break;
		}

	}

	private void showChangeBgDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.notification);
		builder.setTitle("归属地提示框风格");
		int which = sp.getInt("which", 0);
		builder.setSingleChoiceItems(items, which,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 记录选择状态 及其重要
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						dialog.dismiss();
						tv_setting_addressbg_color.setText(items[which]);
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();

	}
}
