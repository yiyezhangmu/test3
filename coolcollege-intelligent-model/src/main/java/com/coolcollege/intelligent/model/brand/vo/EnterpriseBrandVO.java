package com.coolcollege.intelligent.model.brand.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 品牌VO
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
@Data
public class EnterpriseBrandVO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("品牌code")
    private String code;

    @ApiModelProperty("品牌名称")
    private String name;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("初始化状态(0:未初始化 1:已初始化)")
    private Integer initStatus;
}
