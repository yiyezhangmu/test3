package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.common.sync.vo.AuthInfo;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.taobao.api.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/11/29 11:19
 * @Version 1.0
 */
public interface FsService {


    /**
     * 获取企业授权信息
     * @param corpId
     * @param appType
     * @return
     * @throws Exception
     */
    AuthInfoDTO getAuthInfo(String corpId, String appType);

    /**
     * 飞书租户token
     * @param corpId
     * @param appType
     * @return
     * @throws Exception
     */
    public String getAccessToken(String corpId, String appType) ;

    /**
     * 获取企业通讯录可见范围
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    AuthScopeDTO getAuthScope(String corpId, String appType);

    /**
     * 获取指定节点 子部门(全部子部门)包括自己
     * @param id
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    List<SysDepartmentDO> getAllDepts(String id,  String corpId, String appType) throws ApiException;

    /**
     * 获取子部门(直属)
     * @param id
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    List<SysDepartmentDO> getSubDepts(String id,  String corpId, String appType,Boolean fetchChild);

    /**
     * 获取授权范围内 所有的部门
     * @param ids
     * @param corpId
     * @param appType
     * @return
     */
     List<SysDepartmentDO> getAuthScopeAllDepts(List<String> ids, String corpId, String appType);


    /**
     * 获取飞书用户详情
     * @param corpId
     * @param userId
     * @param employeeRoleId
     * @param appType
     * @return
     */
     EnterpriseUserRequest getFsUserDetail(String corpId, String userId,  String employeeRoleId, String appType);


    /**
     * 获取部门下用户 当前部门用户直连
     * @param corpId
     * @param deptId
     * @param appType
     * @return
     */
     List<EnterpriseUserRequest> getDeptUsers(String corpId, String deptId,String appType);

    /**
     * 飞书管理员列表
     * @param corpId
     * @param appType
     * @return
     */
     List<String> getFsAdminList(String corpId, String appType);
}
