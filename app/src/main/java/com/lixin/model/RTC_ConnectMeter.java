package com.lixin.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import lombok.Data;

@Data
public class RTC_ConnectMeter {

    private String wsUrl;
    private String from;

    private String to;

    private SessionDescription sessionDescription;  //SDP

    private String roomId;  // 连接房号

    private IceCandidate iceCandidate;

    private String sessionId = "000-111";

    public RTC_ConnectMeter(String webRTCUrl, String roomId, String toUser, String mine) {
        this.wsUrl = webRTCUrl;
        this.roomId = roomId;
        this.to = toUser;
        this.from = mine;
    }

    public void setIceCandidate(IceCandidate iceCandidate){
        this.iceCandidate = iceCandidate;
    }

    public IceCandidate getIceCandidate(){
      return this.iceCandidate;
    }

    public SessionDescription getSessionDescription(){
        return this.sessionDescription;
    }

    public void setSessionDescription(SessionDescription sdp){
        this.sessionDescription = sdp;
    }
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
