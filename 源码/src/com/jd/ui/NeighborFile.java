package com.jd.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jd.adp.BottomMenuIten;
import com.jd.adp.MySimpleAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class NeighborFile extends MyBaseActivity {

	TextView txtAddNeighbor, txtEditNeighbor, txtDeleteNeighbor;
	// ����URL���߳�
	String url;
	// ��¼�����ھ����ӵĽ��
	boolean conResult = false;
	DisplayMetrics dm;
	private BottomMenuIten menuNew, menuRename, menuDelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		com.jd.util.AppHelper.neighborFile=this;
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//initMenu();
		txtAddNeighbor = (TextView) findViewById(com.tl.pic.brow.R.id.txtAddNeighbor);
		txtEditNeighbor = (TextView) findViewById(com.tl.pic.brow.R.id.txtEditNeighbor);
		txtDeleteNeighbor = (TextView) findViewById(com.tl.pic.brow.R.id.txtDeleteNeighbor);
		txtAddNeighbor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNeighbor();
			}
		});

		txtEditNeighbor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editNeighbor();
			}
		});

		txtDeleteNeighbor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteNeighbor();
			}
		});

	}
//	private void initMenu() {
//		LinearLayout llBottomMenu = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llNeighborMenu);
//
//		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////		bitmapOptions.outWidth = com.jd.util.AppHelper.dip2px(dm.scaledDensity,
////				25);
////		bitmapOptions.outHeight = com.jd.util.AppHelper.dip2px(
////				dm.scaledDensity, 25);
////		bitmapOptions.inJustDecodeBounds = false;
//
//		Bitmap img = BitmapFactory.decodeResource(this.getResources(),
//				com.tl.pic.brow.R.drawable.add, bitmapOptions);
//		com.jd.adp.BottomMenuIten menu = new BottomMenuIten(this, img, "���");
//		menuNew = menu;
//		llBottomMenu.addView(menu);
//		LayoutParams layoutParams = menu.getLayoutParams();
//		layoutParams.width = dm.widthPixels / 3;
//		menu.setLayoutParams(layoutParams);
//		menu.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				addNeighbor();
//			}
//		});
//
//		img = BitmapFactory.decodeResource(this.getResources(),
//				com.tl.pic.brow.R.drawable.edit, bitmapOptions);
//		menu = new BottomMenuIten(this, img, "�༭");
//		menuRename = menu;
//		llBottomMenu.addView(menu);
//		layoutParams = menu.getLayoutParams();
//		layoutParams.width = dm.widthPixels / 3;
//		menu.setLayoutParams(layoutParams);
//		menu.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				editNeighbor();
//			}
//		});
//
//		img = BitmapFactory.decodeResource(this.getResources(),
//				com.tl.pic.brow.R.drawable.remove, bitmapOptions);
//		menu = new BottomMenuIten(this, img, "ɾ��");
//		menuDelete = menu;
//		llBottomMenu.addView(menu);
//		layoutParams = menu.getLayoutParams();
//		layoutParams.width = dm.widthPixels / 3;
//		menu.setLayoutParams(layoutParams);
//		menu.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				deleteNeighbor();
//			}
//		});
//
//	}
//	
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		// TODO Auto-generated method stub
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus) {
//			LayoutParams layoutParams = lst.getLayoutParams();
//			layoutParams.height = dm.heightPixels - com.jd.util.AppHelper.dip2px(dm.density, 50)*2;
//			lst.setLayoutParams(layoutParams);
//		}
//	}
	private void addNeighbor() {
		LayoutInflater flater = LayoutInflater.from(this);
		layout = flater.inflate(com.tl.pic.brow.R.layout.addneighbor, null);
		CheckBox chkAnonymous = (CheckBox) layout
				.findViewById(com.tl.pic.brow.R.id.chkAnonymous);
		chkAnonymous.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				TextView txtLoginId = (TextView) layout
						.findViewById(com.tl.pic.brow.R.id.txtLoginId);
				TextView txtPassword = (TextView) layout
						.findViewById(com.tl.pic.brow.R.id.txtPassword);
				if (isChecked) {
					txtLoginId.setEnabled(false);
					txtPassword.setEnabled(false);
				} else {
					txtLoginId.setEnabled(true);
					txtPassword.setEnabled(true);
				}
			}
		});

		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setView(layout)
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						try {
							TextView txtDomainName = (TextView) layout
									.findViewById(com.tl.pic.brow.R.id.txtDomainName);
							TextView txtServerName = (TextView) layout
									.findViewById(com.tl.pic.brow.R.id.txtServerName);
							TextView txtLoginId = (TextView) layout
									.findViewById(com.tl.pic.brow.R.id.txtLoginId);
							TextView txtPassword = (TextView) layout
									.findViewById(com.tl.pic.brow.R.id.txtPassword);
							TextView txtNeighborName = (TextView) layout
									.findViewById(com.tl.pic.brow.R.id.txtNeighborName);
							CheckBox chkAnonymous = (CheckBox) layout
									.findViewById(com.tl.pic.brow.R.id.chkAnonymous);

							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);

							if (txtServerName.getText().toString().equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�����������·����");
								return;
							}
							
							if(!com.jd.util.NeighborUtil.checkUrl(txtServerName.getText().toString())){
								txtServerName.setText(txtServerName.getText().toString()+"/");
							}
							
							if (!chkAnonymous.isChecked()
									&& txtLoginId.getText().toString()
											.equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�������û�����");
								return;
							}

							url = "smb://";
							if (chkAnonymous.isChecked()) {
								url += txtServerName.getText().toString();
							} else {
								url += txtLoginId.getText().toString() + ":"
										+ txtPassword.getText().toString()
										+ "@"
										+ txtServerName.getText().toString();
							}

							checkCon();

							if (!conResult) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�޷�����Ŀ����������������ã�");
								return;
							}

							field.set(dialog, true);
							com.jd.dal.Neighbor neighbor = new com.jd.dal.Neighbor();
							neighbor.setDomainName(txtDomainName.getText()
									.toString());
							neighbor.setServerName(txtServerName.getText()
									.toString());
							neighbor.setLoginId(txtLoginId.getText().toString());
							neighbor.setPassword(txtPassword.getText()
									.toString());
							neighbor.setAnonymous(chkAnonymous.isChecked());
							neighbor.setNeighborName(txtNeighborName.getText()
									.toString());

							com.jd.dal.NeighborDao dal = new com.jd.dal.NeighborDao(
									NeighborFile.this);
							dal.addNeighbor(neighbor);
							bindList();
						} catch (Exception e) {
							// TODO: handle exception
							com.jd.util.AppHelper.showInfoDlg(NeighborFile.this,
									e.getMessage());
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		dlg.show();
	}

	private void editNeighbor() {

		if (selectedItem == null) {
			com.jd.util.AppHelper.showInfoDlg(NeighborFile.this, "��ѡ��Ҫ�༭�������ھӣ�");
			return;
		}

		TextView txtId = (TextView) selectedItem
				.findViewById(com.tl.pic.brow.R.id.txtId);
		int id = Integer.parseInt(txtId.getText().toString());

		final TextView txtDomainName;
		final TextView txtServerName;
		final TextView txtLoginId;
		final TextView txtPassword;
		final TextView txtNeighborName;
		final CheckBox chkAnonymous;
		final com.jd.dal.Neighbor neighbor;

		try {
			neighbor = new com.jd.dal.NeighborDao(NeighborFile.this)
					.GetNeighborById(id);
			LayoutInflater flater = LayoutInflater.from(this);
			layout = flater.inflate(com.tl.pic.brow.R.layout.addneighbor, null);
			txtDomainName = (TextView) layout.findViewById(com.tl.pic.brow.R.id.txtDomainName);
			txtServerName = (TextView) layout.findViewById(com.tl.pic.brow.R.id.txtServerName);
			txtLoginId = (TextView) layout.findViewById(com.tl.pic.brow.R.id.txtLoginId);
			txtPassword = (TextView) layout.findViewById(com.tl.pic.brow.R.id.txtPassword);
			txtNeighborName = (TextView) layout
					.findViewById(com.tl.pic.brow.R.id.txtNeighborName);
			chkAnonymous = (CheckBox) layout.findViewById(com.tl.pic.brow.R.id.chkAnonymous);

			txtDomainName.setText(neighbor.getDomainName());
			txtServerName.setText(neighbor.getServerName());
			txtLoginId.setText(neighbor.getLoginId());
			txtPassword.setText(neighbor.getPassword());
			txtNeighborName.setText(neighbor.getNeighborName());
			chkAnonymous.setChecked(neighbor.isAnonymous());
			txtDomainName.setTag(id);

			if (neighbor.isAnonymous()) {
				txtLoginId.setEnabled(false);
				txtPassword.setEnabled(false);
			} else {
				txtLoginId.setEnabled(true);
				txtPassword.setEnabled(true);
			}

			bindList();
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(NeighborFile.this, e.getMessage());
			return;
		}

		chkAnonymous.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				TextView txtLoginId = (TextView) layout
						.findViewById(com.tl.pic.brow.R.id.txtLoginId);
				TextView txtPassword = (TextView) layout
						.findViewById(com.tl.pic.brow.R.id.txtPassword);
				if (isChecked) {
					txtLoginId.setEnabled(false);
					txtPassword.setEnabled(false);
				} else {
					txtLoginId.setEnabled(true);
					txtPassword.setEnabled(true);
				}
			}
		});

		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setView(layout)
				.setTitle("�޸������ھ�")
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);

							if (txtServerName.getText().toString().equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�����������·����");
								return;
							}

							if(!com.jd.util.NeighborUtil.checkUrl(txtServerName.getText().toString())){
								txtServerName.setText(txtServerName.getText().toString()+"/");
							}
							
							if (!chkAnonymous.isChecked()
									&& txtLoginId.getText().toString()
											.equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�������û�����");
								return;
							}

							url = "smb://";
							if (chkAnonymous.isChecked()) {
								url += txtServerName.getText().toString();
							} else {
								url += txtLoginId.getText().toString() + ":"
										+ txtPassword.getText().toString()
										+ "@"
										+ txtServerName.getText().toString();
							}

							checkCon();//ʹ���̲߳��������ھӵ�����

							if (!conResult) {
								com.jd.util.AppHelper.showInfoDlg(
										NeighborFile.this, "�޷�����Ŀ����������������ã�");
								return;
							}

							field.set(dialog, true);
							com.jd.dal.Neighbor neighbor = new com.jd.dal.Neighbor();
							neighbor.setId(Integer.parseInt(txtDomainName
									.getTag().toString()));
							neighbor.setDomainName(txtDomainName.getText()
									.toString());
							neighbor.setServerName(txtServerName.getText()
									.toString());
							neighbor.setLoginId(txtLoginId.getText().toString());
							neighbor.setPassword(txtPassword.getText()
									.toString());
							neighbor.setAnonymous(chkAnonymous.isChecked());
							neighbor.setNeighborName(txtNeighborName.getText()
									.toString());

							com.jd.dal.NeighborDao dal = new com.jd.dal.NeighborDao(
									NeighborFile.this);
							dal.updateNeighbor(neighbor);
							bindList();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		dlg.show();
	}

	private void deleteNeighbor() {
		if (selectedItem == null) {
			com.jd.util.AppHelper.showInfoDlg(NeighborFile.this, "��ѡ��Ҫɾ���������ھӣ�");
			return;
		}

		TextView txtId = (TextView) selectedItem
				.findViewById(com.tl.pic.brow.R.id.txtId);
		int id = Integer.parseInt(txtId.getText().toString());

		try {
			new com.jd.dal.NeighborDao(NeighborFile.this)
					.deleteNeighbor(id);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(NeighborFile.this, e.getMessage());
		}
		
		bindList();
	}

	@Override
	public void bindList() {
		try {
			List<com.jd.dal.Neighbor> neighbors = new com.jd.dal.NeighborDao(
					NeighborFile.this).GetAllNeighbors();
			ArrayList<HashMap<String, Object>> neis = new ArrayList<HashMap<String, Object>>();

			for (com.jd.dal.Neighbor neighbor : neighbors) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Id", neighbor.getId());
				if (neighbor.getServerName().equals("")) {
					map.put("Title", neighbor.getServerName());
				} else {
					map.put("Title", neighbor.getNeighborName() + "("
							+ neighbor.getServerName() + ")");
				}
				
				if (neighbor.isAnonymous()) {
					map.put("Path", neighbor.getServerName());
				} else {
					map.put("Path",
							neighbor.getLoginId() + ":"
									+ neighbor.getPassword() + "@"
									+ neighbor.getServerName());
				}
				
				neis.add(map);
			}
			
			MySimpleAdapter adapter = new MySimpleAdapter(NeighborFile.this, neis,
					com.tl.pic.brow.R.layout.neighborlistitem,
					new String[] { "Id", "Title","Path" }, new int[] {
					com.tl.pic.brow.R.id.txtId, com.tl.pic.brow.R.id.txtTitle,com.tl.pic.brow.R.id.txtPath });

			lst.setAdapter(adapter);
			
			RelativeLayout rlMenu=(RelativeLayout)findViewById(com.tl.pic.brow.R.id.rlMenu);
			rlMenu.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(NeighborFile.this, e.getMessage());
		}
	}


	/**
	 * ��������������ھӵ�������
	 * 
	 * @return
	 */
	private void checkCon() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (com.jd.util.NeighborUtil.TestConnection(url)) {
						conResult = true;
					} else {
						conResult = false;
					}
				} catch (Exception e) {
					conResult = false;
				}
			}
		});

		thread.start();
		
		try {
			thread.join();//���̵߳ȴ����߳�ִ�����
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
