package com.coolcollege.intelligent.model.fsGroup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (FsGroup)实体类
 *
 * @author CFJ
 * @since 2024-04-23 09:39:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupVO implements Serializable {

    private Long id;
    /**
     * 群id
     */
    private String chatId;
    /**
     * 群名
     */
    private String name;
    /**
     * 群类型 store,region,other
     */
    private String type;
    /**
     * 群主id
     */
    private String groupOwnerId;
    /**
     * 群主姓名
     */
    private String groupOwnerName;
    /**
     * 绑定部门,json格式
     */
    private String bindRegionIds;

    private Map bindRegionNames;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人id
     */
    private String createUserId;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 更新人
     */
    private String updateUserId;
    /**
     * 更新时间
     */
    private Date updateTime;

    @ApiModelProperty("场景映射id")
    private Long sceneMappingId;

    @ApiModelProperty("群人数")
    private Integer userCount;

    @ApiModelProperty("群场景")
    private List<IdAndNameVO> sceneList;

    @ApiModelProperty("群置顶")
    private List<IdAndNameVO> topMenuList;

    @ApiModelProperty("群菜单")
    private List<IdAndNameVO> menuList;

}

