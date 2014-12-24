package com.mobile.safe.ui;

import com.mobile.safe.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast  {
	
	public static void show(int icon, String msg, Context context){
		Toast toast = new Toast(context);
		View view = View.inflate(context, R.layout.ui_my_toast, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_msg);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_toast_icon);
		tv.setText(msg);
		iv.setImageResource(icon);
		toast.setView(view);
		//toast.setGravity(Gravity.LEFT|Gravity.TOP, xOffset, yOffset);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}
