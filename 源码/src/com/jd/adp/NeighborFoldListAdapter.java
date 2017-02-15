package com.jd.adp;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.*;
import jcifs.smb.SmbFile;

public class NeighborFoldListAdapter extends MyBaseAdapter {

	private List<String> folders = null;
	// private boolean mode = false;
	private Context context = null;
	public String path = null;
	private LayoutInflater flater = null;
	private float scale = 0;
	public List<FolderItemHolder> holders = null;
	private List<Boolean> marks = null;
	Thread threadFolder, threadFile, threadInfo;
	private String folderInfo = "";

	public NeighborFoldListAdapter(Context context, String path, float scale,
			String rootPath) {
		this.context = context;
		this.path = path;
		folders = new ArrayList<String>();
		holders = new ArrayList<FolderItemHolder>();
		marks = new ArrayList<Boolean>();
		this.scale = scale;

		flater = LayoutInflater.from(context);
		//java.io.File file = new File(path);
		getFolders(path);
		try {
			threadFolder.join();
		} catch (Exception e) {
			// TODO: handle exception
		}
		getFiles(path);

		try {
			threadFile.join();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return folders.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return holders.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public String getParentPath() {
		return path;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		FolderItemHolder view = null;
		boolean mark = false;

		convertView = flater.inflate(com.tl.pic.brow.R.layout.foldlistitem,
				null);
		view = new FolderItemHolder();
		if (position >= holders.size()) {
			holders.add(view);
			mark = true;
		} else {
			mark = false;
		}

		view.folder = (ImageView) convertView
				.findViewById(com.tl.pic.brow.R.id.imgFolder);
		view.title = (TextView) convertView
				.findViewById(com.tl.pic.brow.R.id.txtTitle);
		view.info = (TextView) convertView
				.findViewById(com.tl.pic.brow.R.id.txtInfo);
		view.sel = (ImageView) convertView
				.findViewById(com.tl.pic.brow.R.id.imgSel);

		if (mark) {
			view.path = folders.get(position);
			view.folder.setImageBitmap(getImage(position,marks.get(position)));
			view.title.setText(getFolderTitle(position));
			getFolderInfo(position, marks.get(position));
			try {
				threadInfo.join();
			} catch (Exception e) {
				// TODO: handle exception
				com.jd.util.AppHelper.showInfoDlg(context, e.getMessage());
			}

			view.info.setText(folderInfo);
			view.isFolder = marks.get(position);

//			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//			bitmapOptions.inJustDecodeBounds = false;
//
//			Bitmap img = BitmapFactory.decodeResource(context.getResources(),
//					com.tl.pic.brow.R.drawable.uncheck, bitmapOptions);
//			view.sel.setImageBitmap(img);

		} else {
			FolderItemHolder holder = holders.get(position);
			view.path = folders.get(position);
			view.folder.setImageDrawable(holder.folder.getDrawable());
			view.title.setText(holder.title.getText().toString());
			view.info.setText(holder.info.getText().toString());
			view.isFolder = holder.isFolder;
			view.selected = holder.selected;
			//view.sel.setImageDrawable(holder.sel.getDrawable());
		}

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = false;
		if(view.selected){
			Bitmap img = BitmapFactory.decodeResource(context.getResources(),
					com.tl.pic.brow.R.drawable.check, bitmapOptions);
			view.sel.setImageBitmap(img);
		}else{
			//Bitmap img = BitmapFactory.decodeResource(context.getResources(),
					//com.tl.pic.brow.R.drawable.uncheck, bitmapOptions);
			//view.sel.setImageBitmap(img);
			view.sel.setImageBitmap(null);
		}

		convertView.setTag(view);
//		if(position==this.selectedItem){
//			convertView.setBackgroundColor(Color.LTGRAY);  
//		}else{
//			convertView.setBackgroundColor(Color.WHITE);  
//		}
		return convertView;
	}

	private void getFolders(String path) {
		final String neighborPath = path;
		threadFolder = new Thread() {
			@Override
			public void run() {
				try {
					SmbFile dir;
					String path1 = neighborPath;

					if (!path1.startsWith("smb://")) {
						path1 = "smb://" + path1;
					}
					if (!path1.endsWith("/")) {
						path1 = path1 + "/";
					}
					dir = new SmbFile(path1);

					for (SmbFile sub : dir.listFiles()) {
						if (sub.isDirectory()) {
							folders.add(sub.getPath());
							marks.add(true);
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(context, e.getMessage());
				}

			}
		};
		threadFolder.start();
	}

	private void getFiles(String path) {
		final String neighborPath = path;
		threadFile = new Thread() {
			@Override
			public void run() {
				try {
					SmbFile dir;
					String path1 = neighborPath;

					if (!path1.startsWith("smb://")) {
						path1 = "smb://" + path1;
					}
					if (!path1.endsWith("/")) {
						path1 = path1 + "/";
					}
					
					dir = new SmbFile(path1);

					for (SmbFile sub : dir.listFiles()) {
						if (sub.isFile()) {
							folders.add(sub.getPath());
							marks.add(false);
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
					com.jd.util.AppHelper.showInfoDlg(context, e.getMessage());
				}
			}
		};
		threadFile.start();

	}

	private String getFolderTitle(int position) {
		String ret = folders.get(position);
		if(ret.endsWith("/")){
			ret=ret.substring(0,ret.length()-1);
		}
		
		int pos = ret.lastIndexOf("/");
		if (pos > 0) {
			ret = ret.substring(pos + 1, ret.length());
		}
		
		String deRet=com.jd.util.StringUtil.decrypt(ret);
		if(deRet!=null){
			return "[密]"+deRet;
		}else{
			return ret;
		}
	}

	private void getFolderInfo(int position, boolean isFolder) {
		final int pos = position;
		folderInfo = "";

		threadInfo = new Thread() {
			@Override
			public void run() {
				if (pos == 0 && folders.get(pos).equals(path)) {
					folderInfo = "";
				} else {
					try {
						SmbFile dir;
						String path1 = folders.get(pos);

						if (!path1.startsWith("smb://")) {
							path1 = "smb://" + path1;
						}
						if (!path1.endsWith("/")) {
							path1 = path1 + "/";
						}

						dir = new SmbFile(path1);

						if (!dir.exists()) {
							folderInfo = "0个文件";
						}

						if (marks.get(pos)) {

							int fileCnt = 0;
							int folderCnt = 0;

							for (SmbFile file : dir.listFiles()) {
								if (file.isFile()) {
									fileCnt++;
								} else {
									folderCnt++;
								}
							}

							folderInfo = folderCnt + "个文件夹，" + fileCnt + "个文件";
						} else {
							folderInfo = "";
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};

		threadInfo.start();

	}

	/**
	 * 根据是文件还是文件夹来显示不同的图片
	 * 
	 * @param pos
	 * @return
	 */
	private Bitmap getImage(int pos,boolean isFolder) {
		Bitmap img = null;
		Bitmap resizeBmp = null;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.outWidth = com.jd.util.AppHelper.dip2px(scale, 60);
		bitmapOptions.outHeight = com.jd.util.AppHelper.dip2px(scale, 60);
		bitmapOptions.inJustDecodeBounds = false;

		if (isFolder) {
			
			img = BitmapFactory.decodeResource(context.getResources(),
					com.tl.pic.brow.R.drawable.folder, bitmapOptions);
		} else {
			img = BitmapFactory.decodeResource(context.getResources(),
					com.tl.pic.brow.R.drawable.file, bitmapOptions);
		}

		System.gc();
		Matrix matrix = new Matrix();
		float sw = com.jd.util.AppHelper.dip2px(scale, 60) * 1.0f
				/ img.getWidth();
		float sh = com.jd.util.AppHelper.dip2px(scale, 60) * 1.0f
				/ img.getHeight();

		matrix.postScale(sw, sh); // 长和宽放大缩小的比例
		resizeBmp = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
				img.getHeight(), matrix, true);
		return resizeBmp;
	}

}
