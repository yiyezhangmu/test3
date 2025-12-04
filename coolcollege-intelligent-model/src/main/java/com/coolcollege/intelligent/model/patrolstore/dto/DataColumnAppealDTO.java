package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.model.common.FileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-11 01:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataColumnAppealDTO implements Serializable {


    @ApiModelProperty("数据检查项id")
    private Long dataColumnId;

    @ApiModelProperty("申诉内容")
    private String appealContent;

    @ApiModelProperty("申诉原因")
    private String appealRemark;

    @ApiModelProperty("申诉图片")
    private String pictures;

    @ApiModelProperty("申诉视频")
    private String videos;
}