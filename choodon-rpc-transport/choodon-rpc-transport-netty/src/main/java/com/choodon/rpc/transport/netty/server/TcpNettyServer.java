package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.transport.netty.common.*;
import com.choodon.rpc.transport.netty.handler.TcpServerChannelInitializer;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;

@SpiMeta(name = RPCConstants.NETTY_TCP)
public class TcpNettyServer extends AbstractNettyServer {
    private ChannelFuture channelFuture;

    @Override
    public void startup() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        if (isNativeEt()) {
            serverBootstrap.channelFactory(NettyChannelFactory.NATIVE_SERVER);
        } else {
            serverBootstrap.channelFactory(NettyChannelFactory.NIO_SERVER);
        }
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.option(ChannelOption.SO_RCVBUF, 65535);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
        serverBootstrap.childOption(ChannelOption.SO_SNDBUF, 65535);
        serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 65535);
        serverBootstrap.childHandler(new TcpServerChannelInitializer());
        try {
            channelFuture = serverBootstrap.bind(protocolURL.getPort()).sync();
        } catch (InterruptedException e) {
            LoggerUtil.error("server start exception", e);
            throw new RPCFrameworkException("server start exception");
        }

    }

    @Override
    public void shutdwon() {
        channelFuture.channel().close();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}
