package com.mobile.safe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity  extends Activity{

	protected static final String TAG = "BaseSetupActivity";
	public SharedPreferences sp;
	//手指识别器
	private GestureDetector gesture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//获取手指监听 具体实现接口里面的方法
		gesture=new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				if (Math.abs(velocityX)<200) {
					Log.i(TAG, "动作不合法！");
					return true;
				}
				if (Math.abs(e1.getRawY()-e2.getRawY())>100) {
					Log.i(TAG, "竖直方向动作不合法！");
					return true;
				}
				if ((e1.getRawX()-e2.getRawX())>200) {
					showNext();
					return true;
				}
				if ((e2.getRawX()-e1.getRawX())>200) {
					showPre();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		initView();
		setupView();
	}
	/**
	 * 让上面的手指触摸生效
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		gesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	/**
	 * 初始化View
	 */
    public abstract void initView();
    /**
     * 设置View对象的响应事件
     */
    public abstract void setupView();
    /**
     * 显示下面一个界面
     */
    public abstract void showNext(); 
    /**
     * 显示前面一个界面
     */
    public abstract void showPre();
    public void next(View view){
    	showNext();
    }
    public void pre(View view){
        showPre();   	
    }
}
