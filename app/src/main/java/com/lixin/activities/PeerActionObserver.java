package com.lixin.activities;

import com.lixin.model.RTC_ConnectMeter;

import org.webrtc.SessionDescription;

import java.util.List;

public interface PeerActionObserver {
    /**
     *   提议方发过来的Offer处理
     */
    void onOffer(SessionDescription sdp);

    /**
     *  应答方发过来的Answer处理
     * @param sdp
     */
    void onAnswer(SessionDescription sdp);

    /**
     * 接收到对方发过来的Candidate信息
     * @param meter
     */
    void onCandidate(RTC_ConnectMeter meter);

    /**
     * 更新用户列表
     * @param msg
     */
    List onUpdateUserList(Object msg);

    /**
     * 挂断处理
     * @param msg
     */
    void onHangUp(Object msg);

    /**
     * 离开聊天室
     * @param msg
     */
    void onLeaveRoom(Object msg);

    /**
     * 心跳包
     * @param msg
     */
    void heartPackage(Object msg);
}
