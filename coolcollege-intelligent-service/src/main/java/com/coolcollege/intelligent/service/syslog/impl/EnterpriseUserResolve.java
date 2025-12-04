package com.coolcollege.intelligent.service.syslog.impl;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.util.LogUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.model.user.BatchUserRegionMappingDTO;
import com.coolcollege.intelligent.model.user.BatchUserStatusDTO;
import com.coolcollege.intelligent.model.user.UserAddDTO;
import com.coolcollege.intelligent.service.requestBody.user.EnterpriseUserRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.BATCH_MOVE;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.FREEZE;

/**
 * @Author: hu hu
 * @Date: 2025/1/23 15:17
 * @Description:
 */
@Service
@Slf4j
public class EnterpriseUserResolve extends AbstractOpContentResolve {

    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private RegionMapper regionMapper;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
        funcMap.put(FREEZE, this::freeze);
        funcMap.put(BATCH_MOVE, this::batchMove);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.ENTERPRISE_USER;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        String content = "新增了成员「%s（%s）」";
        String name = "";
        String mobile = "";
        UserAddDTO param = LogUtil.paresString(sysLogDO.getReqParams(), "param", UserAddDTO.class);
        if (Objects.nonNull(param)) {
            name = param.getName();
            mobile = param.getMobile();
        }
        return String.format(content, name, mobile);
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        String content = "修改了成员「%s（%s）」";
        String name = "";
        String mobile = "";
        EnterpriseUserRequestBody userRequestBody = LogUtil.paresString(sysLogDO.getReqParams(), "userRequestBody", EnterpriseUserRequestBody.class);
        if (Objects.nonNull(userRequestBody)) {
            name = userRequestBody.getUserName();
            mobile = userRequestBody.getMobile();
        }
        return String.format(content, name, mobile);
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return super.delete(enterpriseId, sysLogDO);
    }

    protected String freeze(String enterpriseId, SysLogDO sysLogDO) {
        BatchUserStatusDTO param = LogUtil.paresString(sysLogDO.getReqParams(), "param", BatchUserStatusDTO.class);
        if (Objects.nonNull(param) && CollectionUtils.isNotEmpty(param.getUnionids())) {
            String content;
            if (param.getUserStatus() == 1) {
                content = "解冻了成员%s";
            } else {
                content = "冻结了成员%s";
            }
            String names = getNames(enterpriseId, param.getUnionids());
            return String.format(content, names);
        }
        return  "";
    }

    protected String batchMove(String enterpriseId, SysLogDO sysLogDO) {
        String content = "批量移动成员%s至组织架构%s";
        BatchUserRegionMappingDTO param = LogUtil.paresString(sysLogDO.getReqParams(), "param", BatchUserRegionMappingDTO.class);
        if (Objects.nonNull(param)) {
            String names = getNames(enterpriseId, param.getUnionIds());
            String regionNames = getRegionNames(enterpriseId, param.getRegionIds());
            return String.format(content, names, regionNames);
        }
        return  "";
    }

    /**
     * 获取用户名称
     * @param enterpriseId
     * @param unionIds
     * @return
     */
    private String getNames(String enterpriseId, List<String> unionIds) {
        List<String> userIdsByUnionIds = enterpriseUserDao.getUserIdsByUnionIds(enterpriseId, unionIds);
        if (CollectionUtils.isEmpty(userIdsByUnionIds)) {
            return "";
        }
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdsByUnionIds);
        if (CollectionUtils.isEmpty(enterpriseUserDOList)) {
            return "";
        }
        return enterpriseUserDOList.stream().map(t -> String.format("「%s（%s）」", t.getName(), t.getMobile())).collect(Collectors.joining("、"));
    }

    /**
     * 获取区域名称
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    private String getRegionNames(String enterpriseId, List<String> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return "";
        }
        List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIds);
        if (CollectionUtils.isEmpty(regionByRegionIds)) {
            return "";
        }
        return regionByRegionIds.stream().map(t -> String.format("「%s」", t.getName())).collect(Collectors.joining("、"));
    }
}
