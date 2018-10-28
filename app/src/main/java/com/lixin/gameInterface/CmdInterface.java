package com.lixin.gameInterface;

/**
 * Created by li on 2018/10/18.
 */

public interface CmdInterface {
    //游戏开始
    String GAMESTART = "0";
    //加速
    String GAME_SPEED_UP = "1";
    //减速
    String GAME_SPEED_DOWN = "2";
    //向左转
    String GAME_TURN_LEFT = "3";
    //向右转
    String GAME_TURN_RIGHT = "4";
    //前进
    String GAME_GO_FORWARD = "5";
    //后退
    String GAME_RETREAT = "6";
    //向上
    String GAME_GO_UP = "7";
    //向下
    String GAME_GO_DOWN = "8";
    //左旋转
    String GAME_LEFT_ROTATE = "9";
    //右旋转
    String GAME_RIGHT_ROTATE = "10";
}
