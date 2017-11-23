package com.choodon.rpc.framework.cluster.ha;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.exception.RPCTimeOutException;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;

import java.util.List;

@SpiMeta(name = RPCConstants.HA_STRATEGY_FAILOVER)
public class FailoverHaStrategy implements HaStrategy {


    public RPCResponse syncCall(RPCRequest request, List<Referer> referers, LoadBalance loadBalance) throws Exception {
        boolean flag = true;
        int times = 0;
        RPCResponse rpcResponse = null;
        while (flag) {
            try {
                Referer referer = loadBalance.select(request, referers);
                rpcResponse = referer.syncCall(request);
            } catch (RPCTimeOutException e) {
                throw e;
            } catch (Exception e) {
                times++;
                LoggerUtil.error("syncCall failed " + times + " times.", e);
                if (times == 3) {
                    flag = false;
                }
            }
        }
        return rpcResponse;
    }


    public RPCFuture asyncCall(RPCRequest request, List<Referer> referers, LoadBalance loadBalance) throws Exception {
        Referer referer = loadBalance.select(request, referers);
        return referer.asyncCall(request);
    }

    public void callback(RPCRequest request, RPCCallback callBack, List<Referer> referers, LoadBalance loadBalance) throws Exception {
        Referer referer = loadBalance.select(request, referers);
        referer.callback(request, callBack);
    }
}
