package com.coolcollege.intelligent.model.question.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: QuestionCacheDataRequest
 * @Description:
 * @date 2022-08-18 15:20
 */
@Data
public class QuestionCacheDataRequest {


    @ApiModelProperty(value = "子工单id")
    private Long questionRecordId;

    @NotNull
    @ApiModelProperty(value = "父工单id")
    private Long questionParentInfoId;

    @NotBlank
    @ApiModelProperty(value = "缓存数据")
    private String saveData;

}
