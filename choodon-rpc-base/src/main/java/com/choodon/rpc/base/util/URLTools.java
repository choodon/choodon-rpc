package com.choodon.rpc.base.util;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.protocol.RPCResponse;

import java.util.Map;

public class URLTools {

    private URLTools() {
    }

    public static URL createProtocolURL(String protocol, String host, Integer port, Map<String, String> parameters) {
        parameters.put(URLParamType.transportProtocol.getName(), protocol);
        parameters.put(URLParamType.transportHost.getName(), host);
        parameters.put(URLParamType.transportPort.getName(), port.toString());
        return new URL(protocol, host, port, null, parameters);
    }

    public static URL createProtocolURL(String protocolURL) {
        URL url = URL.valueOf(protocolURL);
        url.addParameter(URLParamType.transportProtocol.getName(), url.getProtocol());
        url.addParameter(URLParamType.transportHost.getName(), url.getHost());
        url.addParameter(URLParamType.transportPort.getName(), url.getPort().toString());
        return url;
    }

    public static URL createServiceURL(String protocol, String serviceId, String group, String version, Map<String, String> parameters) {
        parameters.put(URLParamType.serviceProtocol.getName(), protocol);
        parameters.put(URLParamType.serviceId.getName(), serviceId);
        parameters.put(URLParamType.group.getName(), group);
        parameters.put(URLParamType.version.getName(), version);
        return new URL(protocol, null, null, serviceId, parameters);
    }

    public static URL createServiceURL(String serviceURL) {
        URL url = URL.valueOf(serviceURL);
        url.addParameter(URLParamType.serviceProtocol.getName(), url.getProtocol());
        url.addParameter(URLParamType.serviceId.getName(), url.getPath());
        return url;
    }

    public static URL createRegistryURL(String protocol, Map<String, String> parameters) {
        parameters.put(URLParamType.registry.getName(), protocol);
        return new URL(protocol, null, null, null, parameters);
    }

    public static URL createRegistryURL(String registryURL) {
        URL url = URL.valueOf(registryURL);
        url.addParameter(URLParamType.registry.getName(), url.getProtocol());
        return url;
    }


    public static URL buildRegistryProviderURL(URL protocolURL, URL serviceURL) {
        serviceURL.setHost(protocolURL.getHost());
        serviceURL.setPort(protocolURL.getPort());
        serviceURL.addParameters(protocolURL.getParameters());
        serviceURL.addParameter(URLParamType.transportTool.getName(), protocolURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()));
        serviceURL.addParameter(URLParamType.transportProtocol.getName(), protocolURL.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue()));
        serviceURL.addParameter(RPCConstants.CATAGRAY, RPCConstants.PROVIDER_CATAGRAY);
        return serviceURL;
    }

    public static URL buildRegistryConsumerURL(URL protocolURL, URL serviceURL) {
        serviceURL.setHost(protocolURL.getHost());
        serviceURL.setPort(protocolURL.getPort());
        serviceURL.addParameters(protocolURL.getParameters());
        serviceURL.addParameter(RPCConstants.CATAGRAY, RPCConstants.CONSUMER_CATAGRAY);
        return serviceURL;
    }

    public static String getProtocolKey(URL protocolURL) {
        return protocolURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()) + "." + protocolURL.getProtocol() + "://" + protocolURL.getHostPortStr();
    }

    public static String getRegistryKey(URL registryURL) {
        return registryURL.getProtocol() + "://0.0.0.0:0000/" + registryURL.getParameter(URLParamType.registyConnecting.getName());
    }

    public static String getServiceKey(URL serviceURL) {
        return serviceURL.getProtocol() + "//0.0.0.0:0000/" + serviceURL.getPath() + "/" + serviceURL.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) + "/" + serviceURL.getParameter(URLParamType.version.getName(), URLParamType.version.getValue());
    }

    public static String getExporterKey(URL protocolURL, URL registryURL) {
        return getProtocolKey(protocolURL) + "-" + getRegistryKey(registryURL);
    }

    public static String getRefererKey(URL serviceURL) {
        return serviceURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()) + "." + serviceURL.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue()) + "//" + serviceURL.getHostPortStr();
    }

    public static String getClusterKey(URL interfaceURL, URL protocolURL, URL registryURL) {
        return getServiceKey(interfaceURL) + "_" + getProtocolKey(protocolURL) + "_" + getRegistryKey(registryURL);
    }


}
