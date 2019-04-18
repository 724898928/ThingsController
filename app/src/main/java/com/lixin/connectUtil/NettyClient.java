package com.lixin.connectUtil;

import com.lixin.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by li on 2018/10/14.
 */

public class NettyClient{

    public static final int DISCONNECTION = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
    private String className = "com.li NettyClient";
    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;
    private ChannelFuture channelFuture = null;
    private static NettyClient nettyClient = null;
    private ArrayBlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(5000);
    private boolean sendFlag = true;
    private SendThread sendThread = new SendThread();

    private int connectState = DISCONNECTION;
    private boolean flag = true;
    private int port;
    private String inetHost;
    public static NettyClient getInstance() {
        if (nettyClient == null) {
            nettyClient = new NettyClient();

        }
        return nettyClient;
    }

    private NettyClient() {
        init();
    }

    private void init() {
        LogUtil.d(className, "init begin");
        port=8266;
        inetHost = "192.168.4.1";
        //port=8000;
        //inetHost = "192.168.4.2";
        setConnectState(DISCONNECTION);
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();

                //心跳包的添加
//                pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 60, 0));
//对消息格式进行验证（MessageDecoder为自定义的解析验证类因协议规定而定）
                LogUtil.d(className, "init  bootstrap.handler(new ChannelInitializer<SocketChannel>()");
              //  pipeline.addLast("messageDecoder", new MessageDecoder());
                pipeline.addLast("clientHandler", new NettyClientHandler(nettyClient));
            }
        });
        connect();
        startSendThread();
        LogUtil.d(className, "init end");
    }

    public void uninit() {
        stopSendThread();
        if (channelFuture != null) {
            channelFuture.channel().closeFuture();
            channelFuture.channel().close();
            channelFuture = null;
        }
        if (group != null) {
            group.shutdownGracefully();
            group = null;
            nettyClient = null;
            bootstrap = null;
        }
        setConnectState(DISCONNECTION);
        flag = false;
    }

    public void insertCmd(String cmd) {
        sendQueue.offer(cmd);
    }

    private void stopSendThread() {
        sendQueue.clear();
        sendFlag = false;
        sendThread.interrupt();
    }

    private void startSendThread() {
        sendQueue.clear();
        sendFlag = true;
        sendThread.start();
    }

    public void connect() {
        LogUtil.d(className, "ChannelFutureListener begin");
        if (getConnectState() != CONNECTED) {
            setConnectState(CONNECTING);
            channelFuture = bootstrap.connect(inetHost, port);
            channelFuture.addListener(listener);
        }
        LogUtil.d(className, "ChannelFutureListener end");
    }

    private ChannelFutureListener listener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            LogUtil.d(className, "ChannelFutureListener begin");
            if (future.isSuccess()) {
                channelFuture = future;
                setConnectState(CONNECTED);
            } else {
                setConnectState(DISCONNECTION);
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (flag) {
                            connect();
                            LogUtil.d(className, "ChannelFutureListener  connect()");
                        }
                    }
                }, 3L, TimeUnit.SECONDS);
            }
            LogUtil.d(className, "ChannelFutureListener end");
        }
    };

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public int getConnectState() {
        return connectState;
    }

    /**
     * 发送消息的线程
     */
    private class SendThread extends Thread {
        @Override
        public void run() {
            LogUtil.d(className, "SendThread running");
            while (sendFlag) {
              try {
                    String cmd = sendQueue.take();
                  LogUtil.d(className, "SendThread cmd = "+cmd);
                  if (channelFuture != null && cmd != null) {
                        channelFuture.channel().writeAndFlush(getSendByteBuf(cmd));
                      LogUtil.d(className, "SendThread cmd ==== ");
                    }else {
                      LogUtil.d(className, "SendThread channelFuture = null ");
                  }
                } catch (InterruptedException | UnsupportedEncodingException e) {
                  LogUtil.d(className, "SendThread InterruptedException ");
                    sendThread.interrupt();
                }
            }
            LogUtil.d(className, "SendThread end");
        }
    }

    private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {

        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);
        return pingMessage;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getInetHost() {
        return inetHost;
    }

    public void setInetHost(String inetHost) {
        this.inetHost = inetHost;
    }
}
