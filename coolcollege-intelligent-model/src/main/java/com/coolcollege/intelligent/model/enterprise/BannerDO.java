package com.coolcollege.intelligent.model.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author 邵凌志
 * @date 2020/7/1 17:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDO {

    /**
     * 企业id
     */
    private String eid;

    /**
     * banner图地址
     */
    @NotBlank(message = "banner图地址不能为空")
    @JsonProperty("banner_url")
    private String bannerUrl;

    /**
     * 跳转地址
     */
    @NotBlank(message = "跳转地址不能为空")
    @JsonProperty("jump_url")
    private String jumpUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;
}
