package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.enterprise.BannerDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseLoginDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EnterpriseMapper {

    /**
     * 获取企业信息
     * @param id
     * @return
     */
    EnterpriseDO selectById(@Param("id") String id);

    /**
     * 插入
     * @param enterpriseDO
     */
    void insertEnterprise(EnterpriseDO enterpriseDO);

    /**
     * 更新
     * @param enterpriseDO
     */
    void updateEnterpriseById(EnterpriseDO enterpriseDO);

    /**
     * 更新全部
     * @param enterpriseDO
     * @return
     */
    int updateInfo(@Param("ep") EnterpriseDTO enterpriseDO);

    /**\
     * 获取基础信息
     * @param id
     * @return
     */
    Map<String, Object> getBaseInfo(@Param("id") String id);

    /**
     * 删除banner图
     * @param id
     * @return
     */
    int deleteBanner(@Param("id") String id);

    /**
     * 设置banner图
     * @param id
     * @param banner
     * @return
     */
    int setBanner(@Param("id") String id, @Param("banners") List<BannerDO> banner);

    /**
     * 获取banner图列表
     * @param id
     * @return
     */
    List<BannerDO> getBannerList(@Param("id") String id);

    /**
     * 保存或更新
     * @param id
     * @param settingDO
     */
    void saveOrUpdateSettings(@Param("id") String id, @Param("set") EnterpriseSettingDO settingDO);

    /**
     * 获取企业配置
     * @param id
     * @return
     */
    EnterpriseSettingDO getEnterpriseSetting(@Param("id") String id);

    /**
     * 获取企业配置(所有企业)
     * @return
     */
    List<EnterpriseSettingDO> getEnterpriseSettingAll();
    /**
     * 根据电话号码获取企业
     * @param mobile
     * @return
     */
    List<EnterpriseLoginDTO> getEnterpriseByMobile(@Param("mobile")String mobile, @Param("password")String password);



    /**
     * 更新阿里云corpId
     * @param id
     * @param groupCropId
     * @return
     */
    int updateAliyunGroupCropId (@Param("id")String id,@Param("groupCropId")String groupCropId);

    /**
     * 更新企业标签
     * @param id
     * @param tag
     * @author: xugangkun
     * @return void
     * @date: 2022/4/8 10:12
     */
    void updateEnterpriseTag (@Param("id")String id, @Param("tag") String tag);

    void updateEnterpriseCSM (@Param("id")String id, @Param("csm") String csm);


    List<EnterpriseBossDTO> listEnterprise(BossEnterpriseExportRequest param);

    List<EnterpriseDO> listEnterpriseAll();
    Map<String, Object> getSystemSetting(@Param("eid") String eid, @Param("model") String model, @Param("field") String field);


    EnterpriseDTO BusinessManagement(@Param("eId") String eId);

    /**
     * 批量获取企业
     * @param enterpriseIds
     * @return
     */
    List<EnterpriseDO> getEnterpriseByIds(@Param("enterpriseIds") List<String> enterpriseIds);

    void truncateBusinessData(@Param("eid") String eid);

    void updateEnterpriseIsLeaveInfo (@Param("id")String id);

    /**
     * 更新数量
     * @param enterpriseId
     * @param limitStoreCount
     * @return
     */
    Integer updateLimitStoreCount(@Param("enterpriseId")String enterpriseId, @Param("limitStoreCount")Integer limitStoreCount);

    /**
     * 更新设备数量
     * @param enterpriseId
     * @param limitDeviceCount
     * @return
     */
    Integer updateDeviceCount(@Param("enterpriseId")String enterpriseId, @Param("limitDeviceCount")Integer limitDeviceCount);

    /**
     * 更新门店数量
     * @param enterpriseId
     * @param storeCount
     * @return
     */
    Integer updateStoreCount(@Param("enterpriseId") String enterpriseId, @Param("storeCount") Integer storeCount);

    /**
     * 获取查询企业id
     * @param enterpriseId
     * @param enterpriseName
     * @param tag
     * @param csm
     * @return
     */
    List<String> getQueryEnterpriseIds(@Param("enterpriseId") String enterpriseId, @Param("enterpriseName")String enterpriseName, @Param("tag")String tag, @Param("csm")String csm);
}
