package com.coolcollege.intelligent.model.system.sysconf;

import lombok.Data;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/3/25 20:22
 */
@Data
public class EnterpriseDictMappingDO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 字典id
     */
    private Long dictId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 字典key值
     */
    private String dictKey;

    /**
     * 字典value值
     */
    private String dictValue;

    /**
     * 字典备注
     */
    private String remark;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 钉钉业务id
     */
    private String dingCorpId;
    /**
     * 删除标志
     */
    private Boolean hasDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
