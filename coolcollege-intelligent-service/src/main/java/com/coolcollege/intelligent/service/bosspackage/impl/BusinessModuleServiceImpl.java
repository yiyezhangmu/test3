package com.coolcollege.intelligent.service.bosspackage.impl;

import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.enums.boss.StandardStateEnum;
import com.coolcollege.intelligent.dao.bosspackage.dao.BusinessModuleDao;
import com.coolcollege.intelligent.dao.bosspackage.dao.BusinessModuleMenuMappingDao;
import com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO;
import com.coolcollege.intelligent.model.bosspackage.dto.BusinessModuleDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.bosspackage.BusinessModuleService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/23 11:28
 */
@Service(value = "businessModuleService")
public class BusinessModuleServiceImpl implements BusinessModuleService {

    @Autowired
    private BusinessModuleDao businessModuleDao;

    @Autowired
    private BusinessModuleMenuMappingDao businessModuleMenuMappingDao;

    @Override
    public List<BusinessModuleDO> selectByModuleName(String moduleName) {
        return businessModuleDao.selectByModuleName(moduleName);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addBusinessModule(BusinessModuleDTO businessModuleDTO, CurrentUser user) {
        BusinessModuleDO moduleDO = new BusinessModuleDO();
        moduleDO.setModuleName(businessModuleDTO.getModuleName());
        moduleDO.setStatus(StandardStateEnum.NORMAL.getCode());
        moduleDO.setCreateTime(new Date());
        moduleDO.setCreateUserId(user.getUserId());
        moduleDO.setCreateUserName(user.getName());
        businessModuleDao.insertSelective(moduleDO);
        setModuleMenus(moduleDO.getId(), businessModuleDTO.getMenus(), businessModuleDTO.getAppMenuList(), user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBusinessModule(BusinessModuleDTO businessModuleDTO, CurrentUser user) {
        BusinessModuleDO moduleDO = businessModuleDao.selectByPrimaryKey(businessModuleDTO.getModuleId());
        if (moduleDO == null) {
            return;
        }
        moduleDO.setModuleName(businessModuleDTO.getModuleName());
        moduleDO.setUpdateUserId(user.getUserId());
        moduleDO.setUpdateUserName(user.getName());
        moduleDO.setUpdateTime(new Date());
        businessModuleDao.updateByPrimaryKeySelective(moduleDO);
        setModuleMenus(businessModuleDTO.getModuleId(), businessModuleDTO.getMenus(), businessModuleDTO.getAppMenuList(), user);
    }

    private void setModuleMenus(Long moduleId, List<Long> menus, List<Long> appMenuList, CurrentUser user) {
        businessModuleMenuMappingDao.deleteByModuleId(moduleId);
        if (CollectionUtils.isNotEmpty(menus)) {
            businessModuleMenuMappingDao.batchInsert(moduleId, menus, PlatFormTypeEnum.PC.getCode(), user.getUserId());
        }
        if (CollectionUtils.isNotEmpty(appMenuList)) {
            businessModuleMenuMappingDao.batchInsert(moduleId, appMenuList, PlatFormTypeEnum.NEW_APP.getCode(), user.getUserId());
        }

    }

    @Override
    public List<BusinessModuleVO> getBusinessModuleList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return businessModuleDao.getModuleList(null);
    }

    @Override
    public BusinessModuleDetailVO getBusinessModuleDetail(Long moduleId) {
        BusinessModuleDetailVO result = new BusinessModuleDetailVO();
        BusinessModuleDO moduleDO = businessModuleDao.selectByPrimaryKey(moduleId);
        List<Long> menuIds = businessModuleMenuMappingDao.selectMenuIdsByModuleId(moduleId, PlatFormTypeEnum.PC.getCode());
        List<Long> appMenuIds = businessModuleMenuMappingDao.selectMenuIdsByModuleId(moduleId, PlatFormTypeEnum.NEW_APP.getCode());
        result.setModuleName(moduleDO.getModuleName());
        result.setModuleId(moduleDO.getId());
        result.setMenus(menuIds);
        result.setAppMenuList(appMenuIds);
        return result;
    }

    @Override
    public List<BusinessModuleVO> getValidModuleList() {
        return businessModuleDao.getModuleList(StandardStateEnum.NORMAL.getCode());
    }

    @Override
    public void updateModuleStatus(String status, Long moduleId) {
        businessModuleDao.updateModuleStatus(status, moduleId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteByPrimaryKey(Long moduleId) {
        businessModuleMenuMappingDao.deleteByModuleId(moduleId);
        return businessModuleDao.deleteByPrimaryKey(moduleId);
    }

}
