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

public class FoldListAdapter extends MyBaseAdapter {

	private List<String> folders = null;
	private boolean mode = false;
	private Context context = null;
	public String path = null;
	private LayoutInflater flater;
	private float scale;
	public List<FolderItemHolder> holders;

	public FoldListAdapter(Context context, String path, float scale,String rootPath) {
		this.context = context;
		this.path = path;
		folders = new ArrayList<String>();
		holders = new ArrayList<FolderItemHolder>();
		this.scale = scale;
		
		flater = LayoutInflater.from(context);
		java.io.File file = new File(path);
		getFolders(file);
		getFiles(file);
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
			File file = new File(folders.get(position));
			if (file.isDirectory()) {
				view.path = folders.get(position);
				view.folder.setImageBitmap(getImage(position, true));
				view.title.setText(getFolderTitle(position));
				view.info.setText(getFolderInfo(position, true));
				view.isFolder = true;
			} else {
				view.path = file.getPath();
				view.folder.setImageBitmap(getImage(position, false));
				view.title.setText(getFolderTitle(position));
				view.info.setText(getFolderInfo(position, false));
				view.isFolder = false;
			}

		} else {
			FolderItemHolder holder = holders.get(position);
			view.path = folders.get(position);
			view.folder.setImageDrawable(holder.folder.getDrawable());
			view.title.setText(holder.title.getText().toString());
			view.info.setText(holder.info.getText().toString());
			view.isFolder = holder.isFolder;
			view.selected = holder.selected;
			view.sel.setImageDrawable(holder.sel.getDrawable());
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

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
		this.notifyDataSetChanged();
	}

	private void getFolders(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory() && !file.getName().startsWith(".")) {
				String filter=com.jd.util.StringUtil.decrypt(file.getPath());
				if(filter==null){
					filter=file.getPath();
				}
				
				if(!filter.startsWith("com.jd.brow")){
					folders.add(file.getPath());
				}
			}
		}
	}

	private void getFiles(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isFile()  && !file.getName().startsWith(".")) {
				folders.add(file.getPath());
			}
		}
	}

	private String getFolderTitle(int position) {
		String ret = folders.get(position);
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

	private String getFolderInfo(int position, boolean isFolder) {

			File dir = new File(folders.get(position));
			if (!dir.exists()) {
				return "0个文件";
			}

			if (isFolder) {
				int fileCnt = 0;
				int folderCnt = 0;

				if(dir.listFiles()==null){
					return "";
				}
				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						fileCnt++;
				
					} else {
						folderCnt++;
					}
				}

				return folderCnt + "个文件夹，" + fileCnt + "个文件";
			} else {
				return "";
			}
		
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


	public void refresh() {
		folders = new ArrayList<String>();
		holders = new ArrayList<FolderItemHolder>();
		java.io.File file = new File(path);
		getFolders(file);
	}

}
