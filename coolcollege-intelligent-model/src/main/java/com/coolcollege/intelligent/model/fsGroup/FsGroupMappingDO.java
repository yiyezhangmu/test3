package com.coolcollege.intelligent.model.fsGroup;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (FsGroupMapping)实体类
 *
 * @author CFJ
 * @since 2024-05-09 09:20:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupMappingDO implements Serializable {
    private static final long serialVersionUID = 760184544621287577L;
    /**
     * 主键
     */    
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String chatId;
    /**
     * 用户id
     */    
    @ApiModelProperty("用户id")
    private String userId;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;

}

