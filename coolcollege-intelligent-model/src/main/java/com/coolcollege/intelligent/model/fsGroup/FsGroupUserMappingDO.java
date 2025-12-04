package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (FsGroupUserMapping)实体类
 *
 * @author CFJ
 * @since 2024-04-26 10:36:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupUserMappingDO implements Serializable {
    private static final long serialVersionUID = -62564058231551228L;
        
    @ApiModelProperty("id")
    private Long id;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String chatId;
    /**
     * 群成员id
     */    
    @ApiModelProperty("群成员id")
    private String userId;
        
    @ApiModelProperty("创建时间")
    private Date createTime;

}

