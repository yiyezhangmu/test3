package com.coolcollege.intelligent.model.region.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/05/31
 */
@Data
@AllArgsConstructor
public class RegionStoreNumMsgDTO {
    private String eid;
    private List<Long> regionIdList;
    public RegionStoreNumMsgDTO(){}

}
