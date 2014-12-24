package com.mobile.safe.adapter;

import com.mobile.safe.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.NoCopySpan.Concrete;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeAdapter extends BaseAdapter {
	private static final String[] names = {"手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "病毒查杀", "系统优化", "高级工具", "设置中心"};

	private static final int[] icons = {R.drawable.safe,
			R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
			R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings};
	private Context mContext;
	private LayoutInflater inflater;
	public HomeAdapter( Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext=mContext;
		inflater=LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView== null) {
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.grid_home_item, null);
			viewHolder.mGridImage=(ImageView)convertView.findViewById(R.id.gv_image);
			viewHolder.mGridName=(TextView)convertView.findViewById(R.id.gv_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.mGridName.setText(names[position]);
		if (position == 0) {
			SharedPreferences sp=mContext.getSharedPreferences("config", mContext.MODE_PRIVATE);
			String  newname=sp.getString("newname", "");
			if (!TextUtils.isEmpty(newname)) {
				viewHolder.mGridName.setText(newname);
			}
		}		
		viewHolder.mGridImage.setImageResource(icons[position]);
		return convertView;
	}
	public class ViewHolder {
		public ImageView mGridImage;
		public TextView mGridName;
	}

}
