package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (FsGroupSceneMapping)实体类
 *
 * @author CFJ
 * @since 2024-04-26 18:37:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupSceneMappingDO implements Serializable {
    private static final long serialVersionUID = -22485741195343357L;
        
    @ApiModelProperty("")
    private Long id;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String chatId;
    /**
     * 配置项id
     */    
    @ApiModelProperty("配置项id")
    private Long sceneId;
        
    @ApiModelProperty("创建时间")
    private Date createTime;


}

