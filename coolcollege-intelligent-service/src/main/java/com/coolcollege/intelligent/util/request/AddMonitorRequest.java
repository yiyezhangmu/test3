package com.coolcollege.intelligent.util.request;

import lombok.Data;

/**
 * describe:
 *https://help.aliyun.com/document_detail/169120.html?spm=a2c4g.11186623.6.582.2eef2c34Ig7G3l
 * @author zhouyiping
 * @date 2021/01/13
 */
@Data
public class AddMonitorRequest {
    private String algorithmVendor;
    private String corpId;
    private String monitorType;
    private String notifierType;
    private String notifierUrl;

}
