package com.choodon.rpc.transport.api;

import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;


@Spi(scope = Scope.PROTOTYPE)
public interface TransportServer extends TransportService {
}
