package com.choodon.rpc.framework.proxy;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.util.MethodUtil;
import com.choodon.rpc.framework.cluster.Cluster;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SingleProtocolSingleRegistryInvocationHandler implements InvocationHandler {
    private Cluster target;

    public SingleProtocolSingleRegistryInvocationHandler(Cluster target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DataArea dataArea = new DataArea();
        dataArea.setArgs(args);
        URL mergerURL = target.getMergerURL();
        dataArea.setHeader(mergerURL.getParameters());
        String serviceDes = new StringBuilder(mergerURL.getPath() + "-").append(mergerURL.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) + "-")
                .append(mergerURL.getParameter(URLParamType.version.getName(), URLParamType.version.getValue())).toString();
        dataArea.addParameter(RPCConstants.SERVICE_HAND_ID, serviceDes + "-" + MethodUtil.getMethodDes(method));
        RPCRequest request = new RPCRequest();
        String serialization = mergerURL.getParameter(URLParamType.serialize.getName(), URLParamType.serialize.getValue());
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization);
        request.setSerializer(serialization);
        /**
         * 做轮询负载的时候用到RPCConstants.CLUSTER_KEY
         */
        request.addParameter(RPCConstants.CLUSTER_KEY, mergerURL.getParameter(RPCConstants.CLUSTER_KEY));
        request.setBytes(serializer.writeObject(dataArea));
        RPCResponse response = target.syncCall(request);
        if (response==null){
            return null;
        }
        return response.getData();
    }
}
