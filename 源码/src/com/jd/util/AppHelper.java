package com.jd.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.LinearLayout;

public class AppHelper {
	public static String oldAlbumRoot=null;//老版本的相册根文件夹
	public static String albumRoot=null;//相册根文件夹
	public static String sdRoot=null;//移动存储根文件夹
	public static String mobileRoot=null;//手机内部存储根文件夹
	public static String neighborRoot="NeighborList";//代表网上邻居的根文件夹
	public static String pathSep="tlljtxr";//路径分隔符
	public static String password=null;
	public static float scale=0;
	public static LinearLayout bottomMenu=null;
	public static String from=null;//在做拷贝、剪切等操作时的来源
	public static String to=null;//在做拷贝、剪切等操作时的目的地
	public static com.jd.ui.FileMainTain fileMainTain=null;
	public static com.jd.ui.AlbumFile albumFile=null;
	public static com.jd.ui.LocalFile localFile=null;
	public static com.jd.ui.NeighborFile neighborFile=null;
	public static com.jd.ui.HouseLink houseFile=null;
	public static com.jd.ui.FileMainTain main=null;	
	public static java.util.Date lastTouch=null;
	public static int bufLen=102400;//读数据的缓冲区的长度。
	public static int deBufLen=10240;//加解密时读取数据的缓冲区的长度
	public static boolean lockScreen=false;//表示是否是由于长时间未触摸而导致的锁屏。
	public static boolean isLogining=false;//标志是否在显示登录界面
	public static String area="album";//标志存储区域	album memory sdcard neighbor	house

	public static boolean isClsRunning(Context context,String cls) {
		boolean run=false;
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE) ;
		List<ActivityManager.RunningTaskInfo> list=am.getRunningTasks(Integer.MAX_VALUE);
		
		for (RunningTaskInfo rti : list) {
			if(rti.baseActivity.getClassName().equals(cls)){
				run=true;
				break;
			}
		}
		
		return run;
    } 
	
	public static AlertDialog.Builder showInfoDlg(Context context,String msg)
	{
		AlertDialog.Builder dlg=new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(msg)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		dlg.show();
		return dlg;
	}
	
	public static List<String> getImgFileNames(String path) {
		List<String> images = new ArrayList<String>();
		File dir = new File(path);
		if (dir.exists()) {
			String[] files = dir.list();

			for (String file : files) {
				try {
					if (isImageFile(file)) {
						images.add(path + "/" + file);
					}
				} catch (Exception e) {
					Log.v("PicBrow", e.getMessage());
				}
			}
		}
		
		return images;
	}

	public static boolean isImageFile(String fName) {
		boolean re;

		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}

    public static int dip2px(float scale, float dipValue) {  
    	 
        return (int) (dipValue * scale + 0.5f);  
 
    }  
 
    public static int px2dip(float scale, float pxValue) {  
         return (int) (pxValue / scale + 0.5f);  
    }  
    
    
}
