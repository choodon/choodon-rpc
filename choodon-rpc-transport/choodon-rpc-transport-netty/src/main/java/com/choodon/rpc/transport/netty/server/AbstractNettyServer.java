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
    protected final HashedWheelTimer timer = new HashedWheelTimer(new NamedThreadFactory("server.timer"));

    @Override
    public void init(URL protocolURL) {
        this.protocolURL = protocolURL;
        ThreadFactory bossFactory = new DefaultThreadFactory("rpc.server.boss", Thread.MAX_PRIORITY);
        ThreadFactory workerFactory = new DefaultThreadFactory("rpc.server.worker", Thread.MAX_PRIORITY);
        boss = initEventLoopGroup(SystemUtil.getProcessorCoreSize(), bossFactory);
        worker = initEventLoopGroup(
                protocolURL.getIntParameter(URLParamType.workThreadNum.getName(), URLParamType.workThreadNum.getIntValue())
                , workerFactory);
        serverBootstrap = new ServerBootstrap().group(boss, worker);
    }

    private EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory tFactory) {
        return isNativeEt() ? new EpollEventLoopGroup(nThreads, tFactory) : new NioEventLoopGroup(nThreads, tFactory);
    }

    protected boolean isNativeEt() {
        return NativeSupport.isSupportNativeET();
    }

}
