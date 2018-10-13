package com.lixin.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.lixin.Util.LogUtil;
import com.lixin.entity.entityInterfaceImp.EntityObjectImp;
import com.lixin.gameInterface.Input;
import com.lixin.gameInterface.Object;
import com.lixin.gameInterface.TouchHandler;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/9/5.
 */

public class Rocker extends EntityObjectImp implements Object{
    private String className = "com.li Rocker ";
    //一般坐标的弧度
    private double degressByNormalSystem = Double.NaN;
    //当前摇杆的弧度
    private double currentRad = Double.NaN;
    //定义摇杆的颜色
    private int rockerColor = Color.GREEN;
    private int screenWidth;
    private int screenHeight;
    // private int rockerColor = Color.RED;
    //摇杆右边界宽度相对于屏幕的百分比
    private static final float rokerCenterXMarginRight4ScreenWidthPercent = 0.05f;
    //摇杆右边界高度相对有屏幕地百分比
    private static final float rokerCenterYMarginRight4ScreenWidthPercent = 0.05f;
    //摇杆半径相对于屏幕的百分比
    private static final float rokerR4ScreenWidthPercent = 0.1f;
    //定义摇杆的两个圆心坐标和半径 百分比计算

    private Circle bigCircle = new Circle();
    private Circle smallCircle = new Circle();
    private Circle btnCircle1 = new Circle();
    private Paint paint;
    private boolean WORKING;
    private int pointer = -1;
    private TouchEvent touchEvent;

    public Rocker(int screenWidth, int screenHeight, TouchEvent touchEvent) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        //创建画笔 设置画笔的颜色
        paint = new Paint();
        //设置画笔的透明度
        paint.setAlpha(0x77);
        bigCircle.setCenterR(screenWidth * rokerR4ScreenWidthPercent);
        bigCircle.setCenterX(screenWidth - bigCircle.getCenterR() * 1.5f
                - rokerCenterXMarginRight4ScreenWidthPercent * screenWidth);
        bigCircle.setCenterY(screenHeight - bigCircle.getCenterR() * 1.5f
                - rokerCenterYMarginRight4ScreenWidthPercent * screenHeight);

        smallCircle.setCenterR(bigCircle.getCenterR() / 2);
        smallCircle.setCenterX(bigCircle.getCenterX());
        smallCircle.setCenterY(bigCircle.getCenterY());

        btnCircle1.setCenterR(smallCircle.getCenterR());
        btnCircle1.setCenterX((screenWidth - bigCircle.getCenterR() * 1.5f
                - rokerCenterXMarginRight4ScreenWidthPercent * screenWidth));
        btnCircle1.setCenterY(screenHeight - (screenHeight - bigCircle.getCenterR() * 1.5f
                - rokerCenterYMarginRight4ScreenWidthPercent * screenHeight));

    }
    private boolean OnClick(TouchEvent touchEvent){
        if (Math.sqrt(Math.pow((bigCircle.getCenterX() - touchEvent.x), 2)
                + Math.pow((bigCircle.getCenterY() - touchEvent.y), 2)) <= bigCircle.getCenterR()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 根据触摸事件控制摇杆
     *
     * @param touchEvent
     */
    public void onTouchEvent(TouchEvent touchEvent) {

        if (OnClick(touchEvent)) {
            pointer = touchEvent.pointer;
        }
        LogUtil.d(className, "observerUpData touchEvent.pointer = " + pointer);
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
        }
    }

    @Override
    public void drawSelf(Canvas canvas) {
        paint.setColor(rockerColor);
        paint.setAlpha(119);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bigCircle.getCenterX(), bigCircle.getCenterY(), bigCircle.getCenterR(), paint);
        canvas.drawCircle(smallCircle.getCenterX(), smallCircle.getCenterY(), smallCircle.getCenterR(), paint);
        canvas.drawCircle(btnCircle1.getCenterX(), btnCircle1.getCenterY(), btnCircle1.getCenterR(), paint);
        // canvas.drawCircle(btn2CenterX, btn2CenterY, btn2CenterR, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText("原点在坐标系的弧度:" + currentRad, 20, 20, paint);
        canvas.drawText("由该弧度计算得出的角度:" + (currentRad * 180) / Math.PI, 20, 40, paint);
        canvas.drawText("原点在左下坐标系的角度:" + degressByNormalSystem, 20, 60, paint);
    }

    public void begin(int touchX, int touchY) {
        if (isBigCirCleInternal(touchX, touchY)) {
            WORKING = true;
            update(touchX, touchY);
        } else {
            WORKING = false;
        }
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
        }
        degressByNormalSystem = getDegrees(bigCircle.getCenterX(), bigCircle.getCenterY(), smallCircle.getCenterX(), smallCircle.getCenterY());

    }

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

    public void reset() {
        smallCircle.setCenterX(bigCircle.getCenterX());
        smallCircle.setCenterY(bigCircle.getCenterY());
        WORKING = false;
    }

    private boolean isBigCirCleInternal(int touchX, int touchY) {
        if (Math.sqrt(Math.pow((bigCircle.getCenterX() - touchX), 2)
                + Math.pow((bigCircle.getCenterY() - touchY), 2)) <= bigCircle.getCenterR()) {
            return true;
        }
        return false;
    }

    /**
     * 判断按钮被按下
     *
     * @return
     */
    public int whichBtnPress(TouchHandler multiTouchHandler) {

        //multiTouchHandler.getTouchY(multiTouchHandler.getTouchEvents().);
        return 1;
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
}
