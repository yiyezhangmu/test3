package com.coolcollege.intelligent.model.fsGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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
public class FsGroupDO implements Serializable {
    private static final long serialVersionUID = -32945805462096925L;
    
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
     * 绑定部门,格式
     */
    private String bindRegionIds;


    private String bindRegionWays;
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
    
    private Integer deleted;

}

