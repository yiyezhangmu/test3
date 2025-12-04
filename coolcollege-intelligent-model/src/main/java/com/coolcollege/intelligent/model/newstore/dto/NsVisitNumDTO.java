package com.coolcollege.intelligent.model.newstore.dto;

import lombok.Data;

/**
 * 拜访次数DTO
 * @author zhangnan
 * @date 2022-03-09 13:43
 */
@Data
public class NsVisitNumDTO {

    private Long newStoreId;

    private Integer visitNum;
}
