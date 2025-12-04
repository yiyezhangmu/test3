package com.coolcollege.intelligent.model.unifytemplate;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : lz
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTemplateDO {
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
    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
    /**
     * 将范围解析之后返回
     */
    private String scopePerson;
}
