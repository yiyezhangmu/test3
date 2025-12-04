package com.coolcollege.intelligent.model.store.dto;

import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StoreGroupDTO {

    private String groupId;
    /**
     * 分组
     */
    @JsonProperty("store_group")
    private StoreGroupDO storeGroup;

    /**
     * 门店集合
     */
    @JsonProperty("store_list")
    private List<StoreDTO> storeList;

    private List<String> storeIds;
    /**
     * 分组中门店数量
     */
    private Integer count;

    @NotNull
    private List<String> groupIdList;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    @ApiModelProperty("是否可以编辑")
    private Boolean editFlag;

    private String userName;
}
