package com.jd.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.jd.adp.MySimpleAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AlbumFile extends MyBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		com.jd.util.AppHelper.albumFile=this;
	}
	
	@Override
	public void bindList() {
		try {
			
//			ArrayList<HashMap<String, Object>> areas = new ArrayList<HashMap<String, Object>>();
//			if(com.jd.util.AppHelper.albumRoot!=null){
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				map.put("Id", "1");
//				map.put("Title", "简道相册");
//				map.put("Path", com.jd.util.AppHelper.albumRoot);
//				areas.add(map);
//			}
//			
//			if(com.jd.util.AppHelper.oldAlbumRoot!=null){
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				map = new HashMap<String, Object>();
//				map.put("Id", "2");
//				map.put("Title", "老相册");
//				map.put("Path", com.jd.util.AppHelper.oldAlbumRoot);
//				areas.add(map);
//				
//			}
//			
//			MySimpleAdapter adapter = new MySimpleAdapter(AlbumFile.this, areas,
//					com.tl.pic.brow.R.layout.neighborlistitem,
//					new String[] { "Id", "Title","Path" }, new int[] {
//					com.tl.pic.brow.R.id.txtId, com.tl.pic.brow.R.id.txtTitle,com.tl.pic.brow.R.id.txtPath });
//
//			lst.setAdapter(adapter);
//			
			
			currentDir=rootDir=com.jd.util.AppHelper.albumRoot;
			com.jd.adp.FoldListAdapter adp = new com.jd.adp.FoldListAdapter(
					AlbumFile.this, currentDir,
					com.jd.util.AppHelper.scale, rootDir);
			lst.setAdapter(adp);
			level++;
			setMenuStatus();
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(AlbumFile.this, e.getMessage());
		}
	}

}
