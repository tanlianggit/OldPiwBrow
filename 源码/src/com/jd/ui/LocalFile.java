package com.jd.ui;

import java.util.ArrayList;
import java.util.HashMap;
import com.jd.adp.MySimpleAdapter;
import android.os.Bundle;

public class LocalFile extends MyBaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.jd.util.AppHelper.localFile=this;
		rootDir=com.jd.util.AppHelper.mobileRoot;
	}
	
	@Override
	public void bindList() {
		try {
			ArrayList<HashMap<String, Object>> areas = new ArrayList<HashMap<String, Object>>();
			if(com.jd.util.AppHelper.mobileRoot!=null){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Id", "1");
				map.put("Title", "ÊÖ»ú´æ´¢");
				map.put("Path", com.jd.util.AppHelper.mobileRoot);
				areas.add(map);
			}
			
			if(com.jd.util.AppHelper.sdRoot!=null){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Id", "2");
				map.put("Title", "SD¿¨");
				map.put("Path", com.jd.util.AppHelper.sdRoot);
				areas.add(map);
			}
			
			
			MySimpleAdapter adapter = new MySimpleAdapter(LocalFile.this, areas,
					com.tl.pic.brow.R.layout.neighborlistitem,
					new String[] { "Id", "Title","Path" }, new int[] {
					com.tl.pic.brow.R.id.txtId, com.tl.pic.brow.R.id.txtTitle,com.tl.pic.brow.R.id.txtPath });

			lst.setAdapter(adapter);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(LocalFile.this, e.getMessage());
		}
	}

}
