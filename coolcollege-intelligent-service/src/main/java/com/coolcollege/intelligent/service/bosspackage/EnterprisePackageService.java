package com.coolcollege.intelligent.service.bosspackage;

import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.CurrentPackageDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * @author ：xugangkun
 * @date ：2022/3/24 14:11
 */
public interface EnterprisePackageService {

    /**
     * 默认查询方法，通过主键获取所有字段的值
     * @param id
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO
     * @date: 2022/3/25 14:13
     */
    EnterprisePackageDO selectByPrimaryKey(Long id);

    /**
     * 通过名称获取套餐
     * @param packageName
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO>
     * @date: 2022/3/29 15:08
     */
    List<EnterprisePackageDO> selectByPackageName(String packageName);

    /**
     * 添加企业套餐
     * @param enterprisePackageDTO
     * @param user
     * @author: xugangkun
     * @return void
     * @date: 2022/3/24 16:24
     */
    void addEnterprisePackage(EnterprisePackageDTO enterprisePackageDTO, CurrentUser user);

    /**
     * 修改企业套餐
     * @param enterprisePackageDTO
     * @param user
     * @author: xugangkun
     * @return void
     * @date: 2022/3/24 16:24
     */
    void updateEnterprisePackage(EnterprisePackageDTO enterprisePackageDTO, CurrentUser user);

    /**
     * 获得所有企业套餐
     * @param pageNum
     * @param pageSize
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO>
     * @date: 2022/3/24 14:12
     */
    List<EnterprisePackageVO> selectAllEnterprisePackage(Integer pageNum, Integer pageSize);

    /**
     * 获得套餐列表
     * @param status
     * @author: xugangkun
     * @return
     * @date: 2022/3/23 10:16
     */
    List<EnterprisePackageVO> getPackageList(String status);

    /**
     * 获得套餐详情
     * @param packageId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageDetailVO
     * @date: 2022/3/24 17:15
     */
    CurrentPackageDetailVO getEnterprisePackageDetail(Long packageId);

    /**
     * 删除套餐
     * @param packageId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/24 15:50
     */
    void deleteByPrimaryKey(Long packageId);

    /**
     * 根据套餐id和平台类型获得菜单id列表
     * @param packageId
     * @param platformType
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/28 10:45
     */
    List<Long> getMenuIdListByPackageId(Long packageId, String platformType, String appType, Boolean accessCoolCollege);
}
