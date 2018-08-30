package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractServerHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicLongMap COUNTER_CONTAINER = AtomicLongMap.create();
    private static final AtomicInteger CHANNEL_COUNTER = new AtomicInteger(0);
    protected static ExecutorService executorService;

    public AbstractServerHandler(URL protocolURL) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(protocolURL.getIntParameter(URLParamType.businessThreadNum.getName(), URLParamType.businessThreadNum.getIntValue()));
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int counter = CHANNEL_COUNTER.incrementAndGet();
        LoggerUtil.info(NetUtil.getHostAndPortStr(ctx.channel().remoteAddress()) + " connected .");
        LoggerUtil.info("Active channel number: " + counter);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        int counter = CHANNEL_COUNTER.decrementAndGet();
        String hostAndPortStr = NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
        COUNTER_CONTAINER.remove(hostAndPortStr);
        LoggerUtil.info(hostAndPortStr + " disconnected .");
        LoggerUtil.info("Active channel number: " + counter);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            String hostAndPortStr = NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            if (COUNTER_CONTAINER.get(hostAndPortStr) >= 3) {
                ctx.channel().close().sync();
            } else {
                COUNTER_CONTAINER.incrementAndGet(hostAndPortStr);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String hostAndPortStr = NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
        ctx.channel().close().sync();
        LoggerUtil.error(hostAndPortStr + " happened exception", cause);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelConfig config = ch.config();

        // 高水位线: ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK
        // 低水位线: ChannelOption.WRITE_BUFFER_LOW_WATER_MARK
        if (!ch.isWritable()) {
            // 当前channel的缓冲区(OutboundBuffer)大小超过了WRITE_BUFFER_HIGH_WATER_MARK
            LoggerUtil.warn("{} is not writable, high water mask: {}, the number of flushed entries that are not written yet: {}.",
                    ch, config.getWriteBufferHighWaterMark(), ch.unsafe().outboundBuffer().size());
            config.setAutoRead(false);
        } else {
            // 曾经高于高水位线的OutboundBuffer现在已经低于WRITE_BUFFER_LOW_WATER_MARK了
            LoggerUtil.warn("{} is writable(rehabilitate), low water mask: {}, the number of flushed entries that are not written yet: {}.",
                    ch, config.getWriteBufferLowWaterMark(), ch.unsafe().outboundBuffer().size());
            config.setAutoRead(true);
        }
    }

}
