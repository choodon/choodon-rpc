package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.Request;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.transport.netty.common.RequestHandleTask;
import com.google.common.util.concurrent.AtomicLongMap;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class TcpServerHandler extends AbstractServerHandler {

    public TcpServerHandler(URL protocolURL) {
        super(protocolURL);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RPCRequest || msg instanceof HeartBeatPing) {
            Request request = (Request) msg;
            request.addParameter(URLParamType.transportProtocol.getName(), RPCConstants.TCP);
            RequestHandleTask task = new RequestHandleTask((Request) msg, ctx);
            if (null == executorService) {
                new Thread(task).start();
            } else {
                executorService.execute(task);
            }
        } else {
            LoggerUtil.error(msg.getClass().getCanonicalName() + " is illegal request msg .");
        }
        ReferenceCountUtil.safeRelease(msg);

    }
}
