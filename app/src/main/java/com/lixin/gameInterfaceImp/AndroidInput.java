package com.lixin.gameInterfaceImp;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.lixin.gameInterface.Input;
import com.lixin.gameInterface.ObserverListener;
import com.lixin.gameInterface.TouchHandler;

import java.util.List;

/**
 * Created by li on 2018/9/1.
 */

public class AndroidInput implements Input,ObserverListener {
    private AccelerometerHandler accelerometerHandler;
    private KeyboardHandler keyboardHandler;
    private TouchHandler touchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        accelerometerHandler = new AccelerometerHandler(context);
        keyboardHandler = new KeyboardHandler(view);
        if (Integer.parseInt(Build.VERSION.SDK) < 5) {
            touchHandler = new SingleTouchHandler(view,scaleX,scaleY);
        }else {
            touchHandler = new MultiTouchHandler(view,this, scaleX, scaleY);
        }
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return keyboardHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    @Override
    public float getAccelX() {
        return accelerometerHandler.getAccelX();
    }

    @Override
    public float getAccelY() {
        return accelerometerHandler.getAccelY();
    }

    @Override
    public float getAccelZ() {
        return accelerometerHandler.getAccelZ();
    }

    @Override
    public List<com.lixin.gameInterfaceImp.TouchEvent> getKeyEvents() {
        return touchHandler.getTouchEvents();
    }

    @Override
    public List<com.lixin.gameInterfaceImp.KeyEvent> getTouchEvent() {
        return keyboardHandler.getKeyEvents();
    }

    @Override
    public void observerUpData(com.lixin.gameInterfaceImp.TouchEvent touchEvent) {

    }

    @Override
    public void OnClickListener(com.lixin.gameInterfaceImp.TouchEvent touchEvent) {

    }
}
