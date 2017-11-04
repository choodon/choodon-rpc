package com.choodon.rpc.framework.cluster.ha;


import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;

import java.util.List;

@Spi(scope = Scope.SINGLETON)
public interface HaStrategy {
    RPCResponse syncCall(RPCRequest request, List<Referer> referers, LoadBalance loadBalance) throws Exception;


    RPCFuture asyncCall(RPCRequest request, List<Referer> referers, LoadBalance loadBalance) throws Exception;

    void callback(RPCRequest request, RPCCallback callBack, List<Referer> referers, LoadBalance loadBalance) throws Exception;

}
