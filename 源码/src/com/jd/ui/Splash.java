package com.jd.ui;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.baidu.mobads.SplashAd.SplashType;
import com.tl.pic.brow.R;
import com.tl.pic.brow.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		MyApplication.getInstance().addActivity(this);
		
		RelativeLayout adsParent = (RelativeLayout) this
				.findViewById(R.id.adsRl);
		SplashAdListener listener=new SplashAdListener() {
			@Override
			public void onAdDismissed() {
				Log.i("RSplashActivity", "onAdDismissed");
				jumpWhenCanClick();// ��ת������Ӧ��������
			}

			@Override
			public void onAdFailed(String arg0) {
				Log.i("RSplashActivity", "onAdFailed");
				jump();
			}

			@Override
			public void onAdPresent() {
				Log.i("RSplashActivity", "onAdPresent");
			}

			@Override
			public void onAdClick() {
				Log.i("RSplashActivity", "onAdClick");
				//���ÿ����ɽ��ܵ��ʱ���ûص�����
				//jumpWhenCanClick();// ��ת������Ӧ��������
			}
		};
		/**
		 * Ĭ�Ͽ������캯����
		 * new SplashAd(Context context, ViewGroup adsParent,
		 * 				SplashAdListener listener, SplashType splashType);
		 * ʵʱ����Ĭ�Ͻ��ܵ����ʹ�������е�jumpWhenCanClick��������ת��
		 */
//		new SplashAd(this, adsParent, listener, SplashType.REAL_TIME);
		/**
		 * ʵʱ����Ĭ�Ͻ��ܵ����������ÿ��������ܵ����ʹ�����¹��캯����
		 * new SplashAd(Context context, ViewGroup adsParent,
		 * 				SplashAdListener listener,String posId, boolean canClick, SplashType splashType);
		 * ��ǰposId�����λID��������Ϊ�գ��ʿ�ʹ�����´�����д�����
		 */ 
		new SplashAd(this, adsParent, listener, "", true, SplashType.REAL_TIME);
		
	}

	/**
	 * �����ÿ����ɵ��ʱ����Ҫ�ȴ���תҳ��رպ����л������������ڡ��ʴ�ʱ��Ҫ����waitingOnRestart�жϡ�
	 * ���⣬�����������Ҫ��onRestart�е���jumpWhenCanClick�ӿڡ�
	 */
	public boolean waitingOnRestart=false;
	private void jumpWhenCanClick() {
		Log.d("test", "this.hasWindowFocus():"+this.hasWindowFocus());
		if(this.hasWindowFocus()||waitingOnRestart){
			this.startActivity(new Intent(Splash.this, Login.class));
			this.finish();
		}else{
			waitingOnRestart=true;
		}
		
	}
	
	/**
	 * ���ɵ���Ŀ�����ʹ�ø�jump��������������jumpWhenCanClick
	 */
	private void jump() {
		this.startActivity(new Intent(Splash.this, Login.class));
		this.finish();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(waitingOnRestart){
			jumpWhenCanClick();
		}
	}
	
}
