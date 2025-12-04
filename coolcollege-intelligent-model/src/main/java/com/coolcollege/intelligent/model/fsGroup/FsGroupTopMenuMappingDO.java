package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 群顶部菜单映射表(FsGroupTopMenuMapping)实体类
 *
 * @author CFJ
 * @since 2024-05-08 15:28:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupTopMenuMappingDO implements Serializable {
    private static final long serialVersionUID = 244730199091162998L;
    /**
     * 主键
     */    
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 顶部菜单id
     */    
    @ApiModelProperty("顶部菜单id")
    private Long menuId;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String chatId;
    /**
     * 飞书置顶id，用做删除修改
     */    
    @ApiModelProperty("飞书置顶id，用做删除修改")
    private String fsTabId;
    /**
     * 创建人
     */    
    @ApiModelProperty("创建人")
    private String createUser;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;

}

