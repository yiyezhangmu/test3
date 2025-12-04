package com.coolcollege.intelligent.service.enterprise.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.AuthInfo;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.scanner.Constant;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/11/29 11:20
 * @Version 1.0
 */
@Service(value = "fsService")
@Slf4j
public class FsServiceImpl implements FsService {
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Override
    public AuthInfoDTO getAuthInfo(String corpId, String appType) {
        log.info("corpId:{},appType:{}", corpId, appType);
        AuthInfoDTO authInfo = null;
        try {
            authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
        } catch (ApiException e) {
            log.info("飞书授权信息获取失败：authInfo:{},e:{}",JSONObject.toJSONString(authInfo),e);
        }
        log.info("fs_authInfo:{}",authInfo);
        return authInfo;
    }

    @Override
    public String getAccessToken(String corpId, String appType){
        log.info("corpId:{},appType:{}", corpId, appType);
        String accessToken = "";
        try {
            accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
        } catch (ApiException e) {
            log.info("飞书token获取失败：token:{},e:{}",accessToken,e);
        }
        log.info("fs_accessToken:{}",accessToken);
        return accessToken;
    }

    @Override
    public AuthScopeDTO getAuthScope(String corpId, String appType)  {
        log.info("corpId:{},appType:{}", corpId, appType);
        AuthScopeDTO authScope = null;
        try {
            authScope = enterpriseInitConfigApiService.getAuthScope(corpId, appType);
        } catch (ApiException e) {
            log.info("飞书authScope获取失败" ,e);
            throw new ServiceException(ErrorCodeEnum.API_ERROR, e.getMessage());
        }
        log.info("authScope:{}",authScope);
        return authScope;
    }

    @Override
    public List<SysDepartmentDO> getAllDepts(String id, String corpId, String appType) throws ApiException {
        List<SysDepartmentDO> sysDepartments = Lists.newArrayList();
        List<SysDepartmentDTO> departments = enterpriseInitConfigApiService.getDepartments(corpId, appType, id);
        log.info("feishu_getAllDepts:{}",JSONObject.toJSONString(departments));
        if (CollectionUtils.isEmpty(departments)) {
            return sysDepartments;
        }
        for (SysDepartmentDTO sysDepartmentDTO:departments) {
            SysDepartmentDO sysDepartment = new SysDepartmentDO();
            sysDepartment.setId(sysDepartmentDTO.getId());
            sysDepartment.setName(sysDepartmentDTO.getName());
            sysDepartment.setParentId(sysDepartmentDTO.getParentId());
            sysDepartment.setDepartOrder(sysDepartmentDTO.getDepartOrder());
            sysDepartment.setDeptManagerUseridList(sysDepartmentDTO.getDeptManagerUseridList());
            sysDepartment.setDefineDepartmentId(sysDepartmentDTO.getDefineDepartmentId());
            sysDepartments.add(sysDepartment);
        }
        return sysDepartments;
    }

    @Override
    public List<SysDepartmentDO> getSubDepts(String id, String corpId, String appType,Boolean fetchChild) {
        List<SysDepartmentDO> sysDepartments = Lists.newArrayList();
        List<SysDepartmentDTO> departments = Lists.newArrayList();;
        try {
            departments = enterpriseInitConfigApiService.getSubDepartments(corpId, appType, id,fetchChild);
        } catch (ApiException e) {
            log.info("飞书子部门获取失败：getSubDepts:{},e:{}",JSONObject.toJSONString(departments),e);
        }
        log.info("feishu_getSubDepts:{}",JSONObject.toJSONString(departments));
        if (CollectionUtils.isEmpty(departments)) {
            return sysDepartments;
        }
        for (SysDepartmentDTO sysDepartmentDTO:departments) {
            SysDepartmentDO sysDepartment = new SysDepartmentDO();
            sysDepartment.setId(sysDepartmentDTO.getId());
            sysDepartment.setName(sysDepartmentDTO.getName());
            sysDepartment.setParentId(sysDepartmentDTO.getParentId());
            sysDepartment.setDepartOrder(sysDepartmentDTO.getDepartOrder());
            sysDepartment.setDeptManagerUseridList(sysDepartmentDTO.getDeptManagerUseridList());
            sysDepartment.setDefineDepartmentId(sysDepartmentDTO.getDefineDepartmentId());
            sysDepartments.add(sysDepartment);
        }
        return sysDepartments;
    }

    /**
     * 获取授权范围内 所有的部门(递归所有部门 )
     * @param ids
     * @param corpId
     * @param appType
     * @return
     */
    @Override
    public List<SysDepartmentDO> getAuthScopeAllDepts(List<String> ids, String corpId, String appType){
        List<SysDepartmentDO> departments = Lists.newArrayList();

        if (CollectionUtils.isEmpty(ids)){
            List<SysDepartmentDO> subDepts = Lists.newArrayList();
            try {
                subDepts = getAllDepts(null, corpId,appType);
            } catch (ApiException e) {
                log.info("飞书获取部门列表失败:id:{},corpId:{},appType:{},e:{}",ids,corpId,appType,e);
            }
            //过滤掉root部门
            subDepts = subDepts.stream().filter(x->!Constants.ROOT_DEPT_ID_STR.equals(x.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDepts)) {
                departments.addAll(subDepts);
            }
        }
        for (String id : ids) {
            try {
                SysDepartmentDTO sysDepartmentDTO = enterpriseInitConfigApiService.getDepartmentDetail(corpId, id, appType);
                SysDepartmentDO sysDepartment = new SysDepartmentDO();
                sysDepartment.setId(sysDepartmentDTO.getId());
                sysDepartment.setName(sysDepartmentDTO.getName());
                sysDepartment.setParentId(sysDepartmentDTO.getParentId());
                sysDepartment.setDepartOrder(sysDepartmentDTO.getDepartOrder());
                sysDepartment.setDeptManagerUseridList(sysDepartmentDTO.getDeptManagerUseridList());
                sysDepartment.setDefineDepartmentId(sysDepartmentDTO.getDefineDepartmentId());
                departments.add(sysDepartment);
            } catch (ApiException e) {
                log.info("飞书获取部门详情失败:id:{},corpId:{},appType:{},e:{}", id, corpId, appType, e);
            }
            //递归查询所有子节点
            List<SysDepartmentDO> subDepts = getSubDepts(id, corpId,appType, Boolean.TRUE);
            if (CollectionUtils.isNotEmpty(subDepts)) {
                departments.addAll(subDepts);
            }
        }

        //去重
        departments = departments.stream().distinct().collect(Collectors.toList());
        Set<String> idSet = departments.stream().map(r -> r.getId()).distinct().collect(Collectors.toSet());

        departments.forEach(d -> {
            if (!idSet.contains(d.getParentId())) {
                d.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
            }
        });

        return departments;
    }

