package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author suzhuhong
 * @Date 2022/9/22 11:37
 * @Version 1.0
 */
@Data
@ApiModel(value = "单项提交")
public class SingleExecutionRequest {

    @ApiModelProperty(value = "检查项数据ID")
    private Long id;
    @ApiModelProperty(value = "检查项备注")
    private String checkText;
    @ApiModelProperty(value = "检查图片 [{\"handle\":\"url1\",\"final\":\"url2\"}]")
    private String checkPics;
    @ApiModelProperty(value = "检查项视频 录音")
    private String checkVideo;
    @ApiModelProperty(value = "自定义表value1")
    private String value1;
    @ApiModelProperty(value = "自定义表value2")
    private String value2;
    @ApiModelProperty(value = "执行状态")
    private Integer status;

}
