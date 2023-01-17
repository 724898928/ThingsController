package com.lixin.entity.entityInterface;

import android.graphics.Canvas;

import com.lixin.connectUtil.NettyClient;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public interface EntityObject {
    void drawSelf(Canvas canvas);
    boolean OnClick(TouchEvent touchEvent,NettyClient nettyClient, String cmd, OnClickListenerObserver onClickListenerObserver);
}
