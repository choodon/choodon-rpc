package com.choodon.rpc.example.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.example.service.HelloWorldService;
import com.choodon.rpc.framework.Interface;

public class ClientTest {
    private static URL protocol = URL.valueOf("tcp://0.0.0.0:8080/");
    private static URL registry = URL.valueOf("zookeeper://0.0.0.0:0000/?registyConnecting=127.0.0.1:2181");
    private static URL interfaceURL = URL.valueOf("choodon://0.0.0.0:0000/com.choodon.rpc.example.service.HelloWorldServiceImpl");

    public static void main(String[] args) throws InterruptedException {
        HelloWorldService helloWorldService = Interface.getRef(protocol, registry, interfaceURL, HelloWorldService.class);
        String hello = null;
        for (int i = 0; i < 10000; i++) {
            try {
                Thread.currentThread().sleep(1000);
                hello = helloWorldService.sayHello("xx");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(hello);
        }


    }
}
