package com.choodon.rpc.base.protocol;


import com.choodon.rpc.base.common.RPCConstants;

public class HeartBeatPong extends Holder {


    public HeartBeatPong(String id) {
        headers.put(RPCConstants.ID, id);
    }
}
