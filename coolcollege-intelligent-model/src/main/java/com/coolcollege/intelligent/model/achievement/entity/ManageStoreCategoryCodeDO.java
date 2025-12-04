package com.coolcollege.intelligent.model.achievement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManageStoreCategoryCodeDO {

    private Long id;

    /**
     * 门店Id store表storeId
     */

    private String storeId;

    /**
     * 实需门店编号，唯一{物理门店编号}_{商品品类编号}
     */

    private String actualStoreNum;

    /**
     * 实需门店名称
     */

    private String actualStoreName;

    /**
     * 物理门店名称
     */

    private String physicalStoreName;

    /**
     * 物理门店编号
     */

    private String physicalStoreNum;

    /**
     * 商品品类
     */
    private String categoryName;

    /**
     * 品类代码
     */
    private String categoryCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUserId;

    /**
     * 是否删除：0.否 1.是
     */
    private Boolean deleted;

}