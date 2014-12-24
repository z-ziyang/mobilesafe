package com.mobile.safe;

import java.util.List;

import com.mobile.safe.engine.BootAppInfo;
import com.mobile.safe.enity.AppInfo;
import com.mobile.safe.utils.MyAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * android.intent.action.BOOT_COMPLETED
 * 
 * @author
 * 
 */
public class CleanStartupActivity extends Activity {

	private List<AppInfo> appInfos;
	private ListView lv_clean_startup;
	private View loading;
	private TextView tv_startup_app_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_startup);
		lv_clean_startup = (ListView) findViewById(R.id.lv_clean_startup);
		tv_startup_app_status = (TextView) findViewById(R.id.tv_startup_app_status);
		loading = (LinearLayout) findViewById(R.id.startup_loading);
		new MyAsyncTask() {

			@Override
			public void onPreExcute() {
				// TODO Auto-generated method stub
				loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExcute() {
				// TODO Auto-generated method stub
				loading.setVisibility(View.INVISIBLE);		
				tv_startup_app_status.setText("自动开机程序" + appInfos.size() + "个");
			    lv_clean_startup.setAdapter(new MyBootAdapter());
			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				try {
					appInfos = BootAppInfo.getBootApps(getApplicationContext());
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.excute();
		
	}
	private class MyBootAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			AppInfo appInfo = appInfos.get(position);
			if (convertView != null && convertView instanceof RelativeLayout) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_clean_startup, null);
				holder = new ViewHolder();
				holder.iv_startup_icon = (ImageView) convertView
						.findViewById(R.id.iv_clean_startup_icon);
				holder.tv_startup_name = (TextView) convertView
						.findViewById(R.id.tv_clean_startup_name);
				holder.tv_startup_packagename = (TextView) convertView
						.findViewById(R.id.tv_clean_startup_packagename);
				convertView.setTag(holder);
			}
			holder.iv_startup_icon.setImageDrawable(appInfo.getAppicon());
			holder.tv_startup_name.setText(appInfo.getAppname());
			holder.tv_startup_packagename.setText(appInfo.getPackname());
			return convertView;
		}
	}
	public class ViewHolder {
		public TextView tv_startup_name, tv_startup_packagename;
		public ImageView iv_startup_icon;
	}
}
