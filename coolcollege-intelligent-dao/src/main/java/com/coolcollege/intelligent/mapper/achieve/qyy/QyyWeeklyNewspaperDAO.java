package com.coolcollege.intelligent.mapper.achieve.qyy;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.qyy.QyyWeeklyNewspaperDataMapper;
import com.coolcollege.intelligent.dao.qyy.QyyWeeklyNewspaperMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.WeeklyNewspaperDataDTO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyRegionDataDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.qyy.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: QyyWeeklyNewspaperDAO
 * @Description:周报
 * @date 2023-04-12 15:58
 */
@Service
@Slf4j
public class QyyWeeklyNewspaperDAO {

    @Resource
    private QyyWeeklyNewspaperMapper qyyWeeklyNewspaperMapper;

    @Resource
    private AchieveQyyRegionDataDAO achieveQyyRegionDataDAO;

    @Resource
    private QyyWeeklyNewspaperDataMapper qyyWeeklyNewspaperDataMapper;

    @Resource
    private StoreDao storeDao;

    @Resource
    private RegionDao regionDao;

    @Resource
    private QyyWeeklyNewspaperDAO qyyWeeklyNewspaperDAO;

    public Boolean addOrUpdateWeeklyNewspaper(String enterpriseId,
                                              QyyWeeklyNewspaperDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param)){
            return false;
        }
        return qyyWeeklyNewspaperMapper.insertSelective(param, enterpriseId) > Constants.ZERO;
    }

    /**
     * 获取周报分页
     * @param enterpriseId
     * @param userId
     * @return
     */
    public Page<QyyWeeklyNewspaperDO> getWeeklyNewspaperPage(String enterpriseId, List<String> mondayOfWeeks, String userId,String conversationId,List<String> regionId,List<String> storeId,Integer pageNum, Integer pageSize){
        if(StringUtils.isBlank(enterpriseId)){
            return null;
        }
        return qyyWeeklyNewspaperMapper.getWeeklyNewspaperPage(enterpriseId, mondayOfWeeks, userId,conversationId,regionId,storeId,pageNum,pageSize);
    }

    /**
     * 获取周报详情
     *
     * @param enterpriseId
     * @param id
     * @return
     */
    public QyyWeeklyNewspaperDO getWeeklyNewspaperDetail(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        return qyyWeeklyNewspaperMapper.getWeeklyNewspaperDetail(enterpriseId, id);
    }

    public QyyWeeklyNewspaperDO getWeeklyNewspaperDetailByType(String enterpriseId, Long id,String type){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        if ("down".equals(type)){
          return qyyWeeklyNewspaperMapper.getWeeklyNewspaperDetailByUp(enterpriseId,id);
        }else if ("up".equals(type)){
            return qyyWeeklyNewspaperMapper.getWeeklyNewspaperDetailByDown(enterpriseId,id);
        }
        return null;
    }

    public WeeklyNewspaperDetailVO getWeeklyNewspaper(String enterpriseId, String mondayOfWeek, String userId, String storeId) {
        return qyyWeeklyNewspaperMapper.getWeeklyNewspaper(enterpriseId,mondayOfWeek,userId,storeId);
    }

    public boolean deleteWeeklyNewspaper(String enterpriseId, Long id) {
        return qyyWeeklyNewspaperMapper.deleteWeeklyNewspaper(enterpriseId,id);
    }

    public PageInfo<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPage(String enterpriseId, StoreNewsPaperDTO paperDTO) {
        if (StringUtils.isBlank(enterpriseId)){
            log.error("没有企业id，enterpriseId：{}",enterpriseId);
        }
        List<String> storeId = new ArrayList<>();
        if (!StringUtils.isBlank(paperDTO.getStoreName())){
            storeId = storeDao.getStoreIdByStoreName(enterpriseId,paperDTO.getStoreName());
        }
        PageHelper.startPage(paperDTO.getPageNum(),paperDTO.getPageSize());
        List<QyyWeeklyNewspaperDO> weeklyNewspaperDetailVOS = qyyWeeklyNewspaperMapper.storeWeeklyNewsPaperByPage(enterpriseId, paperDTO,storeId,paperDTO.getRegionId());
        List<Long> regionIds = weeklyNewspaperDetailVOS.stream().map(QyyWeeklyNewspaperDO::getRegionId).distinct().collect(Collectors.toList());
        List<String> dates = new ArrayList<>();
        Map<String, BigDecimal> salesRateMap = new HashMap<>();
        if (Objects.nonNull(weeklyNewspaperDetailVOS)){
            dates = weeklyNewspaperDetailVOS.stream().map(QyyWeeklyNewspaperDO::getMondayOfWeek).distinct().collect(Collectors.toList());
            salesRateMap  = achieveQyyRegionDataDAO.getRegionSalesRateMap(enterpriseId, regionIds, TimeCycleEnum.WEEK, dates);
        }
        for (QyyWeeklyNewspaperDO weeklyNewspaperDetailVO : weeklyNewspaperDetailVOS) {
            if (salesRateMap.containsKey(weeklyNewspaperDetailVO.getRegionId())){
                weeklyNewspaperDetailVO.setSalesRate(salesRateMap.get(paperDTO.getRegionId()));
            }
            StoreDTO storeByStoreId = storeDao.getStoreByStoreId(enterpriseId, weeklyNewspaperDetailVO.getStoreId());
            if (Objects.nonNull(storeByStoreId)){
                weeklyNewspaperDetailVO.setStoreName(storeByStoreId.getStoreName());
            }
            AchieveQyyRegionDataDO day = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseId, storeByStoreId.getRegionId(), TimeCycleEnum.DAY, LocalDate.now().toString());
            if (Objects.nonNull(day) && Objects.nonNull(day.getDeptName())){
                weeklyNewspaperDetailVO.setCompName(day.getDeptName());
            }
            String weekLyAchieve = null;
            if (Objects.nonNull(storeByStoreId.getThirdDeptId())){
                weekLyAchieve = qyyWeeklyNewspaperDataMapper.getWeekLyAchieve(enterpriseId, storeByStoreId.getThirdDeptId());
                weeklyNewspaperDetailVO.setWeekAchieve("￥"+weekLyAchieve);
            }
            weeklyNewspaperDetailVO.setDingDeptId(storeByStoreId.getThirdDeptId());
            Integer readNum = qyyWeeklyNewspaperDAO.countReadNum(enterpriseId,weeklyNewspaperDetailVO.getId());
            weeklyNewspaperDetailVO.setReadNum(readNum);
        }
        return new PageInfo<>(weeklyNewspaperDetailVOS);
    }

    public void pushWeeklyNewspaperDate(String enterpriseId, WeeklyNewspaperDataDTO weeklyNewspaperDataDTO, String salesJson) throws ParseException {
        WeeklyNewspaperDataDTO.StoreAchieve storeAchieve = weeklyNewspaperDataDTO.getStoreAchieve();
        String dingDeptId = weeklyNewspaperDataDTO.getDingDeptId();
        LocalDate mondyOfWeek = LocalDate.parse(weeklyNewspaperDataDTO.getMondyOfWeek());
        qyyWeeklyNewspaperDataMapper.pushWeeklyNewspaperDate(enterpriseId,storeAchieve,dingDeptId,mondyOfWeek,salesJson);
    }

    public WeeklyNewspaperDataDO getWeeklyNewspaperDate(String enterpriseId, String mondyOfWeek, String dingDeptId) {
        LocalDate date = LocalDate.parse(mondyOfWeek);
        return qyyWeeklyNewspaperDataMapper.getWeeklyNewspaperDate(enterpriseId,date,dingDeptId);
    }


    public void insertHistory(String enterpriseId, String userId, String name, Long id) {
        qyyWeeklyNewspaperDataMapper.insertHistory(enterpriseId,userId,name,id);
    }

    public Integer countReadNum(String enterpriseId, Long id) {
        return qyyWeeklyNewspaperDataMapper.countReadNum(enterpriseId,id);
    }

    public List<QyyReadPeopleDO> readPeople(String enterpriseId, String id) {
        return qyyWeeklyNewspaperDataMapper.readPeople(enterpriseId,id);
    }

    public Integer queryNum(String enterpriseId) {

        return qyyWeeklyNewspaperMapper.queryNum(enterpriseId);
    }

    public PageInfo<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPageNoParam(String enterpriseId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<QyyWeeklyNewspaperDO> weeklyNewspaperDetailVOS = qyyWeeklyNewspaperMapper.storeWeeklyNewsPaperByPageNoParam(enterpriseId);
        //门店id列表
        List<String> storeIdList = weeklyNewspaperDetailVOS.stream().map(QyyWeeklyNewspaperDO::getStoreId).collect(Collectors.toList());
        List<RegionDO> regionByStoreIds = regionDao.getRegionByStoreIds(enterpriseId, storeIdList);
        Map<String, RegionDO> regionDOMap = regionByStoreIds.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity(), (k1, k2) -> k2));
