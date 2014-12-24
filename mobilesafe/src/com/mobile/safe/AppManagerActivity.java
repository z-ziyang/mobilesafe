package com.mobile.safe;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.engine.AppInfoProvider;
import com.mobile.safe.enity.AppInfo;
import com.mobile.safe.utils.DensityUtil;
import com.mobile.safe.utils.MyAsyncTask;
import com.mobile.safe.utils.ShareUtils;

import android.app.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	private TextView tv_mem, tv_sd, tv_appmanger_status;
	private ListView lv_app_manager;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private PopupWindow popwindow;
	private View loading;
	private AppInfo selectAppInfo;
	private LinearLayout ll_uninstall, ll_start, ll_share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_mem = (TextView) findViewById(R.id.tv_free_mem);
		tv_sd = (TextView) findViewById(R.id.tv_free_sd);
		tv_appmanger_status = (TextView) findViewById(R.id.tv_appmanger_status);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		loading = findViewById(R.id.loading);
		tv_mem.setText("内存可用:" + getAvailRom());
		tv_sd.setText("SD卡可用:" + getAvailSD());
		// 利用滚动事件设置悬浮标签信息的更改
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem >= (userAppInfos.size())) {// 当前是系统程序
						tv_appmanger_status.setText("系统程序 ("
								+ systemAppInfos.size() + ")");
					} else {
						tv_appmanger_status.setText("用户程序 ("
								+ userAppInfos.size() + ")");
					}
				}
				dismissPopupWindow();
			}
		});

		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dismissPopupWindow();
				// 由适配器getItem决定
				Object obj = lv_app_manager.getItemAtPosition(position);
				if (obj instanceof AppInfo) {
					// 记录
					selectAppInfo = (AppInfo) obj;
					// 设置布局 并进行显示
					View popView = View.inflate(getApplicationContext(),
							R.layout.ui_popupwind_appmanager, null);
					ll_share = (LinearLayout) popView
							.findViewById(R.id.ll_share);
					ll_start = (LinearLayout) popView
							.findViewById(R.id.ll_start);
					ll_uninstall = (LinearLayout) popView
							.findViewById(R.id.ll_uninstall);
					ll_share.setOnClickListener(AppManagerActivity.this);
					ll_start.setOnClickListener(AppManagerActivity.this);
					ll_uninstall.setOnClickListener(AppManagerActivity.this);
					popwindow = new PopupWindow(popView,
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					// popView.setBackgroundDrawable(new ColorDrawable(
					// Color.TRANSPARENT));
					int[] location = new int[2];
					// 获取当前Item上下左右距离
					view.getLocationInWindow(location);
					// 设置显示位置
					popwindow.showAtLocation(parent,
							Gravity.TOP | Gravity.LEFT, location[0] + 60,
							location[1]);
					ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f,
							1.0f, 0.5f, 0.5f);
					sa.setDuration(600);

					TranslateAnimation tl = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0.1f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					tl.setDuration(600);
					// 动画加入集合中
					AnimationSet set = new AnimationSet(false);
					set.addAnimation(sa);
					set.addAnimation(tl);
					popView.setAnimation(set);
				}

			}
		});
		fillData();
	}
	public void fillData() {
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
				lv_app_manager.setAdapter(new MyAdapter());
			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserapp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}
			}
		}.excute();
	}
	/**
	 * 获取可用空间
	 * 
	 * @return
	 */
	public String getAvailSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(this, blockSize * availableBlocks);
	}
	public String getAvailRom() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(this, blockSize * availableBlocks);
	}
	private void dismissPopupWindow() {
		if (popwindow != null && popwindow.isShowing()) {
			popwindow.dismiss();
			popwindow = null;
		}
	}
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return position;
			} else if (position == (userAppInfos.size() + 1)) {
				return position;
			} else if (position <= userAppInfos.size()) {// 用户程序
				int newposition = position - 1;
				return userAppInfos.get(newposition);
			} else { // 系统程序
				int newposition = position - 1 - userAppInfos.size() - 1;
				return systemAppInfos.get(newposition);
			}

		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		// 关闭 某个Item的点击事件
		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return false;
			} else if (position == (userAppInfos.size() + 1)) {
				return false;
			}
			return super.isEnabled(position);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appinfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundResource(R.color.gray);
				tv.setText("用户程序 (" + userAppInfos.size() + ")");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundResource(R.color.gray);
				tv.setText("系统程序 (" + systemAppInfos.size() + ")");
				return tv;
			} else if (position <= userAppInfos.size()) {// 用户程序
				int newposition = position - 1;
				appinfo = userAppInfos.get(newposition);
			} else { // 系统程序
				int newposition = position - 1 - userAppInfos.size() - 1;
				appinfo = systemAppInfos.get(newposition);
			}
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_app_item, null);
				holder = new ViewHolder();
				holder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_app_name);
				holder.tv_applocation = (TextView) convertView
						.findViewById(R.id.tv_app_location);
				holder.tv_appversion = (TextView) convertView
						.findViewById(R.id.tv_app_version);
				holder.iv_appicon = (ImageView) convertView
						.findViewById(R.id.iv_app_icon);
				holder.ll_container = (LinearLayout) convertView
						.findViewById(R.id.ll_appstatus_container);
				convertView.setTag(holder);
			}

			holder.iv_appicon.setImageDrawable(appinfo.getAppicon());
			holder.tv_appname.setText(appinfo.getAppname());
			holder.tv_appversion.setText(appinfo.getVersion());
			if (appinfo.isInRom()) {
				holder.tv_applocation.setText("手机内存" + appinfo.isUserapp());
			} else {
				holder.tv_applocation.setText("外部存储" + appinfo.isUserapp());
			}
			// 去除 复用缓存 及其重要 不然上下滚动会不断出现
			holder.ll_container.removeAllViews();
			if (appinfo.isUsecontact()) {
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.contact);
				holder.ll_container.addView(
						iv, // 适配不同的分辨率
						DensityUtil.dipTopx(getApplicationContext(), 25),
						DensityUtil.dipTopx(getApplicationContext(), 25));
			}
			if (appinfo.isUsegps()) {
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.gps);
				holder.ll_container.addView(iv,
						DensityUtil.dipTopx(getApplicationContext(), 25),
						DensityUtil.dipTopx(getApplicationContext(), 25));
			}
			if (appinfo.isUsenet()) {
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.net);
				holder.ll_container.addView(iv,
						DensityUtil.dipTopx(getApplicationContext(), 25),
						DensityUtil.dipTopx(getApplicationContext(), 25));
			}
			if (appinfo.isUsesms()) {
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.sms);
				holder.ll_container.addView(iv,
						DensityUtil.dipTopx(getApplicationContext(), 25),
						DensityUtil.dipTopx(getApplicationContext(), 25));
			}
			return convertView;
		}
		public class ViewHolder {
			public TextView tv_applocation;
			public TextView tv_appname;
			public TextView tv_appversion;
			public TextView tv_app_location;
			public ImageView iv_appicon;
			public LinearLayout ll_container;
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.ll_share :
				ShareUtils.shareText(AppManagerActivity.this,
						"推荐你使用一款软件.名称为:" + selectAppInfo.getAppname() + ",版本:"
								+ selectAppInfo.getVersion());
				break;
			case R.id.ll_start :
				startApp();
				break;
			case R.id.ll_uninstall :
				if (selectAppInfo.isUserapp()) {
					uninstallApp();
				} else {
					Toast.makeText(this, "系统应用需要root权限才能卸载.", 1).show();
				}
				break;
			default :
				break;
		}
		dismissPopupWindow();
	}
	/**
	 * 启动某个程序
	 */
	private void startApp() {
		String packname = selectAppInfo.getPackname();
		try {
			// 根据包获取Activity
			// 标识flags 必须设置 
			// flags:: Use any combination of
			// GET_ACTIVITIES, GET_GIDS, GET_CONFIGURATIONS,
			// GET_INSTRUMENTATION, GET_PERMISSIONS, GET_PROVIDERS,
			// GET_RECEIVERS, GET_SERVICES, GET_SIGNATURES,
			// GET_UNINSTALLED_PACKAGES
			PackageInfo packinfo = getPackageManager().getPackageInfo(packname,
					PackageManager.GET_ACTIVITIES);
			// 获取Activity
			ActivityInfo[] activityinfos = packinfo.activities;
			if (activityinfos != null && activityinfos.length > 0) {
				ActivityInfo activityinfo = activityinfos[0];
				// 获取类名
				String className = activityinfo.name;
				Intent intent = new Intent();
				intent.setClassName(packname, className);
				startActivity(intent);
			} else {
				Toast.makeText(this, "无法启动应用程序!", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "无法启动应用程序", 0).show();
		}
	}
	/**
	 * 卸载程序
	 */
	private void uninstallApp() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);	
		intent.setAction(Intent.ACTION_DELETE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + selectAppInfo.getPackname()));
		startActivityForResult(intent, 2);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 2) {
			fillData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
