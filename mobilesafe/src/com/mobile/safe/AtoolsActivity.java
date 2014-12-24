package com.mobile.safe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.mobile.safe.engine.SmsBackUp;

import com.mobile.safe.ui.ActivityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	public void numberAddressQuery(View view) {
		ActivityUtils.startActivity(this, NumberQueryActivity.class);
	}
	public void commonNumberQuery(View view) {
		ActivityUtils.startActivity(this, CommonNumberActivity.class);
	}
	public void appLock(View v) {
		ActivityUtils.startActivity(this, AppLockActivity.class);
	}
	public void smsBackup(View view) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml");
			FileOutputStream fos = new FileOutputStream(file);
			new AsyncTask<OutputStream, Integer, Boolean>() {

				@Override
				protected Boolean doInBackground(OutputStream... params) {
					try {
						SmsBackUp smsUtils = new SmsBackUp(
								getApplicationContext());
						smsUtils.backUpSms(params[0],
								new SmsBackUp.BackupProcessListener() {
									@Override
									public void onProcessUpdate(int process) {
										dialog.setProgress(process);

									}

									@Override
									public void beforeBackup(int max) {
										dialog.setMax(max);

									}
								});
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				@Override
				protected void onPreExecute() {
					dialog = new ProgressDialog(AtoolsActivity.this);
					dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					dialog.setTitle("提示:");
					dialog.setMessage("正在备份短信....");
					dialog.show();
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					dialog.dismiss();
					if (result) {
						Toast.makeText(getApplicationContext(), "备份成功",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "备份失败",
								Toast.LENGTH_LONG).show();
					}
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					// TODO Auto-generated method stub
					super.onProgressUpdate(values);
				}

			}.execute(fos);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "备份失败..", Toast.LENGTH_LONG)
					.show();
		}
	}
	
}
