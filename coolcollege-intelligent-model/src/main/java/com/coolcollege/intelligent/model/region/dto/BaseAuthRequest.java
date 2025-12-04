package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe:门店权限基础类
 *
 * @author zhouyiping
 * @date 2021/08/02
 */
@Data
public class BaseAuthRequest {

    /**
     * 是否有全部权限
     */
    private Boolean isAllStore;

    /**
     *  未处理过的门店Id,直接从配置权表中获取的
     */
    private List<String> storeIdList;

    /**
     * 未处理过的区域Id,直接从配置权表中获取的
     */
    private List<String>  regionIdList;

    /**
     * 获取配置权限的fullRegionPath
     */
    private List<String> fullRegionPathList;

}
