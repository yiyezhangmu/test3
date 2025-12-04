package com.coolcollege.intelligent.model.video;

import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

/**
 * @author byd
 * @date 2022-07-20 14:21
 */
public class TicketAssumeRoleResponse extends AssumeRoleResponse {

    /**
     * 回调信息
     */
    private String callBackUrl;

    /**
     * 分类ID。
     */
    private Long cateId;

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }
}
