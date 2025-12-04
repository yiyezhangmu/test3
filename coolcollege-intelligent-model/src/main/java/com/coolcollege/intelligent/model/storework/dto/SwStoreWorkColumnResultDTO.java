package com.coolcollege.intelligent.model.storework.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author suzhuhong
 * @Date 2023/3/27 16:10
 * @Version 1.0
 */
@Data
public class SwStoreWorkColumnResultDTO {

    private Long id ;

    private Long tbMetaColumnId;

    private String handleStatus;

    private String score;

    private String columnName;

}
