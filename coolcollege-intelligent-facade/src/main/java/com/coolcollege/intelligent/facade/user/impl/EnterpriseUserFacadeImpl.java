package com.coolcollege.intelligent.facade.user.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.openservices.shade.com.google.common.collect.Maps;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.user.EnterpriseUserAllFacadeDTO;
import com.coolcollege.intelligent.facade.dto.user.EnterpriseUserFacadeDTO;
import com.coolcollege.intelligent.facade.user.EnterpriseUserFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserGroupByRegionDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息RPC接口实现
 * @author zhangnan
 * @date 2021-11-19 11:21
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.ENTERPRISE_USER_FACADE_FACADE_UNIQUE_ID ,interfaceType = EnterpriseUserFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class EnterpriseUserFacadeImpl implements EnterpriseUserFacade {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Override
    public ResultDTO<List<EnterpriseUserFacadeDTO>> getUsersByUserIds(String enterpriseId, List<String> userIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIds)) {
            return ResultDTO.successResult();
        }
        // 根据企业id切库
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        String dbName = enterpriseConfigDO.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIds.stream().distinct().collect(Collectors.toList()));
        return ResultDTO.successResult(userDOList.stream().map(this::parseUserDoToUserDto).collect(Collectors.toList()));
    }

    @Override
    public ResultDTO<Integer> getEnterpriseUserNum(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        String dbName = enterpriseConfigDO.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return ResultDTO.successResult(enterpriseUserMapper.countUserAll(enterpriseId));
    }

    /**
     * 用户do转dto
     * @param userDO EnterpriseUserDO
     * @return EnterpriseUserFacadeDTO
     */
    private EnterpriseUserFacadeDTO parseUserDoToUserDto(EnterpriseUserDO userDO) {
        EnterpriseUserFacadeDTO userFacadeDTO = new EnterpriseUserFacadeDTO();
        userFacadeDTO.setUserId(userDO.getUserId());
        userFacadeDTO.setName(userDO.getName());
        userFacadeDTO.setId(userDO.getId());
        return userFacadeDTO;
    }

    @Override
    public PageInfo<EnterpriseUserAllFacadeDTO> getUsersByPage(String enterpriseId,
                                                               String name,
                                                               Integer pageSize,
                                                               Integer pageNum) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        PageHelper.startPage(pageNum, pageSize);
        List<EnterpriseUserAllFacadeDTO> usersByPage = enterpriseUserMapper.getUsersByPage(enterpriseId,name);
        return new PageInfo<>(usersByPage);
    }

    @Override
    public ResultDTO<EnterpriseUserFacadeDTO> getUsersByUnionId(String corpId, String appType, String unionId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.getEnterpriseConfigByCorpIdAndAppType(corpId, appType);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserDO enterpriseUserDO = enterpriseUserMapper.selectByUnionid(enterpriseConfigDO.getEnterpriseId(), unionId);
        return ResultDTO.successResult(this.parseUserDoToUserDto(enterpriseUserDO));
    }

    @Override
    public ResultDTO<Map<String, List<String>>> getUsersByRegionIds(String enterpriseId, List<String> regionIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<UserGroupByRegionDTO> users = enterpriseUserMapper.listUserIdByRegionIdList(enterpriseId, regionIds);

        // 初始化结果map，键为regionId，值为用户ID列表
        Map<String, List<String>> vo = Maps.newHashMap();
        // 初始化一个包含regionIds的Set，用于快速查找
        Set<String> regionIdSet = new HashSet<>(regionIds);

        if (CollectionUtils.isNotEmpty(users)) {
            // 为每个regionId初始化一个空列表
            for (String regionId : regionIds) {
                vo.put(regionId, new ArrayList<>());
            }
            // 遍历用户列表，并根据regionId进行分组
            for (UserGroupByRegionDTO user : users) {
                String userRegionIds = user.getUserRegionIds(); // 用户的区域ID路径
                String userId = user.getUserId(); // 用户ID
                // 将userRegionIds按"/"拆分成单个区域ID
                String[] userRegionIdArray = userRegionIds.split("/");
                // 检查每个拆分后的区域ID是否在regionIdSet中
                for (String userRegionId : userRegionIdArray) {
                    if (regionIdSet.contains(userRegionId)) {
                        vo.get(userRegionId).add(userId);
                    }
                }
            }
        }
        Map<String, List<String>> newVo = vo.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().distinct().collect(Collectors.toList())
                ));
        return ResultDTO.successResult(newVo);
    }

    @Override
    public ResultDTO<Map<String, List<String>>> getRoleNameNameByUserIds(String enterpriseId, List<String> userIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<EntUserRoleDTO> userRoleList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userIds)){
            userRoleList = enterpriseUserRoleMapper.selectUserRoleByUserIds(enterpriseId, userIds);
            Map<String, List<String>> userRoleMap = userRoleList.stream().collect(Collectors.groupingBy(EntUserRoleDTO::getUserId, Collectors.mapping(EntUserRoleDTO::getRoleName, Collectors.toList())));
            return ResultDTO.successResult(userRoleMap);
        }
        return ResultDTO.successResult(new HashMap<>());
    }
}
