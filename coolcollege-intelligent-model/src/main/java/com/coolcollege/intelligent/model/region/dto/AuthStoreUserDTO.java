package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/22
 */
@Data
public class AuthStoreUserDTO {
    private String storeId;
    private String storeName;
    private List<String> userIdList;

}
