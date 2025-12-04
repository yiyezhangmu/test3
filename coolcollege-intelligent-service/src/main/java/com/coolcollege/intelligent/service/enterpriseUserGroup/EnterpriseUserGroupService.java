package com.coolcollege.intelligent.service.enterpriseUserGroup;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupAddRequest;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupRemoveRequest;
import com.coolcollege.intelligent.model.usergroup.vo.UserGroupVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author wxp
 * @Date 2022/12/29 11:18
 * @Version 1.0
 */
public interface EnterpriseUserGroupService {

    /**
     * 增加用户分组
     * @param enterpriseId
     * @param userGroupAddRequest
     * @param user
     * @return
     */
    Boolean saveOrUpdateUserGroup(String enterpriseId, UserGroupAddRequest userGroupAddRequest, CurrentUser user);

    Boolean updateUserGroup(String enterpriseId,String userGroupId,List<String> userIdList);

    void batchDeleteGroup(String enterpriseId, String groupId, List<String> userIdList);

    /**
     * 获取用户分组列表
     * @param enterpriseId
     * @param groupName 分组名，模糊查询用
     * @return
     */
    List<UserGroupVO> listUserGroup(String enterpriseId, String groupName, CurrentUser user);

    UserGroupVO getGroupInfo(String enterpriseId, String groupId, CurrentUser user);

    PageInfo<EnterpriseUserDTO> listUserByGroupId(String enterpriseId, String groupId, String userName, Integer pageNum, Integer pageSize, CurrentUser currentUser);

    void updateUserGroup(String enterpriseId, List<String> groupIdList, String userId, CurrentUser currentUser);

    Boolean configUser(String enterpriseId, UserGroupAddRequest userGroupAddRequest, CurrentUser user);

    Map<String, List<UserGroupDTO>> getUserGroupMap(String enterpriseId, List<String> userIdList);


    Boolean batchRemoveUser(String enterpriseId, UserGroupRemoveRequest userGroupRemoveRequest, CurrentUser user);

    Boolean batchExport(String enterpriseId, UserGroupRemoveRequest userGroupRemoveRequest, CurrentUser user);


}
