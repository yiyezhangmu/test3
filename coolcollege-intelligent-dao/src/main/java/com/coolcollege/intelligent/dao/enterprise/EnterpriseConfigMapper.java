package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageNumDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface EnterpriseConfigMapper {

    /**
     * 获取钉钉corpId
     * @param dingCorpId
     * @return
     */
    List<EnterpriseConfigDO> selectByCorpId(@Param("dingCorpId") String dingCorpId);

    /**
     * 获取企业配置信息
     * @param enterpriseId
     * @return
     */
    EnterpriseConfigDO selectByEnterpriseId(@Param("enterpriseId") String enterpriseId);

    /**
     * 更新企业信息
     * @param enterpriseConfig
     */
    void updateByEnterpriseId(EnterpriseConfigDO enterpriseConfig);

    /**
     * 更新主应用ID
     * @param eid
     * @param mainCorpId
     */
    void updateMainCorpIdByEnterpriseId(@Param("eid")String eid ,
                                        @Param("mainCorpId")String mainCorpId);


    /**
     * 查询所有企业的配置信息
     * @return
     */
    List<EnterpriseConfigDO> selectEnterpriseConfigAll();

    /**
     * 批量获取
     * @param enterpriseIds
     * @return
     */
    List<EnterpriseConfigDO> selectByEnterpriseIds(@Param("enterpriseIds") List<String> enterpriseIds);


    List<EnterpriseConfigDO> selectByEnterpriseIdsAndStatus(@Param("enterpriseIds") List<String> enterpriseIds, @Param("statusList")List<Integer> statusList);

    /**
     * 获取dbServer
     * @param dbName
     * @return
     */
    String getDbServerByDbName(@Param("dbName") String dbName);

    /**
     * 获取dbServer
     * @return
     */
    List<String> getDistinctDbServer();

    /**
     * 企业套餐使用统计
     * @param
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageNumDTO>
     * @date: 2022/3/24 14:40
     */
    List<EnterprisePackageNumDTO> enterprisePackageStatistics();

    /**
     * 更新企业套餐
     * @param enterpriseId
     * @param packageId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/25 11:36
     */
    void updateCurrentPackageByEnterpriseId(@Param("enterpriseId") String enterpriseId, @Param("packageId") Long packageId);

    /**
     * 根据套餐获取企业
     * @param packageId
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO>
     * @date: 2022/3/28 14:08
     */
    List<EnterpriseConfigDO> selectByCurrentPackage(@Param("packageId") Long packageId);

    /**
     * 更新开通的酷学院的coolCollegeEnterpriseId 和 coolCollegeSecret
     * @param dingCorpId
     * @param appType
     * @param coolCollegeEnterpriseId
     * @param coolCollegeSecret
     * @return
     */
    Integer updateCoolCollegeInfo(@Param("dingCorpId")String dingCorpId, @Param("appType")String appType,
                                  @Param("coolCollegeEnterpriseId") String coolCollegeEnterpriseId, @Param("coolCollegeSecret") String coolCollegeSecret);

    /**
     * 根据corpId和appType获取config信息
     * @param dingCorpId
     * @param appType
     * @return
     */
    EnterpriseConfigDO getEnterpriseConfigByCorpIdAndAppType(@Param("dingCorpId")String dingCorpId, @Param("appType")String appType);

    EnterpriseConfigDO selectByDingCorpId(@Param("dingCorpId") String dingCorpId);


    List<EnterpriseBossDTO> selectStoreCountByDingCorpId(@Param("dingCorpId") String dingCorpId);

    List<EnterpriseConfigDO> selectByAppType(@Param("appType") String appType);
}
