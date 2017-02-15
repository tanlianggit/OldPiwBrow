package com.jd.util;

import android.content.Context;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import jcifs.smb.SmbSession;

public class NeighborUtil {
	public static boolean checkUrl(String url)
	{
		if(!url.endsWith("/")){
			return false;
		}
		
		int pos=url.lastIndexOf("/", url.length()-2);
		if(pos<0){
			return false;
		}
		
		return true;
	}
	
	public static boolean TestConnection(String url)
	{
		try {

			SmbFile file=new SmbFile(url);
			if(file.exists()){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	public boolean login(Context context,String url,String loginId,String password){
		try {
			UniAddress dc = UniAddress.getByName(url);
			//UniAddress dc=new UniAddress(url); 
	        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,loginId,password);
	        SmbSession.logon( dc, auth );
	        return true;
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(context, e.getMessage());
			return false;
		}
		

	}
}
