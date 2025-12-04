package com.coolcollege.intelligent.model.region;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @ClassName RegionDO
 * @Description 区域
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDO {
    /**
     * 自增ID
     */
    private Long      id;

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

    /**
     * 父ID
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
     * dinging部门id
     */
    private String synDingDeptId;

    /**
     * 是否删除标记
     */
    private Boolean deleted;

    /**
     *  root path store
     */
    private String regionType;


    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 区域门店数量
     */
    private Integer storeNum;

    /**
     * 区域门店范围是否  非DO 同步时用到的
     */
    private Boolean storeRange = false;

    /**
     * 门店地址 非DO
     */
    private String address;

    /**
     * 门店经度 非DO
     */
    private String longitude;


    /**
     * 纬度 非DO
     */
    private String latitude;

    /**
     * 门店编号 非DO
     */
    private String storeCode;

    /**
     * 门店开业日期 非DO
     */
    private String openDate;

    /**
     * 门店ID
     */
    private String storeId;

    public String fullRegionPath;

    private Integer orderNum;

    private Integer unclassifiedFlag;

    /**
     * 通讯录code 非DO
     */
    private String contactCode;

    /**
     * 门店扩展信息
     */
    private String extendField;

    /**
     * 第三方唯一id
     */
    private String thirdDeptId;

    /**
     * 门店状态
     */
    private String storeStatus;

    /**
     * 是否是外部组织节点
     */
    private Boolean isExternalNode;
    /**
     * 第三方区域类型
     */
    private String thirdRegionType;

    /**
     * 根据配置的门店统计范围统计门店数量（默认统计范围是全部门店状态）
     */
    private Integer storeStatNum;

    public RegionDO(Long id, String name, String parentId, String groupId, Long createTime, String createName) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.groupId = groupId;
        this.createTime = createTime;
        this.createName = createName;
    }
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
