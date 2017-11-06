package com.choodon.rpc.example.server;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.framework.ExporterContext;

public class ServerTest {
    private static URL protocol = URL.valueOf("tcp://0.0.0.0:8080/");
    private static URL registry = URL.valueOf("zookeeper://0.0.0.0:0000/?registyConnecting=127.0.0.1:2181");

    public static void main(String[] args) {
        ExporterContext.export("com.choodon.rpc.example.service", protocol, registry);
    }
}
