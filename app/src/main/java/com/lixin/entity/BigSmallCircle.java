package com.lixin.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.lixin.entity.entityInterface.OnClickListenerObserver;
import com.lixin.util.LogUtil;
import com.lixin.connectUtil.NettyClient;
import com.lixin.entity.entityInterfaceImp.EntityObjectImp;
import com.lixin.gameInterface.CmdInterface;
import com.lixin.gameInterface.ObserverListener;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public class BigSmallCircle extends EntityObjectImp implements ObserverListener {
    private final String TAG = BigSmallCircle.class.getSimpleName();
    private Circle bigCircle;
    private Circle smallCircle;
    //按钮布局大圆半径
    private float largeR;
    //大圆半径
    private float bigCircleR;
    private int screenWidth;
    private int screenHeight;
    private int pointer;
    private int isLift;
    private Paint paint;
    private boolean WORKING;
    //一般坐标的弧度
    private double degressByNormalSystem = Double.NaN;
    //当前摇杆的弧度
    private double currentRad = Double.NaN;
    private double degrees = 45;
    StringBuilder strings = new StringBuilder();
    //摇杆坐标
    private float x, y;

    public BigSmallCircle(int screenWidth, int screenHeight, Paint paint, double degrees, float bigCircleR, float x, float y) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.paint = paint;
        this.degrees = degrees;
        this.bigCircleR = bigCircleR;
        this.x = x;
        this.y = y;
        bigCircle = new Circle(paint);
        smallCircle = new Circle(paint);
        bigCircle.setCenterR(bigCircleR);
        bigCircle.setCenterX(x);
        bigCircle.setCenterY(y);

        smallCircle.setCenterR(bigCircle.getCenterR() / 2);
        smallCircle.setCenterX(bigCircle.getCenterX());
        smallCircle.setCenterY(bigCircle.getCenterY());


    }

    public static double getBtnRad(int scale, double degrees) {
        return Math.toRadians(scale * degrees);
    }

    public static float getRightBtnsX(int screenWidth, float largeR, int scale, double degrees) {
        return (float) (screenWidth - largeR * Math.sin(getBtnRad(scale, degrees)));
    }

    public static float getRightBtnsY(int scale, float largeR, double degrees) {
        return (float) (largeR * Math.cos(getBtnRad(scale, degrees)));
    }

    public static float getLeftBtnsX(int screenWidth, float largeR, int scale, double degrees) {
        return (float) (screenWidth - largeR * Math.sin(getBtnRad(scale, degrees)));
    }

    public static float getLeftBtnsY(int screenHeight, int scale, float largeR, double degrees) {
        return (float) (screenHeight - largeR * Math.cos(getBtnRad(scale, degrees)));
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawCircle(bigCircle.getCenterX(), bigCircle.getCenterY()-50, bigCircle.getCenterR(), paint);
        canvas.drawCircle(smallCircle.getCenterX(), smallCircle.getCenterY()-50, smallCircle.getCenterR(), paint);
    }

    /**
     * 判断是否被按下
     *
     * @param touchEvent
     * @return
     */
    public boolean OnClickInBigCircle(TouchEvent touchEvent) {
        if (Math.sqrt(Math.pow((bigCircle.getCenterX() - touchEvent.x+10), 2)
                + Math.pow((bigCircle.getCenterY() - touchEvent.y+10), 2)) <= bigCircle.getCenterR()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean OnClick(TouchEvent touchEvent, NettyClient nettyClient, String cmd, OnClickListenerObserver oco) {
        if (OnClickInBigCircle(touchEvent)) {
            pointer = touchEvent.pointer;

        }
        LogUtil.d(TAG, "observerUpData touchEvent.pointer = " + pointer);
        if (pointer == touchEvent.pointer) {
            if (touchEvent.type == TouchEvent.TOUCH_UP) {
                reset();
            } else {
                if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
                    begin(touchEvent.x, touchEvent.y);
                } else if (touchEvent.type == TouchEvent.TOUCH_MOVE) {
                    update(touchEvent.x, touchEvent.y);
                }
            }

            if (OnClickInBigCircle(touchEvent)) {
                if (null != oco){
                    oco.onClick(nettyClient);
                }
            }
        }
        return true;
    }

    public void reset() {
        smallCircle.setCenterX(bigCircle.getCenterX());
        smallCircle.setCenterY(bigCircle.getCenterY());
        WORKING = false;
    }

    public void update(int touchX, int touchY) {
        currentRad = getRed(bigCircle.getCenterX(), bigCircle.getCenterY(), touchX, touchY);
        if (WORKING) {
            if (isBigCirCleInternal(touchX, touchY)) {
                smallCircle.setCenterX(touchX);
                smallCircle.setCenterY(touchY);
            } else {
                setSmallCircleXY(bigCircle.getCenterX(), bigCircle.getCenterY(), bigCircle.getCenterR(), currentRad);
            }

            degressByNormalSystem = getDegrees(bigCircle.getCenterX(), bigCircle.getCenterY(), smallCircle.getCenterX(), smallCircle.getCenterY());

        }
    }

    public void begin(int touchX, int touchY) {
        if (isBigCirCleInternal(touchX, touchY)) {
            WORKING = true;
            update(touchX, touchY);
        } else {
            WORKING = false;
        }
    }

    private boolean isBigCirCleInternal(int touchX, int touchY) {
        if (Math.sqrt(Math.pow((bigCircle.getCenterX() - touchX), 2)
                + Math.pow((bigCircle.getCenterY() - touchY), 2)) <= bigCircle.getCenterR()) {
            return true;
        }
        return false;
    }

    /**
     * 获取两点弧度
     *
     * @param bigCenterX
     * @param bigCenterY
     * @param touchX
     * @param touchY
     * @return
     */
    private double getRed(float bigCenterX, float bigCenterY, int touchX, int touchY) {
        float dx = touchX - bigCenterX;
        float dy = touchY - bigCenterY;
        float l = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        float cosAngle = dx / l;
        float rad = (float) Math.acos(cosAngle);
        if (touchY < bigCenterY) {
            rad = -rad;
        }
        return rad;
    }

    /**
     * 获取两点的角度
     * @param bigCenterX
     * @param bigCenterY
     * @param smallCenterX
     * @param smallCenterY
     * @return
     */
    private double getDegrees(float bigCenterX, float bigCenterY, float smallCenterX, float smallCenterY) {
        float ret = (float) Math.atan((bigCenterY - smallCenterY) / (-smallCenterX) * 180 / Math.PI);
        if (bigCenterX < smallCenterX) {
            ret += 180;
        } else {
            ret += 360;
        }
        ret = ret >= 360 ? ret - 360 : ret;
        return ret;
    }

    private void setSmallCircleXY(float bigCenterX, float bigCenterY, float bigCenterR, double currentRad) {
        smallCircle.setCenterX((float) (bigCenterR * Math.cos(currentRad) + bigCenterX));
        smallCircle.setCenterY((float) (bigCenterR * Math.sin(currentRad) + bigCenterY));
    }

    public double getDegressByNormalSystem() {
        return degressByNormalSystem;
    }

    public double getCurrentRad() {
        return currentRad;
    }

    public boolean isWORKING() {
        return WORKING;
    }

    @Override
    public void observerUpData(TouchEvent touchEvent) {
        Log.d(TAG, "observerUpData: ");
    }

    @Override
    public void OnClickListener(TouchEvent touchEvent) {
        Log.d(TAG, "OnClickListener: ");
    }
}
