package com.coolcollege.intelligent.model.store.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/22
 */
@Data
public class StoreAreaDTO {
    private String storeId;
    private String storeName;
    /**
     * 门店所属区域的全部节点信息
     */
    private String regionPath;
 
    /**
     * 门店挂靠的区域Id
     */
    private String areaId;

    /**
     * 门店的挂挂靠的父节点区域ID
     */
    private List<String> areaIdList;

    /**
     * 所属区域id
     */
    private Long regionId;

    private String storeStatus;


    public List<String> getAreaIdList(){
       return StrUtil.splitTrim(regionPath,"/");
    }



}
