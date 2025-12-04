package com.coolcollege.intelligent.model.store.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/8/11 14:10
 */
@Data
public class StoreIdVO {

    private String user_id;

    @JsonProperty("store_ids")
    private List<String> storeIds;
}
