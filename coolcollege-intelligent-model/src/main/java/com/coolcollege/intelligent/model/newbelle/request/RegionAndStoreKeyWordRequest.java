package com.coolcollege.intelligent.model.newbelle.request;

import lombok.Data;

import java.util.List;

@Data
public class RegionAndStoreKeyWordRequest {
    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
    private String authUserId;
    private List<String> storeNewNo;
}
