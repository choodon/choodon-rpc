package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;

import java.util.HashMap;
import java.util.Map;

public class Holder {

    protected byte[] headerBytes;

    protected byte[] bodyBytes;


    protected Object data;

    protected byte[] bytes;

    protected Map<String, String> headers = new HashMap<>();

    protected Map<String, String> attachments = new HashMap<>();

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public long getId() {
        return getParameterLongValue(RPCConstants.ID);
    }

    public void addParameter(String key, String value) {
        headers.put(key, value);
    }

    public void addParameters(Map<String, String> parameters) {
        headers.putAll(parameters);
    }


    public String getParameterValue(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        } else {
            return null;
        }
    }

    public String getParameterValue(String key, String def) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        } else {
            return def;
        }
    }

    public Long getParameterLongValue(String key) {
        if (headers.containsKey(key)) {
            return Long.parseLong(headers.get(key));
        } else {
            return null;
        }
    }

    public Long getParameterLongValue(String key, Long def) {
        if (headers.containsKey(key)) {
            return Long.parseLong(headers.get(key));
        } else {
            return def;
        }
    }

    public Integer getParameterIntValue(String key) {
        if (headers.containsKey(key)) {
            return Integer.parseInt(headers.get(key));
        } else {
            return null;
        }
    }

    public Integer getParameterIntValue(String key, Integer def) {
        if (headers.containsKey(key)) {
            return Integer.parseInt(headers.get(key));
        } else {
            return def;
        }
    }

    public Byte getParameterByteValue(String key) {
        if (headers.containsKey(key)) {
            return Byte.parseByte(headers.get(key));
        } else {
            return null;
        }
    }

    public Byte getParameterByteValue(String key, Byte def) {
        if (headers.containsKey(key)) {
            return Byte.parseByte(headers.get(key));
        } else {
            return def;
        }
    }

    public Boolean getParameterBooleanValue(String key) {
        if (headers.containsKey(key)) {
            return Boolean.parseBoolean(headers.get(key));
        } else {
            return null;
        }
    }

    public Boolean getParameterBooleanValue(String key, boolean def) {
        if (headers.containsKey(key)) {
            return Boolean.parseBoolean(headers.get(key));
        } else {
            return def;
        }
    }


    public byte[] getHeaderBytes() {
        return headerBytes;
    }

    public void setHeaderBytes(byte[] headerBytes) {
        this.headerBytes = headerBytes;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
    }
}