//        List<String> thirdIdList = regionByStoreIds.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
//        List<WeeklyNewspaperDataDO> weekLyAchieveList = qyyWeeklyNewspaperDataMapper.getWeekLyAchieveByThirdIdList(enterpriseId,thirdIdList);
        List<Long> idList = weeklyNewspaperDetailVOS.stream().map(QyyWeeklyNewspaperDO::getId).collect(Collectors.toList());
//        qyyWeeklyNewspaperDAO.countReadNum()

        for (QyyWeeklyNewspaperDO weeklyNewspaperDetailVO : weeklyNewspaperDetailVOS) {
            Integer readNum = qyyWeeklyNewspaperDAO.countReadNum(enterpriseId,weeklyNewspaperDetailVO.getId());
            RegionDO regionDO = regionDOMap.get(weeklyNewspaperDetailVO.getStoreId());
            if (Objects.nonNull(regionDO) && !StringUtils.isBlank(regionDO.getName())){
                weeklyNewspaperDetailVO.setStoreName(regionDO.getName());
            }
            weeklyNewspaperDetailVO.setReadNum(readNum);
            String weekLyAchieve = qyyWeeklyNewspaperDataMapper.getWeekLyAchieve(enterpriseId, regionDO.getThirdDeptId());
            weeklyNewspaperDetailVO.setWeekAchieve(weekLyAchieve);

            String mondayOfWeek = weeklyNewspaperDetailVO.getMondayOfWeek();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化工具类实例化创建
            Date date = new Date(); //new Date对象
            try {
                date = sdf.parse(mondayOfWeek); //格式化来源初值
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance(); //日历时间工具类实例化创建，取得当前时间初值
            calendar.setTime(date);  //覆盖掉当前时间
            calendar.add(Calendar.DATE, 7); //+7
            String endDate = sdf.format(calendar.getTime()); //转换回字符串
            weeklyNewspaperDetailVO.setMondayOfWeek(mondayOfWeek+"~"+endDate);
        }
        return new PageInfo<>(weeklyNewspaperDetailVOS);
    }

    public Long countTotalPaper(String enterpriseId) {
        return qyyWeeklyNewspaperMapper.countTotalPaper(enterpriseId);
    }

    public List<QyyWeeklyNewspaperDO> getWeeklyNewspaperList(String eId,String createDate) {
        return qyyWeeklyNewspaperMapper.getWeeklyNewspaperList(eId,createDate);
    }
}
