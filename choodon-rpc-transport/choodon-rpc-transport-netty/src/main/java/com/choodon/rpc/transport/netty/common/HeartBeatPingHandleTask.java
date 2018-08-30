package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.HeartBeatPong;
import com.choodon.rpc.base.util.NetUtil;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatPingHandleTask implements RejectedRunnable {

    private HeartBeatPing heartBeatPing;

    private ChannelHandlerContext channelHandlerContext;

    public HeartBeatPingHandleTask(HeartBeatPing heartBeatPing, ChannelHandlerContext channelHandlerContext) {
        this.heartBeatPing = heartBeatPing;
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void rejected() {
        String client_Server = NetUtil.getHostAndPortStr(channelHandlerContext.channel().remoteAddress()) + "-->" + NetUtil.getHostAndPortStr(channelHandlerContext.channel().localAddress());
        LoggerUtil.info(client_Server + "heartbeat is busy now.");
        write();
    }

    @Override
    public void run() {
        String client_Server = NetUtil.getHostAndPortStr(channelHandlerContext.channel().remoteAddress()) + "-->" + NetUtil.getHostAndPortStr(channelHandlerContext.channel().localAddress());
        String server_client = NetUtil.getHostAndPortStr(channelHandlerContext.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(channelHandlerContext.channel().remoteAddress());
        LoggerUtil.info(client_Server + " receive  heartbeat ping");
        write();
        LoggerUtil.info(server_client + " send  heartbeat pong");

    }

    private void write() {
        HeartBeatPong heartBeatPong = new HeartBeatPong(heartBeatPing.getId());
        channelHandlerContext.writeAndFlush(heartBeatPong);
    }

}