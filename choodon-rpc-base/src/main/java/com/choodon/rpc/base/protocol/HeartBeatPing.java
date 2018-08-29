package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.log.LoggerUtil;

import java.util.UUID;

public class HeartBeatPing extends Holder {


    public HeartBeatPing(String id) {
        headers.put(RPCConstants.ID, id);
    }

    public HeartBeatPing() {
        headers.put(RPCConstants.ID, UUID.randomUUID().toString());
        LoggerUtil.info("ping id :" + getId());
    }

}
