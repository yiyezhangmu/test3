package com.coolcollege.intelligent.model.elasticSearch.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: RegionPatrolStatisticsRequest
 * @Description: 区域报表
 * @date 2021-10-25 11:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionPatrolStatisticsRequest {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * regionPath列表
     */
    private List<Long> regionIds;
    /**
     * 门店列表
     */
    private List<String> storeIds;
    /**
     * 开始时间
     */
    private Date beginDate;
    /**
     * 截止时间
     */
    private Date endDate;

    /**
     * 是否直连门店数据 true 时 获取直连门店  false 和 null时 获取散向数据
     */
    private Boolean isGetDirectStore;

}
