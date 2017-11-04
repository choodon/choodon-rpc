package com.choodon.rpc.base.protocol;


import com.choodon.rpc.base.common.RPCConstants;

public class HeartBeatPong extends Response {


    public HeartBeatPong(Long id) {
        headers.put(RPCConstants.ID, String.valueOf(id));
    }
}
