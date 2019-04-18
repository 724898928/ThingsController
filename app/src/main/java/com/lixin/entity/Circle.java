package com.lixin.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.lixin.connectUtil.NettyClient;
import com.lixin.entity.entityInterface.EntityObject;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public class Circle implements EntityObject {
    private float centerX = 120;
    private float centerY = 120;
    private float centerR = 40;
    private Paint paint;

    public Circle(Paint paint) {
        this.paint = paint;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getCenterR() {
        return centerR;
    }

    public void setCenterR(float centerR) {
        this.centerR = centerR;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, centerR, paint);
    }

    @Override
    public boolean OnClick(TouchEvent touchEvent, NettyClient nettyClient, String cmd) {
        if (Math.sqrt(Math.pow((centerX - touchEvent.x), 2)
                + Math.pow((centerY - touchEvent.y), 2)) <= centerR) {
            return true;
        } else {
            return false;
        }


    }
}
