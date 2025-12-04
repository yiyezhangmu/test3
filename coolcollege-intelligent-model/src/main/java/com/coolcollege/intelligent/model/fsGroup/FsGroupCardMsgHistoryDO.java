package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡片消息记录表(FsGroupCardMsgHistory)实体类
 *
 * @author CFJ
 * @since 2024-04-28 20:29:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupCardMsgHistoryDO implements Serializable {
    private static final long serialVersionUID = -45753262076817021L;
    /**
     * 主键
     */    
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 卡片消息id
     */    
    @ApiModelProperty("卡片消息id")
    private String messageId;
    /**
     * 收消息的用户id
     */    
    @ApiModelProperty("收消息的用户id")
    private String userId;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 飞书群id
     */    
    @ApiModelProperty("飞书群id")
    private String chatId;

}

