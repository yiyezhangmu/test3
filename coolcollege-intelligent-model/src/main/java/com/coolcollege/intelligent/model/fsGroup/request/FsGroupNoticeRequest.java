package com.coolcollege.intelligent.model.fsGroup.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 群公告表(FsGroupNotice)实体类
 *
 * @author CFJ
 * @since 2024-05-06 11:32:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupNoticeRequest implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    /**
     * 公告名称
     */    
    @ApiModelProperty(value = "公告名称",required = true)
    @NotNull
    private String name;
    /**
     * 封面图片
     */    
    @ApiModelProperty("封面图片")
    private String img;
    /**
     * 封面图片
     */
    @ApiModelProperty("封面图片Url")
    private String imgUrl;
    /**
     * 公告摘要
     */    
    @ApiModelProperty("公告摘要")
    private String content;
    /**
     * 公告详情
     */    
    @ApiModelProperty("公告详情")
    private String detail;
    /**
     * 链接地址
     */
    @ApiModelProperty("跳转地址")
    private String detailUrl;
    /**
     * 公告详情类型1:自定义内容 2:链接地址
     */    
    @ApiModelProperty("公告详情类型1:自定义内容 2:自定义链接")
    private String detailType;
    /**
     * 发送门店 null为发送所有门店
     */
    @ApiModelProperty("发送门店 不传为发送所有门店")
    private List<StoreWorkCommonDTO> sendStoreRegionIds;
    /**
     * 同上
     */
    @ApiModelProperty("区域群列表")
    private String sendRegionChatIds;
    /**
     * 同上
     */
    @ApiModelProperty("其他群列表")
    private String sendOtherChatIds;
    /**
     * 1:立即发送2:定时发送
     */    
    @ApiModelProperty("1:立即发送2:定时发送")
    private String sendTimeType;

    /**
     * 发送时间
     */    
    @ApiModelProperty("发送时间")
    private Date sendTime;

}

