package com.jd.ui;

import java.util.Stack;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
    
    private static Stack<Activity> activityStack;
    private static MyApplication singleton;
    @Override
    public void onCreate()
    {
        super.onCreate();
        singleton=this;
    }
    // Returns the application instance
    public static MyApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity ���Activity��ջ
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * get current Activity ��ȡ��ǰActivity��ջ�����һ��ѹ��ģ�
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }
    /**
     * ������ǰActivity��ջ�����һ��ѹ��ģ�
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * ����ָ����Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * ����ָ��������Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * ��������Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * �˳�Ӧ�ó���
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

}