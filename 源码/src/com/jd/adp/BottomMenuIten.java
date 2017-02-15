package com.jd.adp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomMenuIten extends LinearLayout {
	public BottomMenuIten(Context context){
		super(context);
	}
	
	public BottomMenuIten(Context context,AttributeSet attrs){
		super(context);
		LayoutInflater.from(context).inflate(com.tl.pic.brow.R.layout.bottommenuitem, this, true);  
	}
	
	public BottomMenuIten(Context context,Bitmap img,String title){
		super(context);
		LayoutInflater.from(context).inflate(com.tl.pic.brow.R.layout.bottommenuitem, this, true);  
		ImageView imgV=(ImageView)findViewById(com.tl.pic.brow.R.id.img);
		imgV.setImageBitmap(img);
		TextView info=(TextView)findViewById(com.tl.pic.brow.R.id.info);
		info.setText(title);
				
	}
}
