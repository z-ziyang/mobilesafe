package com.mobile.safe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.enity.MyPackageInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	private PackageManager pm;
	private LinearLayout ll;
	private TextView tv_clean_caches_status;
	private ProgressBar pb;
	private List<MyPackageInfo> myPackageInfos;
	private MyPackageInfo lastPackageInfo;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				final MyPackageInfo info = (MyPackageInfo) msg.obj;
				tv_clean_caches_status
						.setText("正在扫描:"
								+ pm.getApplicationInfo(info.packname, 0)
										.loadLabel(pm));
				if (info.cachesize > 0) {
					myPackageInfos.add(info);
					View view = View.inflate(getApplicationContext(),
							R.layout.list_clean_item, null);
					view.setClickable(true);

					view.setBackgroundResource(R.drawable.home_selector);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//适配不同版本
							String packname = info.getPackname();
							Intent intent = new Intent();
							intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							intent.setData(Uri.parse("package:" + packname));
							startActivity(intent);
							
						}
					});
					ImageView iv = (ImageView) view
							.findViewById(R.id.iv_clean_icon);
					TextView tv_name = (TextView) view
							.findViewById(R.id.iv_clean_appname);
					TextView tv_codesize = (TextView) view
							.findViewById(R.id.iv_clean_codesize);
					iv.setImageDrawable(pm.getApplicationInfo(info.packname, 0)
							.loadIcon(pm));
					tv_name.setText(pm.getApplicationInfo(info.packname, 0)
							.loadLabel(pm));
					tv_codesize.setText("缓存大小:"
							+ Formatter.formatFileSize(getApplicationContext(),
									info.cachesize));
					ll.addView(view, 0);
				}
				if ((info.packname).equals(lastPackageInfo.packname)) {
					int cout = myPackageInfos.size();
					if (cout > 0) {
						tv_clean_caches_status.setText("扫描完成!一共发现" + cout
								+ "个缓存文件");
					} else {
						tv_clean_caches_status.setText("恭喜你，没有发现如何缓存文件！");
					}
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		pm = getPackageManager();
		myPackageInfos = new ArrayList<MyPackageInfo>();
		lastPackageInfo = new MyPackageInfo();
		tv_clean_caches_status = (TextView) findViewById(R.id.tv_clean_cache_status);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		ll = (LinearLayout) findViewById(R.id.ll_container);

		startScannCache(new ScannProcessListener() {

			@Override
			public void onProcessUpdate(int process) {
				// TODO Auto-generated method stub
				pb.setProgress(process);
			}
			@Override
			public void beforeScann(int max, String lastPackageName) {
				// TODO Auto-generated method stub
				pb.setMax(max);
				lastPackageInfo.setPackname(lastPackageName);
			}
		});
	}
	public void startScannCache(final ScannProcessListener listener) {
		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				listener.beforeScann(infos.size(),
						infos.get(infos.size()-1).packageName);
				int total = 0;
				for (PackageInfo info : infos) {
					String packname = info.packageName;
					getPackageSize(packname);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					total++;
					listener.onProcessUpdate(total);
				}
			};
		}.start();
	}
	private void getPackageSize(String packname) {
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[]{String.class,
							IPackageStatsObserver.class});
			method.invoke(pm, new Object[]{packname, new MyObserver(packname)});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class MyObserver extends IPackageStatsObserver.Stub {
		private String packname;

		public MyObserver(String packname) {
			this.packname = packname;
		}
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cachesize = pStats.cacheSize;
			long codesize = pStats.codeSize;

			MyPackageInfo mypackinfo = new MyPackageInfo();
			mypackinfo.codesize = codesize;
			mypackinfo.cachesize = cachesize;
			mypackinfo.packname = packname;
			Message msg = Message.obtain();
			msg.obj = mypackinfo;
			handler.sendMessage(msg);
		}
	}
	public interface ScannProcessListener {
		void beforeScann(int max, String lastPackageName);
		void onProcessUpdate(int process);
	}
}
