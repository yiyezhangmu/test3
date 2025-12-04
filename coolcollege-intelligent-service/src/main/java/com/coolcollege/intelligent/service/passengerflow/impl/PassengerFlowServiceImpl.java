package com.coolcollege.intelligent.service.passengerflow.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceModelEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.device.SceneTypeEnum;
import com.coolcollege.intelligent.common.enums.passenger.FlowTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.passengerflow.PassengerFlowRecodeMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.passengerflow.PassengerFlowRecordDO;
import com.coolcollege.intelligent.model.passengerflow.request.*;
import com.coolcollege.intelligent.model.passengerflow.vo.*;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.HKPassengerFlowAttributesDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowDailyDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowHourlyDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiDeviceKitPeoplecountingDTO;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.COM_CANNOT_START_GREATER_THAN_END;
import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.COM_NOT_MORE_THAN_7_DAYS;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/11
 */
@Service
@Slf4j
public class PassengerFlowServiceImpl implements PassengerFlowService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Resource
    private PassengerFlowRecodeMapper passengerFlowRecodeMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Autowired
    private YingshiDeviceService yingshiDeviceService;
    @Resource
    private VideoServiceApi videoServiceApi;

    private final static long SIX = 6;
    private final static long ZERO = 0;

    private final static Double DOUBLE_ZERO=0D;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Boolean callback(String eid,LocalDateTime nowLocalDateTime) {
        /**
         * 1.判断是否开启了萤石流
         * 2.查询所有支持客流分析的设备。并且绑定了门店
         * 3.过滤非萤石设备
         * 4.过滤不支持客流分析的设备
         * 5.过滤没有开启客流分析的设备
         * 6.查询客流分析的数据
         */
        log.info("passengerFlowService.callback start eid={}",eid);
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.YINGSHIYUN, AccountTypeEnum.PLATFORM);
        if (setting == null) {
            return true;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localTime = df.format(nowLocalDateTime);
        String passengerCallbackKey="passenger_callback_"+localTime+"_"+eid;
        String value = redisUtilPool.getString(passengerCallbackKey);
        if(StringUtils.isNotBlank(value)){
            return true;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        Integer pageSize=100;
        Integer pageNo=1;
        PageHelper.startPage(pageNo,pageSize);
        List<DeviceDO> pageDeviceDOList = deviceMapper.selectBindDevice(eid, DeviceTypeEnum.DEVICE_VIDEO.getCode(), YunTypeEnum.YINGSHIYUN.getCode());
        callbackByTime(eid, nowLocalDateTime, pageDeviceDOList);

        while (CollectionUtils.isNotEmpty(pageDeviceDOList)){
            pageNo++;
            PageHelper.startPage(pageNo,pageSize);
            pageDeviceDOList = deviceMapper.selectBindDevice(eid, DeviceTypeEnum.DEVICE_VIDEO.getCode(),YunTypeEnum.YINGSHIYUN.getCode());
            callbackByTime(eid, nowLocalDateTime, pageDeviceDOList);

        }
        redisUtilPool.setString(passengerCallbackKey,eid,2*24*60*60);
        return true;
    }

    @Override
    public Boolean callbackByTime(String eid, LocalDateTime localDateTime, List<DeviceDO> deviceDOList) {

        List<DeviceDO> deviceList = ListUtils.emptyIfNull(deviceDOList)
                .stream()
                .filter(data -> data.getResource().equals(YunTypeEnum.YINGSHIYUN.getCode()))
                .filter(DeviceDO::getSupportPassenger)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(deviceList)){
            return false;
        }
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(eid);
        Map<Long, String> sceneTypeMap = ListUtils.emptyIfNull(storeSceneList)
                .stream()
                .collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getSceneType, (a, b) -> a));
        //昨天的00：00：00的时间戳
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        LocalDateTime specificDate = LocalDateTime.of(year, month, dayOfMonth, 0, 0);
        LocalDateTime yesterdayLocalDateTime = specificDate.minusDays(1);
        long yesterdayTime = yesterdayLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        int year1 = yesterdayLocalDateTime.getYear();
        int month1 = yesterdayLocalDateTime.getMonthValue();
        int day1 = yesterdayLocalDateTime.getDayOfMonth();
        String yesterday = year1 + "-" + month1 + "-" + day1;
        Date productDayTime = DateUtil.parse(yesterday, "yyyy-MM-dd");

        List<PassengerFlowRecordDO> passengerFlowRecordDOList = new ArrayList<>();
        String yingshiToken = yingshiDeviceService.getRedisToken(eid);
        //统计数据同步
        ListUtils.emptyIfNull(deviceList)
                .forEach(data -> {
                    syncPassengerRecord(eid, yesterdayTime, month1, productDayTime, year1,
                            passengerFlowRecordDOList, yingshiToken, data,sceneTypeMap);
                });
        //插入数据
        ListUtils.partition(passengerFlowRecordDOList, Constants.BATCH_INSERT_COUNT).forEach(data -> {
            passengerFlowRecodeMapper.batchInsertPassengerFlowRecordDO(eid, data);
        });
        return true;
    }

    @Override
    public Boolean updatePassenger(String eid, Long id, Integer flowIn, Integer flowOut, Integer flowInOut) {

        passengerFlowRecodeMapper.updateFlowInOutById(eid, id, flowIn, flowOut, flowInOut);
        return true;
    }

    @Override
    public List<PassengerDeviceHourVO> deviceHourDay(String eid, PassengerDeviceHourDayRequest request) {

        //判断是不是今日数据，今日数据则访问萤石拿实时数据。
        //时间校验
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        //获取原始数据
        List<PassengerFlowRecordDO> passengerFlowRecordDOList = passengerFlowRecodeMapper.hourDay(eid, null,
                request.getDeviceId(),null,null, start, end);
        //组装成表格展示。
        return translatePassengerDeviceHourVO(passengerFlowRecordDOList);
    }

    private List<PassengerDeviceHourVO> translatePassengerDeviceHourVO(List<PassengerFlowRecordDO> passengerFlowRecordDOList) {
        Map<Integer, List<PassengerFlowRecordDO>> hourMap = ListUtils.emptyIfNull(passengerFlowRecordDOList)
                .stream()
                .collect(Collectors.groupingBy(PassengerFlowRecordDO::getFlowHour));
        List<PassengerDeviceHourVO> passengerDeviceHourVOList = new ArrayList<>();
        MapUtils.emptyIfNull(hourMap).forEach((hour, list) -> {
            PassengerDeviceHourVO vo = new PassengerDeviceHourVO();
            vo.setHour(hour);
            List<PassengerDeviceHourDayVO> passengerDeviceHourDayVOList = ListUtils.emptyIfNull(list)
                    .stream()
                    .map(this::mapPassengerDeviceHourDayVO)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(passengerDeviceHourDayVOList)) {
                vo.setPassengerDeviceHourDayVOList(passengerDeviceHourDayVOList);
            }
            passengerDeviceHourVOList.add(vo);
        });
        //时间升序
       return ListUtils.emptyIfNull(passengerDeviceHourVOList)
                .stream()
                .sorted(Comparator.comparing(PassengerDeviceHourVO::getHour))
                .collect(Collectors.toList());
    }
    private List<PassengerStoreDayVO> translatePassengerStoreDayVO(List<PassengerFlowRecordDO> passengerFlowRecordDOList,List<PassengerStoreDayVO> voList, List<StoreDO> storeDOList) {

        Map<String, List<PassengerFlowRecordDO>> storeMap = ListUtils.emptyIfNull(passengerFlowRecordDOList)
                .stream()
                .collect(Collectors.groupingBy(PassengerFlowRecordDO::getStoreId));
        Map<String,List<PassengerDeviceHourDayVO>> map =new HashMap<>();
        MapUtils.emptyIfNull(storeMap).forEach((storeId, list) -> {
            List<PassengerDeviceHourDayVO> passengerDeviceHourDayVOList = ListUtils.emptyIfNull(list)
                    .stream()
                    .map(this::mapPassengerDeviceHourDayVO)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(passengerDeviceHourDayVOList)) {
                map.put(storeId,passengerDeviceHourDayVOList);
            }
        });
        Map<String, StoreDO> storeNameMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        ListUtils.emptyIfNull(voList)
                .forEach(data->{
                    StoreDO storeDO = storeNameMap.get(data.getStoreId());
                    if(storeDO!=null){
                        data.setStoreName(storeDO.getStoreName());
                    }
                    data.setPassengerDeviceHourDayVOList(map.get(data.getStoreId()));
                });
        return voList;

    }

        @Override
    public List<PassengerDeviceHourVO> sceneHourDay(String eid, PassengerSceneHourDayRequest request) {

        //时间校验
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        //获取原始数据
        List<PassengerFlowRecordDO> passengerFlowRecordDOList = passengerFlowRecodeMapper.hourDay(eid, request.getSceneId(),null,storeList,regionPath, start, end);
        //组装成表格展示.
        List<PassengerDeviceHourVO> passengerDeviceHourVOList = translatePassengerDeviceHourVO(passengerFlowRecordDOList);
        //时间升序
        return ListUtils.emptyIfNull(passengerDeviceHourVOList)
                    .stream()
                    .sorted(Comparator.comparing(PassengerDeviceHourVO::getHour))
                    .collect(Collectors.toList());
        }

    private String getFullRegionPath(String eid, Long regionId) {
        String regionPath = null;
        if (regionId != null) {
            RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
            if (regionDO != null) {
                regionPath = regionDO.getFullRegionPath();
            }
        }
        return regionPath;
    }

    @Override
    public List<PassengerStoreDayVO> storeDay(String eid, PassengerStoreDayRequest request) {

        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");

        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<PassengerStoreDayVO> passengerStoreDayVOList = passengerFlowRecodeMapper.pageStore(eid, storeList, regionPath, start, end);
        if(CollectionUtils.isEmpty(passengerStoreDayVOList)){
            return passengerStoreDayVOList;
        }
        List<String> storeIdList =ListUtils.emptyIfNull(passengerStoreDayVOList)
                .stream()
                .map(PassengerStoreDayVO::getStoreId)
                .collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(eid, storeIdList);
        List<String> validStoreIdList = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .map(StoreDO::getStoreId)
                .collect(Collectors.toList());
        List<PassengerFlowRecordDO> passengerFlowRecordDOList = passengerFlowRecodeMapper.storeDay(eid, validStoreIdList, start, end);
        return  translatePassengerStoreDayVO(passengerFlowRecordDOList,passengerStoreDayVOList,storeDOList);
    }

    @Override
    public List<PassengerStoreRankVO> storeRank(String eid, PassengerStoreRankRequest request) {

        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        StoreSceneDo sceneDO = storeSceneMapper.getStoreSceneById(eid, request.getSceneId());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");

        List<PassengerStoreRankVO> passengerStoreRankVOList;
        SceneTypeEnum sceneTypeEnum = SceneTypeEnum.getByCode(sceneDO.getSceneType());
        switch(sceneTypeEnum){
            case STORE_IN:
                PageHelper.startPage(request.getPageNo(),request.getPageSize());
                passengerStoreRankVOList= passengerFlowRecodeMapper.storeRankByIn(eid, request.getSceneId(),
                        storeList, regionPath, start, end);
                break;
            case STORE_IN_OUT:
                PageHelper.startPage(request.getPageNo(),request.getPageSize());
                passengerStoreRankVOList= passengerFlowRecodeMapper.storeRankByInOut(eid, request.getSceneId(),
                        storeList, regionPath, start, end);
                break;

            default:return Collections.emptyList();
        }

        if(CollectionUtils.isEmpty(passengerStoreRankVOList)){
            return passengerStoreRankVOList;
        }
        List<String> storeIdList =ListUtils.emptyIfNull(passengerStoreRankVOList)
                .stream()
                .map(PassengerStoreRankVO::getStoreId)
                .collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(eid, storeIdList);
        Map<String, String> storeMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        ListUtils.emptyIfNull(passengerStoreRankVOList)
                .forEach(data->{
                    String storeName = storeMap.get(data.getStoreId());
                    data.setStoreName(storeName);
                });
        return passengerStoreRankVOList;

    }

    @Override
    public PassengerDeviceHourDayVO getPassengerFlowOverview(String eid, PassengerNewBoardRequest request) {
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        PassengerDeviceHourDayVO overview = passengerFlowRecodeMapper.getPassengerFlowOverview(eid,
                storeList, regionPath, start, end);
        return overview;
    }

    @Override
    public List<PassengerTrendVO> trend(String eid, PassengerNewBoardRequest request) {
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        List<PassengerTrendVO> passengerTrendVOList = passengerFlowRecodeMapper.trend(eid,
                storeList, regionPath, start, end);
        return passengerTrendVOList;
    }

    @Override
    public List<PassengerStoreRankVO> storeRankNew(String eid, PassengerNewBoardRequest request) {
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        List<PassengerStoreRankVO> passengerStoreRankVOList = passengerFlowRecodeMapper.storeRankNew(eid,
                storeList, regionPath, start, end, request.getSortField(), request.getSortType());

        if(CollectionUtils.isEmpty(passengerStoreRankVOList)){
            return passengerStoreRankVOList;
        }
        List<String> storeIdList =ListUtils.emptyIfNull(passengerStoreRankVOList)
                .stream()
                .map(PassengerStoreRankVO::getStoreId)
                .collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(eid, storeIdList);
        Map<String, String> storeMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        final int[] i = {1};
        ListUtils.emptyIfNull(passengerStoreRankVOList)
                .forEach(data->{
                    String storeName = storeMap.get(data.getStoreId());
                    data.setStoreName(storeName);
                    data.setRank(i[0]++);
                });
        return passengerStoreRankVOList;
    }


    @Override
    public PageInfo<PassengerStoreRankVO> passengerFlowList(String eid, PassengerStoreDayRequest request) {
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        Page<PassengerStoreRankVO>  passengerFlowPage = passengerFlowRecodeMapper.passengerFlowList(eid, storeList, regionPath, start, end);

        if(CollectionUtils.isEmpty(passengerFlowPage)){
            return new PageInfo<>();
        }
        List<String> storeIdList =ListUtils.emptyIfNull(passengerFlowPage)
                .stream()
                .map(PassengerStoreRankVO::getStoreId)
                .collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(eid, storeIdList);
        Map<String, String> storeMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        ListUtils.emptyIfNull(passengerFlowPage)
                .forEach(data->{
                    String storeName = storeMap.get(data.getStoreId());
                    data.setStoreName(storeName);
                });
        PageInfo resultPage = new PageInfo(passengerFlowPage);
        return resultPage;
    }

    @Override
    public PassengerGroupVO passengerGroupDistribution(String eid, PassengerNewBoardRequest request) {
        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");
        PassengerGroupVO passengerGroupVO = passengerFlowRecodeMapper.passengerGroupDistribution(eid,
                storeList, regionPath, start, end);
        return passengerGroupVO;
    }

    @Override
    public List<PassengerAchievementVO> passengerAchievement(String eid, PassengerAchievementRequest request) {

        checkPassengerDeviceHourDayRequest(request.getStartTime(),request.getEndTime());
        String regionPath = getFullRegionPath(eid, request.getRegionId());
        Date start = new Date(request.getStartTime());
        Date end = new Date(request.getEndTime());
        List<String> storeList= StrUtil.splitTrim(request.getStoreIdStr(),",");

        List<PassengerAchievementVO> passengerAchievementVOList = passengerFlowRecodeMapper.flowInPercent(eid, storeList, regionPath, start, end);
        //填充进店率
        ListUtils.emptyIfNull(passengerAchievementVOList)
                .forEach(data->{
                    if (Constants.SYNC_PLATFORM_PASSENGER_EIDS.contains(eid)) {
                        data.setFlowInOutCount(data.getFlowPassCount());
                    }
                    if(data.getFlowInCount()!=null&&data.getFlowInOutCount()!=null&&data.getFlowInCount()!=0){
                        Double flowPercent = data.getFlowInCount().doubleValue() / data.getFlowInOutCount().doubleValue();
                        data.setFlowInPercent(flowPercent);
                    }else {
                        data.setFlowInPercent(DOUBLE_ZERO);
                    }
                });
        return ListUtils.emptyIfNull(passengerAchievementVOList)
                .stream()
                .sorted(Comparator.comparing(PassengerAchievementVO::getFlowInPercent).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Async("syncHikPassengerFlowPool")
    public Boolean syncHikPassengerFlow(String eid,String dataTime, AccountTypeEnum accountType) {
        if(accountType == null){
            accountType = AccountTypeEnum.PRIVATE;
        }
        //查询门店 绑定了摄像头且门店编号不为null的设备
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<StoreDO> storeList= storeMapper.getStoreListStoreNumNotNull(eid);
        if (CollectionUtils.isEmpty(storeList)){
            return Boolean.TRUE;
        }
        for (StoreDO storeDO:storeList) {
            //每个门店当天的客流
            List<PassengerDTO> passengerData = videoServiceApi.getPassengerData(eid, YunTypeEnum.HIKCLOUD, accountType, dataTime, storeDO.getStoreNum());
            if (CollectionUtils.isEmpty(passengerData)){
                continue;
            }
            List<HKPassengerFlowAttributesDTO> attributesData = videoServiceApi.getPassengerAttributesData(eid, YunTypeEnum.HIKCLOUD, accountType, dataTime, dataTime, storeDO.getStoreNum());
            Map<String, HKPassengerFlowAttributesDTO> attributesMap = ListUtils.emptyIfNull(attributesData).stream().collect(Collectors.toMap(HKPassengerFlowAttributesDTO::getDateTime, data -> data, (a, b) -> a));
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            List<PassengerFlowRecordDO> passengerFlowRecordDOS = convertDevice(passengerData, storeDO, attributesMap);
            passengerFlowRecodeMapper.batchInsertPassengerFlowRecordDO(eid,passengerFlowRecordDOS);
        }
        return Boolean.TRUE;
    }


    private List<PassengerFlowRecordDO> convertDevice(List<PassengerDTO> passengerData,StoreDO storeDO , Map<String, HKPassengerFlowAttributesDTO> attributesMap){
        List<PassengerFlowRecordDO> result = new ArrayList<>();
        PassengerFlowRecordDO passengerFlowRecordDO = null;
        //一天数据
        Integer flowIn = 0;
        Integer flowOut = 0;
        Integer flowPass = 0;
        String dateTime = "";
        for (PassengerDTO passengerDTO:passengerData) {
            passengerFlowRecordDO = new PassengerFlowRecordDO();
            dateTime = passengerDTO.getDateTime();
            flowIn+=passengerDTO.getPassengerInCount();
            flowOut+=passengerDTO.getPassengerOutCount();
            flowPass+=passengerDTO.getPassengerPassCount();
            convertPassengerFlowRecordDO(dateTime,storeDO,passengerFlowRecordDO,FlowTypeEnum.HOUR.getCode());
            passengerFlowRecordDO.setFlowIn(passengerDTO.getPassengerInCount());
            passengerFlowRecordDO.setFlowOut(passengerDTO.getPassengerOutCount());
            passengerFlowRecordDO.setFlowInOut(passengerDTO.getPassengerInCount()+passengerDTO.getPassengerOutCount());
            passengerFlowRecordDO.setFlowPass(passengerDTO.getPassengerPassCount());
            result.add(passengerFlowRecordDO);
        }
        passengerFlowRecordDO = new PassengerFlowRecordDO();
        PassengerFlowRecordDO passenger = convertPassengerFlowRecordDO(dateTime, storeDO, passengerFlowRecordDO,FlowTypeEnum.DAY.getCode());
        passenger.setFlowHour(0);
        passenger.setFlowIn(flowIn);
        passenger.setFlowOut(flowOut);
        passenger.setFlowInOut(flowIn+flowOut);
        passenger.setFlowPass(flowPass);
        LocalDateTime localDateTime = DateUtils.convertStringToDate(dateTime);
        String date = DateUtil.format(localDateTime, "yyyy-MM-dd");
        HKPassengerFlowAttributesDTO attributesDTO = attributesMap.get(date);
        if(attributesDTO != null){
            YingshiDeviceKitPeoplecountingDTO peoplecountingDTO = YingshiDeviceKitPeoplecountingDTO.convertDTO(attributesDTO);
            if(peoplecountingDTO != null){
                passenger.setAttributeCount(JSONObject.toJSONString(peoplecountingDTO));
            }
        }
        result.add(passenger);
        return result;
    }

    private PassengerFlowRecordDO convertPassengerFlowRecordDO(String dateTime,StoreDO storeDO,PassengerFlowRecordDO passengerFlowRecordDO,String flowType){
        LocalDateTime localDateTime = DateUtils.convertStringToDate(dateTime);
        passengerFlowRecordDO.setStoreId(storeDO.getStoreId());
        passengerFlowRecordDO.setRegionPath(storeDO.getRegionPath());
        passengerFlowRecordDO.setDeviceId("default");
        passengerFlowRecordDO.setHasChildDevice(Boolean.FALSE);
        //默认场景是店内客流
        passengerFlowRecordDO.setSceneId(3L);
        passengerFlowRecordDO.setSceneType("store_in");
        passengerFlowRecordDO.setFlowType(flowType);
        passengerFlowRecordDO.setFlowYear(localDateTime.getYear());
        passengerFlowRecordDO.setFlowMonth(localDateTime.getMonthValue());
        passengerFlowRecordDO.setFlowDay(DateUtil.parse(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),DateUtils.DATE_FORMAT_DAY));
        passengerFlowRecordDO.setFlowHour( localDateTime.getHour());
        passengerFlowRecordDO.setCreateTime(new Date());
        return passengerFlowRecordDO;
    }





    private PassengerDeviceHourDayVO mapPassengerDeviceHourDayVO(PassengerFlowRecordDO recordDO) {
        PassengerDeviceHourDayVO vo = new PassengerDeviceHourDayVO();
        vo.setFlowDay(recordDO.getFlowDay());
        vo.setFlowIn(recordDO.getFlowIn());
        vo.setFlowOut(recordDO.getFlowOut());
        vo.setFlowInOut(recordDO.getFlowInOut());
        return vo;
    }

    private void checkPassengerDeviceHourDayRequest(Long startTime,Long endTime ) {
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault());
        long l = Duration.between(startLocalDateTime, endLocalDateTime).toDays();
        if (l > SIX) {
            throw new ServiceException(COM_NOT_MORE_THAN_7_DAYS);
        }
        if (l < ZERO) {
            throw new ServiceException(COM_CANNOT_START_GREATER_THAN_END);
        }
    }

    private void syncPassengerRecord(String eid, long yesterdayTime, int month1, Date productDayTime, Integer productYearTime,
                                     List<PassengerFlowRecordDO> passengerFlowRecordDOList, String yingshiToken,
                                     DeviceDO data,Map<Long, String> sceneTypeMap) {
        //ipc客流数据同步
        if (data.getHasChildDevice() == null || !data.getHasChildDevice()) {
            syncPassengerDeviceRecord(yesterdayTime, month1, productDayTime, productYearTime, passengerFlowRecordDOList, yingshiToken, data, null,sceneTypeMap);
        }
        OpenDeviceDTO openDeviceDTO = videoServiceApi.getDeviceDetail(eid, data.getDeviceId());
        if (StringUtils.isNotBlank(openDeviceDTO.getModel()) && openDeviceDTO.getModel().startsWith(DeviceModelEnum.ISAPI.getCode())) {
            StoreDO storeDO = storeMapper.getByStoreId(eid, data.getBindStoreId());
            YunTypeEnum yunTypeEnum = YunTypeEnum.getByCode(data.getResource());
            String startTime = DateUtil.format(productDayTime) + " 00:00:00";
            String endTime = DateUtil.format(DateUtil.plusDays(productDayTime,1)) + " 00:00:00";
            YingshiDeviceKitPeoplecountingDTO peoplecountingDTO = videoServiceApi.statisticPeoplecounting(eid, data, storeDO.getStoreNum(), startTime, endTime, yunTypeEnum);
            if (peoplecountingDTO != null) {
                PassengerFlowRecordDO passengerFlowRecordDO = new PassengerFlowRecordDO();
                passengerFlowRecordDO.setStoreId(data.getBindStoreId());
                passengerFlowRecordDO.setRegionPath(data.getRegionPath());
                passengerFlowRecordDO.setDeviceId(data.getDeviceId());
                passengerFlowRecordDO.setHasChildDevice(data.getHasChildDevice()==null?false:data.getHasChildDevice());
                passengerFlowRecordDO.setSceneId(data.getStoreSceneId());
                passengerFlowRecordDO.setSceneType(sceneTypeMap.get(data.getStoreSceneId()));
                passengerFlowRecordDO.setFlowType(FlowTypeEnum.DAY.getCode());
                passengerFlowRecordDO.setFlowIn(peoplecountingDTO.getEnter());
                passengerFlowRecordDO.setFlowOut(peoplecountingDTO.getExit());
                passengerFlowRecordDO.setFlowInOut(peoplecountingDTO.getEnter() + peoplecountingDTO.getExit());
                passengerFlowRecordDO.setFlowPass(peoplecountingDTO.getPass());
                passengerFlowRecordDO.setFlowYear(productYearTime);
                passengerFlowRecordDO.setFlowDay(productDayTime);
                passengerFlowRecordDO.setFlowHour(0);
                passengerFlowRecordDO.setFlowMonth(month1);
                passengerFlowRecordDO.setAttributeCount(JSONObject.toJSONString(peoplecountingDTO));
                passengerFlowRecordDOList.add(passengerFlowRecordDO);
            }
        }


//        //nvr数据同步
//        if (data.getHasChildDevice() != null && data.getHasChildDevice()) {
//            List<DeviceChannelDO> deviceChannelDOList = deviceChannelMapper.listDeviceChannelByDeviceId(eid, Collections.singletonList(data.getDeviceId()), null);
//            List<DeviceChannelDO> nvrEnableDeviceList = ListUtils.emptyIfNull(deviceChannelDOList)
//                    .stream()
//                    .filter(DeviceChannelDO::getEnablePassenger)
//                    .collect(Collectors.toList());
//            ListUtils.emptyIfNull(nvrEnableDeviceList)
//                    .forEach(nvr ->
//                            syncPassengerDeviceRecord(yesterdayTime, month1, productDayTime, productYearTime,
//                                    passengerFlowRecordDOList, yingshiToken, data, Integer.valueOf(nvr.getChannelNo()),sceneTypeMap)
//                    );
//        }
    }

    private void syncPassengerDeviceRecord(long yesterdayTime, int month1, Date productDayTime, Integer productYearTime,
                                           List<PassengerFlowRecordDO> passengerFlowRecordDOList, String yingshiToken,
                                           DeviceDO data, Integer channelNo,Map<Long, String> sceneTypeMap) {

        List<PassengerFlowHourlyDTO> passengerFlowHourlyDTO = yingshiDeviceService.passengerFlowHourly(data.getDeviceId(), channelNo, yesterdayTime, yingshiToken);
        List<PassengerFlowRecordDO> hourPassengerFlowRecodeList = ListUtils.emptyIfNull(passengerFlowHourlyDTO)
                .stream()
                .map(hourlyDTO -> mapPassengerFlowRecodeByHourlyDTO(month1, productDayTime, productYearTime, data, hourlyDTO,sceneTypeMap))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(hourPassengerFlowRecodeList)) {
            //同一个小时索引的数据取最高值
            Map<Integer, PassengerFlowRecordDO> collect = hourPassengerFlowRecodeList.stream()
                    .collect(Collectors.toMap(PassengerFlowRecordDO::getFlowHour, hour -> hour, BinaryOperator.maxBy(Comparator.comparing(PassengerFlowRecordDO::getFlowIn))));
            passengerFlowRecordDOList.addAll(collect.values());
        }
        PassengerFlowDailyDTO passengerFlowDailyDTO = yingshiDeviceService.passengerFlowDaily(data.getDeviceId(), channelNo, yesterdayTime, yingshiToken);
        if (passengerFlowDailyDTO != null) {
            PassengerFlowRecordDO passengerFlowRecordDO = new PassengerFlowRecordDO();
            passengerFlowRecordDO.setStoreId(data.getBindStoreId());
            passengerFlowRecordDO.setRegionPath(data.getRegionPath());
            passengerFlowRecordDO.setDeviceId(data.getDeviceId());
            passengerFlowRecordDO.setHasChildDevice(data.getHasChildDevice()==null?false:data.getHasChildDevice());
            passengerFlowRecordDO.setSceneId(data.getStoreSceneId());
            passengerFlowRecordDO.setSceneType(sceneTypeMap.get(data.getStoreSceneId()));
            passengerFlowRecordDO.setFlowType(FlowTypeEnum.DAY.getCode());
            passengerFlowRecordDO.setFlowIn(passengerFlowDailyDTO.getInFlow());
            passengerFlowRecordDO.setFlowOut(passengerFlowDailyDTO.getOutFlow());
            passengerFlowRecordDO.setFlowInOut(passengerFlowDailyDTO.getInFlow() + passengerFlowDailyDTO.getOutFlow());
            passengerFlowRecordDO.setFlowYear(productYearTime);
            passengerFlowRecordDO.setFlowDay(productDayTime);
            passengerFlowRecordDO.setFlowHour(0);
            passengerFlowRecordDO.setFlowMonth(month1);
            passengerFlowRecordDOList.add(passengerFlowRecordDO);
        }
    }

    private PassengerFlowRecordDO mapPassengerFlowRecodeByHourlyDTO(int month1, Date productDayTime, Integer productYearTime,
                                                                    DeviceDO data, PassengerFlowHourlyDTO hourlyDTO,Map<Long, String> sceneTypeMap) {

        PassengerFlowRecordDO passengerFlowRecordDO = new PassengerFlowRecordDO();
        passengerFlowRecordDO.setStoreId(data.getBindStoreId());
        passengerFlowRecordDO.setRegionPath(data.getRegionPath());
        passengerFlowRecordDO.setDeviceId(data.getDeviceId());
        passengerFlowRecordDO.setHasChildDevice(data.getHasChildDevice()==null?false:data.getHasChildDevice());
        passengerFlowRecordDO.setSceneId(data.getStoreSceneId());
        passengerFlowRecordDO.setSceneType(sceneTypeMap.get(data.getStoreSceneId()));
        passengerFlowRecordDO.setFlowType(FlowTypeEnum.HOUR.getCode());
        passengerFlowRecordDO.setFlowIn(hourlyDTO.getInFlow());
        passengerFlowRecordDO.setFlowOut(hourlyDTO.getOutFlow());
        passengerFlowRecordDO.setFlowInOut(hourlyDTO.getInFlow() + hourlyDTO.getOutFlow());
        passengerFlowRecordDO.setFlowYear(productYearTime);
        passengerFlowRecordDO.setFlowDay(productDayTime);
        passengerFlowRecordDO.setFlowMonth(month1);
        passengerFlowRecordDO.setFlowHour(hourlyDTO.getHourIndex());
        return passengerFlowRecordDO;
    }







}
