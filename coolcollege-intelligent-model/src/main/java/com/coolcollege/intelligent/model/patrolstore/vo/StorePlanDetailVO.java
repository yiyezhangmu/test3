package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: StorePlanDetailRequest
 * @Description:
 * @date 2024-09-04 11:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePlanDetailVO {

    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("计划日期")
    private Date planDate;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店状态 0:未完成 1:已完成")
    private Integer status;

    @ApiModelProperty("完成时间")
    private Date finishTime;

    @ApiModelProperty("最近巡店时间")
    private Date latestPatrolTime;

    @ApiModelProperty("巡店总结")
    private Boolean isOpenSummary;

    @ApiModelProperty("巡店签名")
    private Boolean isOpenAutograph;

    public static List<StorePlanDetailVO> convertList(List<TbPatrolPlanDetailDO> detailList, Map<String, Date> latestPatrolTimeMap, TbPatrolPlanDO patrolPlan) {
        if (CollectionUtils.isEmpty(detailList)) {
            return new ArrayList<>();
        }
        List<StorePlanDetailVO> result = new ArrayList<>(detailList.size());
        detailList.forEach(t -> {
            StorePlanDetailVO storePlanDetailVO = StorePlanDetailVO.builder()
                    .id(t.getId())
                    .storeId(t.getStoreId()).storeName(t.getStoreName())
                    .planDate(t.getPlanDate()).latestPatrolTime(latestPatrolTimeMap.get(t.getStoreId()))
                    .businessId(t.getBusinessId())
                    .supervisorId(t.getSupervisorId())
                    .supervisorName(t.getSupervisorName())
                    .status(t.getStatus())
                    .finishTime(t.getFinishTime())
                    .isOpenSummary(patrolPlan.getIsOpenSummary())
                    .isOpenAutograph(patrolPlan.getIsOpenAutograph())
                    .build();
            result.add(storePlanDetailVO);
        });
        return result;
    }
}
