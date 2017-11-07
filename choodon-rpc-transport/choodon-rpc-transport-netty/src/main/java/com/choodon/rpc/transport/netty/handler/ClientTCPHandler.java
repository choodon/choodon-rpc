package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCTimeOutException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.util.NetUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class ClientTCPHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicLongMap COUNTER_CONTAINER = AtomicLongMap.create();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        LoggerUtil.info(NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + " successfully connected to " + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.info(NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + " disconnected to " + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RPCResponse) {
            RPCContext.receviceResponse((Response) msg);
        } else if (msg instanceof HeartBeatPong) {
            String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            COUNTER_CONTAINER.getAndDecrement(client_Server);
            LoggerUtil.info(client_Server + " receive  heartbeat pong");
        } else {
            LoggerUtil.error(msg.getClass().getCanonicalName() + " is illegal response msg .");
        }
        ReferenceCountUtil.release(msg);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            HeartBeatPing request = new HeartBeatPing();
            RPCContext.setRequest(request);
            ctx.channel().writeAndFlush(request);
            String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            long counter = COUNTER_CONTAINER.getAndIncrement(client_Server);
            LoggerUtil.info(client_Server + " send  heartbeat ping");
            if (counter > 3) {
                ctx.channel().close().sync();
                LoggerUtil.error("Heartbeat Check failed 3 times,close " + client_Server + "channel. ");
            }


        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LoggerUtil.error(NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()) + "connection" + " happened exception", cause);
        ctx.close().sync();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelConfig config = ch.config();

        // 高水位线: ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK
        // 低水位线: ChannelOption.WRITE_BUFFER_LOW_WATER_MARK
        if (!ch.isWritable()) {
            // 当前channel的缓冲区(OutboundBuffer)大小超过了WRITE_BUFFER_HIGH_WATER_MARK
            if (LoggerUtil.isWarnEnabled()) {
                LoggerUtil.warn(
                        "{} is not writable, high water mask: {}, the number of flushed entries that are not written yet: {}.",
                        ch, config.getWriteBufferHighWaterMark(), ch.unsafe().outboundBuffer().size());
            }

            config.setAutoRead(false);
        } else {
            // 曾经高于高水位线的OutboundBuffer现在已经低于WRITE_BUFFER_LOW_WATER_MARK了
            if (LoggerUtil.isWarnEnabled()) {
                LoggerUtil.warn(
                        "{} is writable(rehabilitate), low water mask: {}, the number of flushed entries that are not written yet: {}.",
                        ch, config.getWriteBufferLowWaterMark(), ch.unsafe().outboundBuffer().size());
            }

            config.setAutoRead(true);
        }
    }

}
