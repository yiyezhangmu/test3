package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.store.StoreDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreListVO
 * @Description: 门店列表
 * @date 2023-04-12 10:04
 */
@Data
public class StoreListVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("三方唯一id")
    private String thirdDeptId;


    public static List<StoreListVO> convert(List<StoreDO> storeList){
        if(CollectionUtils.isEmpty(storeList)){
            return Lists.newArrayList();
        }
        List<StoreListVO> resultList = new ArrayList<>();
        for (StoreDO store : storeList) {
            StoreListVO r = new StoreListVO();
            r.setStoreId(store.getStoreId());
            r.setStoreName(store.getStoreName());
            r.setStoreNum(store.getStoreNum());
            r.setThirdDeptId(store.getThirdDeptId());
            resultList.add(r);
        }
        return resultList;
    }

}
