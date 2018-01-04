package com.choodon.rpc.transport.netty.handler;


import com.choodon.rpc.transport.netty.handler.codec.TcpProtocolDecoder;
import com.choodon.rpc.transport.netty.handler.codec.TcpProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final TcpClientHandler clientTCPHandler = new TcpClientHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new TcpProtocolDecoder());
        channelPipeline.addLast(new TcpProtocolEncoder());
        channelPipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
        channelPipeline.addLast(clientTCPHandler);
    }
}
