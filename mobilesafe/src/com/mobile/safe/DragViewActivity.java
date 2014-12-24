package com.mobile.safe;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import android.widget.RelativeLayout;

import android.widget.TextView;

public class DragViewActivity extends Activity {
	private ImageView iv_drag_view;
	private float startX, startY;
	private Display display;
	private TextView tv_drag_view;
	private SharedPreferences sp;
	private long firstClickTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.activity_drag_view);
		tv_drag_view = (TextView) findViewById(R.id.tv_drag_view);
		iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 得到位置 并设置image_drag位置
		int lastX = sp.getInt("lastX", 0);
		int lastY = sp.getInt("lastY", 0);
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag_view
				.getLayoutParams();
		params.leftMargin = lastX;
		params.topMargin = lastY;
		iv_drag_view.setLayoutParams(params);
		// 得到屏幕高度和宽度
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		display = wm.getDefaultDisplay();

		setTVPosition(lastY);
		iv_drag_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						System.out.println("--->" + "DOWN");
						startX = event.getRawX();
						startY = event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE :
						System.out.println("--->" + "MOVE");
						float newX = event.getRawX();
						float newY = event.getRawY();
						float dx = newX - startX;
						float dy = newY - startY;
						int l = iv_drag_view.getLeft();
						int r = iv_drag_view.getRight();
						int t = iv_drag_view.getTop();
						int b = iv_drag_view.getBottom();
						int newl = (int) (l + dx);
						int newr = (int) (r + dx);
						int newt = (int) (t + dy);
						int newb = (int) (b + dy);
				
						// 不能移除屏幕
						if (newl < 0 || newr > display.getWidth() || newt < 0
								|| newb > display.getHeight() - 30) {
							break;
						}
						// 获取文本框的高度
						setTVPosition(newt);
						iv_drag_view.layout(newl, newt, newr, newb);
						startX = event.getRawX();
						startY = event.getRawY();
						break;
					case MotionEvent.ACTION_UP :
						System.out.println("--->" + "UP");
						Editor editor = sp.edit();
						editor.putInt("lastX", iv_drag_view.getLeft());
						editor.putInt("lastY", iv_drag_view.getTop());
						editor.commit();
						break;
					default :
						break;
				}
				return false;
			}

		});
		iv_drag_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 双击事件的处理
				if (firstClickTime > 0) {
					long secondTime = System.currentTimeMillis();
					if (secondTime - firstClickTime < 500) {
						System.out.println("-->" + "双击事件");
						int width=iv_drag_view.getRight()-iv_drag_view.getLeft();
						//左边的距离和右边的距离
					    int l=display.getWidth()/2 - width/2;
					    int r=display.getWidth()/2 + width/2;
					    iv_drag_view.layout(l, iv_drag_view.getTop(), r, iv_drag_view.getBottom());
					    Editor editor = sp.edit();
						editor.putInt("lastX", iv_drag_view.getLeft());
						editor.putInt("lastY", iv_drag_view.getTop());
						editor.commit();
					}
					firstClickTime = 0;
				}
				// 判断 是否是第一次点击 , 记录点击时间
				firstClickTime = System.currentTimeMillis();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						firstClickTime = 0;
					}
				}).start();
			}
		});
	}
	/**
	 * 设置拖动的文本位置
	 * 
	 * @param newt
	 */
	public void setTVPosition(int newt) {
		int height = tv_drag_view.getBottom() - tv_drag_view.getTop();
		if (newt > display.getHeight() / 2) {
			// 图片在下面 ,文本设置在上面
			tv_drag_view.layout(tv_drag_view.getLeft(), 0,
					tv_drag_view.getRight(), height);
		} else {
			// 文本设置在下面
			tv_drag_view.layout(tv_drag_view.getLeft(), display.getHeight()
					- height - 30, tv_drag_view.getRight(),
					display.getHeight() - 30);
		}
	}

}
