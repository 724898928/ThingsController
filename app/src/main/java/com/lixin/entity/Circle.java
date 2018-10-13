package com.lixin.entity;

import com.lixin.Util.LogUtil;
import com.lixin.entity.entityInterface.EntityObject;
import com.lixin.entity.entityInterfaceImp.EntityObjectImp;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public class Circle  extends EntityObjectImp{
    private float centerX = 120;
    private float centerY = 120;
    private float centerR = 40;

    public Circle() {
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
}
