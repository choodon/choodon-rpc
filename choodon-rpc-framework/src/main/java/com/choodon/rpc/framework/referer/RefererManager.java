package com.choodon.rpc.framework.referer;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.util.URLTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefererManager {
    private static Map<String, Referer> refererContainer = new ConcurrentHashMap<>();

    public static Referer create(URL mergerURL) {
        String refererKey = URLTools.getRefererKey(mergerURL);
        if (refererContainer.containsKey(refererKey)) {
            return refererContainer.get(refererKey);
        } else {
            Referer referer = new Referer(mergerURL);
            refererContainer.put(refererKey, referer);
            return refererContainer.get(refererKey);
        }

    }
}
