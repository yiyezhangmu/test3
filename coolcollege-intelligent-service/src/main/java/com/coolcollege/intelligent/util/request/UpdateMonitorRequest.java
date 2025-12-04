package com.coolcollege.intelligent.util.request;

import lombok.Data;

/**
 * describe:
 *https://help.aliyun.com/document_detail/169121.html?spm=a2c4g.11186623.6.583.46bf31fevjCdgk
 * @author zhouyiping
 * @date 2021/01/13
 */
@Data
public class UpdateMonitorRequest {
    private String taskId;
    private String algorithmVendor;
    private String corpId;
    private String ruleName;
    private String deviceOperateType;
    private String deviceList;
    private String picOperateType;
    private String picList;

    private String monitorType;
    private String notifierType;
    private String notifierUrl;
}
