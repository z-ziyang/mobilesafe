package com.mobile.safe;

import java.io.File;

import com.mobile.safe.utils.MyAsyncTask;

import android.app.Activity;
import android.os.Environment;

public class CleanSDActivity extends Activity {
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		//fillData();

	}

	public void fillData() {
		new MyAsyncTask() {

			@Override
			public void onPreExcute() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPostExcute() {
				// TODO Auto-generated method stub

			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
				startScannDir();
			}
		}.excute();
	}

	public void startScannDir() {

		File file = Environment.getExternalStorageDirectory();
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				String dirName = f.getName();
				String path=f.getAbsolutePath();
				// 查询数据库 dirname是否在数据库中存在
				// 记录数据 设置数据适配器
				// 记录路径
				//
			}
		}
	}
	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f);
			}
		} else {
			file.delete();
		}
	}
}
