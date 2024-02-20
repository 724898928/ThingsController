package com.lixin.activities;

import static com.lixin.util.MsgContant.AUDIO_TRACK_ID;
import static com.lixin.util.MsgContant.CHANNEL;
import static com.lixin.util.MsgContant.JOIN_ROOM;
import static com.lixin.util.MsgContant.LOCAL_AUDIO_STREAM;
import static com.lixin.util.MsgContant.LOCAL_VIDEO_STREAM;
import static com.lixin.util.MsgContant.VIDEO_TRACK_ID;
import static com.lixin.util.MsgContant.VOLUME;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lixin.model.RTC_ConnectMeter;
import com.lixin.model.RTC_Message;
import com.lixin.util.MsgContant;
import com.lixin.view.MySurfaceView;
import com.lixin.view.MyOpenGl;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private final static String TAG = MainActivity.class.getSimpleName();
    MyOpenGl myOpenGl;


    private EglBase eglBase;

    @NonNull
    private SurfaceViewRenderer remoteSurfaceView;
    private SurfaceViewRenderer localSurfaceView;

    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;

    private DataChannel channel;

    private ArrayList<PeerConnection.IceServer> iceServers;

    private List<String> streamList;

    private WebSocketClient webSocketClient;

    private AudioSource audioSource;
    private AudioTrack audioTrack;

    private VideoTrack videoTrack;
    public static final int VIDEO_RESOLUTION_WIDTH = 320;
    public static final int VIDEO_RESOLUTION_HEIGHT = 240;
    public static final int VIDEO_FPS = 30;

    private RTC_ConnectMeter meter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.main_layout);
        FrameLayout layout = findViewById(R.id.call_fragment_container);
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};
        if (!EasyPermissions.hasPermissions(this, perms)){
            EasyPermissions.requestPermissions(this,"Need permissions for net and camera & microphone", 0, perms);
        }
        Log.d(TAG, "onCreate: begin");
        // myOpenGl = new MyOpenGl(this);
        //myOpenGl.setRenderer(new SimpleRenderer());

        remoteSurfaceView = findViewById(R.id.remoterSV);
        localSurfaceView = findViewById(R.id.localSV);


        // 创建EglBase对象
        eglBase = EglBase.create();
        initView();
        startCall();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(initControlView(),layoutParams);

        Log.d(TAG, "onCreate: end");
        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    private MySurfaceView initControlView(){
        MySurfaceView mySurfaceView = new MySurfaceView(this);
        mySurfaceView.setAlpha(0);
        //
        mySurfaceView.setZOrderOnTop(true);
        mySurfaceView.setZOrderMediaOverlay(true);
        // 将布局中不展示内容的地方设置成透明.
        mySurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        return  mySurfaceView;
    }

    private void initView() {
        initSurfaceView(localSurfaceView);
        initSurfaceView(remoteSurfaceView);
    }

    private void initSurfaceView(SurfaceViewRenderer surfaceView) {
        surfaceView.init(eglBase.getEglBaseContext(), null);
        surfaceView.setMirror(true);
        surfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        surfaceView.setKeepScreenOn(true);
        surfaceView.setZOrderMediaOverlay(true);
        surfaceView.setEnableHardwareScaler(false);
        surfaceView.setFpsReduction(30);
    }

    private void startCall() {
        try {
            String webRTC_URL = this.getResources().getString(R.string.webRTC_URL);
            String room_id = this.getResources().getString(R.string.webRTC_roomId);
            String toUser = this.getResources().getString(R.string.webRTC_to_user);
            String mine = this.getResources().getString(R.string.webRTC_mineId);
            meter = new RTC_ConnectMeter(webRTC_URL, room_id, toUser, mine);
            RTC_Message message = new RTC_Message(JOIN_ROOM, meter);
            Log.d(TAG, "startCall: message => "+message);
            MyHandle myHandle = new MyHandle(this);
            webSocketClient = new WebSocketClient(this, URI.create(webRTC_URL), message, myHandle);
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Socket Exception:" + e.getMessage());
            Toast.makeText(this, "服务器连接异常!", Toast.LENGTH_SHORT).show();
        }
    }

    public void createPeerConnection() {
        Log.d(TAG, "createPeerConnection: ");
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(this)
                .setEnableInternalTracer(true)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption = true;
        options.disableNetworkMonitor = true;
        Log.d(TAG, "createPeerConnection:  options.disableNetworkMonito");
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBase.getEglBaseContext()))
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true))
                .setOptions(options)
                .createPeerConnectionFactory();
        // 配置stun穿透服务器 进行媒体协商
        iceServers = new ArrayList<>();
        String stun = getResources().getString(R.string.webRTC_stun);
        PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder(stun).createIceServer();
        iceServers.add(iceServer);
        Log.d(TAG, "createPeerConnection:  iceServers.add");
        streamList = new ArrayList<>();
        PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
        PeerConnectionObserver connectionObserver = getObserver();
        peerConnection = peerConnectionFactory.createPeerConnection(configuration, connectionObserver);

       /*
        DataChannel.Init 可配参数说明：
        ordered：是否保证顺序传输；
        maxRetransmitTimeMs：重传允许的最长时间；
        maxRetransmits：重传允许的最大次数；
        */
        DataChannel.Init init = new DataChannel.Init();
        if (null != peerConnection) {
            channel = peerConnection.createDataChannel(CHANNEL, init);
        }
        DataChannelObserver channelObserver = new DataChannelObserver();
        connectionObserver.setObserver(channelObserver);

        webSocketClient.setPeerConnection(peerConnection);
        startLocalVideoCapture(localSurfaceView);
        startLocalAudioCapture();
    }


    @NonNull
    private PeerConnectionObserver getObserver() {
        Log.d(TAG, "getObserver : ");
        return new PeerConnectionObserver() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "PeerConnectionObserver onIceCandidate : " + iceCandidate.toString());
                super.onIceCandidate(iceCandidate);
                setIceCandidate(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                Log.d(TAG, "onAddStream : " + mediaStream.toString());
                List<VideoTrack> videoTracks = mediaStream.videoTracks;
                if (videoTracks != null && videoTracks.size() > 0) {
                    VideoTrack videoTrack = videoTracks.get(0);
                    if (videoTrack != null) {
                        Log.d(TAG, "onAddStream  videoTrack= "+videoTrack);
                        videoTrack.addSink(remoteSurfaceView);
                    }else {
                        Log.d(TAG, "onAddStream  videoTrack= null");
                    }
                }
                List<AudioTrack> audioTracks = mediaStream.audioTracks;
                if (audioTracks != null && audioTracks.size() > 0) {
                    AudioTrack audioTrack = audioTracks.get(0);
                    if (audioTrack != null) {
                        audioTrack.setVolume(VOLUME);
                    }
                }
            }
        };
    }
    /**
     * 呼叫
     *
     * @param iceCandidate
     */
    private void setIceCandidate(IceCandidate iceCandidate) {
        if (null!= meter){
            meter.setIceCandidate(iceCandidate);
            RTC_Message msg = new RTC_Message(MsgContant.CANDIDATE, meter);
            if (null != webSocketClient) {
                Log.d(TAG, "onIceCandidate iceCandidate msg-> " + msg);
                webSocketClient.send(msg.toString());
            } else {
                Log.d(TAG, "onIceCandidate webSocketClient = null");
            }
        }
    }
    private void startLocalVideoCapture(SurfaceViewRenderer localSurfaceView) {
        VideoSource videoSource = peerConnectionFactory.createVideoSource(true);
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create(
                Thread.currentThread().getName(), eglBase.getEglBaseContext());
        VideoCapturer videoCapturer = createVideoCapturer();
        videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, VIDEO_FPS);
        videoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrack.addSink(localSurfaceView);
        MediaStream localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_VIDEO_STREAM);
        localMediaStream.addTrack(videoTrack);
        peerConnection.addTrack(videoTrack, streamList);
        peerConnection.addStream(localMediaStream);

    }

    private VideoCapturer createVideoCapturer() {
        if (Camera2Enumerator.isSupported(this)) {
            return createCameraCapturer(new Camera2Enumerator(this));
        } else {
            return createCameraCapturer(new Camera1Enumerator(true));
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        Log.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Looking for other cameras.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (null != videoCapturer) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private void startLocalAudioCapture() {
        // 语音
        MediaConstraints audioConstranints = new MediaConstraints();
        // 回声消除
        audioConstranints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        // 自动增益
        audioConstranints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        // 高音过滤
        audioConstranints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        // 噪音处理
        audioConstranints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        audioSource = peerConnectionFactory.createAudioSource(audioConstranints);
        audioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        MediaStream localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_AUDIO_STREAM);
        localMediaStream.addTrack(audioTrack);
        audioTrack.setVolume(VOLUME);
        peerConnection.addTrack(audioTrack, streamList);
        peerConnection.addStream(localMediaStream);

    }


    @TargetApi(17)
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    private void close() {
        if (null != peerConnection) {
            peerConnection.close();
        }
        if (null != webSocketClient) {
            webSocketClient.close();
        }
        if (null != remoteSurfaceView) {
            remoteSurfaceView.release();
        }
        if (null != localSurfaceView) {
            localSurfaceView.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: begin");
        // myOpenGl.onPause();
        close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  myOpenGl.onResume();
        Log.d(TAG, "onResume: begin");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权失败!", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
