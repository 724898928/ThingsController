package com.lixin.gameInterfaceImp;

import android.view.MotionEvent;

/**
 * Created by li on 2018/8/25.
 */

public class TouchEvent {
    public static final int TOUCH_DOWN = 0;
    public static final int TOUCH_UP = 1;
    public static final int TOUCH_MOVE = 2;
    public static final int MAX_TOUCHPOINTS = 10;
    public int type;
    public int x,y;
    public int pointer;
    public int id;
    //判断触摸事件是单触摸还是多触摸
    public static boolean isSingleTouch(MotionEvent event){
        if (event.getPointerCount() > MAX_TOUCHPOINTS){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TouchEvent{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", pointer=" + pointer +
                ", id=" + id +
                '}';
    }
}
