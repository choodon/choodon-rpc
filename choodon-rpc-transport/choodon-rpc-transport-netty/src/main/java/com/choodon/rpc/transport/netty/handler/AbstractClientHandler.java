package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class AbstractClientHandler extends ChannelInboundHandlerAdapter {

    protected static final AtomicLongMap COUNTER_CONTAINER = AtomicLongMap.create();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.info(NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + " successfully connected to " + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.info(NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + " disconnected to " + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()));
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
            LoggerUtil.warn(
                    "{} is not writable, high water mask: {}, the number of flushed entries that are not written yet: {}.",
                    ch, config.getWriteBufferHighWaterMark(), ch.unsafe().outboundBuffer().size());

            config.setAutoRead(false);
        } else {
            // 曾经高于高水位线的OutboundBuffer现在已经低于WRITE_BUFFER_LOW_WATER_MARK了
            LoggerUtil.warn(
                    "{} is writable(rehabilitate), low water mask: {}, the number of flushed entries that are not written yet: {}.",
                    ch, config.getWriteBufferLowWaterMark(), ch.unsafe().outboundBuffer().size());

            config.setAutoRead(true);
        }
    }

}
