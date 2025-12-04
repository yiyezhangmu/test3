package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserDepartmentMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.ManualDeptUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserDeptRoleDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/9/22 16:07
 */
@Repository
public class EnterpriseUserDeptDao {

    @Resource
    private EnterpriseUserDepartmentMapper userDeptMapper;

    public UserDeptRoleDTO getUserDeptRole(String eid, String userId) {
        UserDeptRoleDTO userDeptAndRole = userDeptMapper.getUserDeptAndRole(eid, Collections.singletonList(userId));
        userDeptAndRole.setDeptIds(userDeptAndRole.getDeptList().stream().map(m -> m.getId().toString()).collect(Collectors.toList()));
        userDeptAndRole.setRoleIds(userDeptAndRole.getRoles().stream().map(SysRoleDO::getId).collect(Collectors.toList()));
        return userDeptAndRole;
    }

    public List<ManualDeptUserDTO> getManualDeptUserList(String eid, List<String> userIds, List<String> deptIds) {
        List<ManualDeptUserDTO> deptParentAndIdForUser = userDeptMapper.getDeptParentAndIdForUser(eid, userIds, deptIds);
        deptParentAndIdForUser = deptParentAndIdForUser.stream().peek(m -> {
            List<Long> roleIds = m.getRoles().stream().map(SysRoleDO::getId).collect(Collectors.toList());
            m.setRoleIds(roleIds);
        }).distinct().collect(Collectors.toList());
        return deptParentAndIdForUser;
    }
}
