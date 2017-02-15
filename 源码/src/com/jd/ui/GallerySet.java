package com.jd.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

public class GallerySet extends BaseActivity {
	private RadioButton rdo3,rdo4,rdo5,rdo6;
	private RadioButton rdoEasy,rdoDif;
	private CheckBox chkMusic;
	private EditText txtSeq,txtRan,txtTimer,txtDura,txtLock,txtOff;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.tl.pic.brow.R.layout.galleryset);
		
		SharedPreferences sp=this.getSharedPreferences("Settings",MODE_PRIVATE);
		int seq=sp.getInt("Sequent", 1);
		int ran=sp.getInt("Random", 1);
		int timer=sp.getInt("Timer", 2);
		int duration=sp.getInt("Duration", 2);
		int lock=sp.getInt("Lock", 5);
		int offset=sp.getInt("Offset", 0);
		
		txtOff=(EditText)findViewById(com.tl.pic.brow.R.id.txtOff);
		txtSeq=(EditText)findViewById(com.tl.pic.brow.R.id.txtSeq);
		txtRan=(EditText)findViewById(com.tl.pic.brow.R.id.txtRan);
		txtTimer=(EditText)findViewById(com.tl.pic.brow.R.id.txtTimer);
		txtDura=(EditText)findViewById(com.tl.pic.brow.R.id.txtDura);
		txtLock=(EditText)findViewById(com.tl.pic.brow.R.id.txtLock);
		
		txtOff.setText(offset+"");
		txtSeq.setText(seq+"");
		txtRan.setText(ran+"");
		txtTimer.setText(timer+"");
		txtDura.setText(duration+"");
		txtLock.setText(lock+"");
		
		String mode=sp.getString("ExchangeMode", "easy");//��ȡ����ģʽ
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
		
		int count=sp.getInt("Count", 3);//��ȡͼƬ����Ϊ���м���
		
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

		//String mode=sp.getString("ExchangeMode", "easy");//��ȡ����ģʽ
		
		Button btnSet=(Button)findViewById(com.tl.pic.brow.R.id.btnSet);
		Button btnCancel=(Button)findViewById(com.tl.pic.brow.R.id.btnCancel);
		
		btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GallerySet.this.setResult(RESULT_CANCELED);
				GallerySet.this.finish();
			}
			
		});

		btnSet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp=GallerySet.this.getSharedPreferences("Settings",Activity.MODE_PRIVATE);
				Editor ed=sp.edit();
				
				String off=txtOff.getText().toString();
				if(off==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "�������ֽƫ�Ʊ�����");
					return;
				}
				
				try {
					int iOff=Integer.parseInt(off);
					ed.putInt("Offset", iOff);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "��ֽƫ�Ʊ���Ӧ����������0-100����");
					return;
				}
				
				String lock=txtLock.getText().toString();
				if(lock==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "����������ʱ������");
					return;
				}
				
				try {
					int iLock=Integer.parseInt(lock);
					ed.putInt("Lock", iLock);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "˳�򲥷�ʱ����Ӧ����������");
					return;
				}
				
				String seq=txtSeq.getText().toString();
				if(seq==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "������˳�򲥷�ʱ������");
					return;
				}
				
				try {
					int iSeq=Integer.parseInt(seq);
					ed.putInt("Sequent", iSeq);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "˳�򲥷�ʱ����Ӧ����������");
					return;
				}
				
				String ran=txtRan.getText().toString();
				if(ran==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "�������������ʱ������");
					return;
				}
				
				try {
					int iRan=Integer.parseInt(ran);
					ed.putInt("Random", iRan);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "�������ʱ����Ӧ����������");
					return;
				}
				
				String timer=txtTimer.getText().toString();
				if(timer==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "���������Ŷ�������ʱ�䣡");
					return;
				}
				
				try {
					int iTimer=Integer.parseInt(timer);
					ed.putInt("Timer", iTimer);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "���Ŷ�������ʱ��Ӧ����������");
					return;
				}
				
				String dura=txtDura.getText().toString();
				if(dura==""){
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "�����Ŷ������ű�����");
					return;
				}
				
				try {
					int iDura=Integer.parseInt(dura);
					ed.putInt("Duration", iDura);
				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(GallerySet.this, "���Ŷ������ű���Ӧ����������");
					return;
				}
				
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
				
				GallerySet.this.setResult(RESULT_OK);
				GallerySet.this.finish();
			}
			
		});
	}

}
