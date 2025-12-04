package com.coolcollege.intelligent.service.setting.impl;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.setting.EnterpriseNoticeSettingMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.setting.EnterpriseNoticeSettingDO;
import com.coolcollege.intelligent.model.setting.request.EnterpriseNoticeSettingRequest;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeRoleVO;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeSettingVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.service.setting.EnterpriseNoticeSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
@Service
public class EnterpriseNoticeSettingServiceImpl implements EnterpriseNoticeSettingService {

    @Resource
    private EnterpriseNoticeSettingMapper enterpriseNoticeSettingMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private AliyunPersonGroupMapper aliyunPersonGroupMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public List<EnterpriseNoticeSettingVO> listEnterpriseNotice(String eid) {

        List<EnterpriseNoticeSettingDO> enterpriseNoticeSettingList = enterpriseNoticeSettingMapper.selectEnterpriseNoticeSetting(eid);
        if(CollectionUtils.isEmpty(enterpriseNoticeSettingList)){
            return Collections.emptyList();
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<String> personGroupIdList = enterpriseNoticeSettingList.stream()
                .map(EnterpriseNoticeSettingDO::getPersonGroupId)
                .collect(Collectors.toList());
        List<AliyunPersonGroupDO> personGroupDOList = aliyunPersonGroupMapper.listAliyunPersonGroupById(eid, personGroupIdList);

        if(CollectionUtils.isEmpty(personGroupDOList)){
            return Collections.emptyList();
        }
        Map<String, String> personGroupMap = personGroupDOList.stream()
                .filter(a->a.getPersonGroupId()!=null&&a.getPersonGroupName()!=null)
                .collect(Collectors.toMap(AliyunPersonGroupDO::getPersonGroupId, AliyunPersonGroupDO::getPersonGroupName, (a, b) -> a));
        List<String> effectivePersonGroupIdList = personGroupDOList.stream()
                .map(AliyunPersonGroupDO::getPersonGroupId)
                .collect(Collectors.toList());
        //筛选出有效的配置的职位
        List<EnterpriseNoticeSettingDO> effectiveSettingList = enterpriseNoticeSettingList.stream()
                .filter(data -> effectivePersonGroupIdList.contains(data.getPersonGroupId()))
                .collect(Collectors.toList());
        List<Long> roleIdStrList = effectiveSettingList.stream()
                .map(EnterpriseNoticeSettingDO::getRoleIdStr)
                .map(data-> StrUtil.splitTrim(data,","))
                .flatMap(Collection::stream)
                .map(Long::valueOf)
                .collect(Collectors.toList());


        List<SysRoleDO> roleList = sysRoleMapper.getRoleList(eid, roleIdStrList);
        if(CollectionUtils.isEmpty(roleList)){
            return Collections.emptyList();
        }
        Map<Long, String> roleMap = roleList.stream()
                .filter(a->a.getId()!=null&&a.getRoleName()!=null)
                .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName, (a, b) -> a));
        //组装返回数据
        return effectiveSettingList.stream()
                .map(data -> mapEnterpriseNoticeSettingVO(personGroupMap, roleMap, data))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


    }

    private EnterpriseNoticeSettingVO mapEnterpriseNoticeSettingVO(Map<String, String> personGroupMap,
                                                                   Map<Long, String> roleMap,
                                                                   EnterpriseNoticeSettingDO data) {

        List<String> roleIdList = StrUtil.splitTrim(data.getRoleIdStr(), ",");
        List<EnterpriseNoticeRoleVO> roleVOList = roleIdList.stream()
                .map(Long::valueOf)
                .map(roleId -> {
                    String roleName = roleMap.get(roleId);
                    if (StringUtils.isBlank(roleName)) {
                        return null;
                    }
                    EnterpriseNoticeRoleVO roleVO = new EnterpriseNoticeRoleVO();
                    roleVO.setRoleId(roleId);
                    roleVO.setRoleName(roleName);
                    return roleVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleVOList)) {
            return null;
        }
        EnterpriseNoticeSettingVO settingVO = new EnterpriseNoticeSettingVO();
        settingVO.setPersonGroupId(data.getPersonGroupId());
        settingVO.setPersonGroupName(personGroupMap.get(data.getPersonGroupId()));
        settingVO.setRoleVOList(roleVOList);
        return settingVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateEnterpriseNotice(String eid, List<EnterpriseNoticeSettingRequest> requestList) {


        enterpriseNoticeSettingMapper.deleteEnterpriseNoticeSetting(eid);
        List<EnterpriseNoticeSettingDO> enterpriseNoticeSettingDOList = ListUtils.emptyIfNull(requestList)
                .stream()
                .map(data -> {
                    List<Long> roleIdList = data.getRoleIdList();
                    if (CollectionUtils.isEmpty(roleIdList)) {
                        return null;
                    }
                    EnterpriseNoticeSettingDO settingDO = new EnterpriseNoticeSettingDO();
                    settingDO.setPersonGroupId(data.getPersonGroupId());
                    settingDO.setRoleIdStr(roleIdList.stream().map(Object::toString).collect(Collectors.joining(",")));
                    settingDO.setEnterpriseId(eid);
                    return settingDO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(enterpriseNoticeSettingDOList)){
            enterpriseNoticeSettingMapper.batchInsertEnterpriseNoticeSetting(enterpriseNoticeSettingDOList);
        }
        return true;
    }
}
