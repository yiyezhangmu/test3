package com.coolcollege.intelligent.dao.songxia;

import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSalesInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.model.achievement.entity.ManageStoreCategoryCodeDO;
import com.coolcollege.intelligent.model.activity.entity.PromoterStoreInfoDO;
import com.coolcollege.intelligent.model.songXia.ActualStoreDO;
import com.coolcollege.intelligent.model.songXia.PromoterInfoDO;
import com.coolcollege.intelligent.model.songXia.TransactionObjectDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

@Mapper
public interface SongXiaMapper {
    List<SongXiaSalesInfoVO> getSalesInfo(@Param("startReportDate") Date reportDate, @Param("endReportDate") Date endReportDate);

    List<SongXiaSampleInfoVO> getSampleInfo();

    List<ActualStoreDO> getActualStoreByStoreIds(@Param("storeIds") List<String> storeIds);

    List<String> getPromoterStoreIdsByUserId(@Param("userId") String userId);

    List<TransactionObjectDO> getTransactionIdsByStoreIds(@Param("transactionCodes")List<String> transactionCodes);

    PromoterInfoDO getPanasonicPromoterInfoByUserId(@Param("eid")String eid,@Param("userId") String userId);

    Integer batchInsertPromoterStoreInfo(@Param("insertDOList") List<PromoterStoreInfoDO> insertDOList);

    List<ManageStoreCategoryCodeDO> getCategoryMappingByStoreIds(@Param("storeIds")List<String> managerStoreIds);

    List<SongXiaSampleInfoVO> getStockInfo(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
