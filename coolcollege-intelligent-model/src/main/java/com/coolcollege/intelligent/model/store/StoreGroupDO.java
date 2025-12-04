package com.coolcollege.intelligent.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName StoreDO
 * @Description 门店标签
 */
@Data
@Accessors(chain = true)
public class StoreGroupDO {
    /**
     * 自增ID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 分组id
     */
    @JsonProperty("group_id")
    private String groupId;
    /**
     * 分组名称
     */
    @JsonProperty("group_name")
    private String groupName;

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

    /**
     * 来源:(create:自建, sync:从钉钉同步)
     */
    private String source;

    /**
     * 共同编辑人userId集合（前后逗号分隔）
     */
    private String commonEditUserids;

}
