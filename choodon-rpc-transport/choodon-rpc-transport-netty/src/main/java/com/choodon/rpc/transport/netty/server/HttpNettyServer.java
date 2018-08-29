package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.transport.netty.handler.Http1ServerChannelInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@SpiMeta(name = RPCConstants.NETTY_HTTP)
public class HttpNettyServer extends AbstractNettyServer {
    private ChannelFuture channelFuture;

    @Override
    public synchronized void startup() {
        if (isStarted.get()) {
            return;
        }

//        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
//        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
//        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(new Http1ServerChannelInitializer(protocolURL));
        try {
            channelFuture = serverBootstrap.bind(protocolURL.getPort()).sync();
            isStarted.set(true);
        } catch (Exception e) {
            LoggerUtil.error("server start exception", e);
            throw new RPCFrameworkException("server start exception");
        }
    }

    @Override
    public void shutdown() {
        channelFuture.channel().close();
    }
}
