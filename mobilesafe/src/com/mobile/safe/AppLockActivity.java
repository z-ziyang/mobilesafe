package com.mobile.safe;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.db.dao.AppLockDao;
import com.mobile.safe.engine.AppInfoProvider;
import com.mobile.safe.enity.AppInfo;
import com.mobile.safe.utils.DelayExecuter;
import com.mobile.safe.utils.MyAsyncTask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppLockActivity extends Activity implements OnClickListener {
	private TextView tv_lock, tv_unlock, tv_locked_status, tv_unlock_status;
	private LinearLayout ll_unlock, ll_locked;
	private ListView lv_unlock, lv_locked;
	private List<AppInfo> appInfos;
	private ProgressBar pb_loading;
	private AppLockAdapter lockAdapter, unLockAdapter;
	private AppLockDao dao;
	private List<AppInfo> appLockedInfos, appunLockInfos;// 存放已经加锁的信息
	private boolean removingFlag;
	private PopupWindow popwindow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		initView();
		fillData();
		// 未加锁点击事件
		lv_unlock.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (removingFlag) {
					System.out.println("removingFlag" + removingFlag);
					return;
				}
				removingFlag = true;
				// 利用powindow使得屏幕 拒绝用户短时间内操作出现的bug 
				///....不过Bug依然存在 只是缓解了
				popwindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				popwindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0,
						0);

				AppInfo appInfo = (AppInfo) lv_unlock
						.getItemAtPosition(position);
				String packname = appInfo.getPackname();
				dao.add(packname); // 点击锁定 保存数据
				appLockedInfos.add(appInfo);
				appunLockInfos.remove(appInfo); // 移除加锁的程序
				// 设置动画
				TranslateAnimation ta = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(500);
				view.startAnimation(ta);
				// 数据更新延迟800毫秒
				new DelayExecuter() {
					@Override
					public void onPostExecute() {
						unLockAdapter.notifyDataSetChanged();
						lockAdapter.notifyDataSetChanged();
						removingFlag = false;
						popwindow.dismiss();
					}
				}.execute(500);

			}
		});
		lv_locked.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (removingFlag) {
					System.out.println("removingFlag" + removingFlag);
					return;
				}
				removingFlag = true;
				popwindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				popwindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0,
						0);

				AppInfo appInfo = (AppInfo) lv_locked
						.getItemAtPosition(position);
				String packname = appInfo.getPackname();
				dao.delete(packname);
				appunLockInfos.add(appInfo);
				appLockedInfos.remove(appInfo);
				// 设置动画
				TranslateAnimation ta = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(500);
				view.startAnimation(ta);
				// 数据更新延迟600毫秒
				new DelayExecuter() {
					@Override
					public void onPostExecute() {
						unLockAdapter.notifyDataSetChanged();
						lockAdapter.notifyDataSetChanged();
						removingFlag = false;
						popwindow.dismiss();
					}
				}.execute(500);
			}
		});
	}
	public void initView() {
		tv_lock = (TextView) findViewById(R.id.tv_locked);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		lv_locked = (ListView) findViewById(R.id.lv_locked);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		tv_locked_status = (TextView) findViewById(R.id.tv_locked_status);
		tv_unlock_status = (TextView) findViewById(R.id.tv_unlock_status);
		// pop的高度和宽度和屏幕一样 屏蔽操作
		popwindow = new PopupWindow(new View(this), getWindowManager()
				.getDefaultDisplay().getWidth(), getWindowManager()
				.getDefaultDisplay().getHeight());
		appLockedInfos = new ArrayList<AppInfo>();
		appunLockInfos = new ArrayList<AppInfo>();
		dao = new AppLockDao(getApplicationContext());
		tv_lock.setOnClickListener(this);
		tv_unlock.setOnClickListener(this);

	}
	public void fillData() {
		new MyAsyncTask() {

			@Override
			public void onPreExcute() {
				// TODO Auto-generated method stub
				pb_loading.setVisibility(ProgressBar.VISIBLE);
			}

			@Override
			public void onPostExcute() {
				// TODO Auto-generated method stub
				pb_loading.setVisibility(ProgressBar.INVISIBLE);
				unLockAdapter = new AppLockAdapter(appunLockInfos, true);
				lv_unlock.setAdapter(unLockAdapter);

				lockAdapter = new AppLockAdapter(appLockedInfos, false);
				lv_locked.setAdapter(lockAdapter);

			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				// 获取初始数据:加锁和未加锁的
				for (AppInfo info : appInfos) {
					if (dao.find(info.getPackname())) {
						appLockedInfos.add(info);
					} else {
						appunLockInfos.add(info);
					}
				}

			}
		}.excute();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.tv_locked : // 加锁
				tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
				tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				ll_unlock.setVisibility(View.INVISIBLE);
				ll_locked.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_unlock : // 未加锁
				tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				tv_lock.setBackgroundResource(R.drawable.tab_right_default);
				ll_unlock.setVisibility(View.VISIBLE);
				ll_locked.setVisibility(View.INVISIBLE);
				break;
			default :
				break;
		}
	}
	public class AppLockAdapter extends BaseAdapter {

		private List<AppInfo> showAppInfos;
		private boolean unlockflag;
		public AppLockAdapter(List<AppInfo> showAppInfos, boolean unlockflag) {
			this.showAppInfos = showAppInfos;
			this.unlockflag = unlockflag;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (unlockflag) { // 更新状态
				tv_unlock_status.setText("未加锁软件(" + showAppInfos.size() + ")个");
			} else {
				tv_locked_status.setText("加锁软件(" + showAppInfos.size() + ")个");
			}
			return showAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return showAppInfos.get(position);
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
			if (convertView != null && convertView instanceof RelativeLayout) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				if (unlockflag) {  //分情况 进行初始化
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_appunlock_item, null);
				} else {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_applocked_item, null);
				}
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_applock_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_applock_name);
				convertView.setTag(holder);
			}
			AppInfo appinfo = showAppInfos.get(position);
			holder.iv_icon.setImageDrawable(appinfo.getAppicon());
			holder.tv_name.setText(appinfo.getAppname());

			return convertView;
		}
	}
	public class ViewHolder {
		public TextView tv_name;
		public ImageView iv_icon;
	}
}