    @Override
    public EnterpriseUserRequest getFsUserDetail(String corpId, String userId, String employeeRoleId, String appType) {
        EnterpriseUserDTO enterpriseUserDTO = null;
        try {
            enterpriseUserDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, userId, appType);
        } catch (ApiException e) {
            log.info("飞书用户查询失败:userId:{},corpId:{},appType:{},e:{}",userId,corpId,appType,e);
        }
        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
        if (Objects.isNull(enterpriseUserDTO)) {
            //初始化防止出现null 后续用到引发空指针
            enterpriseUserRequest.setEnterpriseUserDO(new EnterpriseUserDO());
            return enterpriseUserRequest;
        }
        EnterpriseUserDO enterpriseUser = new EnterpriseUserDO();
        //激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
        if (enterpriseUserDTO.getActive() != null && enterpriseUserDTO.getActive()) {
            enterpriseUser.setActive(enterpriseUserDTO.getActive());
        } else {
            enterpriseUser.setActive(Boolean.FALSE);
        }
        enterpriseUserRequest.setDepartmentLists(enterpriseUserDTO.getDepartmentLists());
        enterpriseUserRequest.setLeaderInDepts(enterpriseUserDTO.getIsLeaderInDepts());

        if (CollectionUtils.isNotEmpty(enterpriseUserDTO.getIsLeaderInDepts())) {
            enterpriseUser.setIsLeaderInDepts(JSONObject.toJSONString(enterpriseUserDTO.getIsLeaderInDepts()));
        }
        enterpriseUser.setCreateTime(new Date());
        enterpriseUser.setUnionid(enterpriseUserDTO.getUnionid());
        enterpriseUser.setIsAdmin(false);
        enterpriseUser.setRoles(employeeRoleId);
        enterpriseUser.setAppType(appType);
        enterpriseUser.setRemark(enterpriseUserDTO.getUserId());
        enterpriseUser.setUserId(enterpriseUserDTO.getUserId());
        enterpriseUser.setName(enterpriseUserDTO.getName());
        enterpriseUser.setPosition(enterpriseUserDTO.getPosition());
        enterpriseUser.setMobile(enterpriseUserDTO.getMobile());
        enterpriseUser.setJobnumber(enterpriseUserDTO.getJobnumber());
        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
        return enterpriseUserRequest;
    }

    @Override
    public List<EnterpriseUserRequest> getDeptUsers(String corpId, String deptId, String appType) {
        List<EnterpriseUserDTO> departmentUsers = Lists.newArrayList();
        try {
            departmentUsers = enterpriseInitConfigApiService.getDepartmentUsers(corpId, deptId, appType);
            log.info("飞书部门下人员{}:",JSONObject.toJSONString(departmentUsers));
        } catch (ApiException e) {
            log.info("飞书部门下用户查询失败:deptId:{},corpId:{},appType:{},e:{}",deptId,corpId,appType,e);
        }

        if (CollectionUtils.isEmpty(departmentUsers)){
            return  Lists.newArrayList();
        }
        List<EnterpriseUserRequest> userList = Lists.newArrayList();
        for (EnterpriseUserDTO enterpriseUserDTO:departmentUsers) {
            EnterpriseUserDO enterpriseUser = new EnterpriseUserDO();
            enterpriseUser.setCreateTime(new Date());
            enterpriseUser.setIsAdmin(false);
            enterpriseUser.setAppType(appType);
            enterpriseUser.setRemark(enterpriseUserDTO.getRemark());
            enterpriseUser.setUserId(enterpriseUserDTO.getUserId());
            if (enterpriseUserDTO.getIsLeaderInDepts() != null) {
                enterpriseUser.setIsLeaderInDepts(JSONObject.toJSONString(enterpriseUserDTO.getIsLeaderInDepts()));
            }
            EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
            enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
            if (CollectionUtils.isNotEmpty(enterpriseUserDTO.getDepartmentLists())) {
                enterpriseUserRequest.setDepartment(JSONObject.toJSONString(enterpriseUserDTO.getDepartmentLists()));
            }
            userList.add(enterpriseUserRequest);
        }
        return userList;
    }

    @Override
    public List<String> getFsAdminList(String corpId, String appType) {
        List<String> adminUserList = Lists.newArrayList();
        try {
            adminUserList = enterpriseInitConfigApiService.getAdminUserList(corpId, appType);
        } catch (ApiException e) {
            log.info("飞书管理员列表查询失败:corpId:{},appType:{},e:{}",corpId,appType,e);
        }
        return adminUserList;
    }
}
