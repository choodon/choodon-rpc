package com.choodon.rpc.example.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.example.service.HelloWorldService;
import com.choodon.rpc.framework.Interface;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    private static URL protocol = URL.valueOf("choodon://0.0.0.0:8080/?transportProtocol=tcp");
    private static URL registry1 = URL.valueOf("zookeeper://0.0.0.0:0000/?registyConnecting=127.0.0.1:2181");
    private static URL interfaceURL = URL.valueOf("choodon://0.0.0.0:0000/com.choodon.rpc.example.service.HelloWorldServiceImpl");

    public static void main(String[] args) throws InterruptedException {
        List<URL> registryList = new ArrayList<>();
        registryList.add(registry1);
//        registryList.add(registry2);
        List<URL> protocolList = new ArrayList<>();
        protocolList.add(protocol);

        HelloWorldService helloWorldService = Interface.getRef(protocolList, registryList, interfaceURL, HelloWorldService.class);
        String hello = null;
        long time = System.currentTimeMillis();
        Thread.currentThread().sleep(10000);
        for (int i = 0; i < 1000000; i++) {
            try {
                hello = helloWorldService.sayHello("xx");

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i % 10000 == 0) {
                System.out.println("tps=" + (i / (System.currentTimeMillis() - time) * 1000));
            }
        }


    }
}
