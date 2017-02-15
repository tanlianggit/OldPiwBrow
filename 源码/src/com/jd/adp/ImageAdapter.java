package com.jd.adp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private List<String> mimages = new ArrayList<String>();
	private DisplayMetrics mdm;
	float minScaleR;// 最小缩放比例
	Bitmap bitmap;
	ImageView imgView;
	int mpos = 0;
	private Matrix matrix = new Matrix();

	public ImageAdapter(Context c, String path, DisplayMetrics dm) throws Exception{
		mContext = c;
		mdm = dm;

		if (path.startsWith("smb://")) {
			try {
				SmbFile dir = new SmbFile(path);
				if (dir.exists()) {
					String[] files = dir.list();

					for (String file : files) {
						try {
							String realName = com.jd.util.StringUtil.decrypt(file);
							if (realName == null) {
								realName = file;
							}
							if (com.jd.util.AppHelper.isImageFile(realName)) {
								mimages.add(path + "/" + file);
							}
						} catch (Exception e) {
							Log.v("ImageAdapter", e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				throw new Exception();
			}
			
		} else {
			File dir = new File(path);
			if (dir.exists()) {
				String[] files = dir.list();

				for (String file : files) {
					try {
						String realName = com.jd.util.StringUtil.decrypt(file);
						if (realName == null) {
							realName = file;
						}
						if (com.jd.util.AppHelper.isImageFile(realName)) {
							mimages.add(path + "/" + file);
						}
					} catch (Exception e) {
						Log.v("ImageAdapter", e.getMessage());
					}
				}
				
				if(mimages.size()==0){
					throw new Exception();
				}
			}
		}

	}

	public ImageView getImage(int pos) {
		try {
			// 返回原图解码之后的bitmap对象
			bitmap = getImg(pos);
			imgView = new ImageView(mContext);
			imgView.setImageBitmap(bitmap);
			// 设置imageView大小 ，也就是最终显示的图片大小
			imgView.setLayoutParams(new GalleryFlow.LayoutParams(
					mdm.widthPixels, mdm.heightPixels));
			imgView.setScaleType(ScaleType.MATRIX);

			minZoom();
			center();
			imgView.setImageMatrix(matrix);
			imgView.setTag(matrix);
			imgView.setTag(com.tl.pic.brow.R.integer.imgwidth,
					bitmap.getWidth());
			imgView.setTag(com.tl.pic.brow.R.integer.imgheight,
					bitmap.getHeight());

			System.gc();	
		} catch (Exception e) {
			// TODO: handle exception
			if (e.getMessage() != null) {
				Log.v("GetImg", e.getMessage());
			}

			imgView = new ImageView(mContext);
		}
		
		return imgView;

	}

	private Bitmap getImg(int pos) {
		Bitmap img=null;
		try {
			mpos = pos;
			byte[] data = com.jd.util.CryptoTools.getFileBytes(mimages.get(pos));
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			img = BitmapFactory.decodeByteArray(data, 0, data.length,
					bitmapOptions);
			int scale = (int) (bitmapOptions.outWidth * 1.0f / mdm.widthPixels);
			if (scale < 0)
				scale = 1;
			if (scale > 10)
				scale = 10;

			bitmapOptions.inJustDecodeBounds = false;
			bitmapOptions.inSampleSize = scale;
			img = BitmapFactory
					.decodeByteArray(data, 0, data.length, bitmapOptions);
			data = null;
			System.gc();
		} catch (Exception e) {
			// TODO: handle exception
			//img=Bitmap.createBitmap(600, 800, Bitmap.Config.ALPHA_8);
		}

		if(img==null){
			img=Bitmap.createBitmap(600, 800, Bitmap.Config.ALPHA_8);
		}
		return img;
	}
	
	public Bitmap getOriImg(int pos) {
		Bitmap img=null;
		try {
			mpos = pos;
			byte[] data = com.jd.util.CryptoTools.getFileBytes(mimages.get(pos));
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = false;
			img = BitmapFactory.decodeByteArray(data, 0, data.length,
					bitmapOptions);
			
			System.gc();
		} catch (Exception e) {
			// TODO: handle exception
			//img=Bitmap.createBitmap(600, 800, Bitmap.Config.ALPHA_8);
		}

		if(img==null){
			img=Bitmap.createBitmap(600, 800, Bitmap.Config.ALPHA_8);
		}
		return img;
	}

	@SuppressWarnings("unused")
	private Resources getResources() {
		return null;
	}

	public int getCount() {
		return mimages.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public int getPos(String file) {
		for (int i = 0; i < mimages.size(); i++) {
			if (mimages.get(i).equals(file)) {
				return i;
			}
		}

		return -1;
	}

	public String getImagePath(int pos)
	{
		return mimages.get(pos);
	}
	
	public int getPos() {
		return mpos;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		return getImage(position);
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		float sx = (float) mdm.widthPixels / (float) bitmap.getWidth();
		float sy = (float) mdm.heightPixels / (float) bitmap.getHeight();

		minScaleR = Math.min(sx, sy);
		matrix = new Matrix();
		matrix.postScale(minScaleR, minScaleR);
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下放留空则往下移
			int screenHeight = mdm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = imgView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = mdm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);

	}
}