package com.coolcollege.intelligent.service.aliyun.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;

import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunVdsAgeDataDTO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunVdsDataDTO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunVdsSexDataDTO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunVdsTraceDTO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsStatisticsDateVO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.enums.AliyunAggregateEnum;
import com.coolcollege.intelligent.model.enums.AliyunGroupEnum;
import com.coolcollege.intelligent.model.enums.AliyunStorePersonEnum;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsStatisticsService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.AliyunUtilVideo;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2021/1/13 19:48
 */
@Service
@Slf4j
public class AliyunVdsStatisticsServiceImpl implements AliyunVdsStatisticsService {

    @Value("${aliyun.api.video.keyId}")
    private String keyId;

    @Value("${aliyun.api.video.keySecret}")
    private String keySecret;


    @Resource
    private RegionMapper regionMapper;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    @Resource
    private DeviceMapper deviceMapper;


    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String CORP_ACTION = "ListCorpMetricsStatistic";

    private final String ACTION = "ListMetrics";

    private final String TRACK_ACTION = "ListCorpTrackDetail";

    private static final String CORP_NUM_KEY = "TagMetrics";
    private static final String CORP_DATE_KEY = "DateId";
    private static final String CORP_TAG_KEY = "TagValue";
    private static final String NUM_KEY = "TagMetric";
    private static final String DATE_KEY = "DateTime";

