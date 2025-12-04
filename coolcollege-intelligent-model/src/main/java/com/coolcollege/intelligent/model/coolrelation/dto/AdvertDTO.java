package com.coolcollege.intelligent.model.coolrelation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/5/13 14:10
 */
@Data
public class AdvertDTO {

    /**
     * 广告类型
     */
    private String type;

    /**
     * 平台类型
     */
    @NotBlank(message = "平台类型不能为空")
    private String platFormType;
}
