package com.choodon.rpc.transport.netty.handler;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Http1ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final ClientHttpHandler clientHttpHandler = new ClientHttpHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new HttpClientCodec());
        channelPipeline.addLast(new HttpObjectAggregator(65536));
        channelPipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        channelPipeline.addLast(clientHttpHandler);
    }
}
