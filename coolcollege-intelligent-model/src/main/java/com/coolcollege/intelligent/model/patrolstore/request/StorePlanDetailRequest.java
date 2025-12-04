package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StorePlanDetailRequest
 * @Description:
 * @date 2024-09-04 11:44
 */
@Data
public class StorePlanDetailRequest {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("计划日期")
    private Date planDate;

    public TbPatrolPlanDetailDO convert(String userId){
        TbPatrolPlanDetailDO result = TbPatrolPlanDetailDO.builder().id(id)
                .storeId(storeId).storeName(storeName)
                .planDate(planDate).updateUserId(userId)
                .updateTime(new Date())
                .build();
        return result;
    }

    public static List<TbPatrolPlanDetailDO> convertList(List<StorePlanDetailRequest> storePlanList, Long planId, String supervisorId, String supervisorUsername, String userId){
        if(CollectionUtils.isEmpty(storePlanList)){
            return Lists.newArrayList();
        }
        List<TbPatrolPlanDetailDO> resultList = new ArrayList<>();
        storePlanList.forEach(s -> {
            TbPatrolPlanDetailDO detailDO = TbPatrolPlanDetailDO.builder().planId(planId)
                    .planDate(s.getPlanDate()).storeId(s.getStoreId()).storeName(s.getStoreName())
                    .supervisorId(supervisorId).supervisorName(supervisorUsername)
                    .status(0)
                    .createUserId(userId).updateUserId(userId)
                    .build();
            resultList.add(detailDO);
        });
        return resultList;
    }

}
