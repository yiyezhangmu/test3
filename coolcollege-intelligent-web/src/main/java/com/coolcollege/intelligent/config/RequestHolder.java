package com.coolcollege.intelligent.config;

public class RequestHolder {

    private static final ThreadLocal<String> requestHolder = new ThreadLocal<String>();

    public static final String getRequest() {
        return requestHolder.get();
    }

    public static final void setRequest(String request) {
        requestHolder.set(request);
    }
}
