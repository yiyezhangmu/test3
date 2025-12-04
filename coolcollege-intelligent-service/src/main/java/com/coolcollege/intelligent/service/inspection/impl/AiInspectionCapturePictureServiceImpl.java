package com.coolcollege.intelligent.service.inspection.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.DeviceCaptureBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.inspection.AiStatusEnum;
import com.coolcollege.intelligent.common.enums.inspection.TicketCreateRuleEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseDeviceCaptureInfoMapper;
import com.coolcollege.intelligent.dao.inspection.AiInspectionTimePeriodMapper;
import com.coolcollege.intelligent.dao.inspection.dao.AiInspectionStoreMappingDAO;
import com.coolcollege.intelligent.dao.inspection.dao.AiInspectionStorePeriodDAO;
import com.coolcollege.intelligent.dao.inspection.dao.AiInspectionStorePictureDAO;
import com.coolcollege.intelligent.dao.inspection.dao.AiInspectionStrategiesDAO;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dto.EnterpriseQuestionSettingsDTO;
import com.coolcollege.intelligent.model.ai.*;
import com.coolcollege.intelligent.model.ai.dto.InspectionInfoDTO;
import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetAiResultDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.YingShiCloudRecordingMessage;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.entity.EnterpriseDeviceCaptureInfoDO;
import com.coolcollege.intelligent.model.enums.AIPlatformEnum;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.inspection.*;
import com.coolcollege.intelligent.model.inspection.entity.*;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.ai.EnterpriseModelAlgorithmService;
import com.coolcollege.intelligent.service.ai.impl.ShuZiMaLiAiOpenServiceImpl;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author byd
 * @date 2025-10-14 17:40
 */
@Slf4j
@Service
public class AiInspectionCapturePictureServiceImpl implements AiInspectionCapturePictureService {

    @Resource
    private AiInspectionTimePeriodMapper aiInspectionTimePeriodMapper;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private AiInspectionStrategiesDAO aiInspectionStrategiesDAO;

    @Resource
    private AiInspectionStoreMappingDAO aiInspectionStoreMappingDAO;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private AuthVisualService authVisualService;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    @Resource
    private AIService aiService;

    @Resource
    private VideoServiceApi videoServiceApi;

    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private AiInspectionStorePictureDAO aiInspectionStorePictureDAO;

    @Resource
    private AiInspectionStorePeriodDAO aiInspectionStorePeriodDAO;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;

    @Resource
    private EnterpriseModelAlgorithmService enterpriseModelAlgorithmService;

    @Resource
    private AiModelLibraryService aiModelLibraryService;


    @Resource
    private ShuZiMaLiAiOpenServiceImpl shuZiMaLiAiOpenServiceImpl;

    @Resource
    private QuestionParentInfoService questionParentInfoService;


    @Resource
    private EnterpriseDeviceCaptureInfoMapper deviceCaptureInfoMapper;


