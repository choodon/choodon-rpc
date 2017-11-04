package com.choodon.rpc.base.common;


import java.util.HashMap;
import java.util.Map;

public class DataArea {
    private Map<String, String> header = new HashMap<>();
    private Object[] args;

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public boolean containsParameterKey(String key) {
        return header.containsKey(key);
    }

    public void addParameter(String key, String value) {
        header.put(key, value);
    }

    public Byte getParameterByteValue(String key) {
        if (header.containsKey(key)) {
            return Byte.parseByte(header.get(key));
        }
        return null;
    }


    public Character getParameterCharacterValue(String key) {
        if (header.containsKey(key)) {
            return header.get(key).charAt(0);
        }
        return null;
    }

    public String getParameterValue(String key) {
        return header.get(key);
    }

    public Integer getParameterIntValue(String key) {
        if (header.containsKey(key)) {
            return Integer.parseInt(header.get(key));
        }
        return null;
    }

    public Long getParameterLongValue(String key) {
        if (header.containsKey(key)) {
            return Long.parseLong(header.get(key));
        }
        return null;
    }

    public Double getParameterDoubleValue(String key) {
        if (header.containsKey(key)) {
            return Double.parseDouble(header.get(key));
        }
        return null;
    }

    public Float getParameterFloatValue(String key) {
        if (header.containsKey(key)) {
            return Float.parseFloat(header.get(key));
        }
        return null;
    }

    public Boolean getParameterBooleanValue(String key) {
        if (header.containsKey(key)) {
            return Boolean.parseBoolean(header.get(key));
        }
        return null;
    }

    public String getHandlerId() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(header.get(RPCConstants.SERVICE_ID)).append("-").append(header.get(RPCConstants.GROUP))
//                .append("-").append(header.get(RPCConstants.VERSION)).append("-")
//                .append(header.get(RPCConstants.METHOD_DES));
        return header.get(RPCConstants.SERVICE_HAND_ID);
    }

}
