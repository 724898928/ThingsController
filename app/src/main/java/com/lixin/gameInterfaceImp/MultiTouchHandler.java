package com.lixin.gameInterfaceImp;

import android.view.MotionEvent;
import android.view.View;

import com.lixin.util.LogUtil;
import com.lixin.gameInterface.ObserverListener;
import com.lixin.gameInterface.PoolObjectFactory;
import com.lixin.gameInterface.TouchHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/8/30.
 */

/**
 * 多点触摸处理
 */
public class MultiTouchHandler implements TouchHandler {
    private String className = "com.li MultiTouchHandler";
    private Pool<TouchEvent> touchEventPool;
    private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<>();
    private static final int MAX_TOUCHPOINTS = 10;
    private int[] id = new int[MAX_TOUCHPOINTS];
    private int[] touchX = new int[MAX_TOUCHPOINTS];
    private int[] touchY = new int[MAX_TOUCHPOINTS];
    private boolean[] isTouched = new boolean[MAX_TOUCHPOINTS];
    //比例，用来适配屏幕。
    private float scaleX;
    private float scaleY;
    private ObserverListener observerListener ;
    public MultiTouchHandler(View view, ObserverListener observerListener, float scaleX, float scaleY) {
        LogUtil.d(className, "MultiTouchHandler begin");
        PoolObjectFactory factory = new PoolObjectFactory() {
            @Override
            public Object createObject() {
                return new TouchEvent();
            }
        };
        this.observerListener = observerListener;
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        view.setOnTouchListener(this);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        LogUtil.d(className, "MultiTouchHandler end");
    }

    /**
     * 判断是否按下
     *
     * @param pointer
     * @return
     */
    @Override
    public boolean isTouchDown(int pointer) {
        LogUtil.d(className, "isTouchDown begin");
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return false;
            } else {
                LogUtil.d(className, "isTouchDown end");
                return isTouched[index];
            }
        }
    }

    private int getIndex(int pointer) {
        LogUtil.d(className, "getIndex begin");
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            if (id[i] == pointer) {
                return i;
            }
        }
        LogUtil.d(className, "getIndex begin");
        return -1;
    }

    @Override
    public int getTouchX(int pointer) {
        LogUtil.d(className, "getTouchX begin");
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index > MAX_TOUCHPOINTS) {
                return 0;
            } else {
                LogUtil.d(className, "getTouchX end");
                return touchX[index];
            }

        }
    }

    @Override
    public int getTouchY(int pointer) {
        LogUtil.d(className, "getTouchY begin");
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index > MAX_TOUCHPOINTS) {
                return 0;
            } else {
                LogUtil.d(className, "getTouchY end");
                return touchY[index];
            }

        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        LogUtil.d(className, "getTouchEvents begin");
        synchronized (this) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++) {
                {
                    touchEventPool.free(touchEvents.get(i));
                }
            }
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            LogUtil.d(className, "getTouchEvents end");
            return touchEvents;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.d(className, "onTouch begin");
        TouchEvent touchEvent = null;
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                    >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerCount = event.getPointerCount();
            for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
                if (i >= pointerCount) {
                    isTouched[i] = false;
                    id[i] = -1;
                    continue;
                }
                int pointerId = event.getPointerId(i);
                //如果事件是按下，放开，取消，退出事件，标记这个ID
                if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    continue;
                }
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent =  touchEventPool.newObject();
                        touchEvent.type =TouchEvent.TOUCH_DOWN;
                        touchEvent.pointer = pointerId;
                        touchEvent.id = pointerId;
                        touchEvent.x=touchX[i] = (int) (event.getX(i)* scaleX);
                        touchEvent.y=touchY[i] = (int) (event.getY(i)* scaleY);
                        isTouched[i] = true;
                        id[i] = pointerId;
                        touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_UP;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                        isTouched[i] = false;
                        id[i] = -1;
                        touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_MOVE;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                        isTouched[i] = true;
                        id[i] = pointerId;
                        touchEventsBuffer.add(touchEvent);
                        break;

                }
                LogUtil.d(className, "touchEvent = "+touchEvent.toString());
                observerListener.observerUpData(touchEvent);
                observerListener.OnClickListener(touchEvent);
            }
            LogUtil.d(className, "onTouch end");
            return true;
        }
    }

}
