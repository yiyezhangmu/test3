package com.coolcollege.intelligent.model.share.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckTableDO {
    /**
     * 检查表名称
     */
    @JsonProperty("check_table_name")
    private String checkTableName;
    /**
     * 检查项列表
     */
    @JsonProperty("check_item_list")
    private List<CheckItemDO> checkItemList;

}
