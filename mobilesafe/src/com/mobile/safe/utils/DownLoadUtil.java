package com.mobile.safe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.app.ProgressDialog;

public class DownLoadUtil {
    /**
     * 单线程下载文件
     * @param ProgressDialog
     * @param serverPath
     * @param savePath
     * @return
     */
	public static File download(String serverPath,String savePath,ProgressDialog pd) {
		try {
			HttpURLConnection conn=(HttpURLConnection)new URL(serverPath).openConnection();
		    conn.setRequestMethod("GET");
		    conn.setReadTimeout(3000);
		    if (conn.getResponseCode()==200) {
		    	pd.setMax(conn.getContentLength());
				InputStream inputStream=conn.getInputStream();
				File file=new File(savePath);
				FileOutputStream outputStream=new FileOutputStream(file);
				byte[] buffer=new byte[1024];
				int length=0;
				int total=0;
				while((length=inputStream.read(buffer))!=-1) {
					outputStream.write(buffer, 0, length);
					total+=length;
					pd.setProgress(total);
					
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
				return file;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取文件名
	 * @param serverPath
	 * @return
	 */
	public static String getFileName(String serverPath) {
		return serverPath.substring(serverPath.lastIndexOf("/")+1);
	}
}
