package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author 邵凌志
 * @date 2020/7/27 9:39
 */
@Data
public class RegionStoreDTO implements Serializable {

    /**
     * 区域
     */
    private String id;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 门店数量
     */
    private long storeCount;

    /**
     * 门店首字母
     */
    private String key;

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点
     */
    private String parentId;

    /**
     * 子节点
     */
    private List<RegionStoreDTO> children;

    /**
     * 是否是门店
     */
    private boolean storeFlag = false;

    /**
     * 门店下人员数量
     */
    private Integer userNum;

    /**
     * 门店地址
     */
    private String locationAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionStoreDTO that = (RegionStoreDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
