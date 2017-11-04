package com.choodon.rpc.base;

import com.choodon.rpc.base.protocol.RPCResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RPCCallback {
    Map<Long, RPCCallback> callbackContainer = new ConcurrentHashMap<>();
    void callback(RPCResponse response);
}
