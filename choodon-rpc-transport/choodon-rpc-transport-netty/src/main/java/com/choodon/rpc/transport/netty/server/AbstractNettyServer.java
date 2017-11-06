package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.thread.NamedThreadFactory;
import com.choodon.rpc.base.util.NativeSupport;
import com.choodon.rpc.base.util.SystemUtil;
import com.choodon.rpc.transport.api.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractNettyServer implements TransportServer {
    protected boolean isStarted;
    protected URL protocolURL;
    protected ServerBootstrap serverBootstrap;
    protected static EventLoopGroup boss;
    protected static EventLoopGroup worker;

    @Override
    public void init(URL protocolURL) {
        this.protocolURL = protocolURL;
        NioEventLoopGroup boss = new NioEventLoopGroup(SystemUtil.getProcessorCoreSize() * 2 + 40);
        NioEventLoopGroup worker = new NioEventLoopGroup(protocolURL.getIntParameter(URLParamType.workThreadNum.getName(), URLParamType.workThreadNum.getIntValue()));
        serverBootstrap = new ServerBootstrap().group(boss, worker);
    }

}
