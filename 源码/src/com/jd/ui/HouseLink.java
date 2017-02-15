package com.jd.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jd.adp.MySimpleAdapter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HouseLink extends MyBaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		com.jd.util.AppHelper.houseFile=this;
		
		TextView txtDelLink=(TextView) findViewById(com.tl.pic.brow.R.id.txtDeleteLink);
		txtDelLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteLink();
			}
		});
	}
	
	private void deleteLink()
	{
		if (selectedItem == null) {
			com.jd.util.AppHelper.showInfoDlg(HouseLink.this, "请选择要删除的收藏！");
			return;
		}

		TextView txtId = (TextView) selectedItem
				.findViewById(com.tl.pic.brow.R.id.txtId);
		int id = Integer.parseInt(txtId.getText().toString());

		try {
			new com.jd.dal.HouseDao(HouseLink.this)
					.deleteHouse(id);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(HouseLink.this, e.getMessage());
		}
		
		bindList();
	}
	
	@Override
	public void bindList() {
		try {
			List<com.jd.dal.House> houses = new com.jd.dal.HouseDao(
					HouseLink.this).GetAllHouses();
			ArrayList<HashMap<String, Object>> hous = new ArrayList<HashMap<String, Object>>();

			for (com.jd.dal.House house : houses) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Id", house.getId());
				map.put("Path", house.getPath());
				map.put("Title", house.getTitle());
				map.put("Area", house.getArea());
				hous.add(map);
			}
			
			MySimpleAdapter adapter = new MySimpleAdapter(HouseLink.this, hous,
					com.tl.pic.brow.R.layout.linklistitem,
					new String[] { "Id", "Title","Path","Area" }, new int[] {
					com.tl.pic.brow.R.id.txtId, com.tl.pic.brow.R.id.txtTitle,com.tl.pic.brow.R.id.txtPath,com.tl.pic.brow.R.id.txtArea });

			lst.setAdapter(adapter);
			
			RelativeLayout rlMenu=(RelativeLayout)findViewById(com.tl.pic.brow.R.id.rlMenu);
			rlMenu.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(HouseLink.this, e.getMessage());
		}
	}

}
