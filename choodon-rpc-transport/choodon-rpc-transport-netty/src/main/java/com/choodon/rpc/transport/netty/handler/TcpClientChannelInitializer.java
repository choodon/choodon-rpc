package com.choodon.rpc.transport.netty.handler;


import com.choodon.rpc.transport.netty.handler.codec.ProtocolDecoder;
import com.choodon.rpc.transport.netty.handler.codec.ProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        channelPipeline.addLast(new ProtocolDecoder());
        channelPipeline.addLast(new ProtocolEncoder());
        channelPipeline.addLast(new ClientTCPHandler());
    }
}
