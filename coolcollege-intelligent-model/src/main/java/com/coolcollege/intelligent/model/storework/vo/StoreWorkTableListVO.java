package com.coolcollege.intelligent.model.storework.vo;

import ch.qos.logback.classic.db.names.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/26 14:47
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkTableListVO {

    private Long id;

    private Date beginTime;

    private Date EndTime;

    private String tableName;

    private String tableInfo;


    @ApiModelProperty("日清类型")
    private String workCycle;

}
