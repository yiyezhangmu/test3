package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告和群映射表(FsGroupNoticeMapping)实体类
 *
 * @author CFJ
 * @since 2024-05-08 17:17:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupNoticeMappingDO implements Serializable {
    private static final long serialVersionUID = 955270107909037142L;
    /**
     * 主键
     */    
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 公告id
     */    
    @ApiModelProperty("公告id")
    private Long noticeId;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String chatId;
    /**
     * 飞书消息id，用作增删改查
     */    
    @ApiModelProperty("飞书消息id，用作增删改查")
    private String msgId;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;

}

