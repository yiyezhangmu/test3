package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: DataUploadVO
 * @Description: 数据上报时间
 * @date 2023-04-04 16:17
 */
@Data
public class DataUploadVO {

    @ApiModelProperty("数据上报时间 yyyy-MM-dd HH:mm")
    private String etlTm;

}
