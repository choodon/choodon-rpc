package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.transport.api.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractNettyServer implements TransportServer {
    protected AtomicBoolean isStarted=new AtomicBoolean(false);
    protected URL protocolURL;
    protected ServerBootstrap serverBootstrap;
    protected static EventLoopGroup boss;
    protected static EventLoopGroup worker;

    @Override
    public void init(URL protocolURL) {
        this.protocolURL = protocolURL;
        NioEventLoopGroup boss = new NioEventLoopGroup((protocolURL.getIntParameter(URLParamType.bossThreadNum.getName(), URLParamType.bossThreadNum.getIntValue())));
        NioEventLoopGroup worker = new NioEventLoopGroup(protocolURL.getIntParameter(URLParamType.workThreadNum.getName(), URLParamType.workThreadNum.getIntValue()));
        serverBootstrap = new ServerBootstrap().group(boss, worker);
    }

}
