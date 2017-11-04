package com.choodon.rpc.example.service;

import com.choodon.rpc.base.service.annotation.Service;

@Service
public interface HelloWorldService {
    String sayHello(String name);
}
