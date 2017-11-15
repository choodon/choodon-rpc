package com.choodon.rpc.base.common;

import com.choodon.rpc.base.util.SystemUtil;

public enum URLParamType {
    haStrategy("haStrategy", RPCConstants.HA_STRATEGY_FAILOVER),
    loadBalance("loadBalance", RPCConstants.LOAD_BALANCE_ROUNDROBIN),
    cluster("cluster", RPCConstants.DEFAULT),
    serviceProtocol("serviceProtocol", RPCConstants.SERVICE_PROTOCOL),
    serviceId("serviceId", null),
    requestType("requestType", "sync"),
    timeOut("timeOut", "30000"),
    channelNum("channelNum", "10"),
    transportTool("transportTool", RPCConstants.NETTY),
    transportProtocol("transportProtocol", RPCConstants.TCP),
    transportHost("transportHost", "127.0.0.1"),
    transportPort("transportPort", "8080"),
    workThreadNum("workThreadNum", "100"),
    bossThreadNum("bossThreadNum", (SystemUtil.getProcessorCoreSize() * 2 + 1) + ""),
    bussinessThreadNum("bussinessThreadNum", "500"),
    registryRetryInterval("registryRetryInterval", "3000"),
    registrySessionTimeOut("registrySessionTimeOut", "6000"),
    registryTimeOut("registryTimeOut", "6000"),
    registyConnecting("registyConnecting", null),
    registry("registry", RPCConstants.ZOOKEEPER),
    serialize("serialization", RPCConstants.DEFAULT_SERIALIZATION),
    nodeType("nodeType", RPCConstants.NODE_TYPE_SERVICE),
    group("group", RPCConstants.DEFAULT_GROUP),
    serviceImplClassName("serviceImplClassName", null),
    version("version", RPCConstants.DEFAULT_VERSION);


    private String name;
    private String value;

    URLParamType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return Integer.parseInt(value);
    }

    public long getLongValue() {
        return Long.parseLong(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
