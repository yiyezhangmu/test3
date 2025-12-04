package com.coolcollege.intelligent.model.userholder;

/**
 * Created by Joshua on 2017/8/7 11:14
 */
public class CurrentUserEnterprise {

    private String enterpriseId;

    private String enterpriseName;

    private Boolean isCurrent;

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }
}
