package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选店组件中信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComptRegionStoreVO {
    /**
     * 区域列表
     */
    private List<SelectComponentRegionVO> regionList;

    /**
     * 二期需要信息。区域门店列表
     */
    private List<SelectComponentRegionVO> allRegionList;

    /**
     * 门店列表
     */

    private List<SelectComponentStoreVO> storeList;

}
