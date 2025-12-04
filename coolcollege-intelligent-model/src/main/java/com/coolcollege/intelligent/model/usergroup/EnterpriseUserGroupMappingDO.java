package com.coolcollege.intelligent.model.usergroup;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-12-28 07:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUserGroupMappingDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("组别id")
    private String groupId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    public static List<EnterpriseUserGroupMappingDO> convertDO(String userId, List<String> groupIds, String operator){
        List<EnterpriseUserGroupMappingDO> resultList = new ArrayList<>();
        for (String groupId : groupIds) {
            EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
            userGroupMappingDO.setGroupId(groupId);
            userGroupMappingDO.setUserId(userId);
            userGroupMappingDO.setCreateTime(new Date());
            userGroupMappingDO.setUpdateTime(new Date());
            userGroupMappingDO.setCreateUserId(operator);
            userGroupMappingDO.setUpdateUserId(operator);
            userGroupMappingDO.setDeleted(false);
            resultList.add(userGroupMappingDO);
        }
        return resultList;
    }
}