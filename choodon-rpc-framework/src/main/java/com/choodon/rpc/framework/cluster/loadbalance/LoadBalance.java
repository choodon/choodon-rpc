package com.choodon.rpc.framework.cluster.loadbalance;

import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.framework.referer.Referer;

import java.util.List;

@Spi(scope = Scope.SINGLETON)
public interface LoadBalance {
    Referer select(RPCRequest request, List<Referer> referers);
}