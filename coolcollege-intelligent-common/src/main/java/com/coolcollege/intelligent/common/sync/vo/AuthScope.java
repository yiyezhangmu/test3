package com.coolcollege.intelligent.common.sync.vo;

import java.util.List;

/**
 * 钉钉通讯录授权范围
 */
public class AuthScope {

    private List<String> deptIdList;

    private List<String> userIdList;

    public List<String> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<String> deptIdList) {
        this.deptIdList = deptIdList;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }
}
