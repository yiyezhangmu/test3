package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author wxp  店务表和店的状态信息
 * @Date 2022/10/24 17:26
 * @Version 1.0
 */
@Data
@ApiModel
public class StoreWorkTableAndRecordStatusInfo {

    private Integer completeStatus;

    private Integer commentStatus;
    // 执行权限
    private Boolean handleFlag;
    // 点评权限
    private Boolean commentTabDisplayFlag;

}
