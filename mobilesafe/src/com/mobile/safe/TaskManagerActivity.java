package com.mobile.safe;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.engine.TaskInfoProvider;
import com.mobile.safe.enity.TaskInfo;
import com.mobile.safe.ui.MyToast;
import com.mobile.safe.utils.MyAsyncTask;
import com.mobile.safe.utils.TaskUtil;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {

	private TextView tv_process_count, tv_mem_info;
	private int runningProcessCount;
	private long availRam, totalRam;
	private ListView lv_task_manager;
	private View loading;
	private List<TaskInfo> taskInfos, userTaskInfos, sysTaskInfos;
	public MyAdapter myAdapter;
	private Button bt_select_all;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		loading = findViewById(R.id.loading);
		initData();
		fillProcessData();
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_task_manager.getItemAtPosition(position);
				if (obj != null) {
					TaskInfo taskInfo = (TaskInfo) obj;
					if (getPackageName().equals(taskInfo.getPackName())) { // 自己
						return;
					}
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
					} else {
						taskInfo.setChecked(true);
					}
				}
				myAdapter.notifyDataSetChanged();
			}
		});
	}
	public void fillProcessData() {
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
				lv_task_manager.setAdapter(myAdapter);
			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				myAdapter = new MyAdapter();
				taskInfos = TaskInfoProvider
						.getTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				sysTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskinfo : taskInfos) {
					if (taskinfo.isUserTask()) {
						userTaskInfos.add(taskinfo);
					} else {
						sysTaskInfos.add(taskinfo);
					}
				}
			}
		}.excute();
	}
	public void initData() {
		runningProcessCount = TaskUtil
				.getRunningProcessCount(TaskManagerActivity.this);
		availRam = TaskUtil.getAvailRam(this);
		totalRam = TaskUtil.getTotalRam();
		tv_process_count.setText("运行中进程:" + runningProcessCount + "个");
		tv_mem_info.setText("可用/总内存:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
	}
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//默认只显示用户进程
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean showsystem = sp.getBoolean("showsystem", false);
			if (showsystem) {
				return userTaskInfos.size() + 1 + sysTaskInfos.size() + 1;
			}else{
				return userTaskInfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return null;
			} else if (position == (userTaskInfos.size() + 1)) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				int newpositon = position - 1;
				return userTaskInfos.get(newpositon);
			} else {
				int newpositon = position - 1 - userTaskInfos.size() - 1;
				return sysTaskInfos.get(newpositon);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TaskInfo taskInfo;

			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(R.color.gray);
				tv.setTextColor(R.color.black);
				tv.setTextSize(18);
				tv.setText("用户进程:" + userTaskInfos.size() + "个");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(R.color.gray);
				tv.setTextColor(R.color.black);
				tv.setTextSize(18);
				tv.setText("系统进程:" + sysTaskInfos.size() + "个");
				return tv;
			} else if (position <= userTaskInfos.size()) {
				int newpositon = position - 1;
				taskInfo = userTaskInfos.get(newpositon);
			} else {
				int newpositon = position - 1 - userTaskInfos.size() - 1;
				taskInfo = sysTaskInfos.get(newpositon);
			}

			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {

				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_task_item, null);
				holder = new ViewHolder();
				holder.iv_task_icon = (ImageView) convertView
						.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) convertView
						.findViewById(R.id.tv_task_name);
				holder.tv_task_mem_size = (TextView) convertView
						.findViewById(R.id.tv_task_mem);
				holder.check_task = (CheckBox) convertView
						.findViewById(R.id.cb_task_status);
				convertView.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(taskInfo.getTaskIcon());
			holder.tv_task_name.setText(taskInfo.getTaskName());
			holder.tv_task_mem_size.setText("内存占用:"
					+ Formatter.formatFileSize(getApplicationContext(),
							taskInfo.getMemsize()));
			holder.check_task.setChecked(taskInfo.isChecked());
			if (getPackageName().equals(taskInfo.getPackName())) {// 我自己.
				holder.check_task.setVisibility(View.INVISIBLE);
			} else {
				holder.check_task.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

	}
	public class ViewHolder {
		public TextView tv_task_name, tv_task_mem_size;
		public ImageView iv_task_icon;
		public CheckBox check_task;
	}
	/**
	 * 
	 * @param view
	 */
	public void enterSetting(View view) {
		Intent intent=new Intent(this,TaskSettingActivity.class);
		startActivityForResult(intent,0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == 100) {
			myAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 全选 这里注意 应分类选择处理 在清理过程中易出现下表越界
	 * 
	 * @param view
	 */
	private boolean isChecked;
	public void selectAll(View view) {
		if (isChecked) {
			// 已经全选了 ,设置为全不选
			for (TaskInfo info : userTaskInfos) {
				info.setChecked(false);
			}
			for (TaskInfo info : sysTaskInfos) {
				info.setChecked(false);
			}
			isChecked = false;
			bt_select_all.setText("全选");
		} else {
			// 没有全部选择,设置全选
			for (TaskInfo info : userTaskInfos) {
				info.setChecked(true);
				if (getPackageName().equals(info.getPackName())) {
					info.setChecked(false);
				}
			}
			for (TaskInfo info : sysTaskInfos) {
				info.setChecked(true);
			}
			isChecked = true;
			bt_select_all.setText("取消全选");
		}

		myAdapter.notifyDataSetChanged();
	}
	/**
	 * 清理进程
	 * 
	 * @param view
	 */
	public void killProcess(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long saveMem = 0;
		// 获取选中的进程
		List<TaskInfo> killInfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : userTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
				count++;
				saveMem += info.getMemsize();
				killInfos.add(info);
			}
		}
		for (TaskInfo info : sysTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
				count++;
				saveMem += info.getMemsize();
				killInfos.add(info);
			}
		}
		String memStr = Formatter.formatFileSize(this, saveMem);
		// Toast.makeText(this, "杀死了" + count + "个进程,释放了" + memStr + "的内存", 1)
		// .show();
		MyToast.show(R.drawable.notification, "杀死了" + count + "个进程,释放了"
				+ memStr + "的内存", TaskManagerActivity.this);
		// 移除数据
		for (TaskInfo killInfo : killInfos) {
			if (killInfo.isUserTask()) {
				userTaskInfos.remove(killInfo);
			} else {
				sysTaskInfos.remove(killInfo);
			}
		}
		// 更新 数据
		myAdapter.notifyDataSetChanged();
		runningProcessCount -= count;
		availRam += saveMem;
		tv_process_count.setText("运行中进程:" + runningProcessCount + "个");
		tv_mem_info.setText("可用/总内存:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
	}
}
