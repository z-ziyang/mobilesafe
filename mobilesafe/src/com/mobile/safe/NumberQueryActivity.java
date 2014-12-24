package com.mobile.safe;

import com.mobile.safe.db.dao.AddressDao;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberQueryActivity extends Activity {
    private EditText et_query_number;
    private TextView tv_query_address;
    private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		et_query_number=(EditText)findViewById(R.id.et_numberquery);
		tv_query_address=(TextView)findViewById(R.id.tv_address);
		vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
		//文本发生变化时自动查询
		et_query_number.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String number=s.toString();
				String address=AddressDao.getAddress(number);
				tv_query_address.setText("归属地为:"+address);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	public void numberQuery(View v) {
		String number=et_query_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(getApplicationContext(), "号码不能为空！", Toast.LENGTH_LONG).show();
			Animation shake=AnimationUtils.loadAnimation(this, R.anim.shake);
			et_query_number.setAnimation(shake);
		//	vibrator.vibrate(200);
			return;	
		} 
		String address=AddressDao.getAddress(number);
		tv_query_address.setText("归属地为:"+address);
	}
}
