package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/09
 */
@Data
public class AuthRegionStoreDTO {
    String userId;
    private List<AuthRegionStoreUserDTO> authRegionStoreUserList;
}
