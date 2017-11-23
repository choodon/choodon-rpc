package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.transport.netty.handler.codec.TcpProtocolDecoder;
import com.choodon.rpc.transport.netty.handler.codec.TcpProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static ServerTCPHandler serverTCPHandler;

    public TcpServerChannelInitializer(URL protocolURL) {
        if (serverTCPHandler == null) {
            serverTCPHandler = new ServerTCPHandler(protocolURL);
        }
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addLast(new TcpProtocolDecoder());
        channelPipeline.addLast(new TcpProtocolEncoder());
        channelPipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        channelPipeline.addLast(serverTCPHandler);
    }
}
