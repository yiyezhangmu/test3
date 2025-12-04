package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 15:37
 * @Version 1.0
 */
@Data
public class UserRegionMappingDTO {

    /**
     * id
     */
    private Integer id;
    /**
     * 映射主键 区域id
     */
    private String regionId;
    /**
     *  用户id
     */
    private String userId;
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
