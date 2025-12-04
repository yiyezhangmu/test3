package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TaskModelsDTO{
    @ApiModelProperty("记录id")
    private Long id;
    
    @ApiModelProperty("出样数量")
    private Integer goodNum;

    @ApiModelProperty("上架下架时间 时间戳")
    private Long planDelistTime;

    @ApiModelProperty("上传图片，多个逗号分隔")
    private String picture;
    
}
