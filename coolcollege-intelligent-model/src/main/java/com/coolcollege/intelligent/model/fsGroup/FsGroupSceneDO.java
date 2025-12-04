package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (FsGroupScene)实体类
 *
 * @author CFJ
 * @since 2024-04-26 18:37:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupSceneDO implements Serializable {
    private static final long serialVersionUID = -69388253590337092L;
        
    @ApiModelProperty("")
    private Long id;
    /**
     * 配置名
     */    
    @ApiModelProperty("配置名")
    private String name;

    /**
     * code
     */
    @ApiModelProperty("场景code")
    private String sceneCode;
    /**
     * 备注
     */    
    @ApiModelProperty("备注")
    private String remark;
    /**
     * 图片
     */    
    @ApiModelProperty("图片")
    private String img;
    /**
     * 链接地址
     */    
    @ApiModelProperty("链接地址")
    private String linkAddress;
        
    @ApiModelProperty("创建人id")
    private String createUserId;
        
    @ApiModelProperty("创建时间")
    private Date createTime;

}

