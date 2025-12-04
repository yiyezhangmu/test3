package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.vo.PersonalAchievementVO;
import com.coolcollege.intelligent.model.achievement.vo.StoreRealDataVO;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementReportQuery;
import com.coolcollege.intelligent.model.unifytask.query.PersonalAchievementQuery;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * @author byd
 * @date 2024-03-25 10:26
 */
public interface AchievementReportService {

    List<AchieveRegionReportDTO> regionReport(String eid, Long beginTime, Long endTime, String mainClass, String category, String middleClass, Long regionId);

    List<AchieveRegionDetailReportDTO> regionDetailReport(String eid, Long beginTime, Long endTime, String reportType, String category, String middleClass, Long regionId);

    List<AchieveStoreReportDTO> storeReport(String eid, Long beginTime, Long endTime, String mainClass, String category, String middleClass, Long regionId);

    List<AchieveStoreDetailReportDTO> storeDetailReport(String eid, Long beginTime, Long endTime, String reportType, String category, String middleClass, String storeId);


    PageInfo<AchieveGoodTypeReportDTO> goodTypeReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, String category, String middleClass, String storeId,
                                                      String type,Integer pageNum, Integer pageSize);

    List<AchieveStoreDetailReportDTO> goodTypeDetailReport(String eid, Long beginTime, Long endTime, String reportType, String type);

    PageInfo<AchieveGoodTypeReportDTO> categoryReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, Integer pageNumber, Integer pageSize);

    PageInfo<AchieveGoodTypeReportDTO> queryMiddleClassInfoByCategory(String enterpriseId, AchievementReportQuery query);

    List<AchieveStoreDetailReportDTO> categoryReportPic(String enterpriseId, Long beginTime, Long endTime, String category,String reportType, String middleClass);

    PageInfo<PersonalAchievementVO> queryPersonalAchievement(String enterpriseId, PersonalAchievementQuery query);

    List<SysRoleBaseVO> getAllPosition(String enterpriseId);

    StoreRealDataVO getStoreRealData(String enterpriseId, String storeId, Date beginDate, Date endDate);

    List<AchieveGoodTypeReportDTO> queryPersonalTypeAchievement(String enterpriseId, PersonalAchievementQuery query);
}
