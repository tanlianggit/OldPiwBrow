package com.jd.ui;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class GalleryAni extends BaseActivity {
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private float minScaleR;// 最小缩放比例
	private static float MAX_SCALE = 100.0f;// 最大缩放比例
	private ImageView imgView;
	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private com.jd.adp.ImageAdapter adapter;
	private ImageView curV;
	private DisplayMetrics dm;
	private int oWidth, oHeight;
	private boolean showToast = false;
	private AlertDialog.Builder dlg = null;
	private boolean seqMode = false;
	private boolean ranMode = false;
	private boolean scaleMode = false;
	private Timer timer;
	private TimerTask task;
	private com.jd.adp.GalleryFlow galleryFlow = null;
	private Handler handler = null;
	private Handler handlerScale = null;
	private boolean scaleDirection = true;
	private float scaleAni = 1.0f;
	private PointF point = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.tl.pic.brow.R.layout.galleryani);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Bundle bundle = this.getIntent().getExtras();
		String path = bundle.getString("path");
		String file = bundle.getString("file");

		try {
			adapter = new com.jd.adp.ImageAdapter(this, path, dm);
		} catch (Exception e) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("获取图片出错！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									GalleryAni.this.finish();
								}
							});

			dlg.show();
			return;
		}

		galleryFlow = (com.jd.adp.GalleryFlow) this
				.findViewById(com.tl.pic.brow.R.id.gl);
		galleryFlow.setFadingEdgeLength(0);
		galleryFlow.setSpacing(100); // 图片之间的间距
		galleryFlow.setAdapter(adapter);
		int pos = adapter.getPos(file);
		galleryFlow.setSelection(pos);

		galleryFlow.setOnTouchListener(galTouch);

		ImageView imgSeq = (ImageView) findViewById(com.tl.pic.brow.R.id.imgSeq);
		imgSeq.setOnClickListener(new OnClickListener() {
			SharedPreferences sp = GalleryAni.this.getSharedPreferences(
					"Settings", MODE_PRIVATE);
			int seq = sp.getInt("Sequent", 1);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!seqMode) {
					ranMode = false;
					scaleMode = false;
					if (timer != null) {
						task.cancel();
						timer.cancel();
					}

					timer = new Timer();
					task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							int pos = adapter.getPos();
							pos++;
							pos = pos % adapter.getCount();

							Message msg = new Message();
							msg.arg1 = pos;
							handler.sendMessage(msg);
						}
					};
					timer.schedule(task, 100, seq * 1000);
				} else {
					if (timer != null) {
						task.cancel();
						timer.cancel();
						timer = null;
					}
				}

				seqMode = !seqMode;
			}
		});

		ImageView imgRan = (ImageView) findViewById(com.tl.pic.brow.R.id.imgRandom);
		imgRan.setOnClickListener(new OnClickListener() {
			SharedPreferences sp = GalleryAni.this.getSharedPreferences(
					"Settings", MODE_PRIVATE);
			int ran = sp.getInt("Random", 1);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!ranMode) {
					seqMode = false;
					scaleMode = false;
					if (timer != null) {
						task.cancel();
						timer.cancel();
					}

					timer = new Timer();
					task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							int pos = new java.util.Random().nextInt(adapter
									.getCount());
							Message msg = new Message();
							msg.arg1 = pos;
							handler.sendMessage(msg);
						}
					};

					timer.schedule(task, 100, ran * 1000);
				} else {
					if (timer != null) {
						task.cancel();
						timer.cancel();
						timer = null;
					}
				}

				ranMode = !ranMode;
			}
		});

		ImageView imgScale = (ImageView) findViewById(com.tl.pic.brow.R.id.imgScale);
		imgScale.setOnClickListener(new OnClickListener() {
			SharedPreferences sp = GalleryAni.this.getSharedPreferences(
					"Settings", MODE_PRIVATE);
			int tm = sp.getInt("Timer", 2);
			int duration = sp.getInt("Duration", 2);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!scaleMode) {
					seqMode = false;
					ranMode = false;
					if (timer != null) {
						task.cancel();
						timer.cancel();
					}

					timer = new Timer();
					task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (scaleDirection) {
								scaleAni += tm / 50.0;
								if (scaleAni > tm) {
									scaleDirection = false;
								}
							} else {
								scaleAni -= tm / 50.0;
								if (scaleAni < 1.0) {
									scaleDirection = true;
								}
							}

							Message msg = new Message();
							msg.obj = scaleAni;
							handlerScale.sendMessage(msg);
						}
					};

					timer.schedule(task, 100, duration * 1000 / 50);
				} else {
					if (timer != null) {
						point = null;
						task.cancel();
						timer.cancel();
						timer = null;
					}
				}

				scaleMode = !scaleMode;
			}
		});

		ImageView imgWall = (ImageView) findViewById(com.tl.pic.brow.R.id.imgWallper);

		imgWall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	
				ImageView imgV=(ImageView)galleryFlow.getSelectedView();
				 imgV.setDrawingCacheEnabled(true);
				WallpaperManager wallpaperManager = WallpaperManager
						.getInstance(GalleryAni.this);
				
				try {
					Bitmap bitMap=imgV.getDrawingCache();
					
					if (bitMap != null) {
						Bitmap imgNew = Bitmap.createScaledBitmap(bitMap,
								wallpaperManager.getDesiredMinimumWidth()/2,wallpaperManager.getDesiredMinimumHeight(), false);
						//Bitmap imgNew = Bitmap.createScaledBitmap(bitMap,960,800, false);

						Bitmap img=Bitmap.createBitmap(wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight(), Bitmap.Config.RGB_565);
						Canvas canvas = new Canvas(img);
						Paint paint = new Paint();
						SharedPreferences sp=GalleryAni.this.getSharedPreferences("Settings",MODE_PRIVATE);
						int offset=sp.getInt("Offset", 0);
						canvas.drawBitmap(imgNew, wallpaperManager.getDesiredMinimumWidth()*offset/100, 0, paint);
						wallpaperManager.setBitmap(img);
						com.jd.util.AppHelper.showInfoDlg(GalleryAni.this,
								"已将图片设置为壁纸！");
					} else {
						com.jd.util.AppHelper.showInfoDlg(GalleryAni.this,
								"将图片设置为壁纸操作失败！");
					}
					
					System.gc();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					com.jd.util.AppHelper.showInfoDlg(GalleryAni.this,
							"将图片设置为壁纸操作失败！");
				}

			}
		});

		ImageView imgPin = (ImageView) findViewById(com.tl.pic.brow.R.id.imgPin);
		imgPin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GalleryAni.this,
						SpellPic.class);
				Bundle bundle = new Bundle();
				bundle.putString("fileName", adapter.getImagePath(galleryFlow.getSelectedItemPosition()));
				intent.putExtras(bundle);
				GalleryAni.this.startActivity(intent);
			}

		});
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				galleryFlow.setSelection(msg.arg1);
			}

		};

		handlerScale = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				float scale = Float.parseFloat(msg.obj.toString());
				scaleImage(scale);
			}
		};
	}

	OnTouchListener galTouch = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			try {
				com.jd.adp.GalleryFlow ga = (com.jd.adp.GalleryFlow) v;
				ImageView view = null;

				view = (ImageView) ga.getSelectedView();

				if (view != curV) {// 切换图片的时候参数复原
					curV = view;
					imgView = view;
					oWidth = Integer.parseInt(imgView.getTag(
							com.tl.pic.brow.R.integer.imgwidth).toString());
					oHeight = Integer.parseInt(imgView.getTag(
							com.tl.pic.brow.R.integer.imgheight).toString());
					matrix = (Matrix) view.getTag();
					mode = NONE;
					savedMatrix = new Matrix();
					start = new PointF();
					mid = new PointF();
					point = null;
					return false;
				}

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
					mode = NONE;
					float[] vs = new float[9];
					matrix.getValues(vs);

					RectF rectO = new RectF(0, 0, oWidth, oHeight);
					matrix.mapRect(rectO);
					point = new PointF();
					point.set((event.getX() - rectO.left) / vs[0],
							(event.getY() - rectO.top) / vs[4]);
					break;
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						Matrix m = new Matrix();
						m.set(matrix);
						RectF rect = new RectF(0, 0, oWidth, oHeight);
						m.mapRect(rect);

						if (event.getX() < start.x
								&& rect.right > dm.widthPixels + 2
								|| event.getX() > start.x && rect.left < -2
								|| event.getY() > start.y && rect.top < -2
								|| event.getY() < start.y
								&& rect.bottom > dm.heightPixels + 2)// 移动不切换
						{
							matrix.set(savedMatrix);
							matrix.postTranslate(event.getX() - start.x,
									event.getY() - start.y);
							view.setTag(matrix);

							checkView();
							view.setImageMatrix(matrix);
							view.setTag(matrix);
							return true;
						} else {
							if (!showToast) {
								if (adapter.getPos() == 0
										&& event.getX() >= start.x) {
									dlg = com.jd.util.AppHelper.showInfoDlg(
											GalleryAni.this, "已经是第一张图片");
								} else if (adapter.getPos() == (adapter
										.getCount() - 1)
										&& event.getX() <= start.x) {
									dlg = com.jd.util.AppHelper.showInfoDlg(
											GalleryAni.this, "已经是最后一张图片");
								}

								showToast = true;
								final Timer timer = new Timer();
								TimerTask task = new TimerTask() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										if (showToast) {
											showToast = false;
											timer.cancel();
										}
									}
								};

								timer.schedule(task, 1000, 1000);
							}
						}
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);

							checkView();
							view.setImageMatrix(matrix);
							view.setTag(matrix);
						}
					}
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				com.jd.util.AppHelper.showInfoDlg(GalleryAni.this, "获取图片出错！");
				return false;
			}

			return false;
		}

		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}

		public void checkView() {
			float p[] = new float[9];
			matrix.getValues(p);
			if (mode == ZOOM) {
				if (p[0] < minScaleR) {
					matrix.setScale(minScaleR, minScaleR);
				}
				if (p[0] > MAX_SCALE) {
					matrix.set(savedMatrix);
				}
			}
			center();
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

			RectF rect = new RectF(0, 0, oWidth, oHeight);
			m.mapRect(rect);
			float height = rect.height();
			float width = rect.width();

			float deltaX = 0, deltaY = 0;

			if (vertical) {
				// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下放留空则往下移
				int screenHeight = dm.heightPixels;
				if (height < screenHeight) {
					deltaY = (screenHeight - height) / 2 - rect.top;
				} else if (rect.top > 0) {
					deltaY = -rect.top;
				} else if (rect.bottom < screenHeight) {
					deltaY = (imgView.getHeight() - rect.bottom);
				}
			}

			if (horizontal) {
				int screenWidth = dm.widthPixels;
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
	};

	private void scaleImage(float scale) {
		ImageView view = (ImageView) galleryFlow.getSelectedView();
		matrix = new Matrix();
		// matrix = (Matrix) view.getTag();

		if (point == null) {
			Matrix m = (Matrix) view.getTag();
			float[] vs = new float[9];
			m.getValues(vs);
			RectF rectO = new RectF(0, 0, oWidth, oHeight);
			m.mapRect(rectO);
			point = new PointF();
			point.set((dm.widthPixels / 2 - rectO.left) / vs[0],
					(dm.heightPixels / 2 - rectO.top) / vs[4]);
		}

		matrix.postScale(scale, scale);
		RectF rect = new RectF(0, 0, point.x, point.y);
		matrix.mapRect(rect);
		float height = rect.bottom;
		float width = rect.right;
		float deltaX = 0, deltaY = 0;

		deltaX = (dm.widthPixels / 2 - width);
		deltaY = (dm.heightPixels / 2 - height);
		matrix.postTranslate(deltaX, deltaY);
		view.setImageMatrix(matrix);
	}
}
