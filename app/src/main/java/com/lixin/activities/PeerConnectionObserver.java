package com.lixin.activities;

import static com.lixin.util.MsgContant.VOLUME;

import android.os.Message;
import android.util.Log;

import com.lixin.model.RTC_ConnectMeter;
import com.lixin.model.RTC_Message;
import com.lixin.util.MsgContant;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.List;

public class PeerConnectionObserver implements PeerConnection.Observer {

    private final String TAG = PeerConnectionObserver.class.getSimpleName();

    private DataChannelObserver channelObserver;

    private SurfaceViewRenderer remoteSurfaceView;

    private RTC_ConnectMeter meter;

    private WebSocketClient webSocketClient;

//    public PeerConnectionObserver(SurfaceViewRenderer remoteSurfaceView,  WebSocketClient webSocketClient,RTC_ConnectMeter meter) {
//        this.remoteSurfaceView = remoteSurfaceView;
//        this.webSocketClient = webSocketClient;
//        this.meter = meter;
//    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "onSignalingChange : " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "onIceConnectionChange : " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(TAG, "onIceConnectionReceivingChange : " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "onIceGatheringChange : " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
//        if (null != meter){
//            meter.setIceCandidate(iceCandidate);
//            RTC_Message msg = new RTC_Message(MsgContant.CANDIDATE, meter);
//            if (null != webSocketClient){
//                webSocketClient.send(msg.toString());
//            }else {
//                Log.d(TAG, "onIceCandidate webSocketClient = null");
//            }
//        }else {
//            Log.d(TAG, "onIceCandidate meter == null");
//        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {

        Log.d(TAG, "onAddStream: "+mediaStream);
//        List<VideoTrack> videoTracks = mediaStream.videoTracks;
//        if (null != videoTracks && videoTracks.size() > 0){
//            VideoTrack videoTrack = videoTracks.get(0);
//            if (null != videoTrack){
//                Log.d(TAG, "onAddStream videoTrack: "+videoTrack);
//                videoTrack.addSink(remoteSurfaceView);
//            } else {
//                Log.d(TAG, "onAddStream videoTrack == null:");
//            }
//        }
//        List<AudioTrack> audioTracks = mediaStream.audioTracks;
//        if (null != audioTracks && audioTracks.size() > 0){
//            AudioTrack audioTrack = audioTracks.get(0);
//            if (null != audioTrack){
//                audioTrack.setVolume(VOLUME);
//            }
//        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        dataChannel.registerObserver(channelObserver);
    }

    public void setObserver(DataChannelObserver channelObserver){
        this.channelObserver = channelObserver;
    }
    @Override
    public void onRenegotiationNeeded() {

    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }
}
