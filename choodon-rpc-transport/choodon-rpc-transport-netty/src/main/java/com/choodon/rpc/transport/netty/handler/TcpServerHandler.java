package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.enums.MsgTypeEnum;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.transport.netty.common.HeartBeatPingHandleTask;
import com.choodon.rpc.transport.netty.common.RPCRequestHandleTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class TcpServerHandler extends AbstractServerHandler {

    public TcpServerHandler(URL protocolURL) {
        super(protocolURL);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.instance(msg.getClass());
        Runnable task;
        switch (msgTypeEnum) {
            case REQUEST:
                RPCRequest rpcRequest = (RPCRequest) msg;
                rpcRequest.addParameter(URLParamType.transportProtocol.getName(), RPCConstants.TCP);
                task = new RPCRequestHandleTask(rpcRequest, ctx);
                break;
            case HEARTBEAT_PING:
                HeartBeatPing heartBeatPing = (HeartBeatPing) msg;
                heartBeatPing.addParameter(URLParamType.transportProtocol.getName(), RPCConstants.TCP);
                task = new HeartBeatPingHandleTask(heartBeatPing, ctx);
                break;
            default:
                throw new RPCFrameworkException("Illegal msg type class");

        }
        if (null == executorService) {
            new Thread(task).start();
        } else {
            executorService.execute(task);
        }
        ReferenceCountUtil.safeRelease(msg);
    }
}
