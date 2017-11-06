package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.log.LoggerUtil;

public class HeartBeatPing extends Request {


    public HeartBeatPing(long id) {
        headers.put(RPCConstants.ID, String.valueOf(id));
    }

    public HeartBeatPing() {
        headers.put(RPCConstants.ID, String.valueOf(id.getAndIncrement()));
        LoggerUtil.info("ping id :"+getId());
    }

}
