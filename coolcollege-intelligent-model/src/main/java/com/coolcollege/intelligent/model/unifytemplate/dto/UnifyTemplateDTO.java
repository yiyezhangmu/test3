package com.coolcollege.intelligent.model.unifytemplate.dto;

import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.model.unifytemplate.UnifyTemplateDO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnifyTemplateDTO {
    private Long id;
    private String name;
    /**
     * 范围类型
     * 适用范围 all：全部    part：部分   部分的时候需要scope_id不为空
     */
    private String scopeType;
    private String scopeId;
    private String remark;
    private Integer isInitialized;
//    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
    private JSONArray checkItems;
    /**
     * 传递给前端当前用户是否是管理员
     * 用于编辑界面查询单条记录时，对当前权限的判断
     */
    private Boolean adminIs;
    /**
     * 将范围解析之后返回
     */
    private JSONArray scopePerson;
    public UnifyTemplateDTO(UnifyTemplateDO unifyTemplateDO){
        this.id=unifyTemplateDO.getId();
        this.name=unifyTemplateDO.getName();
        this.scopeType=unifyTemplateDO.getScopeType();
        this.remark=unifyTemplateDO.getRemark();
        this.createTime=unifyTemplateDO.getCreateTime();
        this.createUserId=unifyTemplateDO.getCreateUserId();
        this.createUserName=unifyTemplateDO.getCreateUserName();
        this.updateTime=unifyTemplateDO.getUpdateTime();
        this.updateUserId=unifyTemplateDO.getUpdateUserId();
        this.updateUserName=unifyTemplateDO.getUpdateUserName();
    }

}
