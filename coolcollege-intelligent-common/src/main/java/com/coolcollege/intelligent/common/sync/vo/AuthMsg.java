package com.coolcollege.intelligent.common.sync.vo;

public class AuthMsg {
    private String corpId;
    private String appType;
    private Boolean scopeChange;
    private String permanentCode;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Boolean getScopeChange() {
        return scopeChange;
    }

    public void setScopeChange(Boolean scopeChange) {
        this.scopeChange = scopeChange;
    }

    public String getPermanentCode() {
        return permanentCode;
    }

    public void setPermanentCode(String permanentCode) {
        this.permanentCode = permanentCode;
    }
}
