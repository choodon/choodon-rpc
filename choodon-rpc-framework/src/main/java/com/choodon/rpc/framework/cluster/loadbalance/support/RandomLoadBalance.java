package com.choodon.rpc.framework.cluster.loadbalance.support;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpiMeta(name = RPCConstants.LOAD_BALANCE_RANDOM)
public class RandomLoadBalance implements LoadBalance {


    @Override
    public Referer select(RPCRequest request, List<Referer> referers) {
        int index = ThreadLocalRandom.current().nextInt(0, referers.size());
        return referers.get(index);
    }
}
