package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: StoreBillingRankVO
 * @Description: 门店开单排行
 * @date 2023-04-04 14:11
 */
@Data
public class StoreBillingRankVO extends DataUploadVO{

    @ApiModelProperty("开单门店数")
    private Integer salesStoreNum;

    @ApiModelProperty("未开单门店数")
    private Integer noSalesStoreNum;

    @ApiModelProperty("门店开单列表")
    private List<StoreBilling> storeBillingList;

    @ApiModelProperty("门店锚点(快速定位)")
    private List<String> storeAnchorPoint;

    @Data
    public static class StoreBilling{

        @ApiModelProperty("门店id")
        private String storeId;

        @ApiModelProperty("门店名称")
        private String storeName;

        @ApiModelProperty("开单数量")
        private Integer billNum;

    }


    public static StoreBillingRankVO convert(List<AchieveQyyRegionDataDO> billingRank, List<RegionDO> storeRegionList){
        Map<Long, RegionDO> regionNameMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeRegionList)){
            regionNameMap = storeRegionList.stream().collect(Collectors.toMap(k -> k.getId(), Function.identity(), (k1, k2) -> k1));
        }
        StoreBillingRankVO result = new StoreBillingRankVO();
        Date etlTime = null;
        if(CollectionUtils.isNotEmpty(billingRank)){
            List<StoreBilling> storeBillingList = new ArrayList<>();
            for (AchieveQyyRegionDataDO achieveRegionData : billingRank) {
                RegionDO region = regionNameMap.get(achieveRegionData.getRegionId());
                if(Objects.isNull(region)){
                    continue;
                }
                StoreBilling storeBilling = new StoreBilling();
                if(Objects.isNull(etlTime)){
                    etlTime = achieveRegionData.getEtlTm();
                }
                if(Objects.nonNull(achieveRegionData.getEtlTm()) && Objects.nonNull(etlTime) && etlTime.getTime() < achieveRegionData.getEtlTm().getTime()){
                    etlTime = achieveRegionData.getEtlTm();
                }
                storeBilling.setStoreId(region.getStoreId());
                storeBilling.setStoreName(achieveRegionData.getDeptName());
                storeBilling.setBillNum(achieveRegionData.getBillNum());
                storeBillingList.add(storeBilling);
            }
            result.setStoreBillingList(storeBillingList);
        }
        if(Objects.isNull(etlTime)){
            etlTime = new Date();
        }
        result.setEtlTm(DateUtil.format(etlTime, "yyyy-MM-dd HH:mm"));
        return result;
    }

}
