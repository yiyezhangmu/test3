package com.coolcollege.intelligent.model.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户管辖门店表
 * @TableName user_jurisdiction_store
 */
@Data
public class UserJurisdictionStoreDO implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}