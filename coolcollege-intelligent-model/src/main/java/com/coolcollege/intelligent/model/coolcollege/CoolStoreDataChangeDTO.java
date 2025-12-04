package com.coolcollege.intelligent.model.coolcollege;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.List;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 11:29
 * 职位、用户，部门等信息变更的dto
 */
@Data
public class CoolStoreDataChangeDTO {
    /**
     * 操作类型，包含新增-add 删除-delete 修改-update
     */
   private String operation;
    /**
     * 变更数据类型 职位-position 人员-user 部门-region
     */
   private String type;
    /**
     * 数据id
     */
   private List<String> dataIds;
    /**
     * 数据库中是物理删除，因此仅用于职位的删除使用
     */
    private List<SysRoleDO> sysRoleDOS;
    /**
     * 企业id
     */
    private String enterpriseId;
}
