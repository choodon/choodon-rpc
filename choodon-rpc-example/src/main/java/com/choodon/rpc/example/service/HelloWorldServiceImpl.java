package com.choodon.rpc.example.service;

import com.choodon.rpc.base.service.annotation.ServiceImpl;

@ServiceImpl(serviceId = "com.choodon.rpc.example.service.HelloWorldServiceImpl")
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " !";
    }
}
