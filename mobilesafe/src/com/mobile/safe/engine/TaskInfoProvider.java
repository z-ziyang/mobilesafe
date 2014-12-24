package com.mobile.safe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mobile.safe.R;
import com.mobile.safe.enity.TaskInfo;

public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(mContext.ACTIVITY_SERVICE);
		PackageManager pm = mContext.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();

			String packname = processInfo.processName;
			taskInfo.setPackName(packname);

			try {
				// 根据进程名获取applicationInfo
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packname, 0);

				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					taskInfo.setUserTask(true);
				} else {
					taskInfo.setUserTask(false);
				}
				taskInfo.setTaskIcon(applicationInfo.loadIcon(pm));
				taskInfo.setTaskName(applicationInfo.loadLabel(pm).toString());
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				taskInfo.setTaskIcon(mContext.getResources().getDrawable(
						R.drawable.ic_launcher));
				taskInfo.setTaskName(packname);
			}
            //获取进程size
			long memsize = am.getProcessMemoryInfo(new int[]{processInfo.pid})[0]
					.getTotalPrivateDirty() * 1024;
			taskInfo.setMemsize(memsize);
			
			taskInfos.add(taskInfo);

		}
		return taskInfos;
	}

}
