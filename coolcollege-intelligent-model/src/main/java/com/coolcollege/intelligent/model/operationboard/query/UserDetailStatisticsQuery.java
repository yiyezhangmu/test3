package com.coolcollege.intelligent.model.operationboard.query;

import java.util.ArrayList;
import java.util.List;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;
import lombok.ToString;

/**
 * @author shuchang.wei
 * @date 2021/1/11 10:43
 */
@Data
@ToString
public class UserDetailStatisticsQuery extends BaseQuery{
    private List<Long> roleIdList = new ArrayList<>();
    private List<String> userIdList = new ArrayList<>();

    //排序字段
    private String orderField;

    //排序类型:升序:asc 降序:desc
    private String orderType;


    private List<SysRoleDO> defaultRoleList;
}
