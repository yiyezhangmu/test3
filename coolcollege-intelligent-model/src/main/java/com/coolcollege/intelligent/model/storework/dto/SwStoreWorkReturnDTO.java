package com.coolcollege.intelligent.model.storework.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/24 14:00
 * @Version 1.0
 */
@Data
public class SwStoreWorkReturnDTO {

    private String workCycle;

    private List<SwStoreWorkHandleUserDTO> swStoreWorkHandleUserDTOS;

}
