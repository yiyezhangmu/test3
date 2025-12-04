package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;
@Data
public class StoreUserMappingDTO {
    private String userId;
    private String storeId;
    private List<String> userIds;
    private List<String> storeIds;
}