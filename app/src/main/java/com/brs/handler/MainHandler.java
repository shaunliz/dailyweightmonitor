package com.brs.handler;

import android.os.Handler;
import android.os.Message;

import com.brs.dailyweightmonitor.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by ikban on 2015-10-29.
 */
public class MainHandler extends Handler {

    private final WeakReference<MainActivity> mActivity;

    public MainHandler(MainActivity activity) {
        mActivity = new WeakReference<MainActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = mActivity.get();
        if(activity != null) activity.weakHandleMessage(msg);
    }
}
