package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.model.department.dto.DeptForUserDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.UserDeptDTO;
import com.google.common.collect.Lists;

import java.util.List;

public class AIUserTool {


    public static EnterpriseUserDO getAiUser() {
        EnterpriseUserDO dingEnterpriseUserDTO =  new EnterpriseUserDO();
        dingEnterpriseUserDTO.setId(AIEnum.AI_ID.getCode());
        dingEnterpriseUserDTO.setName(AIEnum.AI_NAME.getCode());
        dingEnterpriseUserDTO.setUserId(AIEnum.AI_USERID.getCode());
        dingEnterpriseUserDTO.setMobile(AIEnum.AI_MOBILE.getCode());
        dingEnterpriseUserDTO.setRoles(AIEnum.AI_ROLES.getCode());
        dingEnterpriseUserDTO.setUnionid(AIEnum.AI_UUID.getCode());
        dingEnterpriseUserDTO.setActive(Boolean.TRUE);
        dingEnterpriseUserDTO.setMainAdmin(Boolean.TRUE);
        dingEnterpriseUserDTO.setIsAdmin(Boolean.TRUE);
        dingEnterpriseUserDTO.setSubordinateRange(UserSelectRangeEnum.ALL.getCode());
        dingEnterpriseUserDTO.setUserStatus(UserStatusEnum.NORMAL.getCode());
        return dingEnterpriseUserDTO;
    }

    public static UserDeptDTO getAiUserDept() {
        UserDeptDTO userDeptDTO = new UserDeptDTO();
        userDeptDTO.setUserId(AIEnum.AI_USERID.getCode());
        userDeptDTO.setName(AIEnum.AI_NAME.getCode());
        userDeptDTO.setIsAdmin(Boolean.TRUE);
        userDeptDTO.setMobile(AIEnum.AI_MOBILE.getCode());
        userDeptDTO.setEmail(AIEnum.AI_MOBILE.getCode());
        userDeptDTO.setUnionid(AIEnum.AI_UUID.getCode());
        userDeptDTO.setJobnumber(AIEnum.AI_MOBILE.getCode());
        userDeptDTO.setUserStatus(1);
        userDeptDTO.setThirdOaUniqueFlag(AIEnum.AI_UUID.getCode());
        List<DeptForUserDTO> deptList = Lists.newArrayList();
        DeptForUserDTO deptForUserDTO = new DeptForUserDTO();
        deptForUserDTO.setDeptId(Constants.ROOT_DEPT_ID_STR);
        deptForUserDTO.setDeptName(null);
        deptList.add(deptForUserDTO);
        userDeptDTO.setDeptList(deptList);
        return userDeptDTO;
    }

}
