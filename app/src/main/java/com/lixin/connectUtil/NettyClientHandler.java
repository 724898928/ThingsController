package com.lixin.connectUtil;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by li on 2018/10/14.
 */

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private NettyClient nettyClient = null;
    private String className = "com.li NettyClientHandler";
    public NettyClientHandler(NettyClient nettyClient) {
        super();
        Log.d(className,"NettyClientHandler init");
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String strMsg = (String) msg;
        Log.d(className,"回复的消息：" + strMsg);
      //  new CommandDecoder(strMsg).decode();//将返回的消息进行解析
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.d(className,"ClientHandler -------重连回调------");
        nettyClient.setConnectState(NettyClient.DISCONNECTION);
        nettyClient.connect();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Log.d(className,"NettyClientHandl registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Log.d(className,"NettyClientHandler =====连接成功回调=====");
        nettyClient.setConnectState(NettyClient.CONNECTED);
        super.channelActive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.d(className,"NettyClientHandl 网络异常!");
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
