package com.coolcollege.intelligent.model.share.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckItemDO {
    /**
     * 检查项名称
     */
    @JsonProperty("check_item_name")
    private String checkItemName;


    /**
     * 检查子项对应的图片列表
     */
    @JsonProperty("picture")
    private String picture;

    /**
     * 描述
     */
    private String description;
    /**
     * 子任务id
     */
    @JsonIgnore
    private Long checkItemId;
}
