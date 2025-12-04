package com.coolcollege.intelligent.service.bosspackage.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.bosspackage.dao.BusinessModuleMenuMappingDao;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageDao;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageModuleMappingDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageDTO;
import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageNumDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.CurrentPackageDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.bosspackage.EnterprisePackageService;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/24 14:14
 */
@Service(value = "enterprisePackageService")
@Slf4j
public class EnterprisePackageServiceImpl implements EnterprisePackageService {

    @Autowired
    private EnterprisePackageDao enterprisePackageDao;

    @Autowired
    private EnterpriseConfigDao enterpriseConfigDao;

    @Autowired
    private EnterprisePackageModuleMappingDao enterprisePackageModuleMappingDao;

    @Autowired
    private BusinessModuleMenuMappingDao businessModuleMenuMappingDao;

    @Override
    public EnterprisePackageDO selectByPrimaryKey(Long id) {
        return enterprisePackageDao.selectByPrimaryKey(id);
    }

    @Override
    public List<EnterprisePackageDO> selectByPackageName(String packageName) {
        return enterprisePackageDao.selectByPackageName(packageName);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addEnterprisePackage(EnterprisePackageDTO enterprisePackageDTO, CurrentUser user) {
        EnterprisePackageDO packageDO = new EnterprisePackageDO();
        packageDO.setPackageName(enterprisePackageDTO.getPackageName());
        packageDO.setCreateTime(new Date());
        packageDO.setCreateUserId(user.getUserId());
        packageDO.setCreateUserName(user.getName());
        enterprisePackageDao.insertSelective(packageDO);
        setEnterprisePackageModules(packageDO.getId(), enterprisePackageDTO.getModuleIds(), user.getUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateEnterprisePackage(EnterprisePackageDTO enterprisePackageDTO, CurrentUser user) {
        EnterprisePackageDO enterprisePackageDO = enterprisePackageDao.selectByPrimaryKey(enterprisePackageDTO.getPackageId());
        if (enterprisePackageDO == null) {
            return;
        }
        enterprisePackageDO.setPackageName(enterprisePackageDTO.getPackageName());
        enterprisePackageDO.setUpdateUserId(user.getUserId());
        enterprisePackageDO.setUpdateUserName(user.getName());
        enterprisePackageDO.setUpdateTime(new Date());
        enterprisePackageDao.updateByPrimaryKeySelective(enterprisePackageDO);
        setEnterprisePackageModules(enterprisePackageDTO.getPackageId(), enterprisePackageDTO.getModuleIds(), user.getUserId());
    }

    private void setEnterprisePackageModules(Long packageId, List<Long> moduleIds, String createUserId) {
        enterprisePackageModuleMappingDao.deleteByPackageId(packageId);
        if (CollectionUtils.isNotEmpty(moduleIds)) {
            enterprisePackageModuleMappingDao.batchInsert(packageId, moduleIds, createUserId);
        }
    }

    @Override
    public List<EnterprisePackageVO> selectAllEnterprisePackage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EnterprisePackageVO> result = enterprisePackageDao.selectAll();
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        List<EnterprisePackageNumDTO> packageNums = enterpriseConfigDao.enterprisePackageStatistics();
        Map<Long, Integer> packageNumMap = ListUtils.emptyIfNull(packageNums)
                .stream()
                .collect(Collectors.toMap(EnterprisePackageNumDTO::getEnterprisePackageId, EnterprisePackageNumDTO::getEnterprisePackageNum, (a, b) -> a));
        result.forEach(re -> {
            Integer useNum = packageNumMap.get(re.getId());
            useNum = useNum == null ? 0 : useNum;
            re.setUseNum(useNum);
        });
        return result;
    }

    @Override
    public List<EnterprisePackageVO> getPackageList(String status) {
        return enterprisePackageDao.getPackageList(status);
    }

    @Override
    public CurrentPackageDetailVO getEnterprisePackageDetail(Long packageId) {
        EnterprisePackageDO enterprisePackageDO = enterprisePackageDao.selectByPrimaryKey(packageId);
        if (enterprisePackageDO == null) {
            throw new ServiceException(ErrorCodeEnum.INVALID_ENTERPRISE_PACKAGE);
        }
        List<Long> moduleIds = enterprisePackageModuleMappingDao.selectModuleIdsByPackageId(packageId);
        CurrentPackageDetailVO vo = new CurrentPackageDetailVO();
        vo.setPackageId(packageId);
        vo.setModuleIdList(moduleIds);
        vo.setPackageName(enterprisePackageDO.getPackageName());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByPrimaryKey(Long packageId) {
        enterprisePackageModuleMappingDao.deleteByPackageId(packageId);
        enterprisePackageDao.deleteByPrimaryKey(packageId);
    }

    @Override
    public List<Long> getMenuIdListByPackageId(Long packageId, String platformType, String appType, Boolean accessCoolCollege) {
        try {
            List<Long> moduleIds = enterprisePackageModuleMappingDao.selectModuleIdsByPackageId(packageId);
            if (AppTypeEnum.isCoolCollege(appType)) {
                //如果是数值门店类型  直接过滤业培一体2.0模块
                moduleIds.remove(Constants.TRAINING_BUSINESS_MODULE);
            } else if (!accessCoolCollege){
                //如果是酷店掌类型  并且未开启业培一体，直接过滤业培一体2.0模块
                moduleIds.remove(Constants.TRAINING_BUSINESS_MODULE);
            }
            List<Long> menuIds = businessModuleMenuMappingDao.selectMenuIdsByModuleIds(moduleIds, platformType);
            return menuIds;
        } catch (Exception e) {
            log.error("获取企业菜单失败", e);
        }
        return new ArrayList<>();
    }


}
