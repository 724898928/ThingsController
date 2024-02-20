package com.lixin.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lixin.activities.MainActivity;
import com.lixin.util.LogUtil;
import com.lixin.entity.Rocker;
import com.lixin.gameInterface.ObserverListener;
import com.lixin.gameInterface.TouchHandler;
import com.lixin.gameInterfaceImp.MultiTouchHandler;
import com.lixin.gameInterfaceImp.TouchEvent;
import com.lixin.connectUtil.NettyClient;
import java.util.List;

/**
 * Created by li on 2018/9/2.
 */

public class MySurfaceView extends SurfaceView implements Runnable, ObserverListener, SurfaceHolder.Callback {

    private final String TAG = MySurfaceView.class.getSimpleName();
    private int SCREEN_W;
    private int SCREEN_H;
    private Thread mThread;
    private Thread renderThread = null;
    private  SurfaceHolder holder;
    boolean running = false;
    private  Rocker rocker;
    private  Canvas canvas;
    private DisplayMetrics dm;

    private MultiTouchHandler multiTouchHandler;
    private List<TouchEvent> touchEvents;
    private NettyClient nettyClient;
    private Point outSize;
    private int notchHeight; // 刘海高度
    public MySurfaceView(Activity context) {
        super(context);
       // init();
        notchHeight = getNotchHeight(context);
        LogUtil.d(TAG, "MySurfaceView begin");
        //this.game = game;
        //this.framebuffer =
        nettyClient = NettyClient.getInstance();
        this.holder = getHolder();
        this.holder.addCallback(this);
        // 设置全屏
        DisplayMetrics displayMetrics = ((MainActivity)context).getDisplayMetrics();
        dm = displayMetrics;
        context.getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        surfaceCreated(holder);
        setFocusable(true);

        TouchHandler touchHandler = new MultiTouchHandler(this, this, 1, 1);
        touchEvents = touchHandler.getTouchEvents();
        LogUtil.d(TAG, "MySurfaceView end");
    }

    private int getNotchHeight(Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            int notchHeight = context.getWindow().getDecorView().getHeight();
            LogUtil.d(TAG, "getNotchHeight: notchHeight = "+notchHeight);
          return notchHeight;
        }
        return 0;
    }

    public SurfaceHolder getSurfaceHolder(){
        return this.holder;
    }

    @Override
    public void run() {
        LogUtil.d(TAG, "run begin");
        long startTime = System.currentTimeMillis();
        while (running) {
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    rocker.drawSelf(canvas);
                }
                //将矩形延伸到整个SuffaceView
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < 15) {
                try {
                    Thread.sleep(15 - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.d(TAG, "running");
        }
        LogUtil.d(TAG, "end");
    }

    public void resume() {
        LogUtil.d(TAG, "resume begin");
        renderThread = new Thread(this);
        renderThread.start();
        LogUtil.d(TAG, "resume end");
        running = true;

    }

    public void pause() {
        LogUtil.d(TAG, "pause begin");
        running = false;
        while (true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, "pause end");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SCREEN_W = dm.widthPixels;
        SCREEN_H = dm.heightPixels-150;
        running = true;
        rocker = new Rocker(SCREEN_W, SCREEN_H, nettyClient);
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(TAG, "surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceDestroyed ");
    }

    @Override
    public void observerUpData(TouchEvent touchEvent) {
        LogUtil.d(TAG, "observerUpData begin");
        rocker.OnClick(touchEvent,nettyClient,null,null);
        LogUtil.d(TAG, "observerUpData end");

    }

    @Override
    public void OnClickListener(TouchEvent touchEvent) {

    }

    public boolean isScreenChange() {
        Configuration mConfiguration = this.getResources().getConfiguration(); // 获取设置的配置信息
        int ori = mConfiguration.orientation; // 获取屏幕方向

        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {

            // 竖屏
            return false;
        }
        return false;
    }

    /**
     *
     * 获取当前屏幕旋转角度
     *
     * @param activity
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getDisplayRotation(Activity activity) {
        if (activity == null)
            return 0;

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

}
