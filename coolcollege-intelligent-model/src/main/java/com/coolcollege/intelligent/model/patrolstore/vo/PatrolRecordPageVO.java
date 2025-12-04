package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/12/17 16:55
 * @Description: 行事历待办巡店信息
 */
@Data
@Builder
public class PatrolRecordPageVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("计划日期")
    private Date planDate;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("巡店总结")
    private Boolean isOpenSummary;

    @ApiModelProperty("巡店签名")
    private Boolean isOpenAutograph;

    public static List<PatrolRecordPageVO> convertList(Page<TbPatrolPlanDetailDO> patrolPlanDetailPage, Map<Long, TbPatrolPlanDO> tbPatrolPlanMap) {
        List<PatrolRecordPageVO> result = Lists.newArrayList();
        patrolPlanDetailPage.forEach(d -> {
            PatrolRecordPageVO patrolRecordPageVO = PatrolRecordPageVO.builder()
                    .storeId(d.getStoreId()).storeName(d.getStoreName())
                    .businessId(d.getBusinessId()).planDate(d.getPlanDate())
                    .build();
            TbPatrolPlanDO tbPatrolPlan = tbPatrolPlanMap.get(d.getPlanId());
            if (Objects.nonNull(tbPatrolPlan)) {
                patrolRecordPageVO.setIsOpenSummary(tbPatrolPlan.getIsOpenSummary());
                patrolRecordPageVO.setIsOpenAutograph(tbPatrolPlan.getIsOpenAutograph());
            }
            result.add(patrolRecordPageVO);
        });
        return result;
    }
}
