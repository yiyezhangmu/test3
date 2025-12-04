package com.coolcollege.intelligent.model.system.sysconf;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/3/25 20:15
 */
@Data
@ToString
public class SysDictDO {
    /**
     * 主键
     */
    private Long id;

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
