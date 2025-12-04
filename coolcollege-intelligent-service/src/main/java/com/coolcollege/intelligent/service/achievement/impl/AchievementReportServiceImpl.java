package com.coolcollege.intelligent.service.achievement.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.achievement.AchievementDetailMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTargetDetailMapper;
import com.coolcollege.intelligent.dao.achievement.PanasonicMapper;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.entity.PanasonicTempDO;
import com.coolcollege.intelligent.model.achievement.vo.PersonalAchievementVO;
import com.coolcollege.intelligent.model.achievement.vo.RegionReportVO;
import com.coolcollege.intelligent.model.achievement.vo.StoreRealDataVO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementReportQuery;
import com.coolcollege.intelligent.model.unifytask.query.PersonalAchievementQuery;
import com.coolcollege.intelligent.service.achievement.AchievementReportService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2024-03-25 10:26
 */
@Service
public class AchievementReportServiceImpl implements AchievementReportService {

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private RegionService regionService;

    @Resource
    private AchievementDetailMapper achievementDetailMapper;

    @Resource
    private AchievementTargetDetailMapper achievementTargetDetailMapper;

    @Resource
    private PanasonicMapper panasonicMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private StoreMapper storeMapper;

    @Override
    public List<AchieveRegionReportDTO> regionReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, String category, String middleClass, Long regionId) {
        if (regionId == null) {
            regionId = 1L;
        }
        //是否下探
        //是否是查询子节点数据
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        List<Long> nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, regionId);
        if (CollectionUtils.isEmpty(nextRegionIdList)) {
            return new ArrayList<>();
        }
        List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
        List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
        List<AchieveRegionReportDTO> resultList = new ArrayList<>();
        regionPathDTOList.forEach(regionPathDTO -> {
            AchieveRegionReportDTO regionReportDTO = new AchieveRegionReportDTO();
            regionReportDTO.setRegionId(Long.valueOf(regionPathDTO.getRegionId()));
            regionReportDTO.setRegionName(regionPathDTO.getRegionName());
            Long storeNum = panasonicMapper.getStoreNum(enterpriseId, mainClass, regionPathDTO.getRegionId());
            if(storeNum != null){
                regionReportDTO.setStoreNum(storeNum.intValue());
            }else {
                regionReportDTO.setStoreNum(0);
            }
            RegionReportVO regionReportVO = achievementDetailMapper.getRegionProductData(enterpriseId, mainClass, category, middleClass, beginTimeStr, endTimeStr, regionPathDTO.getRegionId(), null);
            if (regionReportVO != null) {
                regionReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                regionReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                regionReportDTO.setGoodsNum(0L);
                regionReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            resultList.add(regionReportDTO);
        });
        return resultList;
    }

    @Override
    public List<AchieveRegionDetailReportDTO> regionDetailReport(String eid, Long beginTime, Long endTime, String reportType, String category, String middleClass, Long regionId) {
        List<Date> dateList;
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        if ("DAY".equals(reportType)) {
            dateList = DateUtils.getBetweenDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        } else {
            dateList = DateUtils.getBetweenMouthDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        }

        List<RegionReportVO> regionReportList = achievementDetailMapper.getRegionProductDataGroupList(eid, reportType, category, middleClass, beginTimeStr, endTimeStr,
                String.valueOf(regionId), null, null);

        regionReportList.forEach(regionReportVO -> {
            if ("DAY".equals(reportType)) {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getDayTime(), DateUtils.DATE_FORMAT_DAY));
            } else {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getMonthTime(), DateUtils.DATE_FORMAT_MONTH));
            }
        });
        Long storeNum = panasonicMapper.getStoreNum(eid, null, String.valueOf(regionId));
        if(storeNum == null){
            storeNum = 0L;
        }
        List<AchieveRegionDetailReportDTO> reportDTOList = new ArrayList<>();
        Map<String, RegionReportVO> regionReportMap = regionReportList.stream().collect(Collectors.toMap(RegionReportVO::getGroupType, Function.identity()));
        Long finalStoreNum = storeNum;
        dateList.forEach(date -> {
            AchieveRegionDetailReportDTO regionDetailReportDTO = new AchieveRegionDetailReportDTO();
            regionDetailReportDTO.setReportDateTime(date);
            regionDetailReportDTO.setStoreNum(finalStoreNum);
            RegionReportVO regionReportVO;
            if ("DAY".equals(reportType)) {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));
                regionDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));

            } else {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
                regionDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
            }
            if (regionReportVO != null) {
                regionDetailReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                regionDetailReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                regionDetailReportDTO.setGoodsNum(0L);
                regionDetailReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            reportDTOList.add(regionDetailReportDTO);
        });
        return reportDTOList;
    }

    @Override
    public List<AchieveStoreReportDTO> storeReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, String category, String middleClass, Long regionId) {
        List<RegionDO> regionDOList = new ArrayList<>();
        if (regionId != null) {
            regionDOList = regionMapper.getStoreByParentIds(enterpriseId, String.valueOf(regionId));
        } else {
            regionDOList = regionMapper.getAllStore(enterpriseId);
        }
        if(regionDOList.size() > Constants.FIFTY_INT){
            regionDOList = regionDOList.subList(0, Constants.FIFTY_INT);
        }

        if (CollectionUtils.isEmpty(regionDOList)) {
            return new ArrayList<>();
        }
        List<Long> regionIdList = regionDOList.stream().map(regionDO -> Long.valueOf(regionDO.getRegionId())).collect(Collectors.toList());
        List<RegionDO> regionNameDOList = regionMapper.listRegionByIds(enterpriseId, regionIdList);
        Map<Long, String> regionNameMap  = regionNameDOList.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));

        //是否下探
        //是否是查询子节点数据
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        String beginDateStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_MONTH) + ":01";
        String endDateStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MONTH)+ ":31";
        List<AchieveStoreReportDTO> resultList = new ArrayList<>();
        regionDOList.forEach(regionDO -> {
            AchieveStoreReportDTO regionReportDTO = new AchieveStoreReportDTO();
            regionReportDTO.setRegionId(Long.valueOf(regionDO.getRegionId()));
            regionReportDTO.setRegionName(regionNameMap.get(Long.valueOf(regionDO.getRegionId())));
            regionReportDTO.setStoreId(regionDO.getStoreId());
            regionReportDTO.setStoreName(regionDO.getName());
            RegionReportVO regionReportVO = achievementDetailMapper.getRegionProductData(enterpriseId, mainClass, category, middleClass, beginTimeStr, endTimeStr, null, regionDO.getStoreId());
            if (regionReportVO != null) {
                regionReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                regionReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                regionReportDTO.setGoodsNum(0L);
                regionReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            BigDecimal storeTarget = achievementTargetDetailMapper.getAllTargetByStoreId(enterpriseId, beginDateStr, endDateStr, regionDO.getStoreId());
            regionReportDTO.setAchievementTargetAmount(storeTarget);
            resultList.add(regionReportDTO);
        });
        return resultList;
    }

    @Override
    public List<AchieveStoreDetailReportDTO>    storeDetailReport(String eid, Long beginTime, Long endTime, String reportType, String category, String middleClass, String storeId) {
        List<Date> dateList;
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        if ("DAY".equals(reportType)) {
            dateList = DateUtils.getBetweenDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        } else {
            dateList = DateUtils.getBetweenMouthDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        }

        List<RegionReportVO> regionReportList = achievementDetailMapper.getRegionProductDataGroupList(eid, reportType, category, middleClass,
                beginTimeStr, endTimeStr, null, storeId, null);

        regionReportList.forEach(regionReportVO -> {
            if ("DAY".equals(reportType)) {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getDayTime(), DateUtils.DATE_FORMAT_DAY));
            } else {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getMonthTime(), DateUtils.DATE_FORMAT_MONTH));
            }
        });
        List<AchieveStoreDetailReportDTO> reportDTOList = new ArrayList<>();
        Map<String, RegionReportVO> regionReportMap = regionReportList.stream().collect(Collectors.toMap(RegionReportVO::getGroupType, Function.identity()));
        dateList.forEach(date -> {
            AchieveStoreDetailReportDTO storeDetailReportDTO = new AchieveStoreDetailReportDTO();
            storeDetailReportDTO.setReportDateTime(date);
            RegionReportVO regionReportVO;
            if ("DAY".equals(reportType)) {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));

            } else {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
            }
            if (regionReportVO != null) {
                storeDetailReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                storeDetailReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                storeDetailReportDTO.setGoodsNum(0L);
                storeDetailReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            reportDTOList.add(storeDetailReportDTO);
        });
        return reportDTOList;
    }

    @Override
    public PageInfo<AchieveGoodTypeReportDTO> goodTypeReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, String category, String middleClass, String storeId,
                                                             String type,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize,true);
        List<AchieveGoodTypeReportDTO> list = Lists.newArrayList();
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        //根据type做key，storeNum做value，统计多少家门店有这个型号
        List<PanasonicTempDO> dos = panasonicMapper.queryList(enterpriseId, PanasonicTempDO.builder().category(category).storeId(storeId)
                .middleClass(middleClass).type(type).mainClass(mainClass).id(1L).build());
        if(CollectionUtils.isEmpty(dos)){
            return new PageInfo<>();
        }
        List<String> goodsTypeList = dos.stream().map(PanasonicTempDO::getType).collect(Collectors.toList());
        List<RegionReportVO> regionReportVOList = achievementDetailMapper.getProductTypeDataGroupList(enterpriseId, beginTimeStr, endTimeStr, storeId, goodsTypeList);
        Map<String, RegionReportVO> regionReportMap = regionReportVOList.stream().collect(Collectors.toMap(RegionReportVO::getGoodsType, Function.identity()));
        dos.forEach(panasonicTempDO -> {
            AchieveGoodTypeReportDTO achieveGoodTypeReportDTO = new AchieveGoodTypeReportDTO();
            achieveGoodTypeReportDTO.setType(panasonicTempDO.getType());
            achieveGoodTypeReportDTO.setCategory(panasonicTempDO.getCategory());
            achieveGoodTypeReportDTO.setMiddleClass(panasonicTempDO.getMiddleClass());
            RegionReportVO regionReportVO = regionReportMap.get(panasonicTempDO.getType());
            if (regionReportVO != null) {
                achieveGoodTypeReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                achieveGoodTypeReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                achieveGoodTypeReportDTO.setGoodsNum(0L);
                achieveGoodTypeReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            list.add(achieveGoodTypeReportDTO);
        });
        PageInfo pageInfo = new PageInfo<>(dos);
        pageInfo.setList(list);
        return pageInfo;
    }

    @Override
    public List<AchieveStoreDetailReportDTO> goodTypeDetailReport(String eid, Long beginTime, Long endTime,String reportType, String type) {
        List<Date> dateList;
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        if ("DAY".equals(reportType)) {
            dateList = DateUtils.getBetweenDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        } else {
            dateList = DateUtils.getBetweenMouthDate(new Date(beginTime), DateUtil.getBeginOfDay(new Date(endTime)));
        }

        List<RegionReportVO> regionReportList = achievementDetailMapper.getRegionProductDataGroupList(eid, reportType, null, null, beginTimeStr, endTimeStr, null, null, type);

        regionReportList.forEach(regionReportVO -> {
            if ("DAY".equals(reportType)) {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getDayTime(), DateUtils.DATE_FORMAT_DAY));
            } else {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getMonthTime(), DateUtils.DATE_FORMAT_MONTH));
            }
        });
        List<AchieveStoreDetailReportDTO> reportDTOList = new ArrayList<>();
        Map<String, RegionReportVO> regionReportMap = regionReportList.stream().collect(Collectors.toMap(RegionReportVO::getGroupType, Function.identity()));
        dateList.forEach(date -> {
            AchieveStoreDetailReportDTO storeDetailReportDTO = new AchieveStoreDetailReportDTO();
            storeDetailReportDTO.setReportDateTime(date);
            RegionReportVO regionReportVO;
            if ("DAY".equals(reportType)) {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));

            } else {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
            }
            if (regionReportVO != null) {
                storeDetailReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                storeDetailReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                storeDetailReportDTO.setGoodsNum(0L);
                storeDetailReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            reportDTOList.add(storeDetailReportDTO);
        });
        return reportDTOList;
    }

    @Override
    public PageInfo<AchieveGoodTypeReportDTO> categoryReport(String enterpriseId, Long beginTime, Long endTime, String mainClass, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize,true);
        List<AchieveGoodTypeReportDTO> list = Lists.newArrayList();
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        //筛选出所有大类下所有型号
        List<PanasonicTempDO> dos = panasonicMapper.queryList(enterpriseId, PanasonicTempDO.builder().mainClass(mainClass).group("category").build());
        if(CollectionUtils.isEmpty(dos)){
            return new PageInfo<>();
        }
        List<String> categoryList = dos.stream().map(PanasonicTempDO::getCategory).collect(Collectors.toList());
        List<RegionReportVO> regionReportVOList = achievementDetailMapper.getInfoGroupByCategory(enterpriseId, beginTimeStr, endTimeStr);
        Map<String, RegionReportVO> regionReportMap = regionReportVOList.stream().collect(Collectors.toMap(RegionReportVO::getCategory, Function.identity()));
        dos.forEach(panasonicTempDO -> {
            AchieveGoodTypeReportDTO achieveGoodTypeReportDTO = new AchieveGoodTypeReportDTO();
            achieveGoodTypeReportDTO.setType(panasonicTempDO.getType());
            achieveGoodTypeReportDTO.setCategory(panasonicTempDO.getCategory());
            achieveGoodTypeReportDTO.setMiddleClass(panasonicTempDO.getMiddleClass());
            RegionReportVO regionReportVO = regionReportMap.get(panasonicTempDO.getCategory());
            if (regionReportVO != null) {
                achieveGoodTypeReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                achieveGoodTypeReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                achieveGoodTypeReportDTO.setGoodsNum(0L);
                achieveGoodTypeReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            list.add(achieveGoodTypeReportDTO);
        });
        //根据销售额降序排列
        list.sort((o1, o2) -> o2.getAchievementAmount().compareTo(o1.getAchievementAmount()));
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<AchieveGoodTypeReportDTO> queryMiddleClassInfoByCategory(String enterpriseId, AchievementReportQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize(),true);
        String beginTime = DateUtils.convertTimeToString(query.getBeginTime(), DateUtils.DATE_FORMAT_SEC);
        String endTime = DateUtils.convertTimeToString(query.getEndTime(), DateUtils.DATE_FORMAT_SEC);
        List<AchieveGoodTypeReportDTO> dos=achievementDetailMapper.queryMiddleClassInfoByCategory(enterpriseId,query,beginTime,endTime);
        return new PageInfo<>(dos);
    }

    @Override
    public List<AchieveStoreDetailReportDTO> categoryReportPic(String enterpriseId, Long beginTime, Long endTime, String category,String reportType, String middleClass) {
        List<Date> dateList;
        String beginTimeStr = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endTimeStr = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        if ("DAY".equals(reportType)) {
            dateList = DateUtils.getBetweenDate(new Date(beginTime), new Date(endTime));
        } else {
            dateList = DateUtils.getBetweenMouthDate(new Date(beginTime), new Date(endTime));
        }

        List<RegionReportVO> regionReportList = achievementDetailMapper.getRegionProductDataGroupList(enterpriseId, reportType, category, null, beginTimeStr, endTimeStr, null, null, null);

        regionReportList.forEach(regionReportVO -> {
            if ("DAY".equals(reportType)) {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getDayTime(), DateUtils.DATE_FORMAT_DAY));
            } else {
                regionReportVO.setGroupType(DateUtil.format(regionReportVO.getMonthTime(), DateUtils.DATE_FORMAT_MONTH));
            }
        });
        List<AchieveStoreDetailReportDTO> reportDTOList = new ArrayList<>();
        Map<String, RegionReportVO> regionReportMap = regionReportList.stream().collect(Collectors.toMap(RegionReportVO::getGroupType, Function.identity()));
        dateList.forEach(date -> {
            AchieveStoreDetailReportDTO storeDetailReportDTO = new AchieveStoreDetailReportDTO();
            RegionReportVO regionReportVO;
            if ("DAY".equals(reportType)) {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_DAY));

            } else {
                regionReportVO = regionReportMap.get(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
                storeDetailReportDTO.setReportDateStr(DateUtil.format(date, DateUtils.DATE_FORMAT_MONTH));
            }
            if (regionReportVO != null) {
                storeDetailReportDTO.setGoodsNum(regionReportVO.getGoodsNum());
                storeDetailReportDTO.setAchievementAmount(regionReportVO.getAchievementAmount());
            } else {
                storeDetailReportDTO.setGoodsNum(0L);
                storeDetailReportDTO.setAchievementAmount(BigDecimal.ZERO);
            }
            reportDTOList.add(storeDetailReportDTO);
        });
        return reportDTOList;
    }

    @Override
    public PageInfo<PersonalAchievementVO> queryPersonalAchievement(String eid, PersonalAchievementQuery query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<PersonalAchievementVO> dos= ListUtils.emptyIfNull(achievementDetailMapper.queryPersonalAchievement(eid,query));
        for (PersonalAchievementVO vo : dos) {
            String userId = vo.getUserId();
            //查询管辖区域
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
            //如果没有管辖区域就不显示区域信息
            if (CollectionUtils.isEmpty(userAuthMappingDOS)){
                vo.setStoreName(null);
                vo.setRegionName(null);
            }
            for (UserAuthMappingDO userAuthMappingDO : userAuthMappingDOS) {
                //查询区域信息
                RegionDO regionDO = regionMapper.getByRegionId(eid, Long.valueOf(userAuthMappingDO.getMappingId()));
                String fullRegionPath = regionDO.getFullRegionPath();
                //根据fullRegionPath查询用户所管辖的最小单位门店列表
                List<String> storeIds = storeMapper.getStoreIdByFullRegionPath(eid, fullRegionPath);
                if (CollectionUtils.isEmpty(storeIds)){
                    vo.setStoreName(null);
                    vo.setRegionName(null);
                }
                StoreDO byStoreId = storeMapper.getByStoreId(eid, storeIds.get(0));
                vo.setStoreName(byStoreId.getStoreName());
                byStoreId.getRegionId();
                RegionDO regionDO1 = regionMapper.getByRegionId(eid, byStoreId.getRegionId());
                vo.setRegionName(regionDO1.getName());
                break;
            }
        }
        return new PageInfo<>(dos) ;
    }

    @Override
    public List<SysRoleBaseVO> getAllPosition(String enterpriseId) {
        List<SysRoleDO> roleByEid = sysRoleMapper.getRoleByEid(enterpriseId);
        List<SysRoleBaseVO> sysRoleBaseVOS = new ArrayList<>();
        roleByEid.forEach(sysRoleDO -> {
            SysRoleBaseVO sysRoleBaseVO = new SysRoleBaseVO();
            sysRoleBaseVO.setId(sysRoleDO.getId());
            sysRoleBaseVO.setRoleName(sysRoleDO.getRoleName());
            sysRoleBaseVOS.add(sysRoleBaseVO);
        });
        return sysRoleBaseVOS;
    }

    @Override
    public StoreRealDataVO getStoreRealData(String eid,String storeId,Date beginDate, Date endDate) {
        //获取本月开始date
        if (beginDate == null) {
            beginDate = DateUtil.getFirstOfDayMonth(new Date());
        }
        if (endDate == null) {
            endDate = new Date();
        }
        StoreRealDataVO vo=achievementDetailMapper.getStoreRealData(eid, storeId,beginDate, endDate);
        String achievementTarget = vo.getAchievementTarget();
        String achievementAmount = vo.getAchievementAmount();
        if ("0.00".equals(achievementTarget) || "0.00".equals(achievementAmount)) {
            vo.setCompletionRate("0%");
        } else {
            BigDecimal target = new BigDecimal(achievementTarget);
            BigDecimal amount = new BigDecimal(achievementAmount);
            BigDecimal divide = amount.divide(target, 2, BigDecimal.ROUND_HALF_UP);
            vo.setCompletionRate(divide.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"%");
        }
        return vo;
    }

    @Override
    public List<AchieveGoodTypeReportDTO> queryPersonalTypeAchievement(String eid, PersonalAchievementQuery query) {
        List<AchieveGoodTypeReportDTO> dos= ListUtils.emptyIfNull(achievementDetailMapper.queryPersonalTypeAchievement(eid,query));
        return dos;
    }
}
