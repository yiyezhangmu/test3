package com.coolcollege.intelligent.facade.common;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.dto.organization.OrganizationUserDTO;
import com.coolcollege.intelligent.facade.dto.role.RoleDTO;
import com.coolcollege.intelligent.facade.dto.user.UserAuthScopeDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: CommonApiImpl
 * @Description:
 * @date 2022-11-07 14:35
 */
@Service
@SofaService(uniqueId = IntelligentFacadeConstants.COMMON_API_UNIQUE_ID ,interfaceType = CommonApi.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class CommonApiImpl implements CommonApi{

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private AuthVisualService authVisualService;

    @Resource
    private RegionMapper regionMapper;
    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Override
    public ResultDTO<List<RoleDTO>> getRoleByRoleIds(String enterpriseId, List<Long> roleIds) throws ApiException{
        EnterpriseConfigDTO enterpriseConfig = null;
        try {
            enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            String dbName = enterpriseConfig.getDbName();
            DataSourceHelper.changeToSpecificDataSource(dbName);
            List<SysRoleDO> roleByRoleIds = sysRoleService.getRoleByRoleIds(enterpriseId, roleIds);
            List<RoleDTO> resultList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(roleByRoleIds)){
                for (SysRoleDO role : roleByRoleIds) {
                    RoleDTO roleDTO = convertDTO(role);
                    resultList.add(roleDTO);
                }
            }
            return ResultDTO.SuccessResult(resultList);
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    @Override
    public ResultDTO<UserAuthScopeDTO> getUserAuthStoreIdsAndUserIds(String enterpriseId, String userId) throws ApiException{
        EnterpriseConfigDTO enterpriseConfig = null;
        try {
            enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            String dbName = enterpriseConfig.getDbName();
            DataSourceHelper.changeToSpecificDataSource(dbName);
            com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO userAuthScope = authVisualService.getUserAuthStoreIdsAndUserIds(enterpriseId, userId);
            return ResultDTO.SuccessResult(new UserAuthScopeDTO(userAuthScope.getIsAdmin(), userAuthScope.getUserIds(), userAuthScope.getStoreIds()));
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    @Override
    public ResultDTO<OrganizationUserDTO> getOrganizationUserIds(String enterpriseId, String storeId) throws ApiException{
        EnterpriseConfigDTO enterpriseConfig = null;
        try {
            enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            String dbName = enterpriseConfig.getDbName();
            DataSourceHelper.changeToSpecificDataSource(dbName);
            List<String> storeUserIds = authVisualService.getStoreAuthUserIds(enterpriseId, Arrays.asList(storeId));
            return ResultDTO.SuccessResult(new OrganizationUserDTO(storeUserIds));
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    @Override
    public ResultDTO<List<String>> getUserNumOfRoot(String enterpriseId) throws ApiException{
        //cc
        EnterpriseConfigDTO enterpriseConfig = null;
        try {
            enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            String dbName = enterpriseConfig.getDbName();
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //查询根节点
            Long l = regionMapper.selectRootRegionId(enterpriseId);

            List<String> num =  enterpriseUserMapper.selectUserByRegionId(enterpriseId, l.toString());
            return ResultDTO.SuccessResult(num);
        } catch (Exception e) {
            throw new ApiException(e);
        }

    }

    private RoleDTO convertDTO(SysRoleDO param){
        RoleDTO result = new RoleDTO();
        result.setId(param.getId());
        result.setRoleName(param.getRoleName());
        result.setAppMenu(param.getAppMenu());
        result.setPriority(param.getPriority());
        result.setSource(param.getSource());
        result.setRoleAuth(param.getRoleAuth());
        result.setPositionType(param.getPositionType());
        result.setSynDingRoleId(param.getSynDingRoleId());
        result.setPriority(param.getPriority());
        result.setRoleEnum(param.getRoleEnum());
        return result;
    }
}
