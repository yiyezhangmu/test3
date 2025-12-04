package com.coolcollege.intelligent.model.homeTemplate;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 14:03
 * @Version 1.0
 */
@Data
public class HomeTemplateRoleMappingDO {
    /**
     *主键
     */
    private Integer id;

    /**
     * 模板ID
     */
    private Integer templateId;

    /**
     *角色ID
     */
    private Long roleId;

    /**
     *创建人
     */
    private String createId;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *更新人
     */
    private String updateId;
    /**
     *更新时间
     */
    private Date updateTime;
}
