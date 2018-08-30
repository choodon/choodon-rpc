package com.choodon.rpc.base.protocol;


import com.choodon.rpc.base.common.URLParamType;

public class HeartBeatPong extends Holder {


    public HeartBeatPong(String id) {
        headers.put(URLParamType.id.getName(), id);
    }
}
