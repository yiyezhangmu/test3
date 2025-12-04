package com.coolcollege.intelligent.model.fsGroup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 飞书群菜单表(FsGroupMenu)实体类
 *
 * @author CFJ
 * @since 2024-05-08 18:58:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupMenuVO implements Serializable {
    private static final long serialVersionUID = 997824727788173174L;
    /**
     * id
     */    
    @ApiModelProperty("id")
    private Long id;
    /**
     * 菜单名
     */    
    @ApiModelProperty("菜单名")
    private String menuName;
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


    @ApiModelProperty("创建人姓名")
    private String createUserName;

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

    @ApiModelProperty("配置群数量")
    private Integer groupCount;

    @ApiModelProperty("发送门店群的区域")
    private List<IdAndNameVO> sendStoreRegionList;
    @ApiModelProperty("发送区域群List")
    private List<IdAndNameVO> sendRegionChatList;
    @ApiModelProperty("发送其他群的List")
    private List<IdAndNameVO> sendOtherChatList;

}

