package com.lixin.gameInterface;

import android.view.View;

import com.lixin.gameInterfaceImp.TouchEvent;

import java.util.List;

/**
 * Created by li on 2018/8/25.
 */

public interface TouchHandler extends View.OnTouchListener {
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
}
