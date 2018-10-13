package com.lixin.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.lixin.Util.LogUtil;
import com.lixin.entity.Rocker;
import com.lixin.gameInterface.ObserverListener;
import com.lixin.gameInterface.TouchHandler;
import com.lixin.gameInterfaceImp.AndroidGame;
import com.lixin.gameInterfaceImp.MultiTouchHandler;
import com.lixin.gameInterfaceImp.TouchEvent;

import java.util.List;

/**
 * Created by li on 2018/9/2.
 */

public class MySurfaceView extends SurfaceView implements Runnable, ObserverListener, SurfaceHolder.Callback {

    private int SCREEN_W;
    private int SCREEN_H;
    private Thread mThread;
    AndroidGame game;
    Bitmap framebuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    boolean running = false;
    Rocker rocker;
    Canvas canvas;
    Display display;

    private static final int MAX_TOUCHPOINTS = 10;
    private Paint touchPaints[] = new Paint[MAX_TOUCHPOINTS];
    private MultiTouchHandler multiTouchHandler;
    private List<TouchEvent> touchEvents;
    private String className = "com.li MySurfaceView";

    public MySurfaceView(Activity context) {
        super(context);
        LogUtil.d(className, "MySurfaceView begin");
        //this.game = game;
        //this.framebuffer = bitmap;
        this.holder = getHolder();
        //为surfaceView添加状态监听
        //multiTouchHandler = new MultiTouchHandler(this, 1, 1);
        WindowManager windowManager = context.getWindowManager();
        display = windowManager.getDefaultDisplay();
        surfaceCreated(holder);
        setFocusable(true);
        //  int id = (event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK) >>> MotionEvent.ACTION_POINTER_ID_SHIFT;
        TouchHandler touchHandler = new MultiTouchHandler(this, this, 1, 1);
        touchEvents = touchHandler.getTouchEvents();
        LogUtil.d(className, "MySurfaceView end");
    }

    @Override
    public void run() {
        LogUtil.d(className, "run begin");
        long startTime = System.currentTimeMillis();
        while (running) {
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.WHITE);
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
            LogUtil.d(className, "running");
        }
        LogUtil.d(className, "end");
    }

    public void resume() {
        LogUtil.d(className, "resume begin");
        renderThread = new Thread(this);
        renderThread.start();
        LogUtil.d(className, "resume end");
        running = true;

    }

    public void pause() {
        LogUtil.d(className, "pause begin");
        running = false;
        while (true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(className, "pause end");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SCREEN_W = display.getWidth();
        SCREEN_H = display.getHeight();
        running = true;
        rocker = new Rocker(SCREEN_W, SCREEN_H, null);
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(className, "surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(className, "surfaceDestroyed ");
    }

    @Override
    public void observerUpData(TouchEvent touchEvent) {
        LogUtil.d(className, "observerUpData begin");
        rocker.onTouchEvent(touchEvent);
        LogUtil.d(className, "observerUpData end");

    }
}
