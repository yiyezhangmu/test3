package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 飞书群置顶菜单表(FsGroupTopMenu)实体类
 *
 * @author CFJ
 * @since 2024-05-10 10:17:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupTopMenuDO implements Serializable {
    private static final long serialVersionUID = 747147380924602316L;
    /**
     * id
     */    
    @ApiModelProperty("id")
    private Long id;
    /**
     * 置顶消息名
     */    
    @ApiModelProperty("置顶消息名")
    private String topName;
    /**
     * 0:自定义链接1:系统地址
     */    
    @ApiModelProperty("0:自定义链接1:系统地址")
    private Integer urlType;
    /**
     * 自定义链接地址
     */    
    @ApiModelProperty("自定义链接地址")
    private String url;
    /**
     * 系统地址code
     */    
    @ApiModelProperty("系统地址code")
    private String urlCode;
    /**
     * 发送门店 null为发送所有门店
     */    
    @ApiModelProperty("发送门店 null为发送所有门店")
    private String sendStoreRegionIds;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String sendRegionChatIds;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String sendOtherChatIds;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 创建人
     */    
    @ApiModelProperty("创建人")
    private String createUser;
    /**
     * 更新时间
     */    
    @ApiModelProperty("更新时间")
    private Date updateTime;
    /**
     * 更新人
     */    
    @ApiModelProperty("更新人")
    private String updateUser;

}

