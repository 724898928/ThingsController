package com.lixin.connectUtil;

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by li on 2018/10/14.
 */

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private NettyClient nettyClient = null;
    private String className = "com.li NettyClientHandler";
    private StringBuilder stringBuilder = new StringBuilder();
    public NettyClientHandler(NettyClient nettyClient) {
        super();
        Log.d(className, "NettyClientHandler init");
        this.nettyClient = nettyClient;
    }
//NettyClientHandler: 回复的消息：UnpooledByteBufAllocator$InstrumentedUnpooledHeapByteBuf(ridx: 0, widx: 19, cap: 64)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* super.channelRead(ctx, msg);*/
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                stringBuilder.append(Character.toString((char) in.readByte()));
            }
            Log.d(className, "回复的消息：" + stringBuilder);
            stringBuilder.setLength(0);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.d(className, "ClientHandler -------重连回调------");
        nettyClient.setConnectState(NettyClient.DISCONNECTION);
        nettyClient.connect();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Log.d(className, "NettyClientHandl registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Log.d(className, "NettyClientHandler =====连接成功回调=====");
        nettyClient.setConnectState(NettyClient.CONNECTED);
        super.channelActive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.d(className, "NettyClientHandl 网络异常!");
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
