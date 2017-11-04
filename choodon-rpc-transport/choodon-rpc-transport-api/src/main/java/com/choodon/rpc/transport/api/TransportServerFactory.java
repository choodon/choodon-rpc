package com.choodon.rpc.transport.api;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;


@Spi(scope = Scope.SINGLETON)
public interface TransportServerFactory {
    TransportServer createServer(URL protocolURL);

}
