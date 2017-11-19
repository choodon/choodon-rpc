package com.choodon.rpc.base.util;

import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;


public class NetUtil {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static final String LOCAL_IP_ADDRESS;

    static {
        InetAddress localAddress;
        try {
            localAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            localAddress = null;
        }

        if (localAddress != null && isValidAddress(localAddress)) {
            LOCAL_IP_ADDRESS = localAddress.getHostAddress();
        } else {
            LOCAL_IP_ADDRESS = getFirstLocalAddress();
        }
    }

    public static String getLocalAddress() {
        return LOCAL_IP_ADDRESS;
    }

    /**
     * 获取网卡中第一个有效IP
     */
    private static String getFirstLocalAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":")) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return "127.0.0.1";
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public static boolean isValidIP(String ip) {
        return (ip != null && !"0.0.0.0".equals(ip) && !"127.0.0.1".equals(ip) && IP_PATTERN.matcher(ip).matches());
    }

    public static String getHostAndPortStr(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();

    }
}
