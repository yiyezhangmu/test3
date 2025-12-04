package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.store.StoreDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanPageVO
 * @Description:
 * @date 2024-09-04 11:37
 */
@Data
public class PatrolPlanAuthStoreVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("最近巡店时间")
    private Date latestPatrolTime;

    public static List<PatrolPlanAuthStoreVO> convertList(List<StoreDO> storeList, Map<String, Date> latestPatrolTimeMap){
        if(CollectionUtils.isEmpty(storeList)){
            return Lists.newArrayList();
        }
        List<PatrolPlanAuthStoreVO> resultList = Lists.newArrayList();
        for (StoreDO store : storeList) {
            PatrolPlanAuthStoreVO patrolPlanAuthStoreVO = new PatrolPlanAuthStoreVO();
            patrolPlanAuthStoreVO.setStoreId(store.getStoreId());
            patrolPlanAuthStoreVO.setStoreName(store.getStoreName());
            if(Objects.nonNull(latestPatrolTimeMap)){
                patrolPlanAuthStoreVO.setLatestPatrolTime(latestPatrolTimeMap.get(store.getStoreId()));
            }
            resultList.add(patrolPlanAuthStoreVO);
        }
        return resultList;
    }

}
