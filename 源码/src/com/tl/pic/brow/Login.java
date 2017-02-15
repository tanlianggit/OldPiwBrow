package com.tl.pic.brow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jd.ui.AlbumFile;
import com.jd.ui.LocalFile;
import com.jd.ui.MyApplication;
import com.tl.pic.brow.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		MyApplication.getInstance().addActivity(this);
		DisplayMetrics mdm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mdm);
		com.jd.util.AppHelper.scale = mdm.scaledDensity;
		com.jd.util.AppHelper.isLogining=true;

		
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText txtPwd = (EditText) findViewById(R.id.txtPwd);

				if (com.jd.util.AppHelper.lockScreen) {
					// ����
					// ��ԭ���벻�ԣ�ֱ���˳�
					if (!txtPwd.getText().toString()
							.equals(com.jd.util.AppHelper.password)) {
						MyApplication.getInstance().AppExit();
					} else {
						// �������
						com.jd.util.AppHelper.lockScreen = false;
						com.jd.util.AppHelper.isLogining=false;
						com.jd.util.AppHelper.lastTouch = new java.util.Date();
						Login.this.finish();
					}
				} else {
					// �״ε�¼
					com.jd.util.AppHelper.password = txtPwd.getText()
							.toString();
					com.jd.util.CryptoTools
							.InitCrypt(com.jd.util.AppHelper.password);
					getStorage();
					com.jd.util.AppHelper.isLogining=false;
					Intent intent = new Intent(Login.this,
							com.jd.ui.FileMainTain.class);
					startActivity(intent);
					Login.this.finish();
				}
			}
		});

		TextView txtHelp = (TextView) findViewById(com.tl.pic.brow.R.id.txtHelp);
		txtHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Login.this, com.jd.ui.Help.class);
				Login.this.startActivity(intent);
			}
		});

		TextView txtSet = (TextView) findViewById(com.tl.pic.brow.R.id.txtSet);
		txtSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Login.this,
						com.jd.ui.GallerySet.class);
				Login.this.startActivityForResult(intent, 2);
			}
		});
	}

	/**
	 * ��ȡϵͳ�Ĵ洢Ŀ¼
	 */
	private void getStorage() {
		File externRoot = Environment.getExternalStorageDirectory();
		// ����Ŀ¼
		com.jd.util.AppHelper.albumRoot = externRoot.getPath()
				+ "/"
				+ com.jd.util.StringUtil.encryptStr("com.jd.brow")
				+ "/"
				+ com.jd.util.StringUtil
						.encryptStr(com.jd.util.AppHelper.password);
		File dir = new File(com.jd.util.AppHelper.albumRoot);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String oldAlbum = externRoot.getPath() + "/browencrypic";
		dir = new File(oldAlbum);
		if (dir.exists()) {
			com.jd.util.AppHelper.oldAlbumRoot = oldAlbum;
		}

		com.jd.util.StorageUtil storeUtil = new com.jd.util.StorageUtil(
				Login.this);
		String[] paths = storeUtil.getVolumePaths();
		List<String> pas = new ArrayList<String>();
		for (String path : paths) {
			if (!path.contains("usbotg")) {
				pas.add(path);
			}
		}

		if (pas.size() == 1) {
			com.jd.util.AppHelper.mobileRoot = pas.get(0);// �ֻ��ڲ��洢
		} else if (pas.size() > 1) {
			for (String path : pas) {
				if (!path.equals(externRoot.getPath())) {
					com.jd.util.AppHelper.sdRoot = path;// �ⲿ�洢
				} else if (!path.contains("usbotg")) {
					com.jd.util.AppHelper.mobileRoot = path;// �ֻ��ڲ��洢
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			closeDialog();
		}

		return true;
	}

	protected void closeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("�Ƿ��˳�������ļ���������?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("�˳�",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MyApplication.getInstance().AppExit();
						System.exit(0);
					}
				});
		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}