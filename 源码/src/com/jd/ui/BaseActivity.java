package com.jd.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class BaseActivity extends Activity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		com.jd.util.AppHelper.lastTouch=new java.util.Date();
		return super.dispatchKeyEvent(event);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		com.jd.util.AppHelper.lastTouch=new java.util.Date();
		return super.dispatchTouchEvent(ev);
	}

//	@Override
//	public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
//		// TODO Auto-generated method stub
//		
//	}

}
