package com.choodon.rpc.base;


import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RPCFuture {
    private RPCRequest request;
    private static ConcurrentHashMap<String, RPCFuture> futureContainer = new ConcurrentHashMap<>();
    private RPCResponse response;

    public RPCFuture(RPCRequest request) {
        this.request = request;
    }


    public RPCResponse get() {
        return RPCContext.get(request);
    }

    public static RPCFuture create() {
        RPCFuture future = new RPCFuture(RPCContext.getRequest());
        RPCContext.addResponse(RPCContext.getId(), null);
        futureContainer.put(RPCContext.getId(), future);
        return future;
    }

    public RPCResponse get(long timeout, TimeUnit unit) {
        long timeOut = unit.toMillis(timeout);
        return RPCContext.get(request.getId(), timeOut);
    }

    public boolean isDone() {
        return null != RPCContext.getResponse(request.getId());
    }

    public static RPCFuture removePRCFuture(String id) {
        return futureContainer.remove(id);
    }

    public void setResponse(RPCResponse response) {
        this.response = response;
    }

}
