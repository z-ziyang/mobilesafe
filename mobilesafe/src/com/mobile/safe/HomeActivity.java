package com.mobile.safe;

import java.util.zip.Inflater;

import com.mobile.safe.adapter.HomeAdapter;
import com.mobile.safe.utils.MD5Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener {

	private GridView gv_home;
	private Intent intent;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_home);
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter(this));
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
					case 0 :
						if (isSetupPwd()) {
							showNormalEntryDialog();
						} else {
							showFirstEntryDialog();
						}
						break;
					case 1 :
						intent = new Intent(HomeActivity.this,
								CallSmsSafeActivity.class);
						startActivity(intent);
						break;
					case 2 :
						intent = new Intent(HomeActivity.this,
								AppManagerActivity.class);
						startActivity(intent);
						break;	
					case 3 :
						intent = new Intent(HomeActivity.this,
								TaskManagerActivity.class);
						startActivity(intent);
						break;		
					case 4 :
						intent = new Intent(HomeActivity.this,
								TrafficManagerActivity.class);
						startActivity(intent);
						break;	
					case 6 :
						intent = new Intent(HomeActivity.this,
								SystemOptActivity.class);
						startActivity(intent);
						break;							
					case 7 :
						intent = new Intent(HomeActivity.this,
								AtoolsActivity.class);
						startActivity(intent);
						break;

					case 8 :
						intent = new Intent(HomeActivity.this,
								SettingActivity.class);
						startActivity(intent);
						break;

					default :
						break;
				}
			}
		});
	}
	private EditText et_first_entry_pwd;
	private EditText et_first_entry_confirm;
	private Button bt_first_entry_ok;
	private Button bt_first_entry_cancel;
	private AlertDialog alertDialog;
	/**
	 * 第一次进入对话框
	 */
	private void showFirstEntryDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_first_entry, null);
		et_first_entry_pwd = (EditText) view
				.findViewById(R.id.et_first_entry_pwd);
		et_first_entry_confirm = (EditText) view
				.findViewById(R.id.et_first_entry_confirm);
		bt_first_entry_cancel = (Button) view
				.findViewById(R.id.bt_first_entry_cancel);
		bt_first_entry_ok = (Button) view.findViewById(R.id.bt_first_entry_ok);
		bt_first_entry_cancel.setOnClickListener(this);
		bt_first_entry_ok.setOnClickListener(this);
		alertDialog = builder.create();
		alertDialog.setView(view, 0, 0, 0, 0);
		alertDialog.show();

	}
	private EditText et_normal_entry_pwd;
	private Button bt_normal_entry_ok;
	private Button bt_normal_entry_cancel;
	/**
	 * 正常进入对话框
	 */
	private void showNormalEntryDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_normal_entry, null);
		et_normal_entry_pwd = (EditText) view
				.findViewById(R.id.et_normal_entry_pwd);
		bt_normal_entry_cancel = (Button) view
				.findViewById(R.id.bt_normal_entry_cancel);
		bt_normal_entry_ok = (Button) view
				.findViewById(R.id.bt_normal_entry_ok);
		bt_normal_entry_cancel.setOnClickListener(this);
		bt_normal_entry_ok.setOnClickListener(this);
		alertDialog = builder.create();
		alertDialog.setView(view, 0, 0, 0, 0);
		alertDialog.show();
	}
	/**
	 * 判断密码是否设置为空
	 * 
	 * @return
	 */
	private boolean isSetupPwd() {
		String pwd = sp.getString("pwd", "");
		if (TextUtils.isEmpty(pwd)) {
			return false;
		} else {
			return true;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_first_entry_ok :
				String pwd = et_first_entry_pwd.getText().toString().trim();
				String pwd_confirm = et_first_entry_confirm.getText()
						.toString().trim();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(this, "密码不能为空！", Toast.LENGTH_LONG).show();
					return;
				}
				if (!pwd.equals(pwd_confirm)) {
					Toast.makeText(this, "密码必须一致！", Toast.LENGTH_LONG).show();
					return;
				}
				Editor editor = sp.edit();
				editor.putString("pwd", MD5Util.encode(pwd));
				editor.commit();
				alertDialog.dismiss();
				break;
			case R.id.bt_first_entry_cancel :
				alertDialog.dismiss();
				break;
			case R.id.bt_normal_entry_cancel :
				alertDialog.dismiss();
				break;
			case R.id.bt_normal_entry_ok :
				String entry_pwd = et_normal_entry_pwd.getText().toString()
						.trim();
				if (TextUtils.isEmpty(entry_pwd)) {
					Toast.makeText(this, "密码不能为空！", Toast.LENGTH_LONG).show();
					return;
				}
				String save_pwd = sp.getString("pwd", "");
				if (!(MD5Util.encode(entry_pwd)).equals(save_pwd)) {
					Toast.makeText(this, "密码错误！", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(HomeActivity.this,
						LostFindActivity.class);
				startActivity(intent);
				alertDialog.dismiss();
				break;
			default :
				break;
		}
	}
}
