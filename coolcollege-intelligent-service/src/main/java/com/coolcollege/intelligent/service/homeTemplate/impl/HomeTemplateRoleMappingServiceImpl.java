package com.coolcollege.intelligent.service.homeTemplate.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateRoleMappingService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/7/4 14:44
 * @Version 1.0
 */
@Service
public class HomeTemplateRoleMappingServiceImpl implements HomeTemplateRoleMappingService {


    @Override
    public HomeTemplateRoleMappingDO initHomeTempRoleMapping(Long roleId,String CoolPosition){
        HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = new HomeTemplateRoleMappingDO();
        homeTemplateRoleMappingDO.setRoleId(roleId);
        homeTemplateRoleMappingDO.setTemplateId(CoolPositionTypeEnum.STORE_INSIDE.getCode().equals(CoolPosition)? Constants.INDEX_TWO:Constants.INDEX_ONE);
        homeTemplateRoleMappingDO.setCreateId("System");
        homeTemplateRoleMappingDO.setCreateTime(new Date());
        homeTemplateRoleMappingDO.setUpdateId("System");
        homeTemplateRoleMappingDO.setUpdateTime(new Date());
        return homeTemplateRoleMappingDO;
    }
}
