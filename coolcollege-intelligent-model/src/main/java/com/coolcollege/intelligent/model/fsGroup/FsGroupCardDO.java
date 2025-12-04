package com.coolcollege.intelligent.model.fsGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 群卡片管理(FsGroupCard)实体类
 *
 * @author CFJ
 * @since 2024-04-26 19:24:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupCardDO implements Serializable {
    private static final long serialVersionUID = -78640172088408105L;
    /**
     * 主键
     */    
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改时间
     */    
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 卡片code
     */    
    @ApiModelProperty("卡片code")
    private String cardCode;
    /**
     * 场景Id
     */    
    @ApiModelProperty("场景Id")
    private Long sceneId;
    /**
     * 卡片模版id
     */    
    @ApiModelProperty("卡片模版id")
    private String cardTemplateId;
    /**
     * 卡片模版内容
     */    
    @ApiModelProperty("卡片模版内容")
    private String cardTemplate;
    /**
     * 场景卡片名称
     */    
    @ApiModelProperty("场景卡片名称")
    private String cardName;
    /**
     * 场景卡片描述
     */    
    @ApiModelProperty("场景卡片描述")
    private String cardDesc;
    /**
     * 可见角色  用,分割
     */    
    @ApiModelProperty("可见角色  用,分割")
    private String sendRole;
    /**
     * 是否被删除
     */    
    @ApiModelProperty("是否被删除")
    private Long isDeleted;

}

