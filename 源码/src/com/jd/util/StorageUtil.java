package com.jd.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Environment;
import android.os.storage.StorageManager;

/**
 * 本类用于获取手机的存储目录
 * @author Administrator
 *
 */
public class StorageUtil {

	private Activity mActivity;
	private StorageManager mStorageManager;
	private Method mMethodGetPaths;

	public StorageUtil(Activity activity) {
		mActivity = activity;
		if (mActivity != null) {

			mStorageManager = (StorageManager) mActivity
					.getSystemService(Activity.STORAGE_SERVICE);
			try {
				mMethodGetPaths = mStorageManager.getClass().getDeclaredMethod(
						"getVolumePaths");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] getVolumePathsFor14() {
		String[] paths = null;
		try {
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (Exception e) {
		}
		return paths;
	}

	public String[] getVolumePaths() {
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			return getVolumePathsFor14();
		} else if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return new String[] { Environment.getExternalStorageDirectory()
					.getAbsolutePath() };
		}
		return null;
	}
}
