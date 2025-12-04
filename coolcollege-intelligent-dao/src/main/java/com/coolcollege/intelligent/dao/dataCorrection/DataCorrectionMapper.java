package com.coolcollege.intelligent.dao.dataCorrection;

import com.coolcollege.intelligent.model.dataCorrection.RoleDuplicateDTO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleJobRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName DataCorrectionMapper
 * @Description 数据订正专属mapper
 * @author zyp
 */
@Mapper
public interface DataCorrectionMapper {

   List<String> countRoleDuble(@Param("eid") String eid);

   List<RoleDuplicateDTO> selectRoleFixDuplicate(@Param("eid") String eid,
                                                 @Param("userIdList")List<String> userIdList);
   Integer batchDeleteUserRole(@Param("eid") String eid,
                          @Param("idList")List<Long> idList);
   Integer insertRoleFixAdmin(@Param("eid") String eid,
                              @Param("userId") String userId);


    Integer updateStoreStorePath(@Param("eid") String eid,
                                 @Param("storeId") String storeId);

    /**
     * 更新门店的区域路径为全路径
     * @param eid
     * @param storeId
     * @author: xugangkun
     * @return void
     * @date: 2022/1/13 14:19
     */
    Integer updateStoreStorePath2(@Param("eid") String eid,
                                 @Param("storeId") String storeId);


    Integer updateRootStorePath(@Param("eid") String eid,
                                 @Param("storeId") String storeId);
    /**
     * 巡店记录主表
     * @param eid
     * @param storeId
     * @return
     */
   Integer updateTbPatrolStoreRecordStorePath(@Param("eid") String eid,
                                            @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 巡店记录检查表信息
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDataTableStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 标准检查项
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDataStaTableColumnStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 自定检查表项
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDataDefTableColumnStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 陈列记录
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDisplayTableRecordStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 陈列记录检查项数据
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDisplayTableDataColumnStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 陈列记录检查内容
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateTbDisplayTableDataContentStorePath(@Param("eid") String eid,
                                                    @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 门店任务表
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateUnifyTaskStoreStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 子任务表
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateUnifyTaskSubStorePath(@Param("eid") String eid,
                                             @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 告警列表
     * @param eid
     * @param storeId
     * @return
     */
    Integer updateVideoEventRecordStorePath(@Param("eid") String eid,
                                        @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

 /**
  * 设备
  * @param eid
  * @param storeId
  * @return
  */
 Integer updateDeviceStorePath(@Param("eid") String eid,
                                         @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    /**
     * 设备和门店的关系绑定
     * @Author chenyupeng
     * @Date 2021/7/1
     * @param eid
     * @return: java.lang.Integer
     */
    Integer updateDeviceBindStoreId(@Param("eid") String eid);
    List<String> selectDeviceBindByAliyun(@Param("eid") String eid);

    /**
     *
     * @param rootCorpId
     * @return
     */
    List<String> selectEnterpriseByRootVdsCorpId(@Param("rootCorpId")String rootCorpId);
    void updateRootCorpIdToNullByRootCorpId(@Param("rootCorpId")String rootCorpId);

    void batchUpdateStoreAdressAndLngLat(@Param("eid") String eid,
                                         @Param("stores")List<StoreDO> storeDOList);

    Integer updateAchievementDetailRegionPath(@Param("eid") String eid,
                                          @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    Integer updateAchievementTargetRegionPath(@Param("eid") String eid,
                                              @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    Integer updateAchievementTargetDetailRegionPath(@Param("eid") String eid,
                                              @Param("storeId") String storeId, @Param("isRunIncrement") Boolean isRunIncrement);

    void fixDevice(@Param("eid") String eid,@Param("deviceDO") DeviceDO deviceDO);

    /**
     * 根据企业id查询企业库用户
     * @param enterpriseId
     * @return
     */
    List<EnterpriseUserDO> selectEnterpriseUser(@Param("enterpriseId") String enterpriseId);

    /**
     * 更新门店区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbPatrolStoreRecordRegionPath(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId, @Param("regionId") Long regionId, @Param("regionPath") String regionPath);


    /**
     * 更新巡店记录检查表信息区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDataTableRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新标准检查项区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDataStaTableColumnRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新自定检查项区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDataDefTableColumnRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新陈列记录区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDisplayTableRecordRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新陈列记录检查项数据区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDisplayTableDataColumnRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新陈列记录检查内容区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateTbDisplayTableDataContentRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新门店任务表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateUnifyTaskStoreRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新子任务表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateUnifyTaskSubRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新告警列表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateVideoEventRecordRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新设备区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionPath
     * @return
     */
    Integer updateDeviceRegionPath(String enterpriseId, String storeId, String regionPath);

    /**
     * 更新业绩明细表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateAchDetailRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新业绩目标表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateAchTargetRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);

    /**
     * 更新业绩目标详情表区域路径
     * @param enterpriseId
     * @param storeId
     * @param regionId
     * @param regionPath
     * @return
     */
    Integer updateAchTargetDetailRegionPath(String enterpriseId, String storeId, Long regionId, String regionPath);
    /**
     * 更新平台库用户信息
     * @param list
     */
    Integer updatePlatformUsers(@Param("list") List<EnterpriseUserDO> list);

    List<ScheduleJobRequest> getJob(@Param("eid") String eid);

    List<String> getJobDistinctEid(@Param("eids") List<String> eids);

}
