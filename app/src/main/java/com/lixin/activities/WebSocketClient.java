package com.lixin.activities;

import static com.lixin.util.MsgContant.ANSWER;
import static com.lixin.util.MsgContant.OFFER;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.lixin.model.RTC_ConnectMeter;
import com.lixin.model.RTC_Message;
import com.lixin.model.RTC_Room;
import com.lixin.model.RTC_User;
import com.lixin.util.MsgContant;

import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.net.URI;
import java.util.ArrayList;

import io.netty.util.internal.StringUtil;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient implements PeerActionObserver {

    private final static String TAG = WebSocketClient.class.getSimpleName();

    private String sessionId;

    private RTC_ConnectMeter meter;
    private RTC_Message message;

    private PeerConnection peerConnection;

    private MySdpObserver observer;

    private Handler handler;

    private Context context;

    private android.os.Message osMsg;

    public WebSocketClient(Context context, URI serverUri, RTC_Message<RTC_Room> startMsg, Handler handler){
        super(serverUri);
        this.context = context;
        this.handler = handler;
        this.message = startMsg;
        this.meter = new Gson().fromJson(this.message.getData().toString(), RTC_ConnectMeter.class);

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen: ServerHandshake handshakedata: "+handshakedata);
        if (null != this.message){
            this.send(this.message.toString());
        }
        osMsg = new android.os.Message();
        osMsg.what = 1;
        handler.handleMessage(osMsg);
    }

    @Override
    public void onMessage(String message) {
        RTC_ConnectMeter getData = null;
        Log.d(TAG, "onMessage: message => "+message);
        if(!StringUtil.isNullOrEmpty(message)){
            RTC_Message msg = new Gson().fromJson(message, RTC_Message.class);
            String msgData = new Gson().toJson(msg.getData()) ;
            if (null != msgData){
                getData = new Gson().fromJson(msgData,RTC_ConnectMeter.class);
            }
            Log.d(TAG, "onMessage getData: "+getData);
            switch (msg.getType()){
                case MsgContant.JOIN_ROOM:
                    if (null!=getData){
                        this.createOffer();
                        this.onJoinRoom();
                    }else {
                        osMsg.what = 2;
                        handler.handleMessage(osMsg);
                        handler.getLooper().quitSafely();
                    }
                    break;
                case OFFER:
                    SessionDescription sessionDescription1 = getData.getSessionDescription();
                    peerConnection.setRemoteDescription(observer, sessionDescription1);
                    this.createAnswer();
                    break;
                case MsgContant.CANDIDATE:
                    this.onCandidate(getData);
                    break;
                case MsgContant.UPDATE_USER_LIST:
                    this.onUpdateUserList(msg);
                    break;
                case MsgContant.HANGUP:
                    this.onHangUp(msg);
                    break;
                case MsgContant.LEAVE_ROOM:
                    this.onLeaveRoom(msg);
                    break;
                case MsgContant.HEART_PACKAGE:
                    this.heartPackage(msg);
                    break;
                default:
                    Log.d(TAG, "未知消息: "+msg);
            }
        }

    }

    private void createOffer() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        if (null != observer){
            peerConnection.createOffer(observer,mediaConstraints);
        }else {
            Log.d(TAG, "createOffer: observer == null");
        }
    }
    private void createAnswer() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        if (null != observer){
            peerConnection.createAnswer(observer,mediaConstraints);
        }else {
            Log.d(TAG, "createAnswer: observer == null");
        }
    }

    private void onJoinRoom() {
        Log.d(TAG, "onJoinRoom: ");
    }


    public void setPeerConnection(PeerConnection peerConnection){
        this.peerConnection = peerConnection;
        this.observer = initObserver(this);
    }

    private MySdpObserver initObserver(WebSocketClient webSocketClient) {
        Log.d(TAG, "MySdpObserver initObserver");
        return new MySdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e(TAG, "onCreateSuccess sessionDescription = " + sessionDescription);
                //将会话描述设置在本地
                webSocketClient.peerConnection.setLocalDescription(this, sessionDescription);
                SessionDescription localDescription = webSocketClient.peerConnection.getLocalDescription();
                SessionDescription.Type type = localDescription.type;
                Log.e(TAG, "onCreateSuccess == " + " type == " + type);
                //接下来使用之前的WebSocket实例将offer发送给服务器
                if (type == SessionDescription.Type.OFFER) {
                    //呼叫
                    webSocketClient.onOffer(sessionDescription);
                } else if (type == SessionDescription.Type.ANSWER) {
                    //应答
                    webSocketClient.onAnswer(sessionDescription);
                } else if (type == SessionDescription.Type.PRANSWER) {
                    //再次应答

                }
            }
        };

    }

    @Override
    public void onOffer(SessionDescription sdp) {
        Log.d(TAG, "onOffer: SessionDescription ->" + sdp);
        if (null != this.meter){
            this.meter.setSessionDescription(sdp);
            RTC_Message message = new RTC_Message(OFFER, this.meter);
            this.send(message.toString());
        }
    }

    @Override
    public void onAnswer(SessionDescription sdp) {
        Log.d(TAG, "onAnswer: SessionDescription ->" + sdp);
        if (null != this.meter){
            this.meter.setSessionDescription(sdp);
            RTC_Message msg = new RTC_Message(OFFER, this.meter);
            this.send(msg.toString());
        }
    }

    @Override
    public void onCandidate(RTC_ConnectMeter meter) {
        Log.d(TAG, "onCandidate: msg =>"+meter);
        IceCandidate iceCandidate = meter.getIceCandidate();
        if (null != iceCandidate){
            Log.d(TAG, "onCandidate iceCandidate="+iceCandidate);
            peerConnection.addIceCandidate(iceCandidate);
        }else {
            Log.d(TAG, "onCandidate iceCandidate=null");
        }
    }

    @Override
    public ArrayList<RTC_User> onUpdateUserList(Object msg) {
        Log.d(TAG, "onUpdateUserList: msg => "+msg);
        RTC_Message getMsg = new Gson().fromJson(msg.toString(),RTC_Message.class);
        ArrayList<RTC_User> users = new Gson().fromJson(getMsg.getData().toString(), ArrayList.class);
        Log.d(TAG, "onUpdateUserList: sendMsg ->"+users);
        return users;
    }

    @Override
    public void onHangUp(Object msg) {
        Log.d(TAG, "onHangUp!");
    }

    @Override
    public void onLeaveRoom(Object msg) {
        Log.d(TAG, "onLeaveRoom!");
    }

    @Override
    public void heartPackage(Object msg) {
        Log.d(TAG, "服务心跳包!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose! reason => "+reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError! ex=> "+ex);
    }
}
