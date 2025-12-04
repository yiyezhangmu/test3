package com.coolcollege.intelligent.model.newbelle.request;

import lombok.Data;

import java.util.List;

@Data
public class RegionAndStoreRequest {
    private Long parentId;
    private String authUserId;
    private List<String> storeNewNo;
}
