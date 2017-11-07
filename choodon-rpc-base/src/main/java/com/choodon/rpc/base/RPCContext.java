package com.choodon.rpc.base;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.exception.RPCTimeOutException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.protocol.Request;
import com.choodon.rpc.base.protocol.Response;
import com.choodon.rpc.base.serialization.Serializer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RPCContext {

    private static ThreadLocal<Request> requestHolder = new ThreadLocal();
    private static ThreadLocal<Response> responseHolder = new ThreadLocal();
    private static ConcurrentHashMap<Long, Response> responsesContainer = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, CountDownLatch> countDownLatchContainer = new ConcurrentHashMap<>();

    public static void setRequest(Request request) {
        requestHolder.set(request);
    }

    public static Request getRequest() {
        return requestHolder.get();
    }

    public static void removeRequest() {
        requestHolder.remove();
    }

    public static void setResponse(Response response) {
        responseHolder.set(response);
    }

    public static Response getResponse() {
        return responseHolder.get();
    }

    public static void removeResponse() {
        responseHolder.remove();
    }

    public static long getRequestId() {
        return requestHolder.get().getId();
    }

    public static long getResponseId() {
        return responseHolder.get().getId();
    }


    public static void addResponse(Long key, Response response) {
        responsesContainer.put(key, response);
    }

    public static void removeResponse(Long key) {
        responsesContainer.remove(key);
    }

    public static Response syncGet() {
        long timeOut = getRequest().getParameterLongValue(URLParamType.timeOut.getName(), URLParamType.timeOut.getLongValue());
        long id = getRequestId();
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

    public static void receviceResponse(Response response) {
        if (response instanceof RPCResponse) {
            String serializationType = response.getSerializer();
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializationType);
            DataArea dataArea = serializer.readObject(response.getBytes(), DataArea.class);
            response.setData(dataArea.getArgs()[0]);
            response.addParameter(dataArea.getHeader());
        }
        String requestType = response.getParameterValue(URLParamType.requestType.getName(), URLParamType.requestType.getValue());
        if (requestType.equalsIgnoreCase(RPCConstants.CALL_TYPE_SYNC)) {
            long id = response.getId();
            if(countDownLatchContainer.containsKey(id)){
                responsesContainer.put(id, response);
            }else{
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

    public static Response get(Request request) {
        long id = request.getId();
        long timeOut = request.getParameterLongValue(URLParamType.timeOut.getName(), URLParamType.timeOut.getLongValue());
        return get(id, timeOut);
    }

    public static Response getResponse(long id) {
        return responsesContainer.get(id);
    }

    public static Response get(long id, long timeOut) {
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
