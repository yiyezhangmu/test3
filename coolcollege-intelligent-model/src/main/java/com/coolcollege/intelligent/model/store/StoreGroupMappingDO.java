package com.coolcollege.intelligent.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName StoreLabelMappingDO
 * @Description 门店标签映射
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StoreGroupMappingDO {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 门店id
     */
    @JsonProperty("store_id")
    private String storeId;

    /**
     * 组别id
     */
    @JsonProperty("group_id")
    private String groupId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    public StoreGroupMappingDO(String storeId, String groupId, Long createTime, String createUser) {
        this.storeId = storeId;
        this.groupId = groupId;
        this.createTime = createTime;
        this.createUser = createUser;
    }
}
