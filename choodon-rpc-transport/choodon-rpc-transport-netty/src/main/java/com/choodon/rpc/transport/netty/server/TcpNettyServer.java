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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@SpiMeta(name = RPCConstants.NETTY_TCP)
public class TcpNettyServer extends AbstractNettyServer {
    private ChannelFuture channelFuture;

    @Override
    public void startup() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
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
    }

}
