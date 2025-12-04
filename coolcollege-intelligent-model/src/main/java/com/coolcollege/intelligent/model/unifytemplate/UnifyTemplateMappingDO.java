package com.coolcollege.intelligent.model.unifytemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 映射关系表   陈列模板机器检查项
 * @author LiZhuo
 * @date 2020.11.19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTemplateMappingDO {
    private Long id;
    /**
     * 模板id （常规/快照）
     */
    private Long displayTemplateId;
    /**
     * 检查项id  （常规/快照）
     */
    private Long displayCheckItemId;
    /**
     * DISPLAY: 常规表   DISPLAY_PG:快照
     */
    private String type;
    private Long createTime;
    private String createUserId;
    private String createUserName;
    private Long updateTime;
    private String updateUserId;
    private String updateUserName;
}
