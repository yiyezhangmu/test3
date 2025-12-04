package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/27 16:12
 * @Version 1.0
 */
@Data
public class SwStoreWorkTableDTO {

    private Long id;

    private String tableName;

    private BigDecimal totalScore;

    private Date beginTime;

    private Date endTime;

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

    private List<SwStoreWorkColumnResultDTO> swStoreWorkColumnResultDTOS;

}
