package com.choodon.rpc.example.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.example.service.HelloWorldService;
import com.choodon.rpc.framework.Interface;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    private static URL protocol = URL.valueOf("tcp://0.0.0.0:8080/");
    private static URL registry1 = URL.valueOf("zookeeper://0.0.0.0:0000/?registyConnecting=127.0.0.1:2181");
    private static URL registry2 = URL.valueOf("zookeeper://0.0.0.0:0000/?registyConnecting=127.0.0.1:2182");
    private static URL interfaceURL = URL.valueOf("choodon://0.0.0.0:0000/com.choodon.rpc.example.service.HelloWorldServiceImpl");

    public static void main(String[] args) throws InterruptedException {
        List<URL> registryList = new ArrayList<>();
        registryList.add(registry1);
        registryList.add(registry2);
        List<URL> protocolList = new ArrayList<>();
        protocolList.add(protocol);

        HelloWorldService helloWorldService = Interface.getRef(protocolList, registryList, interfaceURL, HelloWorldService.class);
        String hello = null;
//        for (int i = 0; i < 10000; i++) {
//            try {
////                Thread.currentThread().sleep(1000);
//                hello = helloWorldService.sayHello("xx");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println(hello);
//        }


    }
}
