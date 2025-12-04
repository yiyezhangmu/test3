package com.coolcollege.intelligent.model.aliyun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 邵凌志
 * @date 2021/1/13 20:28
 */
@Data
public class AliyunVdsBaseStatisticsVO {

    private Integer pageSize = 20;

    private Integer pageNum = 1;

//    @NotBlank(message = "阿里云CorpId不能为空")
    private String corpId;
}
