package com.coolcollege.intelligent.model.newstore.dto;

import lombok.Data;

/**
 * 新店分析表DTO
 * @author zhangnan
 * @date 2022-03-08 11:24
 */
@Data
public class NsCommonNumDTO {

    private String newStoreType;

    private Integer ongoingNum;

    private Integer completedNum;

    private Integer failedNum;

}