    @Override
    public void capturePicture(String enterpriseId, String captureTime) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SEC);

        LocalDateTime captureDateTime = LocalDateTime.parse(captureTime, customFormatter);

        // 获取当前日期是周几，返回1（星期一）到7（星期日）
        Integer dayOfWeekNumber = captureDateTime.getDayOfWeek().getValue();

        String hourTime = captureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        // 例如，输出：今天是周4（代表星期四）
        log.info("capturePicture# eid :{} 今天是周:{}, formattedHHmmTime:{}", enterpriseId, dayOfWeekNumber, hourTime);

        // 5. 查询匹配的时间段配置
        List<AiInspectionTimePeriodDO> matchedPeriods = aiInspectionTimePeriodMapper.findMatchingStrategiesWithPeriods(
                enterpriseId, hourTime, dayOfWeekNumber);

        if (CollectionUtils.isEmpty(matchedPeriods)) {
            log.info("当前时间 {} 没有匹配的抓拍时间段, eid:{}", captureTime, enterpriseId);
            return;
        }

        matchedPeriods.forEach(periodDO -> {
            log.info("isInPeriodRound#开始执行: currentTime={}, beginTime={}, endTime={}, period={}, eid:{}",
                    captureTime, periodDO.getBeginTime(), periodDO.getEndTime(), periodDO.getPeriod(), enterpriseId);
            boolean isInPeriodRound = isInPeriodRound(hourTime, periodDO.getBeginTime(), periodDO.getEndTime(), periodDO.getPeriod());
            log.info("isInPeriodRound#是否在执行范围: currentTime={}, beginTime={}, endTime={}, period={}, eid:{}, isInPeriodRound:{}",
                    captureTime, periodDO.getBeginTime(), periodDO.getEndTime(), periodDO.getPeriod(), enterpriseId, isInPeriodRound);
            if (!isInPeriodRound) {
                log.info("当前时间 {} 不在时间段 {} 内, eid:{}", captureTime, periodDO.getId(), enterpriseId);
                return;
            }
            //创建消息 异步执行分解和抓拍任务
            AiInspectionTimePeriodDealDTO aiInspectionTimePeriodDealDTO = AiInspectionTimePeriodDealDTO.builder()
                    .id(periodDO.getId())
                    .beginTime(periodDO.getBeginTime())
                    .endTime(periodDO.getEndTime())
                    .period(periodDO.getPeriod())
                    .inspectionId(periodDO.getInspectionId())
                    .enterpriseId(enterpriseId)
                    .periodTime(captureTime)
                    .captureTime(captureTime)
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(aiInspectionTimePeriodDealDTO), RocketMqTagEnum.AI_INSPECTION_DATA_DEAL);
        });

    }

    @Override
    public void decomposeStores(String enterpriseId, AiInspectionTimePeriodDealDTO aiInspectionTimePeriodDealDTO) {
        Long inspectionId = aiInspectionTimePeriodDealDTO.getInspectionId();
        AiInspectionStrategiesDO aiInspectionStrategiesDO = aiInspectionStrategiesDAO.selectByPrimaryKey(inspectionId, enterpriseId);
        List<AiInspectionStoreMappingDO> aiInspectionStoreMappingDOList =
                aiInspectionStoreMappingDAO.selectByInspectionIdList(Collections.singletonList(inspectionId), enterpriseId);

        // 分别获取区域和分组ID列表
        List<String> regionIdList = aiInspectionStoreMappingDOList.stream()
                .filter(x -> UnifyTaskConstant.StoreType.REGION.equals(x.getType()))
                .map(AiInspectionStoreMappingDO::getMappingId)
                .collect(Collectors.toList());

        List<String> storeIdList = aiInspectionStoreMappingDOList.stream()
                .filter(x -> UnifyTaskConstant.StoreType.STORE.equals(x.getType()))
                .map(AiInspectionStoreMappingDO::getMappingId)
                .collect(Collectors.toList());

        Set<String> allStoreIds = Sets.newHashSet();



        Set<String> storeSet = Sets.newHashSet();
        //区域
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<String> regionPathList = regionMapper.getFullPathByIds(enterpriseId, regionIdList);
            List<StoreAreaDTO> areaStoreList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if (CollectionUtils.isNotEmpty(areaStoreList)) {
                allStoreIds.addAll(areaStoreList.stream().map(StoreAreaDTO::getStoreId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
            }
        }
        //门店Od
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            allStoreIds.addAll(storeIdList);
        }

        if (CollectionUtils.isNotEmpty(allStoreIds)) {
            AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, aiInspectionStrategiesDO.getCreateUserId(), new ArrayList<>(allStoreIds));
            log.info("##unify task regionList authStore={}", JSON.toJSONString(authStore));
            if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                List<String> effticeGroupStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(authStore.getStoreIdList()));
                if (CollectionUtils.isNotEmpty(effticeGroupStoreIdList)) {
                    storeSet.addAll(effticeGroupStoreIdList);
                }
            }
        }
        log.info("decomposeStores#storeSet.size={}, eid:{} inspectionId:{}, periodTime：{}", storeSet.size(), enterpriseId, inspectionId, aiInspectionTimePeriodDealDTO.getPeriodTime());
        if (CollectionUtils.isEmpty(storeSet)) {
            log.info("decomposeStores#storeSet.size={}, eid:{} inspectionId:{}, periodTime：{} 没有有效门店", storeSet.size(), enterpriseId, inspectionId, aiInspectionTimePeriodDealDTO.getPeriodTime());
            return;
        }

        // 循环 storeSet
        storeSet.forEach(storeId -> {
            AiInspectionTimePeriodStoreDealDTO aiInspectionTimePeriodStoreDealDTO = AiInspectionTimePeriodStoreDealDTO.builder()
                    .id(aiInspectionTimePeriodDealDTO.getId())
                    .beginTime(aiInspectionTimePeriodDealDTO.getBeginTime())
                    .endTime(aiInspectionTimePeriodDealDTO.getEndTime())
                    .period(aiInspectionTimePeriodDealDTO.getPeriod())
                    .inspectionId(aiInspectionTimePeriodDealDTO.getInspectionId())
                    .enterpriseId(aiInspectionTimePeriodDealDTO.getEnterpriseId())
                    .captureTime(aiInspectionTimePeriodDealDTO.getCaptureTime())
                    .storeId(storeId)
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(aiInspectionTimePeriodStoreDealDTO), RocketMqTagEnum.AI_INSPECTION_STORE_TASK);
        });
    }

    @Override
    public void storeCapture(String enterpriseId, AiInspectionTimePeriodStoreDealDTO timePeriodStoreDealDTO) {
        Long inspectionId = timePeriodStoreDealDTO.getInspectionId();
        AiInspectionStrategiesDO aiInspectionStrategiesDO = aiInspectionStrategiesDAO.selectByPrimaryKey(inspectionId, enterpriseId);
        AiInspectionStrategiesExtendInfo extendInfoConfig = new AiInspectionStrategiesExtendInfo();
        if(StringUtils.isNotBlank(aiInspectionStrategiesDO.getExtendInfo())){
            extendInfoConfig = JSONObject.parseObject(aiInspectionStrategiesDO.getExtendInfo(), AiInspectionStrategiesExtendInfo.class);
        }
        log.info("##unify task storeCapture#enterpriseId:{}, inspectionId:{}, extendInfoConfig：{}", enterpriseId, inspectionId, JSONObject.toJSONString(extendInfoConfig));
        // 2. 开启智能调度
        if (extendInfoConfig != null && extendInfoConfig.getEnableFrequencyScheduling() != null
                && extendInfoConfig.getEnableFrequencyScheduling().equals(Constants.INDEX_ONE)) {

            //判断是否有不合格抓拍
            if (extendInfoConfig.getFailHandleType() != 0) {
                int count = 0;
                if (extendInfoConfig.getFailHandleType().equals(Constants.INDEX_ONE)) {
                    count = aiInspectionStorePeriodDAO.countInspectionStorePictureByTime(enterpriseId,
                            DateUtil.convert(timePeriodStoreDealDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC, DateUtils.DATE_FORMAT_DAY) + " " + timePeriodStoreDealDTO.getBeginTime() + ":00",
                            DateUtil.convert(timePeriodStoreDealDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC, DateUtils.DATE_FORMAT_DAY)+ " " + timePeriodStoreDealDTO.getEndTime() + ":00",
                            timePeriodStoreDealDTO.getStoreId(), inspectionId);
                    if (count > 0) {
                        log.info("##当前时段有不合格不再检测storeCapture#enterpriseId:{}, inspectionId:{}, storeId:{}, count:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), count);
                        return;
                    }
                }
                if (Objects.equals(extendInfoConfig.getFailHandleType(), Constants.INDEX_TWO)) {
                    count = aiInspectionStorePeriodDAO.countInspectionStorePictureByDay(enterpriseId, DateUtil.convert(timePeriodStoreDealDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC,
                            DateUtils.DATE_FORMAT_DAY), timePeriodStoreDealDTO.getStoreId(), inspectionId);
                    if (count > 0) {
                        log.info("##当日有不合格不再检测storeCapture#enterpriseId:{}, inspectionId:{}, storeId:{}, count:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), count);
                        return;
                    }
                }
            }

            String timeKey = Optional.ofNullable(timePeriodStoreDealDTO.getBeginTime()).orElse("").replace(":", "") +
                    Optional.ofNullable(timePeriodStoreDealDTO.getEndTime()).orElse("").replace(":", "");

            String lockKey = MessageFormat.format(RedisConstant.CAPTURE_CHECK_PASS_LOCK_PICTURE_KEY,
                    enterpriseId, timePeriodStoreDealDTO.getInspectionId() + Constants.UNDERLINE + timePeriodStoreDealDTO.getStoreId()
                            + Constants.UNDERLINE + DateUtil.convert(timePeriodStoreDealDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC,
                            DateUtils.DATE_FORMAT_DAY) + timeKey);

            String passIntervalKey = lockKey + "_passInterval";
            String passIntervalStr = redisUtilPool.getString(passIntervalKey);
            log.info("storeCapture#lockKey:{}, passIntervalKey:{}, passInterval:{}", lockKey, passIntervalKey, passIntervalStr);
            Integer passInterval = StringUtils.isBlank(passIntervalStr) ? 0: Integer.parseInt(passIntervalStr);
            if(passInterval >  0){
                //计算出当前抓拍间隔
                passInterval = Math.min(passInterval + timePeriodStoreDealDTO.getPeriod(), Math.max(extendInfoConfig.getMaxCaptureInterval(), timePeriodStoreDealDTO.getPeriod()));
                DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SEC);

                LocalDateTime captureDateTime = LocalDateTime.parse(timePeriodStoreDealDTO.getCaptureTime(), customFormatter);

                String hourTime = captureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                log.info("storeCapture#hourTime:{}, beginTime:{}, endTime:{}, passInterval:{}, captureTime:{}, storeId:{}",
                        hourTime, timePeriodStoreDealDTO.getBeginTime(), timePeriodStoreDealDTO.getEndTime(), passInterval,
                        timePeriodStoreDealDTO.getCaptureTime(), timePeriodStoreDealDTO.getStoreId());
                //判断是否在当前时间范围内
                boolean isInPeriodRound = isInPeriodRound(hourTime, timePeriodStoreDealDTO.getBeginTime(), timePeriodStoreDealDTO.getEndTime(), passInterval);
                log.info("storeCapture#result#hourTime:{}, beginTime:{}, endTime:{}, passInterval:{}, captureTime:{} isInPeriodRound:{}, storeId:{}",
                        hourTime, timePeriodStoreDealDTO.getBeginTime(), timePeriodStoreDealDTO.getEndTime(), passInterval, timePeriodStoreDealDTO.getCaptureTime(), isInPeriodRound, timePeriodStoreDealDTO.getStoreId());
                if(!isInPeriodRound){
                    log.info("storeCapture#该门店不在当前时间段内,不在生成门店任务inspectionId{},storeId:{},CaptureTime:{}", inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime());
                    return;
                }
            }
        }


        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, timePeriodStoreDealDTO.getStoreId());

        if (!Constants.STORE_STATUS_OPEN.equals(storeDO.getStoreStatus())) {
            log.info("storeCapture#该门店的未开业,不在生成门店任务inspectionId{},storeId:{},CaptureTime:{},storeStatus:{}", inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), storeDO.getStoreStatus());
            return;
        }

        List<Long> storeSceneIdList = Arrays.stream(aiInspectionStrategiesDO.getTags().split(Constants.COMMA))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<DeviceDO> videoDeviceList = deviceMapper.getByStoreId(enterpriseId, timePeriodStoreDealDTO.getStoreId());
        if (CollectionUtils.isEmpty(videoDeviceList)) {
            log.info("该门店没有设备 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime());
            return;
        }
        //摄像机
        List<DeviceDO> deviceList = videoDeviceList.stream().filter(device ->
                        (device.getHasChildDevice() == null || !device.getHasChildDevice())
                                && storeSceneIdList.contains((device.getStoreSceneId())))
                .collect(Collectors.toList());

        Map<String, DeviceDO> videoDeviceMap = videoDeviceList.stream().collect(Collectors.toMap(DeviceDO::getDeviceId, Function.identity()));
        Map<Long, DeviceDO> videoDeviceIdMap = videoDeviceList.stream().collect(Collectors.toMap(DeviceDO::getId, Function.identity()));

        //查询录像机下的通道NVR
        List<String> deviceParentIdList = videoDeviceList.stream().filter(device -> device.getHasChildDevice() != null && device.getHasChildDevice())
                .map(DeviceDO::getDeviceId).collect(Collectors.toList());
        List<DeviceChannelDO> videoChannelDeviceList = null;
        if (CollectionUtils.isNotEmpty(deviceParentIdList)) {
            videoChannelDeviceList = deviceChannelMapper.getByParentDeviceIds(enterpriseId, deviceParentIdList, storeSceneIdList);
        }
        List<AiInspectionStorePictureDO> storePictureDOList = new ArrayList<>();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(timePeriodStoreDealDTO.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());

        //摄像机抓拍
        if (CollectionUtils.isNotEmpty(deviceList)) {
            for (DeviceDO deviceDO : deviceList) {
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                this.deviceCapturePicture(enterpriseId, timePeriodStoreDealDTO, aiInspectionStrategiesDO, storeDO,
                        timePeriodStoreDealDTO.getCaptureTime(), storePictureDOList,
                        deviceDO.getId(), 0L, deviceDO.getStoreSceneId(), deviceDO.getDeviceId(), null, deviceDO);
            }
        }
        Map<Long, DeviceChannelDO> videoChannelDeviceMap = new HashMap<>();

        //录像机抓拍
        if (CollectionUtils.isNotEmpty(videoChannelDeviceList)) {
            videoChannelDeviceMap = videoChannelDeviceList.stream().collect(Collectors.toMap(DeviceChannelDO::getId, Function.identity()));
            for (DeviceChannelDO deviceChannelDO : videoChannelDeviceList) {
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                DeviceDO deviceDO = videoDeviceMap.get(deviceChannelDO.getParentDeviceId());
                this.deviceCapturePicture(enterpriseId, timePeriodStoreDealDTO, aiInspectionStrategiesDO, storeDO,
                        timePeriodStoreDealDTO.getCaptureTime(), storePictureDOList,
                        deviceDO.getId(), deviceChannelDO.getId(), deviceChannelDO.getStoreSceneId(), deviceDO.getDeviceId(), deviceChannelDO.getChannelNo(), deviceDO);
            }
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());

        if (CollectionUtils.isEmpty(storePictureDOList)) {
            log.info("该门店没有需要AI执行图片,未找到对应场景的门店 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime());
            return;
        }
        // 获取本周周一的日期
        LocalDate mondayThisWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Date mondayThisWeekDate = Date.from(mondayThisWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        DataSourceHelper.changeToSpecificDataSource(config.getDbName());


        AiInspectionStorePictureDO aiInspectionStorePictureDO = storePictureDOList.get(0);

        AiInspectionStorePeriodDO aiInspectionStorePeriodDO = AiInspectionStorePeriodDO.builder()
                .inspectionId(aiInspectionStrategiesDO.getId())
                .sceneId(aiInspectionStorePictureDO.getSceneId())
                .sceneName(aiInspectionStrategiesDO.getSceneName())
                .storeId(aiInspectionStorePictureDO.getStoreId())
                .storeName(aiInspectionStorePictureDO.getStoreName())
                .regionId(aiInspectionStorePictureDO.getRegionId())
                .regionPath(aiInspectionStorePictureDO.getRegionPath())
                .captureDate(aiInspectionStorePictureDO.getCaptureDate())
                .captureTime(aiInspectionStorePictureDO.getCaptureTime())
                .weekDay(mondayThisWeekDate)
                .remark(aiInspectionStorePictureDO.getRemark())
                .createUserId("")
                .createTime(new Date())
                .updateTime(new Date())
                .deleted(false)
                .build();

        aiInspectionStorePeriodDAO.insertSelective(aiInspectionStorePeriodDO, enterpriseId);

        storePictureDOList.forEach(pictureDO -> {
            pictureDO.setInspectionPeriodId(aiInspectionStorePeriodDO.getId());
            pictureDO.setWeekDay(mondayThisWeekDate);
        });

        //查询需要AI执行分析的图片
        List<AiInspectionStorePictureDO> pictureDOList = storePictureDOList.stream().filter(pictureDO -> Objects.equals(pictureDO.getAiStatus(), AiStatusEnum.CAPTURE_SUCCESS.getCode()))
                .collect(Collectors.toList());

        //是否包含抓拍中
        boolean isCaptureInProgress = storePictureDOList.stream().anyMatch(pictureDO -> Objects.equals(pictureDO.getAiStatus(), AiStatusEnum.CAPTURE_IN_PROGRESS.getCode()));
        log.info("该门店执行图片是否有抽帧 eid:{} , businessId :{} storeId : {}, CaptureTime:{}, isCaptureInProgress:{}",
                enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), isCaptureInProgress);

        if (CollectionUtils.isEmpty(pictureDOList) && !isCaptureInProgress) {
            aiInspectionStorePictureDAO.batchInsert(storePictureDOList, enterpriseId);
            log.info("该门店没有需要AI执行图片 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime());
            return;
        }

        DataSourceHelper.reset();

        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, aiInspectionStrategiesDO.getSceneId());
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelAlgorithmDTO.getModelCode());
        //同步执行
        if (aiModel.getSyncGetResult() != null && aiModel.getSyncGetResult()) {
            //ai执行图片url地址
            pictureDOList.forEach(pictureDO -> {
                    handelAIResult(enterpriseId, pictureDO, modelAlgorithmDTO.getSceneId());
            });

            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            aiInspectionStorePictureDAO.batchInsert(storePictureDOList, enterpriseId);
            // 不包含异步抽帧则计算抓拍结果
            if(isCaptureInProgress){
                log.info("该门店执行图片有抽帧任务，等待抽帧完成在计算结果 eid:{} , businessId :{} storeId : {}, CaptureTime:{}, isCaptureInProgress:{}",
                        enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), isCaptureInProgress);
                DataSourceHelper.reset();
                batchSaveDeviceCaptureInfo(enterpriseId, storePictureDOList, videoDeviceIdMap, videoChannelDeviceMap);
                return;
            }
            //计算以及更新结果
            this.updateAiInspectionResult(enterpriseId, storePictureDOList, aiInspectionStorePeriodDO, extendInfoConfig,
                    timePeriodStoreDealDTO.getBeginTime(), timePeriodStoreDealDTO.getEndTime());
            //不合格判断是否发起工单
            //处理工单流程
            this.handleInspectionResultAndCreateTicket(aiInspectionStorePeriodDO, aiInspectionStrategiesDO,
                    storePictureDOList, enterpriseId, timePeriodStoreDealDTO.getBeginTime(), timePeriodStoreDealDTO.getEndTime(),
                    extendInfoConfig);

        } else {
            // 不包含异步抽帧则计算抓拍结果
            if(isCaptureInProgress){
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                aiInspectionStorePictureDAO.batchInsert(storePictureDOList, enterpriseId);
                log.info("该门店执行图片有抽帧任务，数字码力等待抽帧完成在计算结果 eid:{} , businessId :{} storeId : {}, CaptureTime:{}, isCaptureInProgress:{}",
                        enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), isCaptureInProgress);
                DataSourceHelper.reset();
                batchSaveDeviceCaptureInfo(enterpriseId, storePictureDOList, videoDeviceIdMap, videoChannelDeviceMap);
                return;
            }

            List<String> pictureUrlList = pictureDOList.stream().map(AiInspectionStorePictureDO::getPicture).collect(Collectors.toList());
            pictureDOList.forEach(pictureDO -> {
                pictureDO.setAiStatus(AiStatusEnum.ANALYZING.getCode());
            });

            //异步执行
            log.info("执行图片分析 异步 eid:{} , businessId :{} storeId : {}, clea:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime());
            try {
                AIResolveDTO aiResolveDTO = aiService.aiAsyncInspectionResolve(enterpriseId, aiInspectionStrategiesDO.getSceneId(), pictureUrlList, aiInspectionStorePeriodDO.getId());
                log.info("执行图片分析 异步结果 eid:{} , businessId :{} storeId : {}, CaptureTime:{}, result:{}",
                        enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), JSONObject.toJSONString(aiResolveDTO));
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                aiInspectionStorePictureDAO.batchInsert(storePictureDOList, enterpriseId);
            }catch (Exception e){
                log.error("执行图片分析 异步异常 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, inspectionId, timePeriodStoreDealDTO.getStoreId(), timePeriodStoreDealDTO.getCaptureTime(), e);
                pictureDOList.forEach(pictureDO -> {
                    pictureDO.setAiStatus(AiStatusEnum.FAILED.getCode());
                });
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                aiInspectionStorePictureDAO.batchInsert(storePictureDOList, enterpriseId);
            }

        }
    }

    private void handelAIResult(String enterpriseId, AiInspectionStorePictureDO pictureDO, Long sceneId) {
        AiInspectionResult aiInspectionResult = null;
        try {
            aiInspectionResult = aiService.aiInspectionResolve(enterpriseId, sceneId, Collections.singletonList(pictureDO.getPicture()));
        } catch (Exception e) {
            log.error("执行图片分析异常 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, pictureDO.getInspectionId(), pictureDO.getStoreId(), pictureDO.getCaptureTime(), e);
        }
        if (aiInspectionResult != null) {
            pictureDO.setAiContent(aiInspectionResult.getAiContent());
        }
        DataSourceHelper.reset();
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, sceneId);
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelAlgorithmDTO.getModelCode());
        //如果检查不合格，且是大模型则再重新检查一次
        if(aiInspectionResult != null && aiInspectionResult.getResult() != null && MetaTableConstant.CheckResultNameConstant.FAIL_NAME.equals(aiInspectionResult.getResult())
                && (AIPlatformEnum.HUOSHAN.getCode().equals(aiModel.getPlatformCode()) || AIPlatformEnum.BAILIAN.getCode().equals(aiModel.getPlatformCode()))){
            log.info("重新执行图片分析eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, pictureDO.getInspectionId(), pictureDO.getStoreId(), pictureDO.getCaptureTime());
            try {
                aiInspectionResult = aiService.aiInspectionResolve(enterpriseId, sceneId, Collections.singletonList(pictureDO.getPicture()));
            } catch (Exception e) {
                log.error("重新执行图片分析异常eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, pictureDO.getInspectionId(), pictureDO.getStoreId(), pictureDO.getCaptureTime(), e);
            }
            if (aiInspectionResult != null) {
                pictureDO.setAiContent(aiInspectionResult.getAiContent());
            }
        }

        if (aiInspectionResult == null) {
            pictureDO.setAiStatus(AiStatusEnum.FAILED.getCode());
            pictureDO.setAiFailReason("执行图片分析异常");
        } else {
            String aiResultStr = aiInspectionResult.getResult();
            if (StringUtils.isEmpty(aiResultStr)) {
                pictureDO.setAiStatus(AiStatusEnum.FAILED.getCode());
                pictureDO.setAiFailReason("执行图片分析异常");
            } else {
                pictureDO.setAiStatus(AiStatusEnum.COMPLETED.getCode());
                String aiResultMessage = aiInspectionResult.getMessage();
                pictureDO.setAiFailReason(aiResultMessage);
                if (MetaTableConstant.CheckResultNameConstant.PASS_NAME.equals(aiResultStr)) {
                    pictureDO.setAiResult(MetaTableConstant.CheckResultConstant.PASS);
                }else if (MetaTableConstant.CheckResultNameConstant.INAPPLICABLE_NOT_NAME.equals(aiResultStr)) {
                    pictureDO.setAiResult(MetaTableConstant.CheckResultConstant.INAPPLICABLE);
                    pictureDO.setAiFailReason(aiResultMessage);
                } else if (MetaTableConstant.CheckResultNameConstant.FAIL_NAME.equals(aiResultStr)){
                    pictureDO.setAiResult(MetaTableConstant.CheckResultConstant.FAIL);
                    pictureDO.setAiFailReason(aiResultMessage);
                }else {
                    pictureDO.setAiResult(MetaTableConstant.CheckResultConstant.INAPPLICABLE);
                    pictureDO.setAiFailReason(aiResultMessage);
                }
            }
        }
    }

    public void updateAiInspectionResult(String enterpriseId,
                                         List<AiInspectionStorePictureDO> storePictureDOList,
                                         AiInspectionStorePeriodDO aiInspectionStorePeriodDO,
                                         AiInspectionStrategiesExtendInfo extendInfoConfig,
                                         String beginTime, String endTime) {

        // 1. 计算并更新周期结果
        String periodResult = calculatePeriodResult(storePictureDOList);
        aiInspectionStorePeriodDO.setAiPeriodResult(periodResult);
        aiInspectionStorePeriodDAO.updateByPrimaryKeySelective(aiInspectionStorePeriodDO, enterpriseId);
        aiInspectionStorePictureDAO.updateResultByPeriodId(enterpriseId, aiInspectionStorePeriodDO.getAiPeriodResult(), aiInspectionStorePeriodDO.getId());

        // 2. 开启智能调度
        if (extendInfoConfig != null
                && extendInfoConfig.getEnableFrequencyScheduling() != null
                && extendInfoConfig.getEnableFrequencyScheduling().equals(Constants.INDEX_ONE)) {
            log.info("开启智能调度 eid:{} , businessId :{} storeId : {}, CaptureTime:{} ,periodResult:{}", enterpriseId, aiInspectionStorePeriodDO.getInspectionId(),
                    aiInspectionStorePeriodDO.getStoreId(), aiInspectionStorePeriodDO.getCaptureTime(), periodResult);
            handleFrequencyScheduling(enterpriseId, aiInspectionStorePeriodDO, periodResult, extendInfoConfig, beginTime, endTime);
        }
    }

    /**
     * 处理抓拍频率智能调度
     */
    private void handleFrequencyScheduling(String enterpriseId,
                                           AiInspectionStorePeriodDO periodDO,
                                           String periodResult,
                                           AiInspectionStrategiesExtendInfo extendInfoConfig,
                                           String beginTime, String endTime) {
        String timeKey = Optional.ofNullable(beginTime).orElse("").replace(":", "") +
                Optional.ofNullable(endTime).orElse("").replace(":", "");
        String lockKey = MessageFormat.format(RedisConstant.CAPTURE_CHECK_PASS_LOCK_PICTURE_KEY,
                enterpriseId, periodDO.getInspectionId() + Constants.UNDERLINE + periodDO.getStoreId()
                        + Constants.UNDERLINE + DateUtil.format(periodDO.getCaptureTime(), DateUtils.DATE_FORMAT_DAY) + timeKey);
        String passNumKey = lockKey + "_passNum";
        String passIntervalKey = lockKey + "_passInterval";

        if (MetaTableConstant.CheckResultConstant.PASS.equals(periodResult)) {
            // 检测合格：增加连续合格次数
            handlePassInspection(extendInfoConfig, passNumKey, passIntervalKey);
        } else if (MetaTableConstant.CheckResultConstant.FAIL.equals(periodResult) && extendInfoConfig.getResetIntervalType() != null
                && extendInfoConfig.getResetIntervalType().equals(Constants.INDEX_TWO)) {
            redisUtilPool.delKey(passIntervalKey);
            redisUtilPool.delKey(passNumKey);
        }
    }

    /**
     * 处理检测合格逻辑 - 增加抓拍间隔
     */
    private void handlePassInspection(AiInspectionStrategiesExtendInfo extendInfoConfig, String passNumKey, String passIntervalKey) {
        // 合格计数键的过期时间：默认今天
        long remainingSeconds = Duration.between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toMillis()/1000;


        // 递增合格计数
        Long passNum = redisUtilPool.incrby(passNumKey, 1, (int)remainingSeconds);

        if (passNum == null) {
            log.error("Redis操作失败，无法增加合格计数");
            return;
        }

        log.info("检测合格，当前连续合格次数：passNum{},passNumKey:{}， remainingSeconds:{}", passNum, passNumKey, remainingSeconds);

        // 达到合格次数阈值，增加抓拍间隔
        if (passNum >= extendInfoConfig.getQualifiedCountThreshold()) {

            String intervalStr = redisUtilPool.getString(passIntervalKey);
            Integer currentInterval =  intervalStr != null ? Integer.parseInt(intervalStr) : 0;
            log.info("达到合格次数阈值，开始增加抓拍间隔 currentInterval :{}", currentInterval);
            // 计算新的抓拍间隔
            int newInterval = currentInterval + extendInfoConfig.getIntervalIncrement();

            // 不超过最大间隔限制
            if (extendInfoConfig.getMaxCaptureInterval() > 0) {
                newInterval = Math.min(newInterval, extendInfoConfig.getMaxCaptureInterval());
            }
            redisUtilPool.setNxExpire(passIntervalKey, String.valueOf(newInterval), (int)remainingSeconds * 1000);
            log.info("达到合格次数阈值{}次，抓拍间隔从{}分钟增加至{}分钟,passIntervalKey:{} ",
                    extendInfoConfig.getQualifiedCountThreshold(), currentInterval, newInterval, passIntervalKey);
            redisUtilPool.delKey(passNumKey);
        }
    }
    /**
     * 计算周期结果（Stream API版本）
     */
    private String calculatePeriodResult(List<AiInspectionStorePictureDO> storePictureDOList) {
        //总体检测合格
        if (storePictureDOList.stream().allMatch(pictureDO -> Objects.equals(pictureDO.getAiResult(), MetaTableConstant.CheckResultConstant.PASS))) {
            return MetaTableConstant.CheckResultConstant.PASS;
        } else if (storePictureDOList.stream().anyMatch(pictureDO -> Objects.equals(pictureDO.getAiResult(), MetaTableConstant.CheckResultConstant.FAIL))) {
            return MetaTableConstant.CheckResultConstant.FAIL;
        } else {
            return MetaTableConstant.CheckResultConstant.INAPPLICABLE;
        }
    }


    /**
     * 批量保存设备捕获信息
     * @param enterpriseId 企业ID
     * @param storePictureTaskList 需要处理的图片任务列表
     * @param videoDeviceIdMap 设备映射关系
     */
    private void batchSaveDeviceCaptureInfo(String enterpriseId, List<AiInspectionStorePictureDO> storePictureTaskList,
                                            Map<Long, DeviceDO> videoDeviceIdMap,  Map<Long, DeviceChannelDO> videoChannelDeviceMap) {
        if (CollectionUtils.isEmpty(storePictureTaskList)) {
            return;
        }
        storePictureTaskList = storePictureTaskList.stream().filter(storePicture -> StringUtils.isNotBlank(storePicture.getCaptureTaskId())).collect(Collectors.toList());
        List<EnterpriseDeviceCaptureInfoDO> deviceCaptureInfoList = new ArrayList<>();
        storePictureTaskList.forEach(storePicture -> {
            DeviceDO deviceDO = videoDeviceIdMap.get(storePicture.getDeviceId());
            DeviceChannelDO deviceChannelDO = videoChannelDeviceMap.get(storePicture.getDeviceChannelId());
            EnterpriseDeviceCaptureInfoDO deviceCaptureInfo = EnterpriseDeviceCaptureInfoDO.builder()
                    .enterpriseId(enterpriseId)
                    .businessId(String.valueOf(storePicture.getId()))
                    .businessType(DeviceCaptureBusinessTypeEnum.AI_INSPECTION.getCode())
                    .captureTaskId(storePicture.getCaptureTaskId())
                    .taskResult(0)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            if(deviceChannelDO != null){
                deviceCaptureInfo.setChannelNo(deviceChannelDO.getChannelNo());
            }
            if(deviceDO != null){
                deviceCaptureInfo.setResource(deviceDO.getResource());
                deviceCaptureInfo.setDeviceId(deviceDO.getDeviceId());
            }
            deviceCaptureInfoList.add(deviceCaptureInfo);
        });

        deviceCaptureInfoMapper.insertBatch(deviceCaptureInfoList);
    }


    @Async("generalThreadPool")
    @Override
    public void getInspectionResult(String enterpriseId, Long inspectionPeriodId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        //获取三天前的日期
        String threeDaysAgo = DateUtils.getDateThreeDaysAgo();
        List<AiInspectionStorePictureDO>  storePictureDOList = aiInspectionStorePictureDAO.selectInspectionPeriodIdAiStatus(enterpriseId, threeDaysAgo, inspectionPeriodId);
        List<Long> inspectionPeriodIdList = storePictureDOList.stream().map(AiInspectionStorePictureDO::getInspectionPeriodId).distinct().collect(Collectors.toList());

        Map<Long, List<AiInspectionStorePictureDO> > storePictureDOMap = storePictureDOList.stream().collect(Collectors.groupingBy(AiInspectionStorePictureDO::getInspectionPeriodId));

        inspectionPeriodIdList.forEach(periodId -> {
            AIResolveRequestDTO request = new AIResolveRequestDTO();
            InspectionInfoDTO inspectionInfoDTO = new InspectionInfoDTO();
            inspectionInfoDTO.setBusinessId(String.valueOf(periodId));
            request.setInspectionInfoDTO(inspectionInfoDTO);
            try{
                ShuZhiMaLiGetAiResultDTO aiResultDTO = shuZiMaLiAiOpenServiceImpl.getAiResult(enterpriseId, AiResolveBusinessTypeEnum.AI_INSPECTION, request);
                if (aiResultDTO != null) {
                    //处理结果
                    dealCallBackResult(enterpriseId, aiResultDTO, periodId, storePictureDOMap);
                }
            }catch (Exception e){
                log.error("执行图片分析 异步异常 eid:{} , businessId :{}", enterpriseId, periodId, e);
            }

        });
    }

    @Override
    public void callBackResult(String enterpriseId, ShuZhiMaLiGetAiResultDTO aiResultDTO) {
        String businessId = "";
        String[] parts = aiResultDTO.getOutBizNo().split(Constants.MOSAICS);

        if (parts.length >= 3) {
            businessId = parts[2];
        }
        if(StringUtils.isBlank(businessId)){
            log.error("执行图片分析 异步异常 eid:{} , businessId :{}", enterpriseId, aiResultDTO.getOutBizNo());
            return;
        }
        List<AiInspectionStorePictureDO>  storePictureDOList = aiInspectionStorePictureDAO.selectInspectionPeriodIdAiStatus(enterpriseId, null, Long.valueOf(businessId));
        Map<Long, List<AiInspectionStorePictureDO>> storePictureDOMap = storePictureDOList.stream().collect(Collectors.groupingBy(AiInspectionStorePictureDO::getInspectionPeriodId));

        if(CollectionUtils.isEmpty(storePictureDOList)){
            log.error("执行图片分析 异步异常 eid:{} , businessId :{}", enterpriseId, aiResultDTO.getOutBizNo());
            return;
        }

        dealCallBackResult(enterpriseId, aiResultDTO, Long.valueOf(businessId), storePictureDOMap);
    }

    @Override
    public void aiInspectionQuestionBuild(String enterpriseId, AiInspectionQuestionCreateDTO aiInspectionQuestionCreateDTO) {
        AiInspectionStrategiesDO aiInspectionStrategiesDO = aiInspectionStrategiesDAO.selectByPrimaryKey(aiInspectionQuestionCreateDTO.getInspectionId(), enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(aiInspectionQuestionCreateDTO.getEnterpriseId());
        EnterpriseQuestionSettingsDTO questionSettingsDTO = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, aiInspectionStrategiesDO.getSceneId());
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());

        List<TaskProcessDTO> process = modelAlgorithmDTO.getProcess();

        if (CollectionUtils.isEmpty(process)) {
            log.info("未配置流程 eid:{} , businessId :{}, storeId:{}, time:{}", enterpriseId, aiInspectionQuestionCreateDTO.getInspectionId(),
                    aiInspectionQuestionCreateDTO.getStoreId(), aiInspectionQuestionCreateDTO.getCaptureTime());
            return;
        }
        long remainingMillis = Duration.between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toMillis();

        //自动发起一个批次发起一次
        if (aiInspectionQuestionCreateDTO.getTicketCreateRule() != null && aiInspectionQuestionCreateDTO.getTicketCreateRule() ==
                TicketCreateRuleEnum.AUTO_ONCE_PER_PERIOD.getCode()) {
            String timeKey = Optional.ofNullable(aiInspectionQuestionCreateDTO.getBeginTime()).orElse("").replace(":", "") +
                    Optional.ofNullable(aiInspectionQuestionCreateDTO.getEndTime()).orElse("").replace(":", "");
            String lockKey = "enterprise:" + enterpriseId + ":auto_once_per_period:" + aiInspectionQuestionCreateDTO.getInspectionId() + "_"
                    + aiInspectionQuestionCreateDTO.getStoreId() + "_" + DateUtil.convert(aiInspectionQuestionCreateDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC, DateUtils.DATE_FORMAT_DAY) + timeKey;
            boolean lock = redisUtilPool.setNxExpire(lockKey, Constants.ONE_STR, (int)remainingMillis);
            if (!lock) {
                log.info("自动发起(按次)已存在 eid:{} , businessId :{}, storeId:{}, time:{}", enterpriseId, aiInspectionQuestionCreateDTO.getInspectionId(),
                        aiInspectionQuestionCreateDTO.getStoreId(), aiInspectionQuestionCreateDTO.getCaptureTime());
                return;
            }
        }

        //自动发起每次
        if (aiInspectionQuestionCreateDTO.getTicketCreateRule() != null && aiInspectionQuestionCreateDTO.getTicketCreateRule() ==
                TicketCreateRuleEnum.AUTO_ONCE_PER_DAY.getCode()) {
            String lockKey = "enterprise:" + enterpriseId + ":auto_once_per_day:" + aiInspectionQuestionCreateDTO.getInspectionId() + "_" + aiInspectionQuestionCreateDTO.getStoreId() + "_"
                    + DateUtil.convert(aiInspectionQuestionCreateDTO.getCaptureTime(), DateUtils.DATE_FORMAT_SEC, DateUtils.DATE_FORMAT_DAY);
            boolean lock = redisUtilPool.setNxExpire(lockKey, Constants.ONE_STR, (int)remainingMillis);
            if (!lock) {
                log.info("自动发起(按天)已存在 eid:{} , businessId :{}, storeId:{}, time:{}", enterpriseId, aiInspectionQuestionCreateDTO.getInspectionId(),
                        aiInspectionQuestionCreateDTO.getStoreId(), aiInspectionQuestionCreateDTO.getCaptureTime());
                return;
            }
        }
        Integer expiryTime;
        Integer expiryPolicy = modelAlgorithmDTO.getExpiryPolicy();
        if(expiryPolicy != null && expiryPolicy == 1){
            expiryTime = modelAlgorithmDTO.getExpiryTimes();
        }else {
            expiryTime = questionSettingsDTO.getAutoQuestionTaskValidity();
        }
        BuildQuestionDTO buildQuestionDTO = new BuildQuestionDTO();
        buildQuestionDTO.setStoreId(aiInspectionQuestionCreateDTO.getStoreId());
        buildQuestionDTO.setTaskName(modelAlgorithmDTO.getSceneName() + aiInspectionQuestionCreateDTO.getCaptureTime());
        buildQuestionDTO.setEndTime(org.apache.commons.lang3.time.DateUtils.addHours(new Date(), expiryTime));
        buildQuestionDTO.setProcess(process);
        buildQuestionDTO.setTaskDesc(StringUtils.join(aiInspectionQuestionCreateDTO.getErrMsgList(), ","));
        if(StringUtils.isNotBlank(buildQuestionDTO.getTaskDesc()) && buildQuestionDTO.getTaskDesc().length() > Constants.FIVE_HUNDRED){
            buildQuestionDTO.setTaskDesc(buildQuestionDTO.getTaskDesc().substring(0, Constants.FIVE_HUNDRED));
        }
        QuestionTaskInfoDTO taskInfo = new QuestionTaskInfoDTO();
        taskInfo.setPhotos(aiInspectionQuestionCreateDTO.getFailImageList());
        taskInfo.setFailPictureList(aiInspectionQuestionCreateDTO.getFailPictureList());
        taskInfo.setBusinessId(aiInspectionQuestionCreateDTO.getInspectionPeriodId());
        buildQuestionDTO.setTaskInfo(taskInfo);
        BuildQuestionRequest buildQuestionRequest = new BuildQuestionRequest();
        buildQuestionRequest.setQuestionType(QuestionTypeEnum.AI_INSPECTION.getCode());
        buildQuestionRequest.setQuestionList(Collections.singletonList(buildQuestionDTO));
        buildQuestionRequest.setTaskName(buildQuestionDTO.getTaskName());
        questionParentInfoService.buildQuestion(enterpriseId, buildQuestionRequest, Constants.AI, true, false);

    }

    @Override
    public Boolean handelDeviceCaptureCallBack(YingShiCloudRecordingMessage recordingMessage) {
        DataSourceHelper.reset();
        String taskId = recordingMessage.getBody().getTaskId();
        EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO = deviceCaptureInfoMapper.selectByTaskId(taskId);
        if(deviceCaptureInfoDO == null){
            log.info("handelDeviceCaptureCallBack无设备异步抓拍信息 taskId:{}", taskId);
            return true;
        }
        if(deviceCaptureInfoDO.getTaskResult() != 0){
            log.info("handelDeviceCaptureCallBack设备异步抓拍信息已处理完成 taskId:{}", taskId);
            return true;
        }
        String enterpriseId = deviceCaptureInfoDO.getEnterpriseId();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(deviceCaptureInfoDO.getEnterpriseId());

        String cacheLockKey = MessageFormat.format(RedisConstant.DEVICE_CAPTURE_PICTURE_RESULT_KEY, enterpriseId, deviceCaptureInfoDO.getCaptureTaskId());

        // 获取分布式锁
        boolean lockAcquired = redisUtilPool.setNxExpire(cacheLockKey, "1", 30 * 1000);
        if (lockAcquired) {
            log.debug("handelDeviceCaptureCallBack#设备正在处理中，跳过 eid:{}, deviceId:{}， taskId:{}",
                    enterpriseId, deviceCaptureInfoDO.getDeviceId(), deviceCaptureInfoDO.getCaptureTaskId());
            return true;
        }

        // 2. 根据状态处理不同情况
        if (recordingMessage.isTaskCompleted() || recordingMessage.isTaskException()) {
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            // 1. 获取视频文件信息
            VideoFileDTO fileDTO = getVideoFileInfo(enterpriseId, deviceCaptureInfoDO);
            if (fileDTO == null) {
                return true;
            }

            handleSuccessCapture(deviceCaptureInfoDO, fileDTO, config, enterpriseId);
        } else if (recordingMessage.isTaskCancelled() || recordingMessage.isTaskException()) {
            VideoFileDTO fileDTO = new VideoFileDTO();
            fileDTO.setErrorCode(recordingMessage.getBody().getErrorCode());
            fileDTO.setErrorMsg(recordingMessage.getBody().getErrorMsg());
            handleFailedCapture(deviceCaptureInfoDO, fileDTO, config, enterpriseId);
        }else {
            log.info("handelDeviceCaptureCallBack无设备异步抓拍信息处理中未开始排队中 taskId:{}", taskId);
        }
        return true;
    }

    @Async("generalThreadPool")
    @Override
    public void queryDeviceCaptureResult(String enterpriseId) {
        DataSourceHelper.reset();
        Long lastId = 0L;
        String createTime = DateUtil.format(new Date(), DateUtils.DATE_FORMAT_SEC);
        // 1. 获取企业配置和设备抓拍信息
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        boolean hasNext = true;
        int pageNum = 1;
        while (hasNext) {
            DataSourceHelper.reset();
            PageHelper.startPage(pageNum, Constants.PAGE_SIZE, false);

            List<EnterpriseDeviceCaptureInfoDO> deviceCaptureInfoDOList = deviceCaptureInfoMapper.selectList(enterpriseId, lastId, createTime);

            if (CollectionUtils.isEmpty(deviceCaptureInfoDOList)) {
                log.info("无设备异步抓拍信息 eid:{}", enterpriseId);
                return;
            }
            if (deviceCaptureInfoDOList.size() < Constants.PAGE_SIZE) {
                hasNext = false;
            }
            //更新lastId为当前页最后一条记录的ID
            lastId = deviceCaptureInfoDOList.get(deviceCaptureInfoDOList.size() - 1).getId();

            // 2. 切换到特定数据源并处理每个设备
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            for (EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO : deviceCaptureInfoDOList) {
                try {
                    String cacheLockKey = MessageFormat.format(RedisConstant.DEVICE_CAPTURE_PICTURE_RESULT_KEY, enterpriseId, deviceCaptureInfoDO.getCaptureTaskId());

                    // 获取分布式锁
                    boolean lockAcquired = redisUtilPool.setNxExpire(cacheLockKey, "1", 30 * 1000);
                    if (lockAcquired) {
                        try {
                            DataSourceHelper.reset();
                            deviceCaptureInfoDO = deviceCaptureInfoMapper.selectByTaskId(deviceCaptureInfoDO.getCaptureTaskId());
                            if (deviceCaptureInfoDO.getTaskResult() != 0) {
                                log.info("handelDeviceCaptureCallBack设备异步抓拍信息已处理完成 taskId:{}", deviceCaptureInfoDO.getCaptureTaskId());
                                continue;
                            }
                            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                            processSingleDeviceCapture(deviceCaptureInfoDO, config, enterpriseId);
                        } finally {
                            redisUtilPool.delKey(cacheLockKey);
                        }
                    } else {
                        log.debug("queryDeviceCaptureResult#设备正在处理中，跳过 eid:{}, deviceId:{}， taskId:{}",
                                enterpriseId, deviceCaptureInfoDO.getDeviceId(), deviceCaptureInfoDO.getCaptureTaskId());
                    }
                } catch (Exception e) {
                    log.error("queryDeviceCaptureResult处理设备抓拍结果异常 eid:{}", enterpriseId, e);
                }
            }
        }
    }

    /**
     * 处理单个设备的抓拍结果
     */
    private void processSingleDeviceCapture(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO,
                                            EnterpriseConfigDO config, String enterpriseId) {
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());

        // 1. 获取视频文件信息
        VideoFileDTO fileDTO = getVideoFileInfo(enterpriseId, deviceCaptureInfoDO);
        if (fileDTO == null) {
            return;
        }

        // 2. 根据状态处理不同情况
        if (Constants.INDEX_ZERO.equals(fileDTO.getStatus())) {
            handleSuccessCapture(deviceCaptureInfoDO, fileDTO, config, enterpriseId);
        } else if (Constants.INDEX_TWO.equals(fileDTO.getStatus())) {
            handleFailedCapture(deviceCaptureInfoDO, fileDTO, config, enterpriseId);
        } else if (Constants.INDEX_ONE.equals(fileDTO.getStatus())) {
            handleProcessingCapture(deviceCaptureInfoDO, config, enterpriseId);
        }
    }

    /**
     * 获取视频文件信息
     */
    private VideoFileDTO getVideoFileInfo(String enterpriseId, EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO) {
        try {
            return videoServiceApi.getVideoFile(enterpriseId, deviceCaptureInfoDO.getDeviceId(),
                    deviceCaptureInfoDO.getCaptureTaskId());
        } catch (Exception e) {
            log.error("获取设备异步失败deviceId:{}", deviceCaptureInfoDO.getDeviceId(), e);
            return null;
        }
    }

    /**
     * 处理成功的抓拍结果
     */
    private void handleSuccessCapture(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO,
                                      VideoFileDTO fileDTO, EnterpriseConfigDO config,
                                      String enterpriseId) {
        // 设置成功状态
        deviceCaptureInfoDO.setTaskResult(1);

        // 获取并处理图片文件
        List<TaskFileDTO> taskFiles = getTaskFiles(enterpriseId, deviceCaptureInfoDO);
        if (CollectionUtils.isEmpty(taskFiles)) {
            log.info("无图片 eid:{} , taskId:{}", enterpriseId, deviceCaptureInfoDO.getCaptureTaskId());
            return;
        }

        // 上传图片并更新URL
        String imageUrl = uploadAndGetImageUrl(taskFiles.get(0), enterpriseId);
        deviceCaptureInfoDO.setPicUrl(imageUrl);
        deviceCaptureInfoDO.setErrorCode(fileDTO.getErrorCode());
        deviceCaptureInfoDO.setErrorMsg(fileDTO.getErrorMsg());
        deviceCaptureInfoDO.setFileId(fileDTO.getFileId());

        // 更新数据库
        updateDeviceCaptureInfo(deviceCaptureInfoDO);

        // 处理业务相关结果
        handleBusinessResult(deviceCaptureInfoDO, config, enterpriseId, imageUrl, AiStatusEnum.CAPTURE_SUCCESS, null);
    }

    /**
     * 处理失败的抓拍结果
     */
    private void handleFailedCapture(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO,
                                     VideoFileDTO fileDTO, EnterpriseConfigDO config,
                                     String enterpriseId) {
        deviceCaptureInfoDO.setTaskResult(Constants.INDEX_TWO);
        deviceCaptureInfoDO.setErrorCode(fileDTO.getErrorCode());
        deviceCaptureInfoDO.setErrorMsg(fileDTO.getErrorMsg());

        updateDeviceCaptureInfo(deviceCaptureInfoDO);
        handleBusinessResult(deviceCaptureInfoDO, config, enterpriseId, null, AiStatusEnum.CAPTURE_FAILED, fileDTO.getErrorMsg());
    }

    /**
     * 处理进行中的抓拍结果（超时检查）
     */
    private void handleProcessingCapture(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO,
                                         EnterpriseConfigDO config, String enterpriseId) {
        // 检查是否超时（10分钟）
        if (System.currentTimeMillis() - deviceCaptureInfoDO.getCreateTime().getTime() > Constants.TEN_MINUTE) {
            deviceCaptureInfoDO.setTaskResult(Constants.INDEX_THREE);
            deviceCaptureInfoDO.setErrorMsg("图片返回超时失败");
            updateDeviceCaptureInfo(deviceCaptureInfoDO);
            handleBusinessResult(deviceCaptureInfoDO, config, enterpriseId, null, AiStatusEnum.CAPTURE_FAILED, "图片返回超时失败");
        }
    }

    /**
     * 获取任务文件列表
     */
    private List<TaskFileDTO> getTaskFiles(String enterpriseId, EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO) {
        try {
            return videoServiceApi.getTaskFiles(enterpriseId, deviceCaptureInfoDO.getDeviceId(),
                    deviceCaptureInfoDO.getCaptureTaskId());
        } catch (Exception e) {
            log.error("获取图片失败getTaskFiles eid:{} , taskId:{}, deviceId:{}", enterpriseId,
                    deviceCaptureInfoDO.getCaptureTaskId(), deviceCaptureInfoDO.getDeviceId(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 上传图片并获取URL
     */
    private String uploadAndGetImageUrl(TaskFileDTO taskFileDTO, String enterpriseId) {
        FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(taskFileDTO.getUrl(), enterpriseId, null);
        return fileUploadParam.getServer() + fileUploadParam.getFileNewName();
    }

    /**
     * 更新设备抓拍信息
     */
    private void updateDeviceCaptureInfo(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO) {
        DataSourceHelper.reset();
        deviceCaptureInfoMapper.updateByPrimaryKeySelective(deviceCaptureInfoDO);
    }

    /**
     * 处理业务相关结果
     */
    private void handleBusinessResult(EnterpriseDeviceCaptureInfoDO deviceCaptureInfoDO,
                                      EnterpriseConfigDO config, String enterpriseId,
                                      String imageUrl, AiStatusEnum status, String errorMsg) {
        if (deviceCaptureInfoDO.getBusinessType().equals(DeviceCaptureBusinessTypeEnum.AI_INSPECTION.getCode())) {
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            handelCaptureResult(enterpriseId, Long.valueOf(deviceCaptureInfoDO.getBusinessId()),
                    imageUrl, config.getDbName(), status, errorMsg);
        }
    }

    private void handelCaptureResult(String enterpriseId, Long id, String picUrl, String dbName, AiStatusEnum aiStatusEnum, String errorMsg){
        AiInspectionStorePictureDO aiInspectionStorePictureDO = aiInspectionStorePictureDAO.selectById(enterpriseId, id);
        aiInspectionStorePictureDO.setPicture(picUrl);
        aiInspectionStorePictureDO.setAiStatus(aiStatusEnum.getCode());
        aiInspectionStorePictureDO.setAiFailReason(errorMsg);
        DataSourceHelper.reset();
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, aiInspectionStorePictureDO.getSceneId());
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelAlgorithmDTO.getModelCode());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //同步执行
        if (aiModel.getSyncGetResult() != null && aiModel.getSyncGetResult()) {
            if(AiStatusEnum.CAPTURE_SUCCESS.getCode().equals(aiStatusEnum.getCode())){
                //抓拍成功，进行AI分析
                handelAIResult(enterpriseId, aiInspectionStorePictureDO, aiInspectionStorePictureDO.getSceneId());
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
            AiInspectionStrategiesDO aiInspectionStrategiesDO = aiInspectionStrategiesDAO.selectByPrimaryKey(aiInspectionStorePictureDO.getInspectionId(), enterpriseId);
            AiInspectionStrategiesExtendInfo extendInfoConfig = new AiInspectionStrategiesExtendInfo();
            if(StringUtils.isNotBlank(aiInspectionStrategiesDO.getExtendInfo())){
                extendInfoConfig = JSONObject.parseObject(aiInspectionStrategiesDO.getExtendInfo(), AiInspectionStrategiesExtendInfo.class);
            }
            aiInspectionStorePictureDAO.updateByPrimaryKeySelective(aiInspectionStorePictureDO, enterpriseId);
            //计算是否全部计算AI分析完成，没有在进行中的抓拍任务
            AiInspectionStorePeriodDO aiInspectionStorePeriodDO = aiInspectionStorePeriodDAO.selectByPrimaryKey(enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());
            List<AiInspectionStorePictureDO> storePictureDOList = aiInspectionStorePictureDAO.selectListByPeriodId(enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());
            Set<Integer> targetStatusSet = Stream.of(
                    AiStatusEnum.NOT_EXECUTED.getCode(),
                    AiStatusEnum.ANALYZING.getCode(),
                    AiStatusEnum.CAPTURE_IN_PROGRESS.getCode(),
                    AiStatusEnum.CAPTURE_SUCCESS.getCode()
            ).collect(Collectors.toSet());
            boolean notAllCompleted = storePictureDOList.stream().anyMatch(pictureDO -> targetStatusSet.contains(pictureDO.getAiStatus()));
            if (notAllCompleted) {
                log.info("抓拍任务未完成全部分析等待下次抓拍 eid:{} , periodId:{}", enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());
                return;
            }
            Date captureTime = aiInspectionStorePeriodDO.getCaptureTime();

            LocalDateTime captureDateTime = captureTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 格式化小时和分钟
            String hourTime = captureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String beginTime = hourTime;
            String endTime = hourTime;

            AiInspectionTimePeriodDO aiInspectionTimePeriodDO = aiInspectionTimePeriodMapper.getMatchingStrategyWithPeriod(enterpriseId, hourTime, aiInspectionStorePeriodDO.getInspectionId());
            if(aiInspectionTimePeriodDO != null){
                beginTime = aiInspectionTimePeriodDO.getBeginTime();
                endTime = aiInspectionTimePeriodDO.getEndTime();
            }
            //计算已经更新结果
            this.updateAiInspectionResult(enterpriseId, storePictureDOList, aiInspectionStorePeriodDO, extendInfoConfig,
                    beginTime, endTime);

            //处理工单流程
            this.handleInspectionResultAndCreateTicket(aiInspectionStorePeriodDO, aiInspectionStrategiesDO,
                    storePictureDOList, enterpriseId, beginTime, endTime, extendInfoConfig);
        }else {
            aiInspectionStorePictureDAO.updateByPrimaryKeySelective(aiInspectionStorePictureDO, enterpriseId);
            List<AiInspectionStorePictureDO> storePictureDOList = aiInspectionStorePictureDAO.selectListByPeriodId(enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());

            //计算是否全部抓拍完成，没有在进行中的抓拍任务
            Set<Integer> targetStatusSet = Stream.of(
                    AiStatusEnum.CAPTURE_FAILED.getCode(),
                    AiStatusEnum.CAPTURE_SUCCESS.getCode()
            ).collect(Collectors.toSet());
            boolean allCompleted = storePictureDOList.stream().allMatch(pictureDO -> targetStatusSet.contains(pictureDO.getAiStatus()));
            if (!allCompleted) {
                log.info("抓拍任务未完成全部分析等待下次抓拍异步提交 eid:{} , periodId:{}", enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());
                return;
            }
            List<AiInspectionStorePictureDO> pictureDOList = storePictureDOList.stream().filter(pictureDO -> StringUtils.isNotBlank(pictureDO.getPicture())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(pictureDOList)){
                //没有抓拍到 图片
                AiInspectionStorePeriodDO aiInspectionStorePeriodDO = aiInspectionStorePeriodDAO.selectByPrimaryKey(enterpriseId, aiInspectionStorePictureDO.getInspectionPeriodId());
                aiInspectionStorePeriodDO.setAiPeriodResult(MetaTableConstant.CheckResultConstant.INAPPLICABLE);
                aiInspectionStorePeriodDAO.updateByPrimaryKeySelective(aiInspectionStorePeriodDO, enterpriseId);
                aiInspectionStorePictureDAO.updateResultByPeriodId(enterpriseId, aiInspectionStorePeriodDO.getAiPeriodResult(), aiInspectionStorePeriodDO.getId());
            }
            List<String> pictureUrlList = pictureDOList.stream().map(AiInspectionStorePictureDO::getPicture).collect(Collectors.toList());
            pictureDOList.forEach(pictureDO -> {
                pictureDO.setAiStatus(AiStatusEnum.ANALYZING.getCode());
            });
            try {
                AIResolveDTO aiResolveDTO = aiService.aiAsyncInspectionResolve(enterpriseId, aiInspectionStorePictureDO.getSceneId(), pictureUrlList, aiInspectionStorePictureDO.getInspectionPeriodId());
                log.info("handelCaptureResult#执行图片分析异步结果 eid:{} , businessId :{} storeId : {}, CaptureTime:{}, result:{}",
                        enterpriseId, aiInspectionStorePictureDO.getInspectionId(), aiInspectionStorePictureDO.getStoreId(), aiInspectionStorePictureDO.getCaptureTime(), JSONObject.toJSONString(aiResolveDTO));
                DataSourceHelper.changeToSpecificDataSource(dbName);
                aiInspectionStorePictureDAO.batchUpdate(enterpriseId,storePictureDOList);
            }catch (Exception e){
                log.error("handelCaptureResult#执行图片分析异步异常 eid:{} , businessId :{} storeId : {}, CaptureTime:{}", enterpriseId, aiInspectionStorePictureDO.getInspectionId(), aiInspectionStorePictureDO.getStoreId(), aiInspectionStorePictureDO.getCaptureTime(), e);
                pictureDOList.forEach(pictureDO -> {
                    pictureDO.setAiStatus(AiStatusEnum.FAILED.getCode());
                });
                DataSourceHelper.changeToSpecificDataSource(dbName);
                aiInspectionStorePictureDAO.batchUpdate(enterpriseId, pictureDOList);
            }
        }
    }


    void dealCallBackResult(String enterpriseId, ShuZhiMaLiGetAiResultDTO aiResultDTO, Long periodId, Map<Long, List<AiInspectionStorePictureDO>> storePictureDOMap) {
        List<ShuZhiMaLiGetAiResultDTO.InspectResult> inspectResultList = aiResultDTO.getInspectResult();
        if(CollectionUtils.isNotEmpty(inspectResultList)){
            Map<String, String> resultMap = inspectResultList.stream().collect(Collectors.toMap(ShuZhiMaLiGetAiResultDTO.InspectResult::getFilePath, ShuZhiMaLiGetAiResultDTO.InspectResult::getCheckResult));
            List<AiInspectionStorePictureDO> pictureList = storePictureDOMap.get(periodId);
            pictureList.forEach(pictureDO -> {
                String result = resultMap.get(pictureDO.getPicture());
                if (result != null && MetaTableConstant.CheckResultNameConstant.PASS_NAME.equals(result)) {
                    pictureDO.setAiResult( MetaTableConstant.CheckResultConstant.PASS);
                    pictureDO.setAiStatus(AiStatusEnum.COMPLETED.getCode());
                }
                if (result != null && MetaTableConstant.CheckResultNameConstant.FAIL_NAME.equals(result)) {
                    pictureDO.setAiResult( MetaTableConstant.CheckResultConstant.FAIL);
                    pictureDO.setAiStatus(AiStatusEnum.COMPLETED.getCode());
                }
                if (result != null && "违规".equals(result)) {
                    pictureDO.setAiResult( MetaTableConstant.CheckResultConstant.FAIL);
                    pictureDO.setAiStatus(AiStatusEnum.COMPLETED.getCode());

                }
                if (result != null && "检测失败".equals(result)) {
                    pictureDO.setAiResult( MetaTableConstant.CheckResultConstant.FAIL);
                    pictureDO.setAiStatus(AiStatusEnum.FAILED.getCode());
                }
                aiInspectionStorePictureDAO.updateByPrimaryKeySelective(pictureDO, enterpriseId);
            });
            AiInspectionStorePeriodDO aiInspectionStorePeriodDO = aiInspectionStorePeriodDAO.selectByPrimaryKey(enterpriseId, periodId);
            List<AiInspectionStorePictureDO> storePictureDOList = aiInspectionStorePictureDAO.selectListByPeriodId(enterpriseId, periodId);
            AiInspectionStrategiesDO aiInspectionStrategiesDO = aiInspectionStrategiesDAO.selectByPrimaryKey(aiInspectionStorePeriodDO.getInspectionId(), enterpriseId);

            AiInspectionStrategiesExtendInfo extendInfoConfig = new AiInspectionStrategiesExtendInfo();
            if(StringUtils.isNotBlank(aiInspectionStrategiesDO.getExtendInfo())){
                extendInfoConfig = JSONObject.parseObject(aiInspectionStrategiesDO.getExtendInfo(), AiInspectionStrategiesExtendInfo.class);
            }
            Date captureTime = aiInspectionStorePeriodDO.getCaptureTime();

            LocalDateTime captureDateTime = captureTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 格式化小时和分钟
            String hourTime = captureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String beginTime = hourTime;
            String endTime = hourTime;

            AiInspectionTimePeriodDO aiInspectionTimePeriodDO = aiInspectionTimePeriodMapper.getMatchingStrategyWithPeriod(enterpriseId, hourTime, aiInspectionStorePeriodDO.getInspectionId());
            if(aiInspectionTimePeriodDO != null){
                beginTime = aiInspectionTimePeriodDO.getBeginTime();
                endTime = aiInspectionTimePeriodDO.getEndTime();
            }
            //总体检测合格
            //计算以及更新结果
            this.updateAiInspectionResult(enterpriseId, storePictureDOList, aiInspectionStorePeriodDO, extendInfoConfig,
                    beginTime, endTime);
            //处理工单流程
            this.handleInspectionResultAndCreateTicket(aiInspectionStorePeriodDO, aiInspectionStrategiesDO,
                    storePictureDOList, enterpriseId, beginTime, endTime,
                    extendInfoConfig);

        }
    }

    /**
     * 处理AI巡检结果并创建工单
     */
    private void handleInspectionResultAndCreateTicket(AiInspectionStorePeriodDO aiInspectionStorePeriodDO,
                                                       AiInspectionStrategiesDO aiInspectionStrategiesDO,
                                                       List<AiInspectionStorePictureDO> storePictureDOList,
                                                       String enterpriseId,
                                                       String beginTime,
                                                       String endTime,
                                                       AiInspectionStrategiesExtendInfo extendInfoConfig) {

        // 检查是否需要创建工单
        if (!MetaTableConstant.CheckResultConstant.FAIL.equals(aiInspectionStorePeriodDO.getAiPeriodResult())
                || StringUtils.isBlank(aiInspectionStrategiesDO.getExtendInfo())) {
            return;
        }

        // 获取工单创建规则
        Integer ticketCreateRule = extendInfoConfig.getTicketCreateRule();
        if (ticketCreateRule == null) {
            ticketCreateRule = 0;
        }

        log.info("执行图片分析 检测结果:{}, ticketCreateRule:{}", aiInspectionStorePeriodDO.getAiPeriodResult(), ticketCreateRule);

        // 检查是否启用工单创建
        if (ticketCreateRule == 0) {
            return;
        }

        log.info("执行图片分析 发起工单 eid:{}, businessId:{}, storeId:{}, CaptureTime:{}",
                enterpriseId, aiInspectionStrategiesDO.getId(), aiInspectionStorePeriodDO.getStoreId(), aiInspectionStorePeriodDO.getCaptureTime());

        // 处理失败图片信息
        List<AiInspectionStorePictureDO> failPictureDOList = storePictureDOList.stream()
                .filter(pictureDO -> Objects.equals(pictureDO.getAiResult(), MetaTableConstant.CheckResultConstant.FAIL))
                .collect(Collectors.toList());

        List<String> errMsgList = new ArrayList<>();
        List<AiInspectionStoreFailPictureDTO> failPictureDTOList = failPictureDOList.stream().map(pictureDO -> {
            AiInspectionStoreFailPictureDTO aiInspectionStoreFailPictureDTO = new AiInspectionStoreFailPictureDTO();
            aiInspectionStoreFailPictureDTO.setPicture(pictureDO.getPicture());
            if (StringUtils.isNotBlank(pictureDO.getAiFailReason()) && AIHelper.isJsonValid(pictureDO.getAiFailReason())) {
                JSONObject jsonObject = JSONObject.parseObject(pictureDO.getAiFailReason());
                aiInspectionStoreFailPictureDTO.setMessage(jsonObject.getString("message"));
                errMsgList.add(jsonObject.getString("message"));
            } else {
                if (!errMsgList.contains(Constants.FAIL_MESSAGE)) {
                    errMsgList.add(Constants.FAIL_MESSAGE);
                }
                aiInspectionStoreFailPictureDTO.setMessage(Constants.FAIL_MESSAGE);
            }
            return aiInspectionStoreFailPictureDTO;
        }).collect(Collectors.toList());

        // 构建工单创建DTO
        AiInspectionQuestionCreateDTO aiInspectionQuestionCreateDTO = new AiInspectionQuestionCreateDTO();
        aiInspectionQuestionCreateDTO.setInspectionId(aiInspectionStrategiesDO.getId());
        aiInspectionQuestionCreateDTO.setInspectionPeriodId(aiInspectionStorePeriodDO.getId());
        aiInspectionQuestionCreateDTO.setSceneId(aiInspectionStrategiesDO.getSceneId());
        aiInspectionQuestionCreateDTO.setEnterpriseId(enterpriseId);
        aiInspectionQuestionCreateDTO.setCaptureTime(DateUtils.convertTimeToString(aiInspectionStorePeriodDO.getCaptureTime().getTime(), DateUtils.DATE_FORMAT_SEC));
        aiInspectionQuestionCreateDTO.setFailImageList(storePictureDOList.stream()
                .filter(pictureDO -> Objects.equals(pictureDO.getAiResult(), MetaTableConstant.CheckResultConstant.FAIL))
                .map(AiInspectionStorePictureDO::getPicture)
                .collect(Collectors.toList()));
        aiInspectionQuestionCreateDTO.setTicketCreateRule(ticketCreateRule);
        aiInspectionQuestionCreateDTO.setStoreId(aiInspectionStorePeriodDO.getStoreId());
        aiInspectionQuestionCreateDTO.setBeginTime(beginTime);
        aiInspectionQuestionCreateDTO.setEndTime(endTime);
        aiInspectionQuestionCreateDTO.setFailPictureList(failPictureDTOList);
        aiInspectionQuestionCreateDTO.setErrMsgList(errMsgList);

        // 发送工单创建消息
        simpleMessageService.send(JSONObject.toJSONString(aiInspectionQuestionCreateDTO), RocketMqTagEnum.AI_INSPECTION_STORE_QUESTION);
    }

    /**
     * 单次判断当前时间是否在间隔轮次执行中
     *
     * @param currentTime 当前时间 (HH:mm)
     * @param beginTime   开始时间 (HH:mm)
     * @param endTime     结束时间 (HH:mm)
     * @param period      间隔时间 (分钟)
     * @return 是否在轮次执行中
     */
    public static boolean isInPeriodRound(String currentTime, String beginTime, String endTime, Integer period) {
        try {
            LocalTime current = parseTime(currentTime);
            LocalTime begin = parseTime(beginTime);
            LocalTime end = parseEndTime(endTime);

            // 1. 检查是否在总时间范围内
            if (current.isBefore(begin) || current.isAfter(end)) {
                return false;
            }

            // 2. 如果没有设置间隔时间，直接返回false（必须严格匹配轮次）
            if (period == null || period <= 0) {
                return false;
            }

            // 3. 计算从开始时间到当前时间的总分钟数
            long totalMinutes = Duration.between(begin, current).toMinutes();

            // 4. 检查是否能被间隔整除（即是否正好在轮次边界上）
            return totalMinutes % period == 0;

        } catch (Exception e) {
            log.error("isExactlyOnPeriodRound#时间轮次判断失败: currentTime={}, beginTime={}, endTime={}, period={}",
                    currentTime, beginTime, endTime, period, e);
            return false;
        }
    }

    private static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr);
    }
    /**
     * 解析结束时间（支持 "24:00"）
     */
    private static LocalTime parseEndTime(String endTimeStr) {
        if ("24:00".equals(endTimeStr)) {
            return LocalTime.MAX;
        }
        return LocalTime.parse(endTimeStr);
    }

    public void deviceCapturePicture(String enterpriseId, AiInspectionTimePeriodStoreDealDTO timePeriodStoreDealDTO,
                                     AiInspectionStrategiesDO aiInspectionStrategiesDO,
                                     StoreDO storeDO,
                                     String currentTime,
                                     List<AiInspectionStorePictureDO> storePictureDOList,
                                     Long deviceId, Long deviceChannelId, Long storeSceneId,
                                     String deviceNo, String channelNo,
                                     DeviceDO deviceDO) {
        AiInspectionStorePictureDO storePictureDO = AiInspectionStorePictureDO.builder()
                .inspectionId(aiInspectionStrategiesDO.getId())
                .sceneId(aiInspectionStrategiesDO.getSceneId())
                .sceneName(aiInspectionStrategiesDO.getSceneName())
                .storeId(timePeriodStoreDealDTO.getStoreId())
                .storeName(storeDO.getStoreName())
                .regionId(storeDO.getRegionId())
                .regionPath(storeDO.getRegionPath())
                .captureDate(DateUtil.parse(currentTime, DateUtils.DATE_FORMAT_SEC))
                .captureTime(org.apache.commons.lang3.time.DateUtils.truncate(DateUtil.parse(currentTime, DateUtils.DATE_FORMAT_SEC), Calendar.SECOND))
                .deviceId(deviceId)
                .deviceChannelId(deviceChannelId)
                .createUserId("")
                .storeSceneId(storeSceneId)
                .createTime(new Date())
                .updateTime(new Date())
                .aiStatus(AiStatusEnum.NOT_EXECUTED.getCode())
                .deleted(false)
                .build();
        storePictureDOList.add(storePictureDO);
        try {
            String time = DateUtil.convert(currentTime, DateUtils.DATE_FORMAT_SEC, DateUtils.DATE_FORMAT_SEC_8);
            String cacheKey = MessageFormat.format(RedisConstant.DEVICE_CAPTURE_PICTURE_KEY, enterpriseId, deviceId + "_" + deviceChannelId + "_" + time);
            String cacheLockKey = MessageFormat.format(RedisConstant.DEVICE_CAPTURE_LOCK_PICTURE_KEY, enterpriseId, deviceId + "_" + deviceChannelId + "_" + time);

            // 1. 先尝试从Redis获取缓存的URL
            String url = redisUtilPool.getString(cacheKey);
            log.info("deviceCapturePicture#设备抓拍图片: deviceNo={}, channelNo={}, time={}, cacheKey:{}, url:{}", deviceNo, channelNo, time, cacheKey, url);

            // 2. 如果缓存中没有，则调用视频服务API
            if (StringUtils.isBlank(url)) {
                // 1. 获取分布式锁（设置锁自动过期）
                try {
                    boolean lockAcquired = redisUtilPool.setNxExpire(cacheLockKey, "1", 5 * 1000);
                    if (!lockAcquired) {
                        Thread.sleep(5 * 1000);
                        url = redisUtilPool.getString(cacheKey);
                    }
                    if (StringUtils.isBlank(url)) {
                        //编辑门店校验全局唯一
                        String videoUrl = videoServiceApi.capture(enterpriseId, deviceNo, channelNo, null);
                        log.info("deviceCapturePicture#设备抓拍图片: deviceNo={}, channelNo={}, time={}, videoUrl:{}", deviceNo, channelNo, time, videoUrl);
                        if (StringUtils.isNotBlank(videoUrl)) {
                            FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(videoUrl, enterpriseId, null);
                            url = fileUploadParam.getServer() + fileUploadParam.getFileNewName();
                        }
                    }
                } finally {
                    redisUtilPool.delKey(cacheKey);
                }
                // 3. 如果获取成功，缓存到Redis
                if (StringUtils.isNotBlank(url)) {
                    redisUtilPool.setString(cacheKey, url, 60 * 5);
                }
            }
            if (StringUtils.isNotBlank(url)) {
                storePictureDO.setPicture(url);
                storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_SUCCESS.getCode());
            } else {
                storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_FAILED.getCode());
            }
        } catch (Exception e) {
            storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_FAILED.getCode());
            log.error("deviceCapturePicture抓取设备图片失败, eid:{} , deviceNo : {}, channelNo:{}", enterpriseId, deviceNo, channelNo, e);
        }
        // 抓拍失败且设备为萤石国标时，尝试进行抽帧
        if (YunTypeEnum.YINGSHIYUN_GB.getCode().equals(deviceDO.getResource())
                && Objects.equals(AiStatusEnum.CAPTURE_FAILED.getCode(), storePictureDO.getAiStatus())) {
            try {
                String taskId = videoServiceApi.captureByTime(enterpriseId, deviceNo, channelNo, Collections.singletonList(currentTime));
                log.info("captureByTime#已发起萤石国标抽帧任务, taskId:{}, eid:{}, deviceId:{}, channelNo:{}, currentTime:{}",
                        taskId, enterpriseId, deviceNo, channelNo, currentTime);
                if (StringUtils.isNotBlank(taskId)) {
                    storePictureDO.setCaptureTaskId(taskId);
                    storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_IN_PROGRESS.getCode());
                    DataSourceHelper.reset();

                } else {
                    storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_FAILED.getCode());
                    storePictureDO.setAiFailReason("抽帧接口提交失败");
                }
            } catch (Exception e) {
                storePictureDO.setAiStatus(AiStatusEnum.CAPTURE_FAILED.getCode());
                storePictureDO.setAiFailReason("抽帧接口提交失败");
                log.error("captureByTime#抓取设备图片失败, eid:{}, deviceId:{}, errMsg:{}",
                        enterpriseId, deviceNo, e.getMessage(), e);
            }
        }

    }
}
