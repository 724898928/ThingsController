package com.lixin.activities;

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyHandle extends Handler {

    private final static String TAG = MyHandle.class.getSimpleName();

    private final WeakReference<Activity> mActivityReference;

    public MyHandle(Activity activity) {
        this.mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void handleMessage(@NonNull android.os.Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "MyHandle handleMessage: msg = " + msg);
        MainActivity activity = (MainActivity) mActivityReference.get();
        switch (msg.what) {
            case 1:
                if (null != activity) {
                    activity.createPeerConnection();
                }
                break;
            case 2:
                Looper.prepare();
                Toast.makeText(activity, "要连接的用户没有上线", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
        }
    }
}
