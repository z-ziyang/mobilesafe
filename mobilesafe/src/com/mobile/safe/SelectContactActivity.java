package com.mobile.safe;

import java.util.List;

import com.mobile.safe.engine.ContactInfoProvider;
import com.mobile.safe.enity.ContactInfo;
import com.mobile.safe.utils.MyAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {

	private LayoutInflater inflater;
	private List<ContactInfo> contactInfos;
	private ListView lv_select_contact;
	private View loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
		inflater = LayoutInflater.from(SelectContactActivity.this);
		loading = findViewById(R.id.con_loading);

		MyAsyncTask myAsyncTask = new MyAsyncTask() {

			@Override
			public void onPreExcute() {
				// TODO Auto-generated method stub
				loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExcute() {
				// TODO Auto-generated method stub
				lv_select_contact.setAdapter(new ContactAdapter());
				loading.setVisibility(View.INVISIBLE);
			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				contactInfos = ContactInfoProvider
						.getContactInfo(SelectContactActivity.this);
			}
		};
        myAsyncTask.excute();
		lv_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				ContactInfo contactInfo = contactInfos.get(position);
				String phone = contactInfo.getNumber();
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				finish();

			}
		});
	}	
	private class ContactAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(getApplicationContext(),
					R.layout.contact_list_item, null);
			TextView tv_name = (TextView) view
					.findViewById(R.id.tv_contact_name);
			TextView tv_phone = (TextView) view
					.findViewById(R.id.tv_contact_number);
			ContactInfo info = contactInfos.get(position);
			tv_name.setText(info.getName());
			tv_phone.setText(info.getNumber());
			return view;
		}
	}
}
