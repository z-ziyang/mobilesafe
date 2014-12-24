package com.mobile.safe.ui;

import com.mobile.safe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout{

	private View view;
	private TextView tv_settingview_title;
	private TextView tv_settingview_content;
	private CheckBox tv_check_status;
	private String checked_text;
	private String unchecked_text;
	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);	
	}
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
		//将传进来的属性集attrs和自定义属性建立映射关系
     	TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.setting_view_style);
	    //获取XML传进来的content title的值   	
	    String title=typedArray.getString(R.styleable.setting_view_style_title);
	    checked_text=typedArray.getString(R.styleable.setting_view_style_checked_text);
	    unchecked_text=typedArray.getString(R.styleable.setting_view_style_unchecked_text);
	    tv_settingview_content.setText(unchecked_text);
	    tv_settingview_title.setText(title);
	   
	    //释放资源
	    typedArray.recycle();
	}
	public SettingView(Context context) {
		super(context);
		initView(context);
		}

	public void initView(Context context) {
		view=View.inflate(context, R.layout.ui_setting, this);
		tv_settingview_content=(TextView) view.findViewById(R.id.tv_settingview_content);
		tv_settingview_title=(TextView) view.findViewById(R.id.tv_settingview_title);
	    tv_check_status=(CheckBox)view.findViewById(R.id.tv_checkBox);
	}
	/**
	 * 返回checkbox状态
	 * @return
	 */
    public boolean isChecked() {
    	
    	return tv_check_status.isChecked();
    }
    public void setChecked(boolean checked) {
    	tv_check_status.setChecked(checked);
    	if (checked) {
			setContent(checked_text);
		} else {
			setContent(unchecked_text);
		}
    }
    public void setContent(String text) {
    	tv_settingview_content.setText(text);
    }
}
