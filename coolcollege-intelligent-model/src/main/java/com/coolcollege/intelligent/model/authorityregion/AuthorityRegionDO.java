package com.coolcollege.intelligent.model.authorityregion;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
* @Author: huhu
* @Date: 2024/11/25 16:04
* @Description: 授权区域
*/
@Data
@Builder
public class AuthorityRegionDO {
    /**
    * 主键
    */
    private Long id;

    /**
    * 授权区域名称
    */
    private String name;

    /**
    * 人员id集合
    */
    private String userIds;

    /**
     * 人员名称集合
     */
    private String userNames;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 创建人
    */
    private String createName;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 更新人
    */
    private String updateName;

    /**
    * 删除标识
    */
    private Boolean deleted;
}