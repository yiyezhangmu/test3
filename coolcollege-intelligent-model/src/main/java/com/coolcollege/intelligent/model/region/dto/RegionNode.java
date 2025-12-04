package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName RegionNode
 * @Description 用一句话描述什么
 */
@Data
public class RegionNode {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 区域ID
     */
    private String regionId;

    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parentId;

    private String parentName;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建人
     */
    private String createName;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 更新人
     */
    private String updateName;

    /**
     * 子节点
     */
    private List<RegionNode> children;

    /**
     * 是否有区域权限
     */
    private Boolean isAuth;

    /**
     * 门店数量
     */
    private Long storeCount;

    /**
     * dinging部门id
     */
    private String synDingDeptId;

    /**
     * root path store
     */
    private String regionType;

    /**
     * 路径
     */
    private String regionPath;

    private String fullRegionPath;

    private Boolean isExternalNode;

    public String getFullRegionPath() {
        if(id != null && id == 1L){
            return "/1/";
        }
        if (StringUtils.isNotBlank(regionPath)) {
            return regionPath + id + "/";
        } else {
            return "/" + id + "/";
        }
    }

}
