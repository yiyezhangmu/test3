package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/24 14:44
 * @Version 1.0
 */
@Data
public class SwStoreWorkHandleUserDTO {

    private List<SwStoreWorkDataUserDTO> swStoreWorkDataUserDTOS;

    private List<EnterpriseUserSingleDTO> handleUserIdList;

    private List<EnterpriseUserSingleDTO> commentUserIdList;
}
