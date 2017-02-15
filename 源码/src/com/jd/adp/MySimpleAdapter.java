package com.jd.adp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class MySimpleAdapter extends SimpleAdapter {
	protected int selectedItem=-1;
	
	public MySimpleAdapter(Context context,
			List<HashMap<String, Object>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	public void setSelectedItem(int pos)
	{
		this.selectedItem=pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view= super.getView(position, convertView, parent);
		
		if(position==this.selectedItem){
			view.setBackgroundColor(Color.LTGRAY);  
		}else{
			view.setBackgroundColor(Color.WHITE);  
		}
		return view;
	}
	
	
}
