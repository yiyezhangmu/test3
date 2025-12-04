package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2025/1/15 10:10
 * @Description:
 */
@Data
public class NsVisitRecordDTO {

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 拜访状态：ongoing进行中，completed完成
     */
    private String status;

    /**
     * 门店名称
     */
    private String newStoreName;

    /**
     * 拜访提交时间：开始
     */
    private Long completedBeginTime;

    /**
     * 拜访提交时间：结束
     */
    private Long completedEndTime;

    /**
     * 门店类型
     */
    private List<String> newStoreTypes;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 拜访表id
     */
    private List<Long> metaTableIds;


}
