package com.choodon.rpc.base;


import com.choodon.rpc.base.protocol.Request;
import com.choodon.rpc.base.protocol.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RPCFuture {
    private Request request;
    private static ConcurrentHashMap<Long, RPCFuture> futureContainer = new ConcurrentHashMap<>();
    private Response response;

    public RPCFuture(Request request) {
        this.request = request;
    }


    public Response get() {
        return RPCContext.get(request);
    }

    public static RPCFuture create() {
        RPCFuture future = new RPCFuture(RPCContext.getRequest());
        RPCContext.addResponse(RPCContext.getRequestId(), null);
        futureContainer.put(RPCContext.getRequestId(), future);
        return future;
    }

    public Response get(long timeout, TimeUnit unit) {
        long timeOut = unit.toMillis(timeout);
        return RPCContext.get(request.getId(), timeOut);
    }

    public boolean isDone() {
        return null != RPCContext.getResponse(request.getId());
    }

    public static RPCFuture removePRCFuture(long id) {
        return futureContainer.remove(id);
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
