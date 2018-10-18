package com.lixin.entity.entityInterface;

import android.graphics.Canvas;

import com.lixin.gameInterface.Object;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public interface EntityObject {
    void drawSelf(Canvas canvas);
    boolean OnClick(TouchEvent touchEvent);
}
