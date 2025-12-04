package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 15:45
 * @Version 1.0
 */
@Data
public class SubordinateMappingDTO {
    /**
     * id
     */
    private Integer id;
    /**
     *  用户id
     */
    private String userId;
    /**
     * 映射主键 区域id
     */
    private String regionId;
    /**
     * 人员id
     */
    private String personalId;
    /**
     * 类型 0 下属 ， 1 直属上级
     */
    private Integer type;
    /**
     * 创建人id
     */
    private String createId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人id
     */
    private String updateId;
    /**
     * 更新时间
     */
    private Date updateTime;
}
