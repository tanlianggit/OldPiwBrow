package com.jd.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.baidu.mobads.AdView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SpellPic extends BaseActivity {
	Bitmap[][] imgArr;// 保存分割后的小图片的数组
	List<ImageView> ivs = new ArrayList<ImageView>();// 保存ImageView的集合
	boolean change = false;// 是否需要交换图片，用在图片的点击事件中
	ImageView imgPre = null;// 交换中先点击的图片
	ImageView imgAft = null;// 交换中后点击的图片
	private SoundPool sp;
	HashMap<Integer, Integer> map;// 保存音乐的集合
	int count = 3;// 获取图片划分为几行几列
	int picWidth = 0;// 图片的宽度
	int picHeight = 0;// 图片的宽度
	int clickCount = 0;// 交换次数
	int shortTime = 0;// 当前图片的最短时间
	int minClick = 0;// 当前图片的最小交换次数
	String fName = null;// 当前的图片文件名
	String exchangeMode;// 图片交换模式
	java.util.Date begDt;// 游戏开始时间
	com.jd.dal.Spell spell = null;// 游戏数据实体
	boolean playMusic = true;// 是否播放音乐
	// java.util.Date endDt;//游戏结束时间
	DisplayMetrics dm;
	private AdView adView;
	private boolean showingDlg=false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		setContentView(com.tl.pic.brow.R.layout.spell);

		fName = this.getIntent().getExtras().getString("fileName");

		
		TextView txtReplay = (TextView) findViewById(com.tl.pic.brow.R.id.txtReplay);
		txtReplay.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getImage();
			}

		});

		initSoundPool();
		adView = new AdView(this);
		LinearLayout llAds = (LinearLayout) findViewById(com.tl.pic.brow.R.id.llAds);
		llAds.addView(adView);
		//getImage();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			RelativeLayout rlSpell=(RelativeLayout)findViewById(com.tl.pic.brow.R.id.rlSpell);
			LayoutParams layoutParams = rlSpell.getLayoutParams();
			layoutParams.height = dm.heightPixels -  com.jd.util.AppHelper.dip2px(dm.scaledDensity, 50)-4;
			rlSpell.setLayoutParams(layoutParams);
			
			TableLayout tlSpell=(TableLayout)findViewById(com.tl.pic.brow.R.id.tlSpell);
			layoutParams = tlSpell.getLayoutParams();
			layoutParams.height = rlSpell.getHeight() - com.jd.util.AppHelper.dip2px(dm.scaledDensity, 100)-4;
			tlSpell.setLayoutParams(layoutParams);
			
			if(!showingDlg)
			{
				getImage();
			}
		}
	}

	/**
	 * 获取图片并且进行分割
	 */
	private void getImage() {
		// 以下代码获取图片的宽度
		try {
		begDt = null;
		imgPre=null;
		imgAft=null;
		
		SharedPreferences sp = this.getSharedPreferences("Settings",
				MODE_PRIVATE);
		exchangeMode = sp.getString("ExchangeMode", "easy");// 获取交互模式
		playMusic = sp.getBoolean("PlayMusic", true);
		clickCount = 0;// 清除交换计数
		count = sp.getInt("Count", 3);// 获取图片划分为几行几列
		TextView txtInfo = (TextView) findViewById(com.tl.pic.brow.R.id.txtInfo);

		com.jd.dal.SpellDao dao = new com.jd.dal.SpellDao(this);
		if (exchangeMode.equals("easy")) {
			spell = dao.getSpellByfName(fName, 0, count);
		} else {
			spell = dao.getSpellByfName(fName, 1, count);
		}

		if (spell == null) {
			shortTime = 0;
			minClick = 0;
			String str = "移动：" + clickCount + "步<br/>时间：0秒"
					+ "<br/><br/>最短时间：暂无<br/>最少步数：暂无";

			txtInfo.setText(Html.fromHtml(str));
		} else {
			shortTime = spell.getShortTime();
			minClick = spell.getMinClick();
			String str = "移动：" + clickCount + "步<br/>时间：0秒" + "<br/><br/>最短："
					+ shortTime + "秒<br/>" + "最少：" + minClick + "步";
			txtInfo.setText(Html.fromHtml(str));
		}
		
		// 以下代码为了不同的格数而调整布局的宽度
		TableLayout lyImg = (TableLayout) findViewById(com.tl.pic.brow.R.id.tlSpell);
		lyImg.removeAllViews();
		ivs.clear();

		// 调整下部布局的宽度（预览，按钮区域）
		ImageView imgPreview = (ImageView) findViewById(com.tl.pic.brow.R.id.imgPreview);

		// 加载图片
		Bitmap img = null;

			byte[] data = com.jd.util.CryptoTools.getFileBytes(fName);
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = false;
			img = BitmapFactory.decodeByteArray(data, 0, data.length,
					bitmapOptions);
			data = null;
			System.gc();

		//计算图片的宽度和高度
		picWidth = lyImg.getWidth() / count;
		picHeight = (dm.heightPixels -  com.jd.util.AppHelper.dip2px(dm.scaledDensity, 150))/ count;
		// 分割图片
		imgArr = new Bitmap[count][count];
		Bitmap imgNew = Bitmap.createScaledBitmap(img, com.jd.util.AppHelper.dip2px(dm.scaledDensity, 100),
				com.jd.util.AppHelper.dip2px(dm.scaledDensity, 100), false);
		imgPreview.setImageBitmap(imgNew);
		imgNew = Bitmap.createScaledBitmap(img, picWidth * count,
				picHeight * count, false);
		img.recycle();
		imgPreview.setImageBitmap(imgNew);
		boolean[][] seled = new boolean[count][count];

		
			// 生成小图片
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					Bitmap imgS = Bitmap.createBitmap(imgNew, j * picWidth, i
							* picHeight, picWidth, picHeight);
					imgArr[i][j] = imgS;
					seled[i][j] = false;
				}
			}

			// 随机设置图片
			ImageView imgV = null;
			for (int i = 0; i < count; i++) {
				TableRow row = new TableRow(SpellPic.this);
				for (int j = 0; j < count; j++) {
					imgV = new ImageView(SpellPic.this);
					TableRow.LayoutParams params = new TableRow.LayoutParams(
							picWidth, picHeight);
					params.setMargins(1, 1, 1, 1);
					imgV.setScaleType(ScaleType.FIT_XY);
					imgV.setOnClickListener(img_click);

					int ii, jj;
					Random ran = new Random();
					do {
						ii = ran.nextInt(count);
						jj = ran.nextInt(count);
					} while (seled[ii][jj]);

					seled[ii][jj] = true;

					imgV.setTag(com.tl.pic.brow.R.integer.i, ii);
					imgV.setTag(com.tl.pic.brow.R.integer.j, jj);
					imgV.setImageBitmap(imgArr[ii][jj]);
					imgV.setTag(com.tl.pic.brow.R.integer.ImageViewNo, i
							* count + j + 1);

					row.addView(imgV, params);
					ivs.add(imgV);
				}

				lyImg.addView(row);
				lyImg.requestLayout();
			}
		} catch (Exception e) {
			AlertDialog.Builder dlg=new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("获取图片出错！")
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					SpellPic.this.finish();
				}
			});
			
			dlg.show();
		}
		
	}

	/**
	 * ImageView的点击事件 交换图片并且判断拼图是否完成
	 */
	private OnClickListener img_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 如果是第一次点击，则imgPre=v
			playSound(1);

			if (exchangeMode.equals("difficult")) {
				difMode(v);
			} else {
				easyMode(v);
			}
		}

	};

	/**
	 * 本方法完成随意模式的图片交换
	 * 
	 * @param v
	 */
	private void easyMode(View v) {
		if (!change) {
			imgPre = (ImageView) v;
			imgPre.setAlpha(100);
		} else {
			imgAft = (ImageView) v;
			imgAft.setAlpha(100);
			// 交换图片
			exchange();
			for (ImageView vv : ivs) {
				vv.setAlpha(255);
			}
			judge();// 判断拼图是否完成
		}
		change = !change;
	}

	/**
	 * 本方法完成严格模式的图片交换
	 * 
	 * @param v
	 */
	private void difMode(View v) {
		if(imgPre==null){
			imgPre = (ImageView) v;
			imgPre.setAlpha(100);
		}else{
			ImageView tmp=(ImageView) v;
			int preNo,aftNo;
			preNo=Integer.parseInt(imgPre.getTag(
					com.tl.pic.brow.R.integer.ImageViewNo).toString());
			
			aftNo=Integer.parseInt(tmp.getTag(
					com.tl.pic.brow.R.integer.ImageViewNo).toString());
			
			if(!(preNo==aftNo+1 || preNo==aftNo-1 || preNo==aftNo+count || preNo==aftNo-count))
			{
				//com.jd.util.AppHelper.showInfoDlg(this, "必须是相邻的图片才能交换！");
				return;
			}
			
			imgAft=tmp;
			exchange();
			for (ImageView vv : ivs) {
				vv.setAlpha(255);
			}
			judge();// 判断拼图是否完成
			imgPre=null;
			imgAft=null;
		}
	}

	private void exchange() {
		if (begDt == null) {
			begDt = new java.util.Date();
		}
		
		clickCount++;
		TextView txtInfo = (TextView) findViewById(com.tl.pic.brow.R.id.txtInfo);
		java.util.Date endDt = new java.util.Date();
		int playTime = (int) ((endDt.getTime() - begDt.getTime()) / 1000);
		if (shortTime == 0 || minClick == 0) {
			String str = "移动：" + clickCount + "步<br/>时间：" + playTime
					+ "秒<br/><br/>最短时间：暂无<br/>最少步数：暂无";
			txtInfo.setText(Html.fromHtml(str.toString()));
		} else {
			String str = "移动：" + clickCount + "步<br/>时间：" + playTime
					+ "秒<br/><br/>最短：" + shortTime + "秒<br/>最少：" + minClick
					+ "步";
			txtInfo.setText(Html.fromHtml(str.toString()));
		}

		int tmpPi, tmpPj, tmpAi, tmpAj;

		tmpPi = Integer.parseInt(imgPre.getTag(com.tl.pic.brow.R.integer.i)
				.toString());
		tmpPj = Integer.parseInt(imgPre.getTag(com.tl.pic.brow.R.integer.j)
				.toString());
		tmpAi = Integer.parseInt(imgAft.getTag(com.tl.pic.brow.R.integer.i)
				.toString());
		tmpAj = Integer.parseInt(imgAft.getTag(com.tl.pic.brow.R.integer.j)
				.toString());

		imgPre.setTag(com.tl.pic.brow.R.integer.i, tmpAi);
		imgPre.setTag(com.tl.pic.brow.R.integer.j, tmpAj);
		imgAft.setTag(com.tl.pic.brow.R.integer.i, tmpPi);
		imgAft.setTag(com.tl.pic.brow.R.integer.j, tmpPj);

		imgPre.setImageBitmap(imgArr[tmpAi][tmpAj]);
		imgAft.setImageBitmap(imgArr[tmpPi][tmpPj]);
		// playSound(com.tl.pic.brow.R.raw.ba);
	}

	/**
	 * 本方法判断拼图是否完成
	 */
	private void judge() {

		// 判断是否完成拼图
		boolean finished = true;
		TableLayout lyImg = (TableLayout) SpellPic.this
				.findViewById(com.tl.pic.brow.R.id.tlSpell);
		for (int i = 0; i < count; i++) {
			TableRow row = (TableRow) lyImg.getChildAt(i);
			for (int j = 0; j < count; j++) {
				ImageView imgV = (ImageView) row.getChildAt(j);
				int ii = Integer.parseInt(imgV.getTag(
						com.tl.pic.brow.R.integer.i).toString());
				int jj = Integer.parseInt(imgV.getTag(
						com.tl.pic.brow.R.integer.j).toString());

				if (ii != i || jj != j) {
					finished = false;
					break;
				}
			}
		}

		if (finished) {
			playSound(3);
			imgPre = null;
			imgAft = null;

			// 以下代码更新游戏记录
			java.util.Date endDt = new java.util.Date();
			int playTime = (int) ((endDt.getTime() - begDt.getTime()) / 1000);
			begDt = null;// 复位计时

			if (shortTime == 0 || playTime < shortTime) {
				shortTime = playTime;
			}

			if (minClick == 0 || clickCount < minClick) {
				minClick = clickCount;
			}
			clickCount = 0;

			if (spell == null) {
				spell = new com.jd.dal.Spell();
				spell.setfName(fName);
				spell.setGridCount(count);
				spell.setMinClick(minClick);
				if (exchangeMode.equals("easy")) {
					spell.setPlayMode(0);
				} else {
					spell.setPlayMode(1);
				}
				spell.setShortTime(shortTime);
			} else {
				spell.setfName(fName);
				spell.setMinClick(minClick);
				spell.setShortTime(shortTime);
			}

			com.jd.dal.SpellDao dao = new com.jd.dal.SpellDao(this);
			dao.addSpell(spell);

			// 消除图片的痕迹
			for (int i = 0; i < count; i++) {
				TableRow row = (TableRow) lyImg.getChildAt(i);
				for (int j = 0; j < count; j++) {
					ImageView imgV = (ImageView) row.getChildAt(j);
					TableRow.LayoutParams params = new TableRow.LayoutParams(
							picWidth + 1, picHeight + 2);
					params.setMargins(0, 0, 0, 0);
					imgV.setLayoutParams(params);
					imgV.setAlpha(255);
				}
			}

			playSound(3);
			showingDlg=true;
			com.jd.util.AppHelper.showInfoDlg(this, "拼图完成！");
		}
	}

	// 声音池初始化方法
	private void initSoundPool() {
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		map = new HashMap<Integer, Integer>();
		// map.put(1, sp.load(this, com.tl.pic.brow.R.raw.de, 1));
		map.put(3, sp.load(this, com.tl.pic.brow.R.raw.finish, 1));
		map.put(1, sp.load(this, com.tl.pic.brow.R.raw.ba, 1));

	}

	// 播放声音方法
	private void playSound(int sound) {
		AudioManager am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
		// 返回当前AlarmManager最大音量
		int audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// am.setStreamVolume(AudioManager.STREAM_MUSIC, audioMaxVolume, 0);
		// 返回当前AudioManager对象的音量值
		float audioCurrentVolume = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolume / audioMaxVolume;
		if (playMusic) {
			sp.play(map.get(sound), // 播放的音乐Id
					volumnRatio, // 左声道音量
					volumnRatio, // 右声道音量
					1, // 优先级，0为最低
					0, // 循环次数，0无不循环，-1无永远循环
					1);// 回放速度，值在0.5-2.0之间，1为正常速度
		}
	}

	protected void closeDialog() {
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			closeDialog();
		}
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		AudioManager am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, 0);
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, 0);
		}else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			closeDialog();
		}
		return true;
	}

}