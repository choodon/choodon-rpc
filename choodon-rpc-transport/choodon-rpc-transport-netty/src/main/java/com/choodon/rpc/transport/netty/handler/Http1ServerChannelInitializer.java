package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.URL;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Http1ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static ServerHttpHandler serverHttpHandler;

    public Http1ServerChannelInitializer(URL protocolURL) {
        if (serverHttpHandler == null) {
            serverHttpHandler = new ServerHttpHandler(protocolURL);
        }

    }

    @Override
    public void initChannel(SocketChannel ch) {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new HttpServerCodec());
        channelPipeline.addLast(new HttpObjectAggregator(65536));
        channelPipeline.addLast(new CorsHandler(corsConfig));
        channelPipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        channelPipeline.addLast(serverHttpHandler);
    }

}