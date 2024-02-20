package com.lixin.util;

public interface MsgContant {
    String JOIN_ROOM = "joinRoom";
    String OFFER = "offer";         //Offer消息
    String ANSWER = "answer";        //Answer消息
    String CANDIDATE = "candidate";     //Candidate消息
    String HANGUP = "hangUp";         //挂断
    String LEAVE_ROOM = "leaveRoom";        //离开房间
    String UPDATE_USER_LIST = "updateUserList";  //更新房间用户列表
    String HEART_PACKAGE = "heartPackage";  //更新房间用户列表

    String REMOVE_STREAM = "removestream";    //流移除事件
    String ADD_TREAM = "addstream";    //流添加事件
    String NEW_CALL = "newCall";    //监听新的呼叫事件
    String LOCAL_AUDIO_STREAM = "localAudioStream";    //监听新本地流事件
    String LOCAL_VIDEO_STREAM = "localVideoStream";
    String AUDIO_TRACK_ID = "audiotrack";
    String VIDEO_TRACK_ID = "videtrack";
    String FIELD_TRIALS = "WebRTC-H264HighProfile/Enabled/";
    int VOLUME = 10;//声音调节

    String CHANNEL = "channel";

}
