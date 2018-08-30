package com.choodon.rpc.base.service;

public interface ServiceHandler {
    Object doHandler(Object... parameter);

    void doVoidHandler(Object... parameter);
}