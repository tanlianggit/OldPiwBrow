package com.jd.ui;

import java.io.File;
import java.util.List;

import jcifs.smb.SmbFile;

import com.jd.adp.FolderItemHolder;
import com.jd.adp.MySimpleAdapter;
import com.jd.dal.Neighbor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyBaseActivity extends BaseActivity {
	View layout = null;
	ListView lst = null;
	View selectedItem = null;
	boolean waitDouble = true;
	int DOUBLE_CLICK_TIME = 400; // ���ε�����ʱ����
	int level = 0;// �Ӹ�Ŀ¼��ʼ���ļ��еĲ㼶
	String rootDir = null;// ��ǰ�ĸ�Ŀ¼
	String currentDir = null;// ��ǰ��Ŀ¼
	MyHandler handler;
	boolean conResult = false;// �����ھӵ������Լ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (NeighborFile.class.isInstance(this)) {
			this.setContentView(com.tl.pic.brow.R.layout.neighborfile);
		} else if (HouseLink.class.isInstance(this)) {
			this.setContentView(com.tl.pic.brow.R.layout.linkfile);
		} else {
			this.setContentView(com.tl.pic.brow.R.layout.localfile);
		}

		handler = new MyHandler(Looper.myLooper());

		lst = (ListView) findViewById(com.tl.pic.brow.R.id.lvFolder);
		bindList();

		lst.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedItem = arg1;
				final int itemPos = arg2;
				final View item = arg1;
				if (waitDouble == true) {
					if (level == 0) {
						MySimpleAdapter adp = (MySimpleAdapter) arg0
								.getAdapter();
						adp.setSelectedItem(arg2);
						adp.notifyDataSetInvalidated();
					}

					waitDouble = false;
					Thread thread = new Thread() {
						@Override
						public void run() {
							try {
								sleep(DOUBLE_CLICK_TIME);

								if (waitDouble == false) {
									waitDouble = true;
									if (level == 0) {
										return;
									}

									Message msg = new Message();
									msg.arg1 = itemPos;
									msg.obj = item;
									handler.sendMessage(msg);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				} else {
					// ˫��
					String tmpName = null;

					waitDouble = true;
					if (level == 0) {
						TextView txtPath = (TextView) arg1
								.findViewById(com.tl.pic.brow.R.id.txtPath);
						rootDir = txtPath.getText().toString();
						tmpName = rootDir;
					} else {
						FolderItemHolder view = (FolderItemHolder) arg1
								.getTag();
						tmpName = view.path;
					}

					// ���´����ж�˫�������ļ������ļ���
					boolean isFile = false;
					String fileName = null;
					if (level > 0) {
						try {
							if (tmpName.startsWith("smb://")) {
								SmbFile file = new SmbFile(tmpName);
								fileName = file.getName();
								if (file.isDirectory()) {
									isFile = false;
								} else {
									isFile = true;
								}
							} else {
								File file = new File(tmpName);
								fileName = file.getName();
								if (file.isDirectory()) {
									isFile = false;
								} else {
									isFile = true;
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

					if (HouseLink.class.isInstance(MyBaseActivity.this)) {
						isFile = false;
					}

					// ������ļ��У�����ʾ�ļ��е��¼��ļ��м��ļ��б�
					if (!isFile) {
						currentDir = tmpName;

						if (NeighborFile.class.isInstance(MyBaseActivity.this)) {
							RelativeLayout rlMenu = (RelativeLayout) MyBaseActivity.this
									.findViewById(com.tl.pic.brow.R.id.rlMenu);
							rlMenu.setVisibility(View.GONE);

							if (!rootDir.startsWith("smb://")) {
								rootDir = "smb://" + rootDir;
								if (level == 0) {
									currentDir = rootDir;
									checkCon(currentDir);// ʹ���̲߳��������ھӵ�����

									if (!conResult) {
										com.jd.util.AppHelper.showInfoDlg(
												MyBaseActivity.this,
												"�޷�����Ŀ����������������ã�");
										return;
									}
								}
							}

							com.jd.adp.NeighborFoldListAdapter adapter = new com.jd.adp.NeighborFoldListAdapter(
									MyBaseActivity.this, currentDir,
									com.jd.util.AppHelper.scale, rootDir);
							lst.setAdapter(adapter);
							level++;
							setMenuStatus();
						} else if (HouseLink.class
								.isInstance(MyBaseActivity.this)) {
							TextView txtArea = (TextView) arg1
									.findViewById(com.tl.pic.brow.R.id.txtArea);
							String area = txtArea.getText().toString();
							com.jd.util.AppHelper.main.setTabFromOuter(area);
							if (area.equals("album")) {
								com.jd.adp.FoldListAdapter adapter = new com.jd.adp.FoldListAdapter(
										MyBaseActivity.this, currentDir,
										com.jd.util.AppHelper.scale, rootDir);
								com.jd.util.AppHelper.albumFile.level = getLevel(currentDir);

								com.jd.util.AppHelper.albumFile.lst.setAdapter(adapter);
							} else if (area.equals("local")) {
								com.jd.adp.FoldListAdapter adapter = new com.jd.adp.FoldListAdapter(
										MyBaseActivity.this, currentDir,
										com.jd.util.AppHelper.scale, rootDir);
								com.jd.util.AppHelper.localFile.level = getLevel(currentDir);
								com.jd.util.AppHelper.localFile.lst
										.setAdapter(adapter);
								
								if (rootDir.equals(com.jd.util.AppHelper.sdRoot)) {
									com.jd.util.AppHelper.area = "sdcard";
								} else if (rootDir
										.equals(com.jd.util.AppHelper.mobileRoot)) {
									com.jd.util.AppHelper.area = "memory";
								}

							} else if (area.equals("neighbor")) {
								
								checkCon(currentDir);// ʹ���̲߳��������ھӵ�����

								if (!conResult) {
									com.jd.util.AppHelper.showInfoDlg(
											MyBaseActivity.this,
											"�޷����ղأ������Ƿ�������ַ����Ŀ¼�����仯����ɾ�����ղأ�Ȼ���ٴ���Ӵ��ղأ�");
									return;
								}
								
								com.jd.adp.NeighborFoldListAdapter adapter = new com.jd.adp.NeighborFoldListAdapter(
										MyBaseActivity.this, currentDir,
										com.jd.util.AppHelper.scale, rootDir);
								
							
								com.jd.util.AppHelper.neighborFile.level = getLevel(currentDir);
								com.jd.util.AppHelper.neighborFile.lst
										.setAdapter(adapter);
								com.jd.util.AppHelper.neighborFile.findViewById(com.tl.pic.brow.R.id.rlMenu).setVisibility(View.GONE);
							}
							
							com.jd.util.AppHelper.fileMainTain.setMenuStatus(Boolean.TRUE);
							com.jd.util.AppHelper.fileMainTain.showMenu();

							
						} else {
							com.jd.adp.FoldListAdapter adapter = new com.jd.adp.FoldListAdapter(
									MyBaseActivity.this, currentDir,
									com.jd.util.AppHelper.scale, rootDir);
							lst.setAdapter(adapter);
							level++;
							if (rootDir.equals(com.jd.util.AppHelper.sdRoot)) {
								com.jd.util.AppHelper.area = "sdcard";
							} else if (rootDir
									.equals(com.jd.util.AppHelper.mobileRoot)) {
								com.jd.util.AppHelper.area = "memory";
							}
							setMenuStatus();
						}
//						com.jd.util.AppHelper.fileMainTain.setMenuStatus(Boolean.TRUE);
//						com.jd.util.AppHelper.fileMainTain.showMenu();
					} else {
						// ˫�����ļ���1�������ͼ���ļ��������ͼƬ��2���������ͼƬ�����ݲ�������
						if (com.jd.util.StringUtil.decrypt(fileName) != null) {
							fileName = com.jd.util.StringUtil.decrypt(fileName);
						}

						if (com.jd.util.AppHelper.isImageFile(fileName)) {
							// if
							// (com.jd.util.AppHelper.area.equals("neighbor")) {
							// com.jd.util.AppHelper
							// .showInfoDlg(MyBaseActivity.this,
							// "���������ٶȵ�ԭ�򣬲�֧�������ھ�ͼƬ����������������Խ������ھ��ϵ�ͼƬ�������ֻ����ٽ��������");
							// return;
							// } else {
							Intent intent = new Intent(MyBaseActivity.this,
									GalleryAni.class);
							Bundle bundle = new Bundle();
							bundle.putString("path", currentDir);
							bundle.putString("file", tmpName);
							intent.putExtras(bundle);
							MyBaseActivity.this.startActivity(intent);
							// }
						} else {
							com.jd.util.AppHelper.showInfoDlg(
									MyBaseActivity.this, "˫��������ʱֻ֧��ͼƬ�ļ���");
						}
					}
				}
			}
		});

		lst.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				String tmpName = null;
				if (level == 0) {
					return false;
				} else {
					FolderItemHolder view = (FolderItemHolder) arg1.getTag();
					tmpName = view.path;
				}

				// ���´����жϵ�������ļ������ļ���
				boolean isFile = false;
				if (level > 0) {
					try {
						if (tmpName.startsWith("smb://")) {
							SmbFile file = new SmbFile(tmpName);
							if (file.isDirectory()) {
								isFile = false;
							} else {
								isFile = true;
							}
						} else {
							File file = new File(tmpName);
							if (file.isDirectory()) {
								isFile = false;
							} else {
								isFile = true;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				// ������ļ��У�����
				if (!isFile) {
					return false;
				} else {
					// ���ļ���1�������ͼ���ļ�����ƴͼ��2���������ͼƬ�����ݲ�������

					Intent intent = new Intent(MyBaseActivity.this,
							SpellPic.class);
					Bundle bundle = new Bundle();
					bundle.putString("fileName", tmpName);
					intent.putExtras(bundle);
					MyBaseActivity.this.startActivity(intent);
				}
				return false;
			}

		});
	}

	private int getLevel(String path) {
		int count = 0;
		String root = null;

		if (path.startsWith(com.jd.util.AppHelper.albumRoot)) {
			root = com.jd.util.AppHelper.albumRoot;
			com.jd.util.AppHelper.albumFile.rootDir = root;
			com.jd.util.AppHelper.albumFile.currentDir = path;
		} else if (path.startsWith(com.jd.util.AppHelper.oldAlbumRoot)) {
			root = com.jd.util.AppHelper.oldAlbumRoot;
			com.jd.util.AppHelper.albumFile.rootDir = root;
			com.jd.util.AppHelper.albumFile.currentDir = path;
		} else if (path.startsWith(com.jd.util.AppHelper.mobileRoot)) {
			root = com.jd.util.AppHelper.mobileRoot;
			com.jd.util.AppHelper.localFile.rootDir = root;
			com.jd.util.AppHelper.localFile.currentDir = path;
		} else if (path.startsWith(com.jd.util.AppHelper.sdRoot)) {
			root = com.jd.util.AppHelper.sdRoot;
			com.jd.util.AppHelper.localFile.rootDir = root;
			com.jd.util.AppHelper.localFile.currentDir = path;
		} else {
			try {
				List<com.jd.dal.Neighbor> neighbors = new com.jd.dal.NeighborDao(
						this).GetAllNeighbors();
				for (Neighbor neighbor : neighbors) {
					if (path.contains(neighbor.getServerName())) {
						int pos = path.indexOf(neighbor.getServerName());

						root = path.substring(0, pos
								+ neighbor.getServerName().length() - 1);
						break;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			com.jd.util.AppHelper.neighborFile.rootDir = root + "/";
			com.jd.util.AppHelper.neighborFile.currentDir = path;
		}

		if (root == null) {
			return 0;
		}

		while (!root.equals(path)) {
			int pos = path.lastIndexOf("/");
			path = path.substring(0, pos);
			count++;
		}

		return count;
	}

	protected void setMenuStatus() {

		if (AlbumFile.class.isInstance(MyBaseActivity.this)) {
			com.jd.util.AppHelper.fileMainTain.setMenuStatus(Boolean.TRUE);
		} else {
			if (level > 0) {
				com.jd.util.AppHelper.fileMainTain.setMenuStatus(Boolean.TRUE);
			} else {
				com.jd.util.AppHelper.fileMainTain.setMenuStatus(Boolean.FALSE);
			}
		}

		com.jd.util.AppHelper.fileMainTain.showMenu();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (AlbumFile.class.isInstance(MyBaseActivity.this) && level == 1) {
				closeDialog();
			} else {
				if (currentDir != null) {
					if (!currentDir.equals(rootDir)) {
						int pos = -1;
						if (currentDir.endsWith("/")) {
							currentDir = currentDir.substring(0,
									currentDir.length() - 1);
							pos = currentDir.lastIndexOf("/");
							currentDir = currentDir.substring(0, pos) + "/";
						} else {
							pos = currentDir.lastIndexOf("/");
							currentDir = currentDir.substring(0, pos);
						}

						if (LocalFile.class.isInstance(this)
								|| AlbumFile.class.isInstance(this)) {
							com.jd.adp.FoldListAdapter adapter = new com.jd.adp.FoldListAdapter(
									this, currentDir,
									com.jd.util.AppHelper.scale, rootDir);
							lst.setAdapter(adapter);
						} else {
							com.jd.adp.NeighborFoldListAdapter adapter = new com.jd.adp.NeighborFoldListAdapter(
									this, currentDir,
									com.jd.util.AppHelper.scale, rootDir);
							lst.setAdapter(adapter);

						}

						level--;
			
					} else {
						bindList();
						rootDir = null;
						currentDir = null;
						level = 0;
						setMenuStatus();
					}
				} else {
					closeDialog();
				}
			}
		}

		return true;
	}

	public void bindList() {

	}

	public void refreshList() {
		if (LocalFile.class.isInstance(this)
				|| AlbumFile.class.isInstance(this)) {
			com.jd.adp.FoldListAdapter adapter = new com.jd.adp.FoldListAdapter(
					this, currentDir, com.jd.util.AppHelper.scale, rootDir);
			lst.setAdapter(adapter);
		} else if (NeighborFile.class.isInstance(this)) {
			com.jd.adp.NeighborFoldListAdapter adapter = new com.jd.adp.NeighborFoldListAdapter(
					this, currentDir, com.jd.util.AppHelper.scale, rootDir);
			lst.setAdapter(adapter);
		} else {
			bindList();
		}
	}

	class MyHandler extends Handler {
		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			int itemPos = msg.arg1;
			View item = (View) msg.obj;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = false;
			Bitmap img = null;
			ImageView imgView = (ImageView) item
					.findViewById(com.tl.pic.brow.R.id.imgSel);

			if (NeighborFile.class.isInstance(MyBaseActivity.this)) {
				com.jd.adp.NeighborFoldListAdapter adapter = (com.jd.adp.NeighborFoldListAdapter) lst
						.getAdapter();
				adapter.holders.get(itemPos).selected = !adapter.holders
						.get(itemPos).selected;

				if (adapter.holders.get(itemPos).selected) {
					img = BitmapFactory.decodeResource(
							MyBaseActivity.this.getResources(),
							com.tl.pic.brow.R.drawable.check, bitmapOptions);
				} else {
					// img = BitmapFactory.decodeResource(
					// MyBaseActivity.this.getResources(),
					// com.tl.pic.brow.R.drawable.uncheck, bitmapOptions);
					img = null;
				}
			} else {
				com.jd.adp.FoldListAdapter adapter = (com.jd.adp.FoldListAdapter) lst
						.getAdapter();
				adapter.holders.get(itemPos).selected = !adapter.holders
						.get(itemPos).selected;
				if (adapter.holders.get(itemPos).selected) {
					img = BitmapFactory.decodeResource(
							MyBaseActivity.this.getResources(),
							com.tl.pic.brow.R.drawable.check, bitmapOptions);
				} else {
					// img = BitmapFactory.decodeResource(
					// MyBaseActivity.this.getResources(),
					// com.tl.pic.brow.R.drawable.uncheck, bitmapOptions);
					img = null;
				}
			}

			imgView.setImageBitmap(img);
		}
	}

	/**
	 * ��������������ھӵ�������
	 * 
	 * @return
	 */
	private void checkCon(String url) {
		final String fUrl = url;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (com.jd.util.NeighborUtil.TestConnection(fUrl)) {
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
			thread.join();// ���̵߳ȴ����߳�ִ�����
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
