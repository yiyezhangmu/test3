package com.coolcollege.intelligent.service.xianfeng.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.xianfeng.XianFengService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class XianFengServiceImpl implements XianFengService {

    @Resource
    SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleService sysRoleService;
    @Override
    public List<UserDTO> investmentManager(String enterpriseId,Integer pageNum ,Integer pageSize) {
        List<SysRoleDO> sysRoleDOS = sysRoleMapper.selectByRoleNameAndSource(enterpriseId, Constants.INVESTMENT_MANAGER, Constants.CREATE);
        if (CollectionUtils.isEmpty(sysRoleDOS)){
            throw new ServiceException(ErrorCodeEnum.NO_INVESTMENT_MANAGER);
        }
        Long roleId = sysRoleDOS.get(0).getId();
        List<UserDTO> userDTOS = sysRoleService.detailUserSystemRole(enterpriseId, roleId,null, pageNum, pageSize, null);
        return userDTOS;
    }
}
