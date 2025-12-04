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
 * 飞书群置顶表(FsGroupTopMsg)实体类
 *
 * @author CFJ
 * @since 2024-05-06 10:59:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupTopMenuVO implements Serializable {
    private static final long serialVersionUID = -71647495927825545L;
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
     * 同上
     */    
    @ApiModelProperty("同上")
    private String sendRegionChatIds;
    /**
     * 同上
     */    
    @ApiModelProperty("同上")
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
     * 创建人
     */
    @ApiModelProperty("创建人Name")
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
    /**
     * 配置群数量
     */    
    @ApiModelProperty("配置群数量")
    private Integer groupCount;

    @ApiModelProperty("发送门店群的区域")
    private List<IdAndNameVO> sendStoreRegionList;
    @ApiModelProperty("发送区域群List")
    private List<IdAndNameVO> sendRegionChatList;
    @ApiModelProperty("发送其他群的List")
    private List<IdAndNameVO> sendOtherChatList;

}

