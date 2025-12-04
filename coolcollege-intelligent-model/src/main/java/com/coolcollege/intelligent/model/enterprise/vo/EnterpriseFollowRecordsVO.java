package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author chenyupeng
 * @since 2021/11/24
 */
@Data
public class EnterpriseFollowRecordsVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 线索id
     */
    private Long cluesId;

    /**
     * 创建人id
     */
    private String createId;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 修改人id
     */
    private String updateId;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 跟进记录
     */
    private String recordDescribe;

}
