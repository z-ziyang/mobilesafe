package com.mobile.safe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyFileUtils {

	/**
	 * 拷贝文件到某个目录下
	 * @param is
	 * @param filePath
	 * @return
	 */
	public static File copyFile(InputStream is, String filePath) {
		try {
			File file = new File(filePath);
			FileOutputStream os = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int lenght;
			try {
				while ((lenght = is.read(buffer)) != -1) {
					os.write(buffer, 0, lenght);
				}
				os.flush();
				os.close();
				is.close();
				return file;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
