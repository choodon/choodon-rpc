package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.thread.NamedThreadFactory;
import com.choodon.rpc.base.util.NativeSupport;
import com.choodon.rpc.base.util.SystemUtil;
import com.choodon.rpc.transport.api.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractNettyClient implements TransportClient {
    protected URL mergeURL;
    protected Bootstrap bootstrap;
    protected static EventLoopGroup group;

    @Override
    public void init(URL mergeURL) {
        this.mergeURL = mergeURL;
        group = new NioEventLoopGroup(SystemUtil.getProcessorCoreSize() * 2 + 1);
        bootstrap = new Bootstrap().group(group);
        bootstrap.channel(NioSocketChannel.class);
    }

}
