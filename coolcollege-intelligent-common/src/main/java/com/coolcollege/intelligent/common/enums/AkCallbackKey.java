package com.coolcollege.intelligent.common.enums;

public enum AkCallbackKey {
    
    AOKANGCALLBACKTEST("5d18dfd5264b44499e6e3665ba79fcf6","aoKangCallBackTest"),

    AOKANGCALLBACK("0954c8399b5749c395e1c9e20c028c87","aoKangCallBack"),

    AOKANGUPDOWNCALLBACK("798835a93c174ea39344e51bc46b46f1","aoKangUpDownCallBack"),



    ;

    private String eid;
    private String key;

    AkCallbackKey(String eid, String key) {
        this.eid = eid;
        this.key = key;
    }

    public String getEid() {
        return eid;
    }

    public String getKey() {
        return key;
    }
    
}
