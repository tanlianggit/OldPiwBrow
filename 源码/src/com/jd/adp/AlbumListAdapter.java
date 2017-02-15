package com.jd.adp;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;

public class AlbumListAdapter extends MyBaseAdapter {

	private List<String> folders = null;
	private Context context = null;
	public String path = null;
	private LayoutInflater flater;
	private float scale;
	public List<AlbumItemHolder> holders;

	public AlbumListAdapter(Context context, String path, float scale,
			String rootPath) {
		this.context = context;
		this.path = path;
		folders = new ArrayList<String>();
		holders = new ArrayList<AlbumItemHolder>();
		this.scale = scale;

		flater = LayoutInflater.from(context);

		// ������������ļ��У���Ӹ��ļ�������Ϊ����
		if (!path.equals(rootPath)) {
			folders.add(path);
		}

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

		AlbumItemHolder view = null;
		boolean mark = false;

		convertView = flater.inflate(com.tl.pic.brow.R.layout.albumitem, null);
		view = new AlbumItemHolder();
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
		view.forward = (ImageView) convertView
				.findViewById(com.tl.pic.brow.R.id.imgForward);

		if (mark) {
			File file = new File(com.jd.util.StringUtil.encryptStr(folders.get(position)));
			if (file.isDirectory()) {
				view.path = folders.get(position);
				view.folder.setImageBitmap(getFolderImage(position, true));
				view.title.setText(getFolderTitle(position));
				view.info.setText(getFolderInfo(position, true));
				view.isFolder = true;
			} else {
				view.path = file.getPath();
				view.folder.setImageBitmap(getFolderImage(position, false));
				view.title.setText(getFolderTitle(position));
				view.info.setText(getFolderInfo(position, false));
				view.isFolder = false;
			}

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = false;
		} else {
			AlbumItemHolder holder = holders.get(position);
			view.path = folders.get(position);
			view.folder.setImageDrawable(holder.folder.getDrawable());
			view.title.setText(holder.title.getText().toString());
			view.info.setText(holder.info.getText().toString());
			view.isFolder = holder.isFolder;
			view.selected = holder.selected;
		}

		convertView.setTag(view);
//		if(position==this.selectedItem){
//			convertView.setBackgroundColor(Color.LTGRAY);  
//		}else{
//			convertView.setBackgroundColor(Color.WHITE);  
//		}
		return convertView;
	}

	private void getFolders(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()  && !file.getName().startsWith(".")) {
				folders.add(com.jd.util.StringUtil.descryptStr(file.getPath()));
			}
		}
	}

	private void getFiles(File folder) {
		for (File file : folder.listFiles()) {
			String fileName = com.jd.util.StringUtil
					.descryptStr(file.getName());
			if (file.isFile()  && !file.getName().startsWith(".") && com.jd.util.AppHelper.isImageFile(fileName)) {
				folders.add(fileName);
			}
		}
	}

	private String getFolderTitle(int position) {
		String ret = folders.get(position);
		int pos = ret.lastIndexOf("/");
		if (position == 0 && folders.get(position).equals(this.path)) {
			ret = "�����ϼ����";
			return ret;
		}

		if (pos > 0) {
			ret = ret.substring(pos + 1, ret.length());
		}

		return ret;
	}

	private String getFolderInfo(int position, boolean isFolder) {

		if (position == 0 && folders.get(position).equals(this.path)) {
			return "";

		} else {
			File dir = new File(folders.get(position));
			if (!dir.exists()) {
				return "0����Ƭ";
			}

			if (isFolder) {

				int fileCnt = 0;
				int folderCnt = 0;

				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						if (com.jd.util.AppHelper.isImageFile(file.getName())) {
							fileCnt++;
						}
					} else {
						folderCnt++;
					}
				}

				return folderCnt + "����ᣬ" + fileCnt + "����Ƭ";
			} else {
				return "";
			}
		}
	}

	/**
	 * ��ȡָ���ļ����µĵ�һ��ͼƬ��������ļ�����û��ͼƬ����ͳһ����folder.png
	 * 
	 * @param pos
	 * @return
	 */
	private Bitmap getFolderImage(int pos, boolean isFolder) {
		String fileName = null;
		Bitmap img = null;
		Bitmap resizeBmp = null;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.outWidth = com.jd.util.AppHelper.dip2px(scale, 60);
		bitmapOptions.outHeight = com.jd.util.AppHelper.dip2px(scale, 60);
		bitmapOptions.inJustDecodeBounds = false;

		if (isFolder) {
			String path = folders.get(pos);
			File dir = new File(path);

			for (File file : dir.listFiles()) {
				String realName = com.jd.util.StringUtil.descryptStr(file
						.getName());
				if (file.isFile()
						&& com.jd.util.AppHelper.isImageFile(realName)) {
					fileName = file.getPath();
					break;
				}
			}

			if (fileName != null) {
				byte[] data = com.jd.util.CryptoTools
						.getFileBytes(fileName);

				if (data != null) {
					img = BitmapFactory.decodeByteArray(data, 0, data.length,
							bitmapOptions);
					data = null;
				} else {
					img = BitmapFactory.decodeResource(context.getResources(),
							com.tl.pic.brow.R.drawable.folder, bitmapOptions);
				}
			} else {
				img = BitmapFactory.decodeResource(context.getResources(),
						com.tl.pic.brow.R.drawable.folder, bitmapOptions);
			}

		} else {
			fileName = com.jd.util.StringUtil.encryptStr(folders.get(pos));
			byte[] data = com.jd.util.CryptoTools.getFileBytes(fileName);

			if (data != null) {
				img = BitmapFactory.decodeByteArray(data, 0, data.length,
						bitmapOptions);
				data = null;
			} else {
				img = BitmapFactory.decodeResource(context.getResources(),
						com.tl.pic.brow.R.drawable.folder, bitmapOptions);
			}

		}

		System.gc();
		Matrix matrix = new Matrix();
		float sw = com.jd.util.AppHelper.dip2px(scale, 60) * 1.0f
				/ img.getWidth();
		float sh = com.jd.util.AppHelper.dip2px(scale, 60) * 1.0f
				/ img.getHeight();

		matrix.postScale(sw, sh); // ���Ϳ�Ŵ���С�ı���
		resizeBmp = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
				img.getHeight(), matrix, true);
		return resizeBmp;
	}

}
