package com.mobile.safe;

import com.mobile.safe.ui.ActivityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity  extends Activity{

	private TextView tv_lostfind_number;
	private SharedPreferences sp;
	private ImageView iv_lostfind_status;;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_lost_find);
		tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
		tv_lostfind_number.setText(sp.getString("safenumber", ""));
		iv_lostfind_status=(ImageView)findViewById(R.id.iv_lostfind_status);
		boolean protect=sp.getBoolean("proctect", false);
		if (protect) {
			iv_lostfind_status.setImageResource(R.drawable.lock);
		}else {
			iv_lostfind_status.setImageResource(R.drawable.unlock);
		}
		
	    //判断用户是否进行设置向导
		if (!isSetup()) {
			Intent intent=new Intent(this,Setup1Activity.class);
		    startActivity(intent);
		    finish();
		}

	}
	/**
	 * 点击设置向导
	 * @param view
	 */
	public void reEntrySetup(View view) {
		ActivityUtils.startActivityAndFinish(this, Setup1Activity.class);
	}
	private boolean isSetup() {
		return sp.getBoolean("setup", false);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.lost_find_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case R.id.item_change_name :
				//
				AlertDialog.Builder builder=new Builder(this);
				builder.setTitle("更改名称");
				builder.setIcon(R.drawable.notification);
				final EditText et=new EditText(this);
				et.setHint("请输入新的名称");
				builder.setView(et);
				builder.setPositiveButton("确定",new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String newname=et.getText().toString().trim();
						Editor editor=sp.edit();
						editor.putString("newname", newname);
						editor.commit();
						//一般放在SD卡中备份
					}
				});
				builder.create().show();
				break;

			default :
				break;
		}
		return true;
	}

}
