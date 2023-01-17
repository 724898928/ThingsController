package com.lixin.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.lixin.entity.entityInterface.OnClickListenerObserver;
import com.lixin.util.LogUtil;
import com.lixin.connectUtil.NettyClient;
import com.lixin.entity.entityInterface.EntityObject;
import com.lixin.entity.entityInterfaceImp.EntityObjectImp;
import com.lixin.gameInterface.CmdInterface;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/9/5.
 */

public class Rocker extends EntityObjectImp implements EntityObject {
    private final String TAG = Rocker.class.getSimpleName();
    //一般坐标的弧度
    private double degressByNormalSystem = Double.NaN;
    //当前摇杆的弧度
    private double currentRad = Double.NaN;
    //定义摇杆的颜色
    private int rockerColor = Color.GREEN;
    private int screenWidth;
    private int screenHeight;
    //摇杆右边界宽度相对于屏幕的百分比
    private static final float rokerCenterXMarginRight4ScreenWidthPercent = 0.05f;
    //摇杆右边界高度相对有屏幕地百分比
    private static final float rokerCenterYMarginRight4ScreenWidthPercent = 0.05f;
    //摇杆半径相对于屏幕的百分比
    private static final float rokerR4ScreenWidthPercent = 0.1f;
    //定义摇杆的两个圆心坐标和半径 百分比计算

    private BigSmallCircle bigSmallCircleLeft;
    private BigSmallCircle bigSmallCircleRight;
    private Circle btnCircle1;
    private Circle btnCircle2;
    private Circle btnCircle3;
    private Paint paint;
    //按钮半径
    private float btnR;
    private int scale = 1;
    private int isLift = 1;
    //按钮布局大圆半径
    private float largeR;
    private float largeR2;
    private float bigCircleR;
    private NettyClient nettyClient;
    private double degreesLeft = 45;
    private double degreesRight = 22.5;
    StringBuilder strings = new StringBuilder();

    public Rocker(int screenWidth, int screenHeight, NettyClient nettyClient) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.nettyClient = nettyClient;
        //创建画笔 设置画笔的颜色
        paint = new Paint();
        //设置画笔的透明度
        paint.setAlpha(0x77);

        bigCircleR = screenWidth * rokerR4ScreenWidthPercent + 10;
        largeR = (float) (screenWidth / 3 * 1.5);
        largeR2 = (float) (screenWidth / 3 * 1.5) / 2 + 10;

        btnCircle1 = new Circle(paint);
        btnCircle2 = new Circle(paint);
        btnCircle3 = new Circle(paint);

        bigSmallCircleRight = new BigSmallCircle(screenWidth, screenHeight, paint, degreesRight, bigCircleR,
                BigSmallCircle.getRightBtnsX(screenWidth, largeR2, scale, degreesLeft),
                BigSmallCircle.getRightBtnsY(scale, largeR2, degreesLeft));
        bigSmallCircleLeft = new BigSmallCircle(screenWidth, screenHeight, paint, degreesLeft, bigCircleR,
                BigSmallCircle.getLeftBtnsX(screenWidth, largeR2, scale, degreesLeft),
                BigSmallCircle.getLeftBtnsY(screenHeight, scale, largeR2, degreesLeft));

        btnR = bigCircleR / 2 + 20;
        btnCircle1.setCenterR(btnR);
        btnCircle1.setCenterX(BigSmallCircle.getRightBtnsX(screenWidth, largeR, 1, degreesRight));
        btnCircle1.setCenterY(BigSmallCircle.getRightBtnsY(1, largeR, degreesRight));

        btnCircle2.setCenterR(btnR);
        btnCircle2.setCenterX(BigSmallCircle.getRightBtnsX(screenWidth, largeR, 2, degreesRight));
        btnCircle2.setCenterY(BigSmallCircle.getRightBtnsY(2, largeR, degreesRight));

        btnCircle3.setCenterR(btnR);
        btnCircle3.setCenterX(BigSmallCircle.getRightBtnsX(screenWidth, largeR, 3, degreesRight));
        btnCircle3.setCenterY(BigSmallCircle.getRightBtnsY(3, largeR, degreesRight));

    }


    @Override
    public void drawSelf(Canvas canvas) {
        paint.setColor(rockerColor);
        paint.setAlpha(119);
        paint.setStyle(Paint.Style.FILL);
        bigSmallCircleLeft.drawSelf(canvas);
        bigSmallCircleRight.drawSelf(canvas);
        btnCircle1.drawSelf(canvas);
        btnCircle2.drawSelf(canvas);
        btnCircle3.drawSelf(canvas);
        paint.setColor(Color.BLACK);
    }

    /**
     * 根据触摸事件控制摇杆
     *
     * @param touchEvent
     */
    @Override
    public boolean OnClick(TouchEvent touchEvent, NettyClient nettyClient, String cmd, OnClickListenerObserver onClickListenerObserver) {
        nettyClient = this.nettyClient;
        //判断是否是左摇杆

        if (touchEvent.y > screenHeight/2){
            bigSmallCircleLeft.OnClick(touchEvent, nettyClient, null, nettyClient15 -> {
                Log.d(TAG, "OnClick: LeftBigSmallCircle");
                if (touchEvent != null) {
                    float dx = touchEvent.x ;
                    float dy = touchEvent.y ;
                    strings.append(cmd);
                    strings.append(String.valueOf(dx));
                    strings.append(",");
                    strings.append(String.valueOf(dy));
                    nettyClient15.insertCmd(strings.toString());
                    strings.setLength(0);
                }
            });
        }else {
            bigSmallCircleRight.OnClick(touchEvent, nettyClient, null, nettyClient14 -> {
                Log.d(TAG, "OnClick: RightBigSmallCircle");
                if (touchEvent != null) {
                    float dx = touchEvent.x ;
                    float dy = touchEvent.y ;
                    strings.append(String.valueOf(dx));
                    strings.append(",");
                    strings.append(String.valueOf(dy));
                    nettyClient14.insertCmd(strings.toString());
                    strings.setLength(0);
                }
            });
        }

        //判断加速按钮是否按下
        btnCircle1.OnClick(touchEvent, nettyClient, null, nettyClient1 -> {
            if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
                nettyClient1.insertCmd(CmdInterface.GAMESTART);
                LogUtil.d(TAG, "observerUpData 正在加速! ");
            }
        });

        //判断减速按钮是否按下
        btnCircle2.OnClick(touchEvent, nettyClient,null, nettyClient12 -> {
            nettyClient12.insertCmd(CmdInterface.GAME_SPEED_UP);
            if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
                LogUtil.d(TAG, "observerUpData 正在减速! ");
            }
        });
        //判断停止按钮是否按下
        btnCircle3.OnClick(touchEvent, nettyClient, null, nettyClient13 -> {
            nettyClient13.insertCmd(CmdInterface.GAME_SPEED_DOWN);
            if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
                LogUtil.d(TAG, "observerUpData 正在停止! ");
            }
        });
        return true;
    }

}
