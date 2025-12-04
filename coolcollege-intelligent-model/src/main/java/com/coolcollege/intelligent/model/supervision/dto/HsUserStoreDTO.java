package com.coolcollege.intelligent.model.supervision.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/2 10:40
 * @Version 1.0
 */
@Data
public class HsUserStoreDTO {

    private String dingDingUserId;

    private String name;

    private List<HsStoreDTO> storeList;
}
