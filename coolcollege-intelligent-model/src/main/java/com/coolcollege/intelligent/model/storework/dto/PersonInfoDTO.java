package com.coolcollege.intelligent.model.storework.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/14 15:05
 * @Version 1.0
 */
@Data
public class PersonInfoDTO {

    List<StoreWorkCommonDTO> commentPersonInfo;

    List<StoreWorkCommonDTO> cooperatePersonInfo;

}
