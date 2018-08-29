package com.choodon.rpc.transport.api;

import com.choodon.rpc.base.common.URL;

public interface TransportService {
    void init(URL url);

    void startup();

    void shutdown();
}
