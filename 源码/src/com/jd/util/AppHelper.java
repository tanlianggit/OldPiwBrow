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
	public static String oldAlbumRoot=null;//�ϰ汾�������ļ���
	public static String albumRoot=null;//�����ļ���
	public static String sdRoot=null;//�ƶ��洢���ļ���
	public static String mobileRoot=null;//�ֻ��ڲ��洢���ļ���
	public static String neighborRoot="NeighborList";//���������ھӵĸ��ļ���
	public static String pathSep="tlljtxr";//·���ָ���
	public static String password=null;
	public static float scale=0;
	public static LinearLayout bottomMenu=null;
	public static String from=null;//�������������еȲ���ʱ����Դ
	public static String to=null;//�������������еȲ���ʱ��Ŀ�ĵ�
	public static com.jd.ui.FileMainTain fileMainTain=null;
	public static com.jd.ui.AlbumFile albumFile=null;
	public static com.jd.ui.LocalFile localFile=null;
	public static com.jd.ui.NeighborFile neighborFile=null;
	public static com.jd.ui.HouseLink houseFile=null;
	public static com.jd.ui.FileMainTain main=null;	
	public static java.util.Date lastTouch=null;
	public static int bufLen=102400;//�����ݵĻ������ĳ��ȡ�
	public static int deBufLen=10240;//�ӽ���ʱ��ȡ���ݵĻ������ĳ���
	public static boolean lockScreen=false;//��ʾ�Ƿ������ڳ�ʱ��δ���������µ�������
	public static boolean isLogining=false;//��־�Ƿ�����ʾ��¼����
	public static String area="album";//��־�洢����	album memory sdcard neighbor	house

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
		.setTitle("��ʾ")
		.setMessage(msg)
		.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
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

		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* ����չ�������;���MimeType */
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
