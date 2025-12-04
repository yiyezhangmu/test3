package com.coolcollege.intelligent.dao.enterprise.dao;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageNumDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业配置
 * @author ：zhangnan
 * @date ：2022/2/11 14:40
 */
@Repository
public class EnterpriseConfigDao {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    /**
     * 获取企业配置，根据eid和skipEid进行过滤，两个都不传默认返回全部企业
     * @param eidStr 要查询的企业id
     * @param skipEidStr 不需要查询的企业id
     * @return List<EnterpriseConfigDO>
     */
    public List<EnterpriseConfigDO> getSyncEnterpriseConfigDOS(String eidStr, String skipEidStr) {
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        // 查询企业配置列表
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        if(CollectionUtils.isEmpty(enterpriseConfigDOS)) {
            return Lists.newArrayList();
        }
        // 根据查询条件过滤要订正的企业
        List<EnterpriseConfigDO> resultEnterpriseConfigs = enterpriseConfigDOS
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .collect(Collectors.toList());
        return resultEnterpriseConfigs;
    }

    /**
     * 根据企业id查询配置
     * @param enterpriseId 企业id
     * @return EnterpriseConfigDO
     */
    public EnterpriseConfigDO getEnterpriseConfig(String enterpriseId) {
        if(StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        return enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
    }

    /**
     * 企业套餐使用统计
     * @param
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageNumDTO>
     * @date: 2022/3/24 14:40
     */
    public List<EnterprisePackageNumDTO> enterprisePackageStatistics() {
        return enterpriseConfigMapper.enterprisePackageStatistics();
    }

    /**
     * 根据套餐获取企业
     * @param packageId
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO>
     * @date: 2022/3/28 14:08
     */
    public List<EnterpriseConfigDO> selectByCurrentPackage(@Param("packageId") Long packageId) {
        return enterpriseConfigMapper.selectByCurrentPackage(packageId);
    }

    public List<EnterpriseConfigDO> selectByAppType(String appType) {
        return enterpriseConfigMapper.selectByAppType(appType);
    }
}