    @Override
    public Object getNowWeekByCorp(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
//        IAcsClient client = AliyunUtilVideo.getSingleClient(keyId, keySecret);
        // 填充时间
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new HashMap<>();
        }
        fillNowWeekTime(statisticsWeek);
        JSONArray dataList = getCorpDataList(statisticsWeek, statisticsWeek.getBeginTime(), AliyunGroupEnum.CORP_DAY_ALL.getCode());
        return nowWeekData(dataList, statisticsWeek.isContainsYesterday(), CORP_DATE_KEY, CORP_NUM_KEY);
    }

    @Override
    public Object getNowWeek(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new HashMap<>();
        }
        fillNowWeekTime(statisticsWeek);
        JSONArray dataList = getDataList(statisticsWeek, statisticsWeek.getBeginTime(), Collections.singletonList(AliyunStorePersonEnum.ALL_COUNT.getCode()), AliyunAggregateEnum.DAY.getCode());
        return nowWeekData(dataList, statisticsWeek.isContainsYesterday(), DATE_KEY, NUM_KEY);
    }

    @Override
    public Object getTwoWeekByCorp(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new HashMap<>();
        }
        // 获取开始时间
        String lastWeekBeginTime = getLastWeekBeginTime(statisticsWeek);
        JSONArray dataList = getCorpDataList(statisticsWeek, lastWeekBeginTime, AliyunGroupEnum.CORP_DAY_ALL.getCode());
        return getTwoWeekData(dataList, lastWeekBeginTime, statisticsWeek.getBeginTime(), statisticsWeek.getEndTime(), CORP_DATE_KEY, CORP_NUM_KEY);
    }

    @Override
    public Object getTwoWeek(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new HashMap<>();
        }
        // 获取开始时间
        String lastWeekBeginTime = getLastWeekBeginTime(statisticsWeek);
        JSONArray dataList = getDataList(statisticsWeek, lastWeekBeginTime, Collections.singletonList(AliyunStorePersonEnum.ALL_COUNT.getCode()), AliyunAggregateEnum.DAY.getCode());
        return getTwoWeekData(dataList, lastWeekBeginTime, statisticsWeek.getBeginTime(), statisticsWeek.getEndTime(), DATE_KEY, NUM_KEY);
    }

    @Override
    public Object getSexData(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new ArrayList<>();
        }
        getLastWeekBeginTime(statisticsWeek);
        JSONArray dataList = getCorpDataList(statisticsWeek, statisticsWeek.getBeginTime(), AliyunGroupEnum.CORP_GENDER.getCode());
        if (CollUtil.isEmpty(dataList)) {
            return Arrays.asList(new AliyunVdsSexDataDTO("1", 0, "0%"), new AliyunVdsSexDataDTO("2", 0, "0%"));
        }
        // 总数
        int num = 0;
        // 将性别与数量组装
        Map<String, Integer> genderMap = new HashMap<>(4);
        for (Object obj : dataList) {
            JSONObject data = (JSONObject) obj;
            int genderNum = data.getIntValue(CORP_NUM_KEY);
            num += genderNum;
            // 获取性别
            String genderCode = data.getString(CORP_TAG_KEY);
            genderMap.merge(genderCode, genderNum, Integer::sum);
        }
        List<AliyunVdsSexDataDTO> result = new ArrayList<>(3);
        int count = num;
        genderMap.forEach((k, v) -> {
            AliyunVdsSexDataDTO sexData = new AliyunVdsSexDataDTO(k, v, null);
            sexData.setPercent(NumberFormat.getPercentInstance().format((v * 1d) / count));
            result.add(sexData);
        });
        return result;
    }

    @Override
    public Object getAgeData(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new ArrayList<>();
        }
        getLastWeekBeginTime(statisticsWeek);
        JSONArray dataList = getCorpDataList(statisticsWeek, statisticsWeek.getBeginTime(), AliyunGroupEnum.CORP_AGE.getCode());
        if (CollUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        Map<String, List<AliyunVdsDataDTO>> ageMap = new HashMap<>();
        for (Object obj : dataList) {
            JSONObject data = (JSONObject) obj;
            int ageNum = data.getIntValue(CORP_NUM_KEY);
            // 年龄段
            String ageCode = data.getString(CORP_TAG_KEY);
            String ageDate = data.getString(CORP_DATE_KEY);
            AliyunVdsDataDTO ageData = new AliyunVdsDataDTO(ageCode, ageNum);
            List<AliyunVdsDataDTO> values = ageMap.get(ageDate);
            if (CollUtil.isEmpty(values)) {
                values = new ArrayList<>();
            }
            values.add(ageData);
            ageMap.put(ageDate, values);
        }
        List<AliyunVdsAgeDataDTO> result = new ArrayList<>();
        ageMap.forEach((k, v) -> result.add(new AliyunVdsAgeDataDTO(k, v)));
        result.sort(Comparator.comparing(AliyunVdsAgeDataDTO::getDate).reversed());
        return result;
    }

    @Override
    public Object getTrackDetail(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        fillCorpId(eid, statisticsWeek);
        if (StrUtil.isBlank(statisticsWeek.getCorpId())) {
            return new ArrayList<>();
        }
        getLastWeekBeginTime(statisticsWeek);
        statisticsWeek.setPageSize(500);
        JSONArray dataList = getTrackDataList(statisticsWeek, statisticsWeek.getBeginTime());
        if (CollUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<AliyunVdsTraceDTO> result = new ArrayList<>(dataList.size());
        Set<String> deviceIds = new HashSet<>(dataList.size());
        for (Object obj : dataList) {
            JSONObject data = (JSONObject) obj;
            String deviceId = data.getString("DeviceId");
            deviceIds.add(deviceId);
            String startTime = data.getString("StartTime");
            String lastTime = data.getString("LastTime");
            String sourceImage = data.getString("SourceUrl");
            String targetImage = data.getString("TargetUrl");
            result.add(new AliyunVdsTraceDTO(deviceId, startTime, lastTime, sourceImage, targetImage));
        }
        // 获取设备与门店的映射关系
        List<DeviceDTO> deviceStoreList = deviceMapper.getDeviceDTOByDeviceIdList(eid, new ArrayList<>(deviceIds));

        Map<String, DeviceDTO> deviceStoreMap = deviceStoreList.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                .collect(Collectors.toMap(DeviceDTO::getDeviceId, Function.identity()));
        result.forEach(f -> {
            // 设置门店信息
            DeviceDTO deviceStore = deviceStoreMap.get(f.getDeviceId());
            if (deviceStore != null) {
                f.setStoreId(deviceStore.getStoreId());
                f.setStoreName(deviceStore.getStoreName());
            }
        });
        return result;
    }

    /**
     * 组装本周客流数据
     * @param dataList
     * @param containsYesterday
     * @return
     */
    private Map<String, Integer> nowWeekData(JSONArray dataList, boolean containsYesterday, String dateKey, String numKey) {
        Map<String, Integer> result = new HashMap<>();
        int totalNum = 0;
        int todayNum = 0;
        int yesterdayNum = 0;
        LocalDate now = LocalDate.now();
        LocalDate minusDays = now.minusDays(1);
        String today = DATE_FORMATTER.format(now);
        String yesterday = DATE_FORMATTER.format(minusDays);
        for (Object obj : dataList) {
            JSONObject data = (JSONObject) obj;
            int num = data.getIntValue(numKey);
            String date = data.getString(dateKey);
            // 今天
            if (today.equals(date)) {
                todayNum = num;
            }
            // 昨天
            boolean isYesterday = yesterday.equals(date);
            if (isYesterday) {
                yesterdayNum = num;
            }
            // 汇总
            // 如果时间段包含昨天 直接相加
            if (containsYesterday) {
                totalNum += num;
            } else {
                // 如果不包含昨天 并且日期不等于昨天 相加
                if (!isYesterday) {
                    totalNum += num;
                }
            }
        }
        result.put("totalNum", totalNum);
        result.put("todayNum", todayNum);
        result.put("yesterdayNum", yesterdayNum);
        return result;
    }

    private Map<String, List<AliyunVdsDataDTO>> getTwoWeekData(JSONArray dataList, String lastWeekBeginTime, String beginTime, String endTime, String dateKey, String numKey) {
        Map<String, List<AliyunVdsDataDTO>> result = new HashMap<>(2);
        List<AliyunVdsDataDTO> thisWeek = new ArrayList<>();
        List<AliyunVdsDataDTO> lastWeek = new ArrayList<>();
        // 获取日期与客流量的映射
        Map<String, Integer> dateNumMap = dataList.stream()
                .map(m -> {
                    JSONObject obj = (JSONObject) m;
                    return new AliyunVdsDataDTO(obj.getString(dateKey), obj.getIntValue(numKey));
                })
                .collect(Collectors.toMap(AliyunVdsDataDTO::getCode, AliyunVdsDataDTO::getNum));
        // 上周开始日期
        LocalDate lastWeekBeginDate = LocalDate.parse(lastWeekBeginTime, TIME_FORMATTER);
        LocalDate beginDate = LocalDate.parse(beginTime, TIME_FORMATTER);
        LocalDate endDate = LocalDate.parse(endTime, TIME_FORMATTER);
        LocalDate startDate = lastWeekBeginDate;
        for (;(startDate.isBefore(endDate) || startDate.equals(endDate)); startDate = startDate.plusDays(1)) {
            // 日期在前端给定的时间段内
            boolean inThisWeek = (startDate.equals(beginDate) || startDate.isAfter(beginDate)) &&
                    (startDate.equals(endDate) || startDate.isBefore(endDate));
            String thisDate = DATE_FORMATTER.format(startDate);
            Integer num = dateNumMap.getOrDefault(thisDate, 0);
            if (inThisWeek) {
                thisWeek.add(new AliyunVdsDataDTO(thisDate, num));
            } else {
                lastWeek.add(new AliyunVdsDataDTO(thisDate, num));
            }
        }
        thisWeek.sort(Comparator.comparing(AliyunVdsDataDTO::getCode));
        lastWeek.sort(Comparator.comparing(AliyunVdsDataDTO::getCode));
        result.put("thisWeek", thisWeek);
        result.put("lastWeek", lastWeek);
        return result;
    }

    /**
     * 填充本周时间的时间段
     * @param statisticsWeek
     */
    private void fillNowWeekTime(AliyunVdsStatisticsDateVO statisticsWeek) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        // 本周起始时间
        LocalDateTime thisWeekStart = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue()).withHour(0).withMinute(0).withSecond(0);
        // 本周结束时间
        LocalDateTime thisWeekEnd = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue()).withHour(23).withMinute(59).withSecond(59);
        // 如果昨天在本周内
        if ((yesterday.equals(thisWeekStart) || yesterday.isAfter(thisWeekStart))
                &&  (yesterday.equals(thisWeekEnd) || yesterday.isBefore(thisWeekEnd))) {
            statisticsWeek.setContainsYesterday(true);
        } else {
            // 不在本周内 起始时间需要设置为昨天
            thisWeekStart = yesterday;
        }
        statisticsWeek.setBeginTime(TIME_FORMATTER.format(thisWeekStart));
        statisticsWeek.setEndTime(TIME_FORMATTER.format(thisWeekEnd));
    }

    private JSONArray getCorpDataList(AliyunVdsStatisticsDateVO statisticsWeek, String startTime, String code) {
        CommonRequest request = getCommonRequest(CORP_ACTION);
//        List<String> corpIds = Collections.singletonList(statisticsWeek.getCorpId());
        request.putQueryParameter("CorpId", statisticsWeek.getCorpId());
        request.putQueryParameter("TagCode", code);
        request.putQueryParameter("QualitScore", "total");//FIXME 临时调整 by jeffrey

        
        request.putQueryParameter("StartTime", startTime);
        request.putQueryParameter("EndTime", statisticsWeek.getEndTime());
        request.putQueryParameter("PageNumber", statisticsWeek.getPageNum().toString());
        request.putQueryParameter("PageSize", statisticsWeek.getPageSize().toString());

        String res = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        return parseResult(res);
    }

    private JSONArray getDataList(AliyunVdsStatisticsDateVO statisticsWeek, String startTime, List<String> code, String type) {
        CommonRequest request = getCommonRequest(ACTION);
        List<String> corpIds = Collections.singletonList(statisticsWeek.getCorpId());
        request.putQueryParameter("CorpId", JSON.toJSONString(corpIds));
        request.putQueryParameter("StartTime", startTime);
        request.putQueryParameter("EndTime", statisticsWeek.getEndTime());
        request.putQueryParameter("PageNumber", statisticsWeek.getPageNum().toString());
        request.putQueryParameter("PageSize", statisticsWeek.getPageSize().toString());
        request.putQueryParameter("TagCode", JSON.toJSONString(code));
        request.putQueryParameter("AggregateType", type);

        String res = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        return parseResult(res);
    }

    private JSONArray getTrackDataList(AliyunVdsStatisticsDateVO statisticsWeek, String startTime) {
        CommonRequest request = getCommonRequest(TRACK_ACTION);
        List<String> faceIds = Collections.singletonList(statisticsWeek.getFaceId());
        request.putQueryParameter("CorpId", statisticsWeek.getCorpId());
        request.putQueryParameter("PersonId", JSON.toJSONString(faceIds));
        request.putQueryParameter("StartTime", startTime);
        request.putQueryParameter("EndTime", statisticsWeek.getEndTime());
        request.putQueryParameter("PageNumber", statisticsWeek.getPageNum().toString());
        request.putQueryParameter("PageSize", statisticsWeek.getPageSize().toString());

        String res = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        return parseResult(res);
    }

    private JSONArray parseResult(String res) {
        JSONObject data;
        JSONArray dataList;
        try {
            data = JSON.parseObject(res);
            dataList = data.getJSONArray("Data");
        } catch (Exception e) {
            log.error("阿里云数据解析异常", e);
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "阿里云数据获取异常");
        }
        return dataList;
    }

    /**
     * 获取七天前的开始时间
     * @param statisticsWeek
     * @return
     */
    private String getLastWeekBeginTime(AliyunVdsStatisticsDateVO statisticsWeek) {
//        String beginTime = statisticsWeek.getBeginTime();
//        LocalDateTime beginDateTime = statisticsWeek.parse(beginTime, TIME_FORMATTER);
        Instant instant = Instant.ofEpochMilli(statisticsWeek.getBeginDateTime());
        LocalDateTime beginDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        Instant instant2 = Instant.ofEpochMilli(statisticsWeek.getEndDateTime());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(instant2, ZoneId.systemDefault());
        LocalDateTime lastWeekBeginTime = beginDateTime.minusDays(7);
        statisticsWeek.setBeginTime(TIME_FORMATTER.format(beginDateTime));
        statisticsWeek.setEndTime(TIME_FORMATTER.format(endDateTime));
        return TIME_FORMATTER.format(lastWeekBeginTime);
    }

    private CommonRequest getCommonRequest(String action) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.setSysAction(action);
        return request;
    }

    private void fillCorpId(String eid, AliyunVdsStatisticsDateVO statisticsWeek) {
        DataSourceHelper.changeToMy();
        if (statisticsWeek.isAll()) {
            SettingVO settingVO = enterpriseVideoSettingService.getSettingIncludeNull(eid, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            if(settingVO!=null){
                statisticsWeek.setCorpId(settingVO.getRootVdsCorpId());
            }
        }
    }
}
