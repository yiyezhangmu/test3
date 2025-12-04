package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnterpriseStoreSettingMapper {
    /**
     * 添加或保存门店基础信息设置
     * @param eId 门店id
     * @param entity 设置详情
     */
    Integer insertOrUpdate(@Param("eId")String eId, @Param("entity") EnterpriseStoreSettingDO entity);

    /**
     * 获取门店基础信息设置
     * @param eId 企业id
     * @return
     */
    EnterpriseStoreSettingDO getEnterpriseStoreSetting(@Param("eId") String eId);

    /**
     * @Author chenyupeng
     * @Description 修改动态扩展字段
     * @Date 2021/6/29
     * @param eId
     * @param extendField
     */
    Integer updateExtendField(@Param("eId")String eId, @Param("extendField") String extendField);

    /**
     * @Author byd
     * @Description 修改时间
     * @Date 2021/11/19
     * @param eId
     */
    Integer updateStoreTimeSetting(@Param("eId")String eId, @Param("storeLicenseEffectiveTime") Integer storeLicenseEffectiveTime
            , @Param("userLicenseEffectiveTime") Integer userLicenseEffectiveTime);

    Integer updateLicensesSetting(@Param("eid")String enterpriseId,
                                  @Param("needUploadLicenseUserStr")String needUploadLicenseUserStr,
                                  @Param("noNeedUploadLicenseRegionStr") String noNeedUploadLicenseRegionStr,
                                  @Param("noNeedUploadLicenseUserStr") String noNeedUploadLicenseUserStr);
}
