package com.jd.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class SpellSet extends BaseActivity {
	RadioButton rdo3,rdo4,rdo5,rdo6;
	RadioButton rdoEasy,rdoDif;
	CheckBox chkMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.tl.pic.brow.R.layout.spellset);
		
		SharedPreferences sp=this.getSharedPreferences("comjdSpellSet",MODE_PRIVATE);
		String mode=sp.getString("ExchangeMode", "easy");//获取操作模式
		boolean playMusic=sp.getBoolean("PlayMusic", true);
		rdoEasy=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdoEasy);
		rdoDif=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdoDif);
		
		if(mode.equals("easy"))
		{
			rdoEasy.setChecked(true);
		}else
		{
			rdoDif.setChecked(true);
		}
		
		int count=sp.getInt("Count", 3);//获取图片划分为几行几列
		
		rdo3=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdo3);
		rdo4=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdo4);
		rdo5=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdo5);
		rdo6=(RadioButton)findViewById(com.tl.pic.brow.R.id.rdo6);
		
		if(count==3)
		{
			rdo3.setChecked(true);
		}else if(count==4)
		{
			rdo4.setChecked(true);
		}else if(count==5)
		{
			rdo5.setChecked(true);
		}else
		{
			rdo6.setChecked(true);
		}
		
		chkMusic=(CheckBox)findViewById(com.tl.pic.brow.R.id.chkMusic);
		chkMusic.setChecked(playMusic);
		

		Button btnOk=(Button)findViewById(com.tl.pic.brow.R.id.btnOk);
		Button btnCancel=(Button)findViewById(com.tl.pic.brow.R.id.btnCancel);
		
		btnOk.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int count=3;
				
				if(rdo3.isChecked())
				{
					count=3;
				}else if(rdo4.isChecked())
				{
					count=4;
				}else if(rdo5.isChecked())
				{
					count=5;
				}else
				{
					count=6;
				}
				
				SharedPreferences sp=SpellSet.this.getSharedPreferences("comjdSpellSet",Activity.MODE_PRIVATE);
				Editor ed=sp.edit();
				ed.putBoolean("PlayMusic", chkMusic.isChecked());
				ed.putInt("Count", count);
				if(rdoEasy.isChecked())
				{
					ed.putString("ExchangeMode", "easy");
				}else
				{
					ed.putString("ExchangeMode", "difficult");
				}
				ed.commit();
				
				SpellSet.this.setResult(RESULT_OK);
				SpellSet.this.finish();
			}
			
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SpellSet.this.setResult(RESULT_CANCELED);
				SpellSet.this.finish();
			}
			
		});		
	}

}
