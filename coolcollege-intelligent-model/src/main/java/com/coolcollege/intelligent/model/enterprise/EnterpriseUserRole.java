package com.coolcollege.intelligent.model.enterprise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName EnterpriseUserRole
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUserRole {
    private Long id;
    private String roleId;
    private String userId;

    private Integer syncType;

    //创建时间
    private Date createTime;
    //更新时间
    public EnterpriseUserRole(String roleId, String userId) {
        this.roleId = roleId;
        this.userId = userId;
        this.createTime = new Date();
    }

    public EnterpriseUserRole(String roleId, String userId, Integer syncType) {
        this.roleId = roleId;
        this.userId = userId;
        this.syncType = syncType;
        this.createTime = new Date();
    }

    private Date updateTime;

    public static List<EnterpriseUserRole> convertList(List<Long> roleIds, String userId){
        List<EnterpriseUserRole> resultList = new ArrayList<>();
        for (Long roleId : roleIds) {
            resultList.add(new EnterpriseUserRole(String.valueOf(roleId), userId));
        }
        return resultList;
    }
}
