package com.coolcollege.intelligent.model.newstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhangnan
 * @description: 新店拜访记录sql参数DTO
 * @date 2022/3/6 12:35 PM
 */
@Data
public class NsVisitRecordQueryDTO {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 新店id
     */
    private Long newStoreId;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 拜访记录状态
     * @see com.coolcollege.intelligent.common.enums.newstore.NsVisitRecordStatusEnum
     */
    private String status;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 新店名称
     */
    private String newStoreName;

    /**
     * 区域路径
     */
    private Long regionId;

    /**
     * 新店类型，多个
     */
    private List<String> newStoreTypes;

    /**
     * 拜访表id，多个
     */
    private List<Long> metaTableIds;

    /**
     * 拜访提交时间：开始
     */
    private Date completedBeginTime;

    /**
     * 拜访提交时间：结束
     */
    private Date completedEndTime;

}
