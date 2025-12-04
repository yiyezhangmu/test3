package com.coolcollege.intelligent.model.unifytemplate.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class UnifyTemplateVO {
    private Long id;
    private String name;
    private String scope;
    private String scopeType;
    private String scopeId;
    private String remark;
    private JSONArray checkItems;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
}
