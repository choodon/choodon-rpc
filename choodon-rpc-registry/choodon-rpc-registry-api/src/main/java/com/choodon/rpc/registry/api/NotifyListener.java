package com.choodon.rpc.registry.api;

import com.choodon.rpc.base.common.URL;

public interface NotifyListener {
    void notify(URL url, EventTypeEnum envetType);
}
