package com.mobile.safe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.mobile.safe.R;
import com.mobile.safe.engine.UpdateInfoParser;
import com.mobile.safe.enity.UpdateInfo;
import com.mobile.safe.utils.CopyFileUtils;
import com.mobile.safe.utils.DownLoadUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	public static final int PARSE_XML_ERROR = 10;
	public static final int PARSE_XML_SUCCESS = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	private static final int DOWNLOAD_SUCCESS = 15;
	private static final int DOWNLOAD_ERROR = 16;
	private AlphaAnimation alphaAnimation;
	private static final String TAG = "SplashActivity";
	private static final int COPY_ERROR = 17;
	private ProgressDialog progressDialog;
	private TextView iv_splash_version;
	private UpdateInfo updateInfo;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case PARSE_XML_SUCCESS :
					if (getAppVersion().equals(updateInfo.getVersion())) {
						Log.i(TAG, "版本号相同，进入主界面");
						loadMianUI();
					} else {
						Log.i(TAG, "版本号不相同，弹出提示框！提醒下载 ");
						showUpdateDialog();
						loadMianUI();
					}
					break;
				case PARSE_XML_ERROR :
					Toast.makeText(getApplicationContext(), "解析xml错误",
							Toast.LENGTH_LONG).show();
					loadMianUI();
					break;
				case SERVER_ERROR :
					Toast.makeText(getApplicationContext(), "服务器异常",
							Toast.LENGTH_LONG).show();
					loadMianUI();
					break;
				case URL_ERROR :
					Toast.makeText(getApplicationContext(), "服务器地址错误",
							Toast.LENGTH_LONG).show();
					loadMianUI();
					break;
				case NETWORK_ERROR :
					Toast.makeText(getApplicationContext(), "网络错误",
							Toast.LENGTH_LONG).show();
					loadMianUI();
					break;
				case DOWNLOAD_SUCCESS :
					// 安装APK
					File file = (File) msg.obj;
					Log.i(TAG, "安装APK:" + file.getAbsolutePath());
					installAPK(file);
					loadMianUI();
					finish();
					break;
				case DOWNLOAD_ERROR :
					Toast.makeText(getApplicationContext(), "下载失败",
							Toast.LENGTH_LONG).show();
					loadMianUI();
					break;
				case COPY_ERROR:
					Toast.makeText(getApplicationContext(), "拷贝数据库异常", 0).show();
					break;	
				default :
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		setupView();
		copyAddressDB();
		copyCommonNumberDB();
//		Intent intent=new Intent();
//		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//		Intent shortCutIntent=new Intent();
//		shortCutIntent.setAction("com.mobile.safe");
//		shortCutIntent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
//    	intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
//    	intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.atools));
//    	sendBroadcast(intent);
		// 开启线程下载检查 暂不开启
		new Thread(new CheckVersionTask()).start();
		alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
		alphaAnimation.setDuration(3000);
		findViewById(R.id.r_splash).startAnimation(alphaAnimation);
	}
	/**
	 * 释放数据库文件到系统目录
	 */
	private void copyAddressDB() {
		final File file = new File(getFilesDir(), "address.db");
		if (file.exists() && file.length() > 0) {

		} else {
			new Thread() {
				public void run() {
                 Message msg=Message.obtain();
                 try {
					InputStream is=getAssets().open("address.db");
					File tempFile=CopyFileUtils.copyFile(is, file.getAbsolutePath());
					if (tempFile != null) {
						//成功拷贝  
					} else {
						msg.what=COPY_ERROR;
					}
				} catch (Exception e) {
					// TODO: handle exception
					msg.what=COPY_ERROR;
				}finally {
					mHandler.sendMessage(msg);
				}
                 
				};
			}.start();
		}

	}
	/**
	 * 释放常用号码数据库文件到系统目录
	 */
	private void copyCommonNumberDB() {
		final File file = new File(getFilesDir(), "commonnum.db");
		if (file.exists() && file.length() > 0) {

		} else {
			new Thread() {
				public void run() {
                 Message msg=Message.obtain();
                 try {
					InputStream is=getAssets().open("commonnum.db");
					File tempFile=CopyFileUtils.copyFile(is, file.getAbsolutePath());
					if (tempFile != null) {
						//成功拷贝  
					} else {
						msg.what=COPY_ERROR;
					}
				} catch (Exception e) {
					// TODO: handle exception
					msg.what=COPY_ERROR;
				}finally {
					mHandler.sendMessage(msg);
				}
                 
				};
			}.start();
		}

	}
	private void setupView() {
		iv_splash_version = (TextView) findViewById(R.id.iv_splash_version);
		iv_splash_version.setText("版本:" + getAppVersion());
	}
	/**
	 * 安装APK
	 * 
	 * @param file
	 */
	private void installAPK(File file) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
	/**
	 * 跳转到主页面
	 */
	private void loadMianUI() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	private String getAppVersion() {
		PackageManager pkManager = getPackageManager();
		try {
			PackageInfo packageInfo = pkManager.getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 弹出升级的对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级提醒");
		builder.setMessage(updateInfo.getDescription());
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String apkurl = updateInfo.getApkurl();
				progressDialog = new ProgressDialog(SplashActivity.this);
				progressDialog.setTitle("升级操作");
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.show();
				Log.i(TAG, "下载后安装" + apkurl);
				final File file = new File(Environment
						.getExternalStorageDirectory(), DownLoadUtil
						.getFileName(apkurl));
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					new Thread() {
						public void run() {
							Message msg = Message.obtain();
							File saveFile = DownLoadUtil.download(
									updateInfo.getApkurl(),
									file.getAbsolutePath(), progressDialog);
							if (saveFile != null) {
								msg.what = DOWNLOAD_SUCCESS;
								msg.obj = saveFile;
							} else {
								msg.what = DOWNLOAD_ERROR;
							}
							mHandler.sendMessage(msg);
							progressDialog.dismiss();
						};
					}.start();

				} else {
					// SD卡不可用
					Toast.makeText(getApplicationContext(), "SD卡不可用",
							Toast.LENGTH_LONG).show();
					loadMianUI();
				}

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.create().show();
	}
	public class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean isupdate = sp.getBoolean("update", true);
			if (!isupdate) {
				loadMianUI();
				return;
			}
			long startTime = System.currentTimeMillis();
			Message msg = Message.obtain();
			try {
				URL url = new URL(getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(1500);
				if (conn.getResponseCode() == 200) {
					InputStream inputStream = conn.getInputStream();
					updateInfo = UpdateInfoParser.getUpdateInfo(inputStream);
					if (updateInfo == null) {
						msg.what = PARSE_XML_ERROR;
					} else {
						msg.what = PARSE_XML_SUCCESS;
					}
				} else {
					msg.what = SERVER_ERROR;
				}
			} catch (MalformedURLException e) {
				msg.what = URL_ERROR;
				e.printStackTrace();
			} catch (NotFoundException e) {
				msg.what = URL_ERROR;
				e.printStackTrace();
			} catch (IOException e) {
				msg.what = NETWORK_ERROR;
				e.printStackTrace();
			} finally {
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				if (dTime < 2000) {
					try {
						Thread.sleep(2000 - dTime);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mHandler.sendMessage(msg);
			}
		}
	}

}
