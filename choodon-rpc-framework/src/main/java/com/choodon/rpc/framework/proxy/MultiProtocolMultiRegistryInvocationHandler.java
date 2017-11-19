package com.choodon.rpc.framework.proxy;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.util.MethodUtil;
import com.choodon.rpc.framework.cluster.Cluster;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MultiProtocolMultiRegistryInvocationHandler implements InvocationHandler {
    private List<Cluster> target;


    public MultiProtocolMultiRegistryInvocationHandler(List<Cluster> target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DataArea dataArea = new DataArea();
        dataArea.setArgs(args);
        Cluster cluster = selectCluster();
        URL mergerURL = cluster.getMergerURL();
        String serviceKey = new StringBuilder(mergerURL.getPath() + "-").append(mergerURL.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) + "-")
                .append(mergerURL.getParameter(URLParamType.version.getName(), URLParamType.version.getValue())).toString();
        dataArea.addParameter(RPCConstants.SERVICE_HAND_ID, serviceKey + "-" + MethodUtil.getMethodDes(method));
        dataArea.addParameter(URLParamType.requestType.getName(), mergerURL.getParameter(URLParamType.requestType.getName(), URLParamType.requestType.getValue()));
        RPCRequest request = new RPCRequest();
        String serialization = mergerURL.getParameter(URLParamType.serialize.getName(), URLParamType.serialize.getValue());
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization);
        request.setSerializer(serialization);
        request.addParameter(RPCConstants.CLUSTER_KEY, mergerURL.getParameter(RPCConstants.CLUSTER_KEY));
        request.setBytes(serializer.writeObject(dataArea));
        RPCResponse response = cluster.syncCall(request);
        if (response == null) {
            return null;
        }
        return response.getData();
    }

    private Cluster selectCluster() {
        int size = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < target.size(); i++) {
            if (target.get(i).isAvailable()) {
                sb.append(i + ",");
                size++;
            }
        }
        if (size == 0) {
            throw new RPCFrameworkException(target.get(0).getMergerURL() + " has no providers.");
        }
        sb.setLength(sb.length() - 1);
        String[] args = sb.toString().split(",");
        int index = ThreadLocalRandom.current().nextInt(0, args.length);
        return target.get(Integer.parseInt(args[index]));
    }
}
