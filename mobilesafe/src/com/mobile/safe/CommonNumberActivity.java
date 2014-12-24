package com.mobile.safe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobile.safe.db.dao.CommonNumDao;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {

	private ExpandableListView el;
	private List<String> groupNames;
	private Map<Integer, List<String>> childrenCacheInfos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		childrenCacheInfos = new HashMap<Integer, List<String>>();
		el = (ExpandableListView) findViewById(R.id.exl_home);
		el.setAdapter(new MyExpandableAdapter());
		el.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				String number = childrenCacheInfos.get(groupPosition)
						.get(childPosition).split("\n")[1];
				intent.setData(Uri.parse("tel:" + number));
				startActivity(intent);
				return false;
			}
		});
	}
	private class MyExpandableAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			// return CommonNumDao.getGroupCount();
			// 将信息存在内存里
			groupNames = CommonNumDao.getGroupInfos();
			return groupNames.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			// 将每一条数据缓存在 childrenCacheInfos里面
			List<String> childrenInfo;
			if (childrenCacheInfos.containsKey(groupPosition)) {
				childrenInfo = childrenCacheInfos.get(groupPosition);
			} else {
				childrenInfo = CommonNumDao
						.getChildrenInfosByPosition(groupPosition);
				childrenCacheInfos.put(groupPosition, childrenInfo);
			}
			return childrenInfo.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(25);
			tv.setTextColor(R.color.alicBlue);
			tv.setText("      " + groupNames.get(groupPosition));
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(18);
			tv.setTextColor(R.color.black_light);
			// 获取每一个Item信息
			tv.setText(childrenCacheInfos.get(groupPosition).get(childPosition));
			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
