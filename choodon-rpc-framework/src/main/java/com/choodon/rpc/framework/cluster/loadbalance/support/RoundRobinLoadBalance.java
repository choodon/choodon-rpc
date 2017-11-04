package com.choodon.rpc.framework.cluster.loadbalance.support;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;
import com.google.common.util.concurrent.AtomicLongMap;

import java.util.List;

@SpiMeta(name = RPCConstants.LOAD_BALANCE_ROUNDROBIN)
public class RoundRobinLoadBalance implements LoadBalance {
    private static final AtomicLongMap COUNTER_CONTAINER = AtomicLongMap.create();


    @Override
    public Referer select(RPCRequest request, List<Referer> referers) {
        Long counter = COUNTER_CONTAINER.getAndIncrement(request.getParameterValue(RPCConstants.CLUSTER_KEY));
        Long size = (long) referers.size();
        Long remainder = counter % size;
        return referers.get(remainder.intValue());
    }
}
