package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

import java.util.List;

@Data
public class StoreUserInfoDTO {

    private String userId;

    private String userName;

    private String avatar;

    private String mobile;

    private List<String> positionNameList;

}
