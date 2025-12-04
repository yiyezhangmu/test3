package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:32
 * @Version 1.0
 */
@Data
public class OpenApiAddRegionDTO {

    /**
     * 第三方的父节点id
     */
    private String thirdParentId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 第三方管理唯一key
     */
    private String thirdDeptId;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    public boolean check(){
        boolean isDeleted = Objects.isNull(isDelete) ? Boolean.FALSE : isDelete;
        if(!isDeleted && StringUtils.isAnyBlank(regionName, thirdDeptId)){
            return false;
        }
        if(isDeleted && StringUtils.isBlank(thirdParentId)){
            return false;
        }
        return true;
    }



}
