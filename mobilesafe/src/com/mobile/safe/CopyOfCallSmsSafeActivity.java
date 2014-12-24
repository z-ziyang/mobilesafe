package com.mobile.safe;

import java.util.List;

import com.mobile.safe.db.dao.BlackNumDao;
import com.mobile.safe.enity.BlackNumberInfo;
import com.mobile.safe.utils.MyAsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CopyOfCallSmsSafeActivity extends Activity implements OnClickListener {
	private ListView lv_call_sms;
	private View loadingView;
	private BlackNumDao dao;
	private List<BlackNumberInfo> infos;
	private CallSmsAdapter adapter;
	private EditText et_blacknumber;
	private RadioGroup rg_mode;
	private Button bt_blacknumber_ok, bt_blacknumber_cancle, bt_addnumber;
	private AlertDialog dialog;
	private int maxnumber = 20;
	private int offset = 0, totalNumber = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms);
		loadingView = findViewById(R.id.call_loading);
		bt_addnumber = (Button) findViewById(R.id.add_number);
		bt_addnumber.setOnClickListener(this);
		lv_call_sms = (ListView) findViewById(R.id.lv_call_sms);
		dao = new BlackNumDao(this);
		totalNumber = dao.getMaxBlackNum();
		//向下滚动 每次只加载20条数据
		lv_call_sms.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE :
						int position = lv_call_sms.getLastVisiblePosition(); // 19
						int total = infos.size(); // 20
						if (position == (total - 1)) {
							System.out.println("---->" + "移动到了最后面,加载更多的数据");
							offset += maxnumber;
							if (offset > totalNumber) {
								Toast.makeText(getApplicationContext(),
										"没有更多数据了.", Toast.LENGTH_LONG).show();
							    return;
							}
							fillData();
						}
						break;
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		fillData();
	}

	/**
	 * 加载数据
	 */
	public void fillData() {
		MyAsyncTask myAsyncTask = new MyAsyncTask() {
			@Override
			public void onPreExcute() {
				// TODO Auto-generated method stub
				loadingView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExcute() {
				// TODO Auto-generated method stub
				loadingView.setVisibility(View.INVISIBLE);
				if (adapter == null) {
					adapter = new CallSmsAdapter();
					lv_call_sms.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}

			}
			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				// infos = dao.findAllBlackNum();
				if (infos == null) {
					infos = dao.findByPage(maxnumber, offset);
				} else {
					infos.addAll(dao.findByPage(maxnumber, offset));
				}
			}
		};
		myAsyncTask.excute();
	}
	public class CallSmsAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final BlackNumberInfo info = infos.get(position);
			ViewHolder hodler;
			if (convertView != null) {
				// 复用历史缓存的view对象
				hodler = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_callsms_item, null);
				hodler = new ViewHolder();
				hodler.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_call_sms_mode);
				hodler.tv_number = (TextView) convertView
						.findViewById(R.id.tv_call_sms_number);
				hodler.iv_callsms_delete = (ImageView) convertView
						.findViewById(R.id.iv_callsms_delete);
				convertView.setTag(hodler);
			}
			final String number = info.getNumber();
			String mode = info.getMode();
			hodler.iv_callsms_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					boolean result = dao.deleteBlackNum(number);
					if (result) {
						infos.remove(info);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CopyOfCallSmsSafeActivity.this, "失败",
								Toast.LENGTH_LONG).show();
					}

				}
			});
			hodler.tv_number.setText(number);
			if ("1".equals(mode)) {
				hodler.tv_mode.setText("全部拦截");
			} else if ("2".equals(mode)) {
				hodler.tv_mode.setText("电话拦截");
			} else if ("3".equals(mode)) {
				hodler.tv_mode.setText("短信拦截");
			}
			return convertView;
		}
	}
	/**
	 * view对象孩子引用的容器
	 * 
	 * @author 
	 * 
	 */
	public static class ViewHolder {
		private TextView tv_number;
		private TextView tv_mode;
		private ImageView iv_callsms_delete;
	}

	public void addBlackNumber() {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View dialogview = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		dialog.setView(dialogview, 0, 0, 0, 0);
		et_blacknumber = (EditText) dialogview
				.findViewById(R.id.et_blacknumber);
		rg_mode = (RadioGroup) dialogview.findViewById(R.id.ra_mode);
		bt_blacknumber_ok = (Button) dialogview
				.findViewById(R.id.bt_blacknumber_ok);
		bt_blacknumber_cancle = (Button) dialogview
				.findViewById(R.id.bt_blacknumber_cancel);
		bt_blacknumber_ok.setOnClickListener(this);
		bt_blacknumber_cancle.setOnClickListener(this);

		dialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_blacknumber_cancel :
				dialog.dismiss();
				break;
			case R.id.bt_blacknumber_ok :
				String number = et_blacknumber.getText().toString().trim();
				// /获取group中的id
				int id = rg_mode.getCheckedRadioButtonId();
				String mode = "";
				switch (id) {
					case R.id.ra_all :
						mode = "1";
						break;

					case R.id.ra_phone :
						mode = "2";
						break;
					case R.id.ra_sms :
						mode = "3";
						break;
				}
				if (TextUtils.isEmpty(number) || TextUtils.isEmpty(mode)) {
					Toast.makeText(this, "号码或者拦截模式不能为空", Toast.LENGTH_LONG)
							.show();
					return;
				}
				dao.addBlackNum(number, mode);// 数据被存到数据库.
				infos.add(0, new BlackNumberInfo(number, mode));
				// 通知list 刷新
				adapter.notifyDataSetChanged();

				dialog.dismiss();
				break;
			case R.id.add_number :
				addBlackNumber();
				break;
		}

	}

}
