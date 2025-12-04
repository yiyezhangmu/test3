package com.coolcollege.intelligent.model.subtable;


import lombok.Data;

/**
 * 学员实体分表操作
 *
 * @author Aaron
 * @ClassName SubTableController
 * @Description 学员实体分表操作
 */
@Data
public class SubTableDO {

    /**
     * 企业id
     */
    private Long enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 唯一性ID
     */
    private String uniqueId;

    /**
     * 分表名称
     */
    private String subTableName;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createUser;

}
