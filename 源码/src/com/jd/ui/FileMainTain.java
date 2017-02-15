package com.jd.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import com.baidu.mobads.AdView;
import com.jd.adp.BottomMenuIten;
import com.jd.adp.FolderItemHolder;
import com.jd.dal.House;
import com.jd.dal.HouseDao;
import com.tl.pic.brow.R;
import com.tl.pic.brow.Login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class FileMainTain extends TabActivity implements
		OnCheckedChangeListener {
	private TabHost tabHost;
	private Intent fileIntent;
	private Intent localIntent;
	private Intent neighborIntent;
	private Intent houseIntent;
	private AdView adView;
	DisplayMetrics dm;
	private String curTab = "album";
	private HashMap<String, Boolean> menuStatus = new HashMap<String, Boolean>();
	private List<com.jd.adp.FolderItemHolder> seledObjs = new ArrayList<com.jd.adp.FolderItemHolder>();
	private List<com.jd.adp.FolderItemHolder> sFiles = new ArrayList<com.jd.adp.FolderItemHolder>();
	private OpType opType;
	private SourceType from, to;
	private int totalFiles = 0;
	private int currentCnt = 0;
	private ProgressDialog proDlg = null;
	private Handler handler = null;
	private boolean beTerminated = false;// 标志文件复制工作是否被取消
	private Timer timer;
	private TimerTask task;
	private int threadCount = 0;
	private int maxThreadCount = 10;
	private BottomMenuIten menuNew, menuRename, menuDelete, menuMore;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.jd.util.AppHelper.main = this;
		com.jd.util.AppHelper.fileMainTain = this;

		MyApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		setContentView(com.tl.pic.brow.R.layout.filemaintain);
		initMenu();
		setupIntent();
		adView = new AdView(this);
		LinearLayout llAds = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llAds);
		llAds.addView(adView);

		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (com.jd.util.AppHelper.isLogining) {
					return;
				}

				if (com.jd.util.AppHelper.lastTouch == null) {
					com.jd.util.AppHelper.lastTouch = new java.util.Date();
				} else {
					long noTouch = (new java.util.Date().getTime() - com.jd.util.AppHelper.lastTouch
							.getTime()) / (60 * 1000);// 以分钟为单位

					SharedPreferences sp = FileMainTain.this
							.getSharedPreferences("Settings", MODE_PRIVATE);
					int lock = sp.getInt("Lock", 5);

					if (noTouch >= lock) {
						com.jd.util.AppHelper.lastTouch = new java.util.Date();
						com.jd.util.AppHelper.lockScreen = true;
						Intent intent = new Intent(FileMainTain.this,
								Login.class);
						FileMainTain.this.startActivity(intent);
					}
				}
			}
		};

		timer.schedule(task, 1000, 1000);
	}

	private void initMenu() {
		LinearLayout llBottomMenu = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llBottomMenu);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.outWidth = com.jd.util.AppHelper.dip2px(dm.scaledDensity,
				25);
		bitmapOptions.outHeight = com.jd.util.AppHelper.dip2px(
				dm.scaledDensity, 25);
		bitmapOptions.inJustDecodeBounds = false;

		Bitmap img = BitmapFactory.decodeResource(this.getResources(),
				com.tl.pic.brow.R.drawable.add, bitmapOptions);
		com.jd.adp.BottomMenuIten menu = new BottomMenuIten(this, img, "新建");
		menuNew = menu;
		llBottomMenu.addView(menu);
		LayoutParams layoutParams = menu.getLayoutParams();
		layoutParams.width = dm.widthPixels / 4;
		menu.setLayoutParams(layoutParams);
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createFolder();
			}
		});

		img = BitmapFactory.decodeResource(this.getResources(),
				com.tl.pic.brow.R.drawable.edit, bitmapOptions);
		menu = new BottomMenuIten(this, img, "重命名");
		menuRename = menu;
		llBottomMenu.addView(menu);
		layoutParams = menu.getLayoutParams();
		layoutParams.width = dm.widthPixels / 4;
		menu.setLayoutParams(layoutParams);
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				renameFolder();
			}
		});

		img = BitmapFactory.decodeResource(this.getResources(),
				com.tl.pic.brow.R.drawable.remove, bitmapOptions);
		menu = new BottomMenuIten(this, img, "删除");
		menuDelete = menu;
		llBottomMenu.addView(menu);
		layoutParams = menu.getLayoutParams();
		layoutParams.width = dm.widthPixels / 4;
		menu.setLayoutParams(layoutParams);
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteFolder();
			}
		});

		img = BitmapFactory.decodeResource(this.getResources(),
				com.tl.pic.brow.R.drawable.menu, bitmapOptions);
		menu = new BottomMenuIten(this, img, "更多");
		menuMore = menu;
		llBottomMenu.addView(menu);
		layoutParams = menu.getLayoutParams();
		layoutParams.width = dm.widthPixels / 4;
		menu.setLayoutParams(layoutParams);
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopMenu();
			}
		});
	}

	private void createFolder() {
		LayoutInflater flater = LayoutInflater.from(this);
		View dlgLy = flater
				.inflate(com.tl.pic.brow.R.layout.createfolder, null);
		final EditText txtName = (EditText) dlgLy.findViewById(R.id.txtName);
		String path = null;

		if (curTab == "album") {
			path = com.jd.util.AppHelper.albumFile.currentDir;
		} else if (curTab == "local") {
			path = com.jd.util.AppHelper.localFile.currentDir;
		} else {
			path = com.jd.util.AppHelper.neighborFile.currentDir;
		}

		txtName.setTag(path);

		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setView(dlgLy)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);

							if (txtName.getText().toString().equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										FileMainTain.this, "请输入文件夹的名称！");
								return;
							}

							String foldPath = txtName.getTag().toString();

							if (foldPath.startsWith("smb://")) {
								foldPath += txtName.getText().toString();
								SmbFile dir = new SmbFile(foldPath);
								if (!dir.exists()) {
									dir.mkdirs();
									if (dir.exists()) {
										com.jd.util.AppHelper.showInfoDlg(
												FileMainTain.this, "文件夹已创建！");
									} else {
										com.jd.util.AppHelper.showInfoDlg(
												FileMainTain.this, "创建文件夹失败！");
									}
								}
							} else {
								foldPath += "/" + txtName.getText().toString();
								File dir = new File(foldPath);
								if (!dir.exists()) {
									boolean res = dir.mkdirs();
									if (res) {
										com.jd.util.AppHelper.showInfoDlg(
												FileMainTain.this, "文件夹已创建！");
										// } else {
										// createExternalDir(txtName.getTag()
										// .toString(), txtName.getText()
										// .toString());
									}
								} else {
									com.jd.util.AppHelper.showInfoDlg(
											FileMainTain.this, "文件夹已存在！");
								}
							}

							field.set(dialog, true);
							refreshList();

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});

		dlg.show();
	}

	private void renameFolder() {
		getSelectedObjs();

		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要重命名的文件或者文件夹！");
			return;
		} else if (seledObjs.size() > 1) {
			com.jd.util.AppHelper.showInfoDlg(this, "每次只能对一个文件或者文件夹重命名！");
			return;
		}

		LayoutInflater flater = LayoutInflater.from(this);
		View dlgLy = flater
				.inflate(com.tl.pic.brow.R.layout.createfolder, null);
		final EditText txtName = (EditText) dlgLy.findViewById(R.id.txtName);

		com.jd.adp.FolderItemHolder item = seledObjs.get(0);
		final boolean isFile = !item.isFolder;
		final String hz;
		if (isFile) {
			int pos = item.path.lastIndexOf(".");
			if (pos > 0) {
				hz = item.path.substring(pos + 1);
			} else {
				hz = "jpg";
			}

			String fileName = item.title.getText().toString();
			pos = fileName.lastIndexOf(".");
			if (pos > 0) {
				fileName = fileName.substring(0, pos);
			}
			txtName.setText(fileName);
		} else {
			hz = "jpg";
			txtName.setText(item.title.getText().toString());
		}
		txtName.setTag(item.path);

		TextView txtTitle = (TextView) dlgLy
				.findViewById(com.tl.pic.brow.R.id.lblTitle);
		txtTitle.setText("重命名");

		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setView(dlgLy)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);

							if (txtName.getText().toString().equals("")) {
								com.jd.util.AppHelper.showInfoDlg(
										FileMainTain.this, "请输入文件或者文件夹的名称！");
								return;
							}

							if (txtName.getTag().toString()
									.startsWith("smb://")) {
								SmbFile dir = new SmbFile(txtName.getTag()
										.toString());
								if (!isFile) {
									dir.renameTo(new SmbFile(dir.getParent()
											+ "//"
											+ txtName.getText().toString()));
								} else {
									dir.renameTo(new SmbFile(dir.getParent()
											+ "//"
											+ txtName.getText().toString()
											+ "." + hz));
								}
							} else {
								File dir = new File(txtName.getTag().toString());
								if (!isFile) {
									dir.renameTo(new File(dir.getParent()
											+ "//"
											+ txtName.getText().toString()));
								} else {
									dir.renameTo(new File(dir.getParent()
											+ "//"
											+ txtName.getText().toString()
											+ "." + hz));
								}
							}
							field.set(dialog, true);
							refreshList();
						} catch (Exception e) {
							// TODO: handle exception

						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});

		dlg.show();
	}

	private void deleteFolder() {
		getSelectedObjs();
		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
					"请选择要删除的文件、文件夹！");
			return;
		}

		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setTitle("您确定要删除选定的文件、文件夹？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);

							for (int i = 0; i < seledObjs.size(); i++) {
								FolderItemHolder item = seledObjs.get(i);
								if (item.selected) {
									if (item.isFolder) {
										deleteFolder(item.path);
									} else {
										if (item.path.startsWith("smb://")) {
											SmbFile file = new SmbFile(
													item.path);
											file.delete();
										} else {
											File file = new File(item.path);
											file.delete();
										}

									}
								}
							}

							com.jd.util.AppHelper.showInfoDlg(
									FileMainTain.this, "已删除选定的文件、文件夹！");

							field.set(dialog, true);

							refreshList();
						} catch (Exception e) {
							// TODO: handle exception

						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});

		dlg.show();

	}

	private void deleteFolder(String path) {
		try {
			if (path.startsWith("smb://")) {
				SmbFile dir = new SmbFile(path);

				for (SmbFile file : dir.listFiles()) {
					if (file.isFile()) {
						file.delete();
					} else {
						deleteFolder(file.getPath());
					}
				}

				dir.delete();
			} else {
				File dir = new File(path);

				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						file.delete();
					} else {
						deleteFolder(file.getPath());
					}
				}

				dir.delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void refreshList() {
		if (curTab == "album") {
			com.jd.util.AppHelper.albumFile.refreshList();
		} else if (curTab == "local") {
			com.jd.util.AppHelper.localFile.refreshList();
		} else if (curTab == "neighbor") {
			com.jd.util.AppHelper.neighborFile.refreshList();
		} else {
			com.jd.util.AppHelper.houseFile.refreshList();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			LayoutParams layoutParams = tabHost.getLayoutParams();
			LinearLayout llAds = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llAds);
			layoutParams.height = dm.heightPixels - llAds.getHeight();
			tabHost.setLayoutParams(layoutParams);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case com.tl.pic.brow.R.id.rdoAlbum:
				this.tabHost.setCurrentTabByTag("album");
				curTab = "album";
				com.jd.util.AppHelper.area = "album";
				break;
			case com.tl.pic.brow.R.id.rdoLocal:
				this.tabHost.setCurrentTabByTag("local");
				curTab = "local";
				if (com.jd.util.AppHelper.localFile != null) {
					if (com.jd.util.AppHelper.localFile.rootDir != null) {
						if (com.jd.util.AppHelper.localFile.rootDir
								.equals(com.jd.util.AppHelper.sdRoot)) {
							com.jd.util.AppHelper.area = "sdcard";
						} else {
							com.jd.util.AppHelper.area = "memory";
						}
					} else {
						com.jd.util.AppHelper.area = "memory";
					}
				}
				break;
			case com.tl.pic.brow.R.id.rdoNeighbor:
				this.tabHost.setCurrentTabByTag("neighbor");
				curTab = "neighbor";
				com.jd.util.AppHelper.area = "neighbor";
				break;
			case com.tl.pic.brow.R.id.rdoHouse:
				this.tabHost.setCurrentTabByTag("house");
				curTab = "house";
				com.jd.util.AppHelper.area = "house";
				break;
			}

			showMenu();
		}
	}

	public void setTabFromOuter(String area) {
		CompoundButton buttonView = null;
		if (area.equals("album")) {
			buttonView = ((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoAlbum));
		} else if (area.equals("local")) {
			buttonView = ((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoLocal));
		} else if (area.equals("neighbor")) {
			buttonView = ((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoNeighbor));
		} else {
			buttonView = ((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoHouse));
		}

		buttonView.setChecked(true);
		onCheckedChanged(buttonView, true);
	}

	private void setupIntent() {
		this.fileIntent = new Intent(this, AlbumFile.class);
		this.localIntent = new Intent(this, LocalFile.class);
		this.neighborIntent = new Intent(this, NeighborFile.class);
		this.houseIntent = new Intent(this, HouseLink.class);

		((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoAlbum))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoLocal))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoNeighbor))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(com.tl.pic.brow.R.id.rdoHouse))
				.setOnCheckedChangeListener(this);

		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;

		localTabHost.addTab(buildTabSpec("album", "相册",
				com.tl.pic.brow.R.drawable.albumsmall, this.fileIntent));

		localTabHost.addTab(buildTabSpec("local", "本地",
				com.tl.pic.brow.R.drawable.localsmall, this.localIntent));

		localTabHost.addTab(buildTabSpec("neighbor", "网上邻居",
				com.tl.pic.brow.R.drawable.neighborsmall, this.neighborIntent));
		localTabHost.addTab(buildTabSpec("house", "收藏",
				com.tl.pic.brow.R.drawable.housesmall, this.houseIntent));

		menuStatus.put("album", Boolean.TRUE);
		// menuStatus.put("local", Boolean.FALSE);
		menuStatus.put("memory", Boolean.FALSE);
		menuStatus.put("sdcard", Boolean.FALSE);
		menuStatus.put("neighbor", Boolean.FALSE);
		menuStatus.put("house", Boolean.FALSE);

		RadioButton rdo = (RadioButton) findViewById(com.tl.pic.brow.R.id.rdoAlbum);
		rdo.setChecked(true);
	}

	private TabHost.TabSpec buildTabSpec(String tag, String title, int resIcon,
			final Intent content) {
		return this.tabHost.newTabSpec(tag)
				.setIndicator(title, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	public void showMenu() {
		LinearLayout llAds = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llAds);
		int height;
		if (menuStatus.get(com.jd.util.AppHelper.area).equals(Boolean.FALSE)) {
			((LinearLayout) findViewById(com.tl.pic.brow.R.id.llBottomMenu))
					.setVisibility(View.GONE);
			height = llAds.getHeight();
		} else {
			((LinearLayout) findViewById(com.tl.pic.brow.R.id.llBottomMenu))
					.setVisibility(View.VISIBLE);
			height = llAds.getHeight() * 2;

			if (com.jd.util.AppHelper.area.equals("sdcard")) {
				menuNew.setVisibility(View.GONE);
				menuRename.setVisibility(View.GONE);
				menuDelete.setVisibility(View.GONE);
			} else {
				menuNew.setVisibility(View.VISIBLE);
				menuRename.setVisibility(View.VISIBLE);
				menuDelete.setVisibility(View.VISIBLE);
			}
		}

		LayoutParams layoutParams = tabHost.getLayoutParams();
		RelativeLayout rlFileMaintain = (RelativeLayout) findViewById(com.tl.pic.brow.R.id.rlFileMaintain);
		layoutParams.height = rlFileMaintain.getHeight() - height;
		tabHost.setLayoutParams(layoutParams);
	}

	public void setMenuStatus(Boolean status) {
		if (com.jd.util.AppHelper.area.equals("house")) {
			status = false;
		}
		if (menuStatus.containsKey(com.jd.util.AppHelper.area)) {
			menuStatus.remove(com.jd.util.AppHelper.area);
		}

		menuStatus.put(com.jd.util.AppHelper.area, status);
	}

	private void getSelectedObjs() {
		List<FolderItemHolder> holders = null;
		seledObjs.clear();

		if (curTab.equals("neighbor")) {
			com.jd.adp.NeighborFoldListAdapter adapter = (com.jd.adp.NeighborFoldListAdapter) com.jd.util.AppHelper.neighborFile.lst
					.getAdapter();
			holders = adapter.holders;
		} else if (curTab.equals("album")) {
			com.jd.adp.FoldListAdapter adapter = (com.jd.adp.FoldListAdapter) com.jd.util.AppHelper.albumFile.lst
					.getAdapter();
			holders = adapter.holders;
		} else if (curTab.equals("local")) {
			com.jd.adp.FoldListAdapter adapter = (com.jd.adp.FoldListAdapter) com.jd.util.AppHelper.localFile.lst
					.getAdapter();
			holders = adapter.holders;
		} else {
			return;
		}

		for (FolderItemHolder item : holders) {
			if (item.selected) {
				seledObjs.add(item);
			}
		}
	}

	public void showPopMenu() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View popView = inflater.inflate(com.tl.pic.brow.R.layout.filemoremenu,
				null);
		final PopupWindow popupWindow = new PopupWindow(popView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0));

		LinearLayout menuCopy = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llMenuCopy);
		LinearLayout menuCut = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llMenuCut);
		LinearLayout menuPaste = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llMenuPase);
		LinearLayout menuEncrypt = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llMenuEncrypt);
		LinearLayout menuDecrypt = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llMenuDecrypt);
		LinearLayout menuHouse = (LinearLayout) popView
				.findViewById(com.tl.pic.brow.R.id.llHouse);

		if (com.jd.util.AppHelper.area.equals("sdcard")) {
			menuCut.setVisibility(View.GONE);
			menuPaste.setVisibility(View.GONE);
			menuEncrypt.setVisibility(View.GONE);
			menuDecrypt.setVisibility(View.GONE);
		} else {
			menuCut.setVisibility(View.VISIBLE);
			menuPaste.setVisibility(View.VISIBLE);
			menuEncrypt.setVisibility(View.VISIBLE);
			menuDecrypt.setVisibility(View.VISIBLE);
		}

		menuCopy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doCopy();
			}
		});
		menuCut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doCut();
			}
		});
		menuPaste.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doPaste();
			}
		});
		menuEncrypt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doEncrypt();
			}
		});

		menuDecrypt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doDecrypt();
			}
		});

		menuHouse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				doHouse();
			}
		});

		RelativeLayout rl = (RelativeLayout) findViewById(com.tl.pic.brow.R.id.rlFileMaintain);
		popupWindow.showAtLocation(rl, Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		// 获取popwindow焦点
		popupWindow.setFocusable(true);
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		// popupWindow.setAnimationStyle(R.style.AnimationPreview);

		popupWindow.update();
	}

	private void doHouse() {
		getSelectedObjs();

		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要收藏的文件夹！");
			return;
		}

		boolean isAllFolder = true;

		for (com.jd.adp.FolderItemHolder item : seledObjs) {
			if (!item.isFolder) {
				isAllFolder = false;
				break;
			}
		}

		if (!isAllFolder) {
			com.jd.util.AppHelper.showInfoDlg(this, "只能收藏文件夹！");
			return;
		}

		try {
			com.jd.dal.HouseDao houseDao = new HouseDao(this);
			for (com.jd.adp.FolderItemHolder item : seledObjs) {
				if (houseDao.GetHouseByPath(item.path) == null) {
					com.jd.dal.House house = new House();
					house.setPath(item.path);
					house.setTitle(item.title.getText().toString());
					house.setArea(curTab);
					houseDao.addHouse(house);
				}
			}

			com.jd.util.AppHelper.houseFile.refreshList();
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(this, e.getMessage());
		}

	}

	private void doCopy() {
		getSelectedObjs();
		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要复制的文件夹或者文件！");
			return;
		}

		opType = OpType.Copy;
		if (curTab.equals("album")) {
			from = SourceType.Album;
		} else if (curTab.equals("local")) {
			from = SourceType.Local;
		} else {
			from = SourceType.Neighbor;
		}

		sFiles.clear();

		for (com.jd.adp.FolderItemHolder item : seledObjs) {
			sFiles.add(item);
		}
	}

	private void doCut() {
		getSelectedObjs();
		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要复制的文件夹或者文件！");
			return;
		}

		opType = OpType.Cut;
		if (curTab.equals("album")) {
			from = SourceType.Album;
		} else if (curTab.equals("local")) {
			from = SourceType.Local;
		} else {
			from = SourceType.Neighbor;
		}

		sFiles.clear();

		for (com.jd.adp.FolderItemHolder item : seledObjs) {
			sFiles.add(item);
		}
	}

	private void doPaste() {
		totalFiles = 0;
		currentCnt = 0;
		getFilesCount();

		proDlg = new ProgressDialog(this);
		proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDlg.setTitle("文件拷贝");
		proDlg.setMessage("拷贝进度：");
		proDlg.setIndeterminate(false);
		proDlg.setCancelable(false);
		proDlg.setMax(totalFiles);
		proDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						proDlg.dismiss();
						proDlg = null;
					}

				});
		proDlg.setButton(DialogInterface.BUTTON_NEUTRAL, "后台拷贝",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						proDlg.dismiss();
						proDlg = null;
					}
				});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.arg1 < totalFiles) {
					if (proDlg != null) {
						proDlg.setProgress(msg.arg1);
					}
				} else {
					if (proDlg != null) {
						proDlg.dismiss();
					}
					com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
							"文件拷贝已完成！");
					refreshList();
				}
			}
		};

		proDlg.show();

		beTerminated = false;
		Thread thread = new Thread() {
			@Override
			public void run() {
				String desPath = null;
				// TODO Auto-generated method stub
				if (curTab.equals("album")) {
					to = SourceType.Album;
					desPath = com.jd.util.AppHelper.albumFile.currentDir;
				} else if (curTab.equals("local")) {
					to = SourceType.Local;
					desPath = com.jd.util.AppHelper.localFile.currentDir;
				} else {
					to = SourceType.Neighbor;
					desPath = com.jd.util.AppHelper.neighborFile.currentDir;
				}

				try {
					for (com.jd.adp.FolderItemHolder item : sFiles) {
						if (!beTerminated) {
							if (item.isFolder) {
								doPasteFolder(item.path, desPath);
							} else {
								// synchronized (this) {
								while (threadCount >= maxThreadCount) {
									Thread.sleep(1);
								}
								// }

								// doPasteFile(item.path, desPath);
								CopyFile copyFile = new CopyFile(item.path,
										desPath);
								copyFile.run();
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				// if (opType == OpType.Cut) {
				// try {
				// for (int i = 0; i < sFiles.size(); i++) {
				// FolderItemHolder item = seledObjs.get(i);
				// if (item.selected) {
				// if (item.isFolder) {
				// deleteFolder(item.path);
				// } else {
				// if (item.path.startsWith("smb://")) {
				// SmbFile file = new SmbFile(item.path);
				// file.delete();
				// } else {
				// File file = new File(item.path);
				// file.delete();
				// }
				//
				// }
				// }
				// }
				// } catch (Exception e) {
				// // TODO: handle exception
				// }
				//
				// }
			}
		};

		thread.start();
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param path
	 */
	private void doPasteFolder(String path, String desPath) {
		if (beTerminated) {
			return;
		}

		if (from == SourceType.Neighbor) {
			try {
				SmbFile dir = new SmbFile(path);
				if (to == SourceType.Neighbor) {
					SmbFile desDir = new SmbFile(desPath + "/" + dir.getName());
					if (!desDir.exists()) {
						desDir.mkdirs();
					}

					for (SmbFile subItem : dir.listFiles()) {
						if (subItem.isDirectory()) {
							doPasteFolder(subItem.getPath(), desDir.getPath());
						} else {
							// synchronized (this) {
							while (threadCount >= maxThreadCount) {
								Thread.sleep(1);
							}
							// }
							// doPasteFile(subItem.getPath(), desDir.getPath());
							CopyFile copyFile = new CopyFile(subItem.getPath(),
									desDir.getPath());
							copyFile.run();
						}
					}

				} else {
					File desDir = new File(desPath + "/" + dir.getName());
					if (!desDir.exists()) {
						desDir.mkdirs();
					}

					for (SmbFile subItem : dir.listFiles()) {
						if (subItem.isDirectory()) {
							doPasteFolder(subItem.getPath(), desDir.getPath());
						} else {
							// doPasteFile(subItem.getPath(), desDir.getPath());
							// synchronized (this) {
							while (threadCount >= maxThreadCount) {
								Thread.sleep(1);
							}
							// }
							CopyFile copyFile = new CopyFile(subItem.getPath(),
									desDir.getPath());
							copyFile.run();
						}
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {

			try {
				File dir = new File(path);
				if (to == SourceType.Neighbor) {
					SmbFile desDir = new SmbFile(desPath + "/" + dir.getName());
					if (!desDir.exists()) {
						desDir.mkdirs();
					}

					for (File subItem : dir.listFiles()) {
						if (subItem.isDirectory()) {
							doPasteFolder(subItem.getPath(), desDir.getPath());
						} else {
							// synchronized (this) {
							while (threadCount >= maxThreadCount) {
								Thread.sleep(1);
							}
							// }
							// doPasteFile(subItem.getPath(), desDir.getPath());
							CopyFile copyFile = new CopyFile(subItem.getPath(),
									desDir.getPath());
							copyFile.run();
						}
					}

				} else {
					File desDir = new File(desPath + "/" + dir.getName());
					if (!desDir.exists()) {
						desDir.mkdirs();
					}

					for (File subItem : dir.listFiles()) {
						if (subItem.isDirectory()) {
							doPasteFolder(subItem.getPath(), desDir.getPath());
						} else {
							// synchronized (this) {
							while (threadCount >= maxThreadCount) {
								Thread.sleep(1);
							}
							// }
							CopyFile copyFile = new CopyFile(subItem.getPath(),
									desDir.getPath());
							copyFile.run();
							// doPasteFile(subItem.getPath(), desDir.getPath());
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param path
	 */
	private void doPasteFile(String fileName, String desPath) {
		if (beTerminated) {
			return;
		}

		java.io.BufferedInputStream fis = null;
		java.io.BufferedOutputStream fos = null;
		int bufSize = com.jd.util.AppHelper.bufLen;
		byte[] data = new byte[bufSize];

		try {
			if (from == SourceType.Neighbor) {
				SmbFile file = new SmbFile(fileName);
				fis = new java.io.BufferedInputStream(new SmbFileInputStream(
						fileName));
				if (to == SourceType.Neighbor) {
					fos = new BufferedOutputStream(new SmbFileOutputStream(
							new SmbFile(desPath + "/" + file.getName())));
				} else {
					fos = new BufferedOutputStream(new FileOutputStream(
							new File(desPath + "/" + file.getName())));
				}
			} else {
				File file = new File(fileName);
				fis = new BufferedInputStream(new FileInputStream(fileName));
				if (to == SourceType.Neighbor) {
					fos = new BufferedOutputStream(new SmbFileOutputStream(
							new SmbFile(desPath + "/" + file.getName())));
				} else {
					fos = new BufferedOutputStream(new FileOutputStream(
							new File(desPath + "/" + file.getName())));
				}
			}

			int len = fis.read(data);
			while (len != -1) {
				fos.write(data, 0, len);
				len = fis.read(data);
			}

			fos.flush();
			fos.close();
			fis.close();

			currentCnt++;
			Message msg = new Message();
			msg.arg1 = currentCnt;
			handler.sendMessage(msg);
			// proDlg.setProgress(currentCnt);

		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper.showInfoDlg(this, e.getMessage());
		}

	}

	private void getFilesCount() {
		try {
			if (from == SourceType.Neighbor) {
				for (com.jd.adp.FolderItemHolder item : sFiles) {
					SmbFile file = new SmbFile(item.path);
					if (file.isFile()) {
						totalFiles++;
					} else {
						getSubFilesCount(file.getPath());
					}
				}
			} else {
				for (com.jd.adp.FolderItemHolder item : sFiles) {
					File file = new File(item.path);
					if (file.isFile()) {
						totalFiles++;
					} else {
						getSubFilesCount(file.getPath());
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void getSubFilesCount(String path) {
		try {
			if (from == SourceType.Neighbor) {
				SmbFile file = new SmbFile(path);
				for (SmbFile item : file.listFiles()) {
					if (item.isFile()) {
						totalFiles++;
					} else {
						getSubFilesCount(item.getPath());
					}
				}
			} else {
				File file = new File(path);
				for (File item : file.listFiles()) {
					if (item.isFile()) {
						totalFiles++;
					} else {
						getSubFilesCount(item.getPath());
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// private void

	private void doEncrypt() {
		opType = OpType.Encrypt;
		if (curTab.equals("album")) {
			from = SourceType.Album;
		} else if (curTab.equals("local")) {
			from = SourceType.Local;
		} else {
			from = SourceType.Neighbor;
		}

		getSelectedObjs();
		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要加密的文件夹或者文件！");
			return;
		}

		sFiles.clear();

		for (com.jd.adp.FolderItemHolder item : seledObjs) {
			sFiles.add(item);
		}

		totalFiles = 0;
		currentCnt = 0;
		getFilesCount();

		proDlg = new ProgressDialog(this);
		proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDlg.setTitle("文件加密");
		proDlg.setMessage("加密进度：");
		proDlg.setIndeterminate(false);
		proDlg.setCancelable(false);
		proDlg.setMax(totalFiles);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.arg1 < totalFiles) {
					proDlg.setProgress(msg.arg1);
				} else {
					proDlg.dismiss();
					com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
							"文件加密已完成！");
					refreshList();
				}
			}
		};

		proDlg.show();

		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (com.jd.adp.FolderItemHolder item : sFiles) {
					if (item.isFolder) {
						EncryptFolder(item.path);
					} else {
						EncryptFile(item.path);
					}
				}
			}
		};

		thread.start();
	}

	private void EncryptFolder(String path) {
		String dirName = null;// 文件夹的名称
		boolean isEncrypted = false;
		try {
			if (path.startsWith("smb://")) {
				SmbFile dir = new SmbFile(path);
				try {
					dirName = com.jd.util.StringUtil.decrypt(dir.getName());
					if (dirName != null) {
						// 已加密
						isEncrypted = true;
					} else {
						dirName = dir.getParent()
								+ "/"
								+ com.jd.util.StringUtil.encrypt(dir.getName()
										.substring(0,
												dir.getName().length() - 1));
						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				// 文件夹名称需要加密
				SmbFile desDir = null;
				if (!isEncrypted) {
					desDir = new SmbFile(dirName);
					dir.renameTo(desDir);
				}

				if (!dirName.endsWith("/")) {
					dirName += "/";
				}

				desDir = new SmbFile(dirName);

				for (SmbFile item : desDir.listFiles()) {
					if (item.isDirectory()) {
						EncryptFolder(item.getPath());
					} else {
						EncryptFile(item.getPath());
					}
				}

			} else {
				File dir = new File(path);
				try {
					// 源文件夹已加密，不用再加密
					dirName = com.jd.util.StringUtil.decrypt(dir.getName());
					if (dirName != null) {
						// 已加密
						isEncrypted = true;
					} else {
						dirName = dir.getParent() + "/"
								+ com.jd.util.StringUtil.encrypt(dir.getName());
						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
							e.getMessage());
				}

				// 文件夹名称需要加密
				File desDir = null;
				if (!isEncrypted) {
					desDir = new File(dirName);
					dir.renameTo(desDir);
				}

				if (!dirName.endsWith("/")) {
					dirName += "/";
				}

				desDir = new File(dirName);

				for (File item : desDir.listFiles()) {
					if (item.isDirectory()) {
						EncryptFolder(item.getPath());
					} else {
						EncryptFile(item.getPath());
					}
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			com.jd.util.AppHelper
					.showInfoDlg(FileMainTain.this, e.getMessage());
		}

	}

	private void EncryptFile(String path) {
		String fileName = null;// 文件的名称
		boolean isEncrypted = false;// 标志是否已经加密

		try {
			if (path.startsWith("smb://")) {
				SmbFile file = new SmbFile(path);
				try {
					// 源文件夹已加密，不用再加密
					fileName = com.jd.util.StringUtil.decrypt(file.getName());
					if (fileName != null) {
						isEncrypted = true;
					} else {
						fileName = file.getParent()
								+ "/"
								+ com.jd.util.StringUtil
										.encrypt(file.getName());
						isEncrypted = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (!isEncrypted) {
					// 文件名称需要加密
					SmbFile desFile = null;
					desFile = new SmbFile(fileName);
					file.renameTo(desFile);

					java.io.InputStream fis = new SmbFileInputStream(desFile);
					String tmpFileName = fileName + "tmp";
					SmbFile tmpFile = new SmbFile(tmpFileName);
					java.io.OutputStream fos = new SmbFileOutputStream(tmpFile);
					byte data[] = new byte[com.jd.util.AppHelper.deBufLen];
					int len = fis.read(data);
					while (len != -1) {
						data = com.jd.util.CryptoTools.encode(data);
						fos.write(data);
						len = fis.read(data);
					}

					fos.write(com.jd.util.CryptoTools.intToByte4((int) desFile
							.length()));// 最后写入文件的长度
					fos.flush();
					fos.close();
					fis.close();

					desFile.delete();
					tmpFile.renameTo(desFile);
				}

			} else {
				File file = new File(path);
				try {

					fileName = com.jd.util.StringUtil.decrypt(file.getName());
					if (fileName != null) {
						// 文件已加密
						isEncrypted = true;
					} else {
						fileName = file.getParent()
								+ "/"
								+ com.jd.util.StringUtil
										.encrypt(file.getName());
						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				if (!isEncrypted) {

					// 文件名称需要加密
					File desFile = null;
					desFile = new File(fileName);
					file.renameTo(desFile);
					java.io.InputStream fis = new FileInputStream(desFile);
					String tmpFileName = fileName + "tmp";
					File tmpFile = new File(tmpFileName);
					java.io.OutputStream fos = new FileOutputStream(tmpFile);
					byte data[] = new byte[com.jd.util.AppHelper.deBufLen];
					int len = fis.read(data);
					while (len > 0) {
						data = com.jd.util.CryptoTools.encode(data);
						fos.write(data);
						len = fis.read(data);
					}

					fos.write(com.jd.util.CryptoTools.intToByte4((int) desFile
							.length()));// 最后写入文件的长度
					fos.flush();
					fos.close();
					fis.close();

					desFile.delete();
					tmpFile.renameTo(desFile);
				}
			}

			currentCnt++;
			Message msg = new Message();
			msg.arg1 = currentCnt;
			handler.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void doDecrypt() {
		opType = OpType.Decrypt;

		if (curTab.equals("album")) {
			from = SourceType.Album;
		} else if (curTab.equals("local")) {
			from = SourceType.Local;
		} else {
			from = SourceType.Neighbor;
		}

		getSelectedObjs();
		if (seledObjs.size() == 0) {
			com.jd.util.AppHelper.showInfoDlg(this, "请选择要解密的文件夹或者文件！");
			return;
		}

		sFiles.clear();

		for (com.jd.adp.FolderItemHolder item : seledObjs) {
			sFiles.add(item);
		}

		totalFiles = 0;
		currentCnt = 0;
		getFilesCount();

		proDlg = new ProgressDialog(this);
		proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDlg.setTitle("文件解密");
		proDlg.setMessage("解密进度：");
		proDlg.setIndeterminate(false);
		proDlg.setCancelable(false);
		proDlg.setMax(totalFiles);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.arg1 < totalFiles) {
					proDlg.setProgress(msg.arg1);
				} else {
					proDlg.dismiss();
					com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
							"文件解密已完成！");
					refreshList();
				}
			}
		};

		proDlg.show();

		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (com.jd.adp.FolderItemHolder item : sFiles) {
					if (item.isFolder) {
						DecryptFolder(item.path);
					} else {
						DecryptFile(item.path);
					}
				}
			}
		};

		thread.start();
	}

	private void DecryptFolder(String path) {
		String dirName = null;// 文件夹的名称
		boolean isEncrypted = false;
		try {
			if (path.startsWith("smb:")) {
				SmbFile dir = new SmbFile(path);
				try {
					dirName = com.jd.util.StringUtil.decrypt(dir.getName()
							.substring(0, dir.getName().length() - 1));
					if (dirName != null) {
						// 已加密
						isEncrypted = true;
						dirName = dir.getParent() + "/" + dirName + "/";
					} else {
						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				// 文件夹名称需要解密
				SmbFile desDir = null;
				if (isEncrypted) {
					desDir = new SmbFile(dirName);
					dir.renameTo(desDir);
				} else {
					desDir = dir;
				}

				for (SmbFile item : desDir.listFiles()) {
					if (item.isDirectory()) {
						DecryptFolder(item.getPath());
					} else {
						DecryptFile(item.getPath());
					}
				}
			} else {
				File dir = new File(path);
				try {
					dirName = com.jd.util.StringUtil.decrypt(dir.getName());
					if (dirName != null) {
						// 已加密
						isEncrypted = true;
						dirName = dir.getParent() + "/" + dirName;
					} else {
						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				// 文件夹名称需要解密
				File desDir = null;
				if (isEncrypted) {
					desDir = new File(dirName);
					dir.renameTo(desDir);
				} else {
					desDir = dir;
				}

				for (File item : desDir.listFiles()) {
					if (item.isDirectory()) {
						DecryptFolder(item.getPath());
					} else {
						DecryptFile(item.getPath());
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void DecryptFile(String path) {
		String fileName = null;// 文件的名称
		boolean isEncrypted = false;// 标志是否已经加密

		try {
			if (path.startsWith("smb://")) {
				SmbFile file = new SmbFile(path);
				try {
					// 源文件夹已加密，不用再加密
					fileName = com.jd.util.StringUtil.decrypt(file.getName());
					if (fileName != null) {
						isEncrypted = true;
						fileName = file.getParent()
								+ "/"
								+ com.jd.util.StringUtil
										.decrypt(file.getName());
					} else {
						isEncrypted = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (isEncrypted) {
					// 文件名称需要解密
					SmbFile desFile = null;
					if (isEncrypted) {
						desFile = new SmbFile(fileName);
						file.renameTo(desFile);
					}

					// java.io.InputStream fis = new
					// SmbFileInputStream(desFile);
					// String tmpFileName = fileName + "tmp";
					// SmbFile tmpFile = new SmbFile(tmpFileName);
					// java.io.OutputStream fos = new
					// SmbFileOutputStream(tmpFile);
					// byte data[] = new byte[com.jd.util.AppHelper.bufLen];
					// int len = fis.read(data);
					// while (len != -1) {
					// data = com.jd.util.CryptoTools.decode(data);
					// fos.write(data, 0, len);
					// len = fis.read(data);
					// }
					java.io.InputStream fis = new SmbFileInputStream(desFile);
					// 先取出文件的长度
					fis.skip(desFile.length() - 4);
					byte lenData[] = new byte[4];
					fis.read(lenData);
					int fileSize = com.jd.util.CryptoTools.byte4ToInt(lenData,
							0);
					fis.close();
					fis = new SmbFileInputStream(desFile);// 重新打开文件

					String tmpFileName = fileName + "tmp";
					SmbFile tmpFile = new SmbFile(tmpFileName);
					java.io.OutputStream fos = new SmbFileOutputStream(tmpFile);

					byte data[] = new byte[com.jd.util.AppHelper.deBufLen];
					int len = fis.read(data);
					int totalBytes = 0;

					while (len > 0) {
						data = com.jd.util.CryptoTools.decode(data);
						totalBytes += len;

						if (totalBytes >= fileSize) {
							// 最后一段数据
							fos.write(data, 0, fileSize
									% com.jd.util.AppHelper.deBufLen);
							break;
						} else {
							fos.write(data, 0, len);
						}

						len = fis.read(data);
					}
					fos.flush();
					fos.close();
					fis.close();

					desFile.delete();
					tmpFile.renameTo(desFile);
				}
			} else {
				File file = new File(path);
				try {

					fileName = com.jd.util.StringUtil.decrypt(file.getName());
					if (fileName != null) {
						// 文件已加密
						isEncrypted = true;
						fileName = file.getParent()
								+ "/"
								+ com.jd.util.StringUtil
										.decrypt(file.getName());
					} else {

						isEncrypted = false;
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				if (isEncrypted) {
					// 文件名称需要解密
					File desFile = null;
					if (isEncrypted) {
						desFile = new File(fileName);
						file.renameTo(desFile);
					}

					java.io.InputStream fis = new FileInputStream(desFile);
					// 先取出文件的长度
					fis.skip(desFile.length() - 4);
					byte lenData[] = new byte[4];
					fis.read(lenData);
					int fileSize = com.jd.util.CryptoTools.byte4ToInt(lenData,
							0);
					fis.close();
					fis = new FileInputStream(desFile);// 重新打开文件

					String tmpFileName = fileName + "tmp";
					File tmpFile = new File(tmpFileName);
					java.io.OutputStream fos = new FileOutputStream(tmpFile);

					byte data[] = new byte[com.jd.util.AppHelper.deBufLen];
					int len = fis.read(data);
					int totalBytes = 0;

					while (len > 0) {
						data = com.jd.util.CryptoTools.decode(data);
						totalBytes += len;

						if (totalBytes >= fileSize) {
							// 最后一段数据
							fos.write(data, 0, fileSize
									% com.jd.util.AppHelper.deBufLen);
							break;
						} else {
							fos.write(data, 0, len);
						}

						len = fis.read(data);
					}

					fos.flush();
					fos.close();
					fis.close();

					desFile.delete();
					tmpFile.renameTo(desFile);
				}
			}

			currentCnt++;
			Message msg = new Message();
			msg.arg1 = currentCnt;
			handler.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private enum OpType {
		Copy, Cut, Encrypt, Decrypt
	}

	private enum SourceType {
		Album, Local, Neighbor
	}

	private class CopyFile extends Thread {
		private String fileName;
		private String desPath;

		public CopyFile(String fileName, String desPath) {
			this.fileName = fileName;
			this.desPath = desPath;
		}

		public void run() {
			if (beTerminated) {
				return;
			}
			synchronized (this) {
				threadCount++;
			}

			java.io.BufferedInputStream fis = null;
			java.io.BufferedOutputStream fos = null;
			int bufSize = com.jd.util.AppHelper.bufLen;
			byte[] data = new byte[bufSize];

			try {
				if (from == SourceType.Neighbor) {
					SmbFile file = new SmbFile(fileName);
					fis = new java.io.BufferedInputStream(
							new SmbFileInputStream(fileName));
					if (to == SourceType.Neighbor) {
						fos = new BufferedOutputStream(new SmbFileOutputStream(
								new SmbFile(desPath + "/" + file.getName())));
					} else {
						fos = new BufferedOutputStream(new FileOutputStream(
								new File(desPath + "/" + file.getName())));
					}
				} else {
					File file = new File(fileName);
					fis = new BufferedInputStream(new FileInputStream(fileName));
					if (to == SourceType.Neighbor) {
						fos = new BufferedOutputStream(new SmbFileOutputStream(
								new SmbFile(desPath + "/" + file.getName())));
					} else {
						fos = new BufferedOutputStream(new FileOutputStream(
								new File(desPath + "/" + file.getName())));
					}
				}

				int len = fis.read(data);
				while (len != -1) {
					fos.write(data, 0, len);
					len = fis.read(data);
				}

				fos.flush();
				fos.close();
				fis.close();

				synchronized (this) {
					currentCnt++;
					Message msg = new Message();
					msg.arg1 = currentCnt;
					handler.sendMessage(msg);
					threadCount--;
				}
			} catch (Exception e) {
				// TODO: handle exception
				com.jd.util.AppHelper.showInfoDlg(FileMainTain.this,
						e.getMessage());
			}

		}
	}
}
