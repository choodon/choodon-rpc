package com.choodon.rpc.base;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.exception.RPCTimeOutException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.HeartBeatPong;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.serialization.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RPCContext {

    private static ThreadLocal<RPCRequest> requestHolder = new ThreadLocal();
    private static ThreadLocal<RPCResponse> responseHolder = new ThreadLocal();
    private static ConcurrentHashMap<String, RPCResponse> responsesContainer = new ConcurrentHashMap<>();
    private static ThreadLocal<HeartBeatPing> heartBeatPingHolder = new ThreadLocal();
    private static ThreadLocal<HeartBeatPong> heartBeatPongHolder = new ThreadLocal();
    private static ConcurrentHashMap<String, HeartBeatPong> heartBeatPongContainer = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, CountDownLatch> countDownLatchContainer = new ConcurrentHashMap<>();

    public static void setRequest(RPCRequest request) {
        requestHolder.set(request);
    }

    public static void setHeartBeatPing(HeartBeatPing heartBeatPing) {
        heartBeatPingHolder.set(heartBeatPing);
    }

    public static RPCRequest getRequest() {
        return requestHolder.get();
    }

    public static void removeRequest() {
        requestHolder.remove();
    }

    public static void setResponse(RPCResponse response) {
        responseHolder.set(response);
    }

    public static RPCResponse getResponse() {
        return responseHolder.get();
    }

    public static void removeResponse() {
        responseHolder.remove();
    }

    public static String getId() {
        return requestHolder.get().getId();
    }

    public static String getResponseId() {
        return responseHolder.get().getId();
    }


    public static void addResponse(String key, RPCResponse response) {
        responsesContainer.put(key, response);
    }

    public static void removeResponse(Long key) {
        responsesContainer.remove(key);
    }

    public static RPCResponse syncGet() {
        long timeOut = getRequest().getParameterLongValue(URLParamType.timeOut.getName(), URLParamType.timeOut.getLongValue());
        String id = getId();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatchContainer.put(id, countDownLatch);
        boolean success = false;
        try {
            success = countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            countDownLatchContainer.remove(id);
            LoggerUtil.error("watting  response  is Interrupted ", e);
            throw new RPCFrameworkException("watting  response  is Interrupted");
        }
        if (!success) {
            countDownLatchContainer.remove(id);
            throw new RPCTimeOutException("service timeout");
        }
        countDownLatchContainer.remove(id);
        removeRequest();
        return responsesContainer.remove(id);

    }

    public static void receiveResponse(RPCResponse response) throws IOException {
        String serializationType = response.getParameterValue(URLParamType.serialize.getName());
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializationType);
        Map<String, String> header = serializer.deserialize(response.getHeaderBytes(), HashMap.class);
        response.setHeaders(header);
        String requestType = response.getParameterValue(URLParamType.requestType.getName(), URLParamType.requestType.getValue());
        if (requestType.equalsIgnoreCase(RPCConstants.CALL_TYPE_SYNC)) {
            String id = response.getId();
            if (countDownLatchContainer.containsKey(id)) {
                responsesContainer.put(id, response);
            } else {
                return;
            }
            countDownLatchContainer.get(id).countDown();
        } else if (requestType.equalsIgnoreCase(RPCConstants.CALL_TYPE_ASYNC_FUTURE)) {
            RPCFuture future = RPCFuture.removePRCFuture(response.getId());
            future.setResponse(response);
        } else if (requestType.equalsIgnoreCase(RPCConstants.CALL_TYPE_ASYNC_CALLBACK)) {
            RPCCallback.callbackContainer.remove(response.getId()).callback((RPCResponse) response);
        }
    }

    public static RPCResponse get(RPCRequest request) {
        String id = request.getId();
        long timeOut = request.getParameterLongValue(URLParamType.timeOut.getName(), URLParamType.timeOut.getLongValue());
        return get(id, timeOut);
    }

    public static RPCResponse getResponse(String id) {
        return responsesContainer.get(id);
    }

    public static RPCResponse get(String id, long timeOut) {
        if (null != responsesContainer.get(id)) {
            return responsesContainer.remove(id);
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            countDownLatchContainer.put(id, countDownLatch);
            boolean success = false;
            try {
                success = countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LoggerUtil.error("watting  response  is Interrupted ", e);
                throw new RPCFrameworkException("watting  response  is Interrupted");
            }
            if (!success) {
                throw new RPCTimeOutException("service timeout");
            }
            countDownLatchContainer.remove(id);
            return responsesContainer.remove(id);
        }
    }


}
