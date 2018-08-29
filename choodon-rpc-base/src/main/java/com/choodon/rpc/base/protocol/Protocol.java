package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.util.NumberUtils;
import io.netty.util.Signal;

public class Protocol {

    /**
     * PASS通行证
     */
    public static final short PASS = (short) 0xcd;

    public static final void checkPass(short pass) throws Signal {
        if (NumberUtils.notEquals(pass, Protocol.PASS)) {
            throw new RPCFrameworkException("ILLEGAL_PASS");
        }
    }


}
