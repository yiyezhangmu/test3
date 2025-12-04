package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.objectdet20191230.models.DetectWorkwearResponseBody;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CapturePictureTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolAITypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMqInformConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStorePictureMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.AiPictureResultMappingDao;
import com.coolcollege.intelligent.mapper.store.StoreSceneDAO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.AICommentStyleEnum;
import com.coolcollege.intelligent.model.enums.ScheduleCallBackEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.dto.AiPictureResultMappingDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreCapturePictureDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePictureDTO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSignInParam;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSignOutParam;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitParam;
import com.coolcollege.intelligent.model.patrolstore.vo.TbPatrolStorePictureInfoVO;
import com.coolcollege.intelligent.model.patrolstore.vo.TbPatrolStorePictureVO;
import com.coolcollege.intelligent.model.region.dto.PatrolStorePictureMsgDTO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.ai.*;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStorePictureService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author byd
 * @date 2021-08-27 13:51
 */
@Slf4j
@Service
public class PatrolStorePictureServiceImpl implements PatrolStorePictureService {

    @Resource
    private TbPatrolStorePictureMapper tbPatrolStorePictureMapper;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    @Resource
    private StoreSceneDAO storeSceneDAO;

    @Resource
    private TbDataStaTableColumnMapper dataStaTableColumnMapper;

    @Resource
    private DeviceService deviceService;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Autowired
    private PatrolAIService patrolAIService;

    @Autowired
    private HikvisionAIService hikvisionAIService;

    @Autowired
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Autowired
    private AiPictureResultMappingDao aiPictureResultMappingDao;

    @Autowired
    private PatrolStoreService patrolStoreService;

    @Autowired
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private EnterpriseConfigMapper configMapper;

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseMqInformConfigMapper enterpriseMqInformConfigMapper;

    @Autowired
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Resource
    private AIService aiService;

    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Resource
    private AiModelLibraryService aiModelLibraryService;


    @Override
    public void uploadPicture(String eid, TbPatrolStorePictureDTO pictureDTO) {
        List<String> pictureList = pictureDTO.getPictureList();
        List<TbPatrolStorePictureDO> list = new ArrayList<>();
        pictureList.forEach(picture -> {
            TbPatrolStorePictureDO tbPatrolStorePictureDO =
                    TbPatrolStorePictureDO.builder().storeSceneId(pictureDTO.getStoreSceneId()).businessId(pictureDTO.getBusinessId())
                            .picture(picture).deleted(false).createTime(new Date()).updateTime(new Date())
                            .deviceId(0L).deviceChannelId(0L).build();
            list.add(tbPatrolStorePictureDO);
        });
        tbPatrolStorePictureMapper.batchInsert(eid, list);
    }

    @Override
    public TbPatrolStorePictureVO getStoreScenePictureList(String eid, Long businessId, Long storeSceneId) {
        TbPatrolStorePictureVO tbPatrolStorePictureVO = new TbPatrolStorePictureVO();
        List<TbPatrolStorePictureDO> pictureList = tbPatrolStorePictureMapper.getStoreScenePictureList(eid, businessId, storeSceneId);
        List<TbPatrolStorePictureInfoVO> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(pictureList)){
            Set<Long> deviceIdList = pictureList.stream().map(TbPatrolStorePictureDO::getDeviceId).filter(deviceId -> deviceId > 0).collect(Collectors.toSet());
            Set<Long> deviceChannelIdList = pictureList.stream().map(TbPatrolStorePictureDO::getDeviceChannelId).filter(deviceChannelId -> deviceChannelId > 0).collect(Collectors.toSet());
            Set<Long> storeSceneIdList = pictureList.stream().map(TbPatrolStorePictureDO::getStoreSceneId).collect(Collectors.toSet());
            Map<Long, String> sceneNameMap = storeSceneDAO.getStoreSceneNameMap(eid, new ArrayList<>(storeSceneIdList));
            Map<Long, DeviceDO> deviceNameMap = new HashMap<>();
            Map<Long, DeviceChannelDO> deviceChannelMap = new HashMap<>();

            if(CollectionUtils.isNotEmpty(deviceIdList)){
                List<DeviceDO> deviceList = deviceMapper.getDeviceByIdList(eid, new ArrayList<>(deviceIdList));
                deviceNameMap = deviceList.stream().collect(Collectors.toMap(DeviceDO::getId, data -> data, (a, b) -> a));
            }
            if(CollectionUtils.isNotEmpty(deviceChannelIdList)){
                List<DeviceChannelDO> deviceChannelList = deviceChannelMapper.listDeviceChannelByIdList(eid, new ArrayList<>(deviceChannelIdList));
                deviceChannelMap = deviceChannelList.stream().collect(Collectors.toMap(DeviceChannelDO::getId, data -> data, (a, b) -> a));
            }

            Map<Long, String> finalSceneNameMap = sceneNameMap;
            Map<Long, DeviceDO> finalDeviceNameMap = deviceNameMap;
            Map<Long, DeviceChannelDO> finalDeviceChannelMap = deviceChannelMap;
            pictureList.forEach(picture -> {
                TbPatrolStorePictureInfoVO tbPatrolStorePictureInfoVO = new TbPatrolStorePictureInfoVO();
                tbPatrolStorePictureInfoVO.setId(picture.getId());
                tbPatrolStorePictureInfoVO.setPicture(picture.getPicture());
                tbPatrolStorePictureInfoVO.setStoreSceneId(picture.getStoreSceneId());
                tbPatrolStorePictureInfoVO.setBusinessId(picture.getBusinessId());
                tbPatrolStorePictureInfoVO.setDeviceId(picture.getDeviceId());
                tbPatrolStorePictureInfoVO.setDeviceChannelId(picture.getDeviceChannelId());
                tbPatrolStorePictureInfoVO.setRemark(picture.getRemark());
                tbPatrolStorePictureInfoVO.setStoreSceneName(finalSceneNameMap.get(picture.getStoreSceneId()));
                DeviceDO deviceDO  = finalDeviceNameMap.get(picture.getDeviceId());
                DeviceChannelDO deviceChannelDO = finalDeviceChannelMap.get(picture.getDeviceChannelId());
                String name = null;
                String devceNo = null;
                String channelNo = null;
                if(deviceDO != null){
                    name = deviceDO.getDeviceName();
                    devceNo = deviceDO.getDeviceId();
                }
                if(deviceChannelDO != null){
                    name = deviceChannelDO.getChannelName();
                    devceNo = deviceChannelDO.getParentDeviceId();
                    channelNo = deviceChannelDO.getChannelNo();
                }
                tbPatrolStorePictureInfoVO.setDeviceName(name);
                tbPatrolStorePictureInfoVO.setChannelNo(channelNo);
                tbPatrolStorePictureInfoVO.setDeviceNo(devceNo);
                resultList.add(tbPatrolStorePictureInfoVO);
            });
        }
        tbPatrolStorePictureVO.setPictureList(resultList);

        String key = redisConstantUtil.getCapturePicture(eid + "_" + businessId
                + "_" + (storeSceneId == null ? 0 : storeSceneId));
        String captureStatus = redisUtilPool.getString(key);
        //
        if(Constants.STRING_INDEX_ONE.equals(captureStatus)){
            tbPatrolStorePictureVO.setCaptureStatus(Constants.INDEX_ONE);
        }else {
            tbPatrolStorePictureVO.setCaptureStatus(Constants.INDEX_TWO);
        }
        return tbPatrolStorePictureVO;
    }

    @Override
    public void capturePicture(String eid, Long businessId, Long storeSceneId,String patrolType) {
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(eid, businessId);
        if (recordDO == null || recordDO.getDeleted() == 1) {
            log.info("巡店记录不存在或已被删除 eid:{} , businessId :{}", eid, businessId);
            return;
        }
        patrolType = recordDO.getPatrolType();
        //如果任务未开始，调用定时器，到时间再去执行
        if(recordDO.getSubBeginTime().after(new Date())){
            setScheduler(eid, businessId, recordDO.getSubBeginTime());
            log.info("任务未开始，调用定时器，到时间再去执行 eid:{} , businessId :{}", eid, businessId);
            return;
        }

        //查询巡店数据
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = dataStaTableColumnMapper.selectByBusinessId(eid, businessId, PATROL_STORE);
        List<Long> storeSceneIdList = ListUtils.emptyIfNull(tbDataStaTableColumnDOS).stream().map(TbDataStaTableColumnDO::getStoreSceneId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<DeviceDO> videoDeviceList = deviceMapper.getByStoreId(eid, recordDO.getStoreId());
        if (CollectionUtils.isEmpty(videoDeviceList)) {
            log.info("该门店没有设备 eid:{} , businessId :{} storeId : {}", eid, businessId, recordDO.getStoreId());
            List<Long> newList = new ArrayList<>();
            //处理默认图片手动抓拍
            if (storeSceneId != null) {
                //手动抓拍图
                Long sceneId = storeSceneId;
                //若果没有抓拍场面就，默认去第一个
                if (sceneId == 0L && CollectionUtils.isNotEmpty(storeSceneIdList)) {
                    sceneId = storeSceneIdList.get(0);
                }
                newList.add(sceneId);
            }else {
                newList = storeSceneIdList;
            }
            dealLeftStoreScene(eid, newList, businessId, storeSceneId != null);
            if(TaskTypeEnum.PATROL_STORE_AI.getCode().equals(patrolType)){
                TbDataTableDO tbDataTableDO = tbDataTableMapper.selectOneByBusinessId(eid, recordDO.getId(), PATROL_STORE);
                List<TbMetaColumnResultDO> tbMetaColumnResultDOS = tbMetaColumnResultMapper.selectByMetaTableId(eid, tbDataTableDO.getMetaTableId());
                Map<String, TbMetaColumnResultDO> resultDOMap = tbMetaColumnResultDOS.stream().collect(Collectors.toMap(data ->data.getMetaColumnId()+data.getMappingResult(), data -> data, (a, b) -> a));
                for (TbDataStaTableColumnDO tbDataStaTableColumnDO : tbDataStaTableColumnDOS) {
                    tbDataStaTableColumnDO.setCheckResult(CheckResultEnum.INAPPLICABLE.getCode());
                    TbMetaColumnResultDO tbMetaColumnResultDO = resultDOMap.get(tbDataStaTableColumnDO.getMetaColumnId() + tbDataStaTableColumnDO.getCheckResult());
                    if(tbMetaColumnResultDO != null){
                        tbDataStaTableColumnDO.setCheckResultId(tbMetaColumnResultDO.getId());
                        tbDataStaTableColumnDO.setCheckResultName(tbMetaColumnResultDO.getResultName());
                        tbDataStaTableColumnDO.setCheckScore(tbMetaColumnResultDO.getMaxScore());
                    }
                }
                //更新结果
                aiPatrol(eid,tbDataStaTableColumnDOS,recordDO,businessId,patrolType);
            }

            return;
        }

        List<DeviceDO> deviceList = videoDeviceList.stream().filter(device ->
                        (device.getHasChildDevice() == null || !device.getHasChildDevice())
                                && (storeSceneId == null || storeSceneId.equals(device.getStoreSceneId())))
                .collect(Collectors.toList());

        Map<String, DeviceDO> videoDeviceMap  = videoDeviceList.stream().filter(device -> device.getHasChildDevice() != null && device.getHasChildDevice()).collect(Collectors.toMap(DeviceDO::getDeviceId, Function.identity()));

        //查询录像机下的通道
        List<String> deviceParentIdList = videoDeviceList.stream().filter(device -> device.getHasChildDevice() != null && device.getHasChildDevice())
                .map(DeviceDO::getDeviceId).collect(Collectors.toList());
        List<DeviceChannelDO> videoChannelDeviceList = null;
        if (CollectionUtils.isNotEmpty(deviceParentIdList)) {
            videoChannelDeviceList = deviceChannelMapper.listDeviceChannelByDeviceId(eid, deviceParentIdList, storeSceneId);
        }
        List<TbPatrolStorePictureDO> tbPatrolStorePictureDOS = new ArrayList<>();
        CapturePictureTypeEnum capturePictureTypeEnum = CapturePictureTypeEnum.TIMING;
        if(TaskTypeEnum.PATROL_STORE_AI.getCode().equals(patrolType)){
            capturePictureTypeEnum = CapturePictureTypeEnum.AI;
        }
        //摄像机抓拍
        if (CollectionUtils.isNotEmpty(deviceList)) {
            for (DeviceDO deviceDO : deviceList) {
                try {
                    TbPatrolStorePictureDO tbPatrolStorePictureDO = deviceService.beginCapture(eid, recordDO.getId(), deviceDO.getId(), 0L,
                            deviceDO.getDeviceId(), null, deviceDO.getStoreSceneId(), YunTypeEnum.getByCode(deviceDO.getResource()),capturePictureTypeEnum);
                    storeSceneIdList.remove(tbPatrolStorePictureDO.getStoreSceneId());
                    tbPatrolStorePictureDOS.add(tbPatrolStorePictureDO);
                } catch (Exception e) {
                    log.error("抓取设备图片失败, edi:{} , deviceId : {}", eid, deviceDO.getDeviceId(), e);
                }
            }
        }
        //录像机抓拍
        if (CollectionUtils.isNotEmpty(videoChannelDeviceList)) {
            for (DeviceChannelDO deviceChannelDO : videoChannelDeviceList) {
                DeviceDO deviceDO = videoDeviceMap.get(deviceChannelDO.getParentDeviceId());
                if(deviceDO == null){
                    log.info("抓取设备图片失败 父设备不存在, edi:{} , parentDeviceId : {}", eid, deviceChannelDO.getParentDeviceId());
                    continue;
                }
                try {
                    TbPatrolStorePictureDO tbPatrolStorePictureDO = deviceService.beginCapture(eid, recordDO.getId(), 0L, deviceChannelDO.getId(),
                            deviceChannelDO.getParentDeviceId(), deviceChannelDO.getChannelNo(), deviceChannelDO.getStoreSceneId(), YunTypeEnum.getByCode(deviceDO.getResource()),capturePictureTypeEnum);
                    storeSceneIdList.remove(tbPatrolStorePictureDO.getStoreSceneId());
                    tbPatrolStorePictureDOS.add(tbPatrolStorePictureDO);
                } catch (Exception e) {
                    log.error("抓取设备图片失败, edi:{} , deviceId : {}", eid, deviceChannelDO.getParentDeviceId(), e);
                }
            }
        }
        List<Long> metaColumnIdList = ListUtils.emptyIfNull(tbDataStaTableColumnDOS).stream().map(TbDataStaTableColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectByIds(eid, metaColumnIdList);
        boolean notAllAiCheck = tbMetaStaTableColumnDOS.stream().anyMatch(v -> YesOrNoEnum.NO.getCode().equals(v.getIsAiCheck()));
        boolean notAllAiType = tbMetaStaTableColumnDOS.stream().anyMatch(v -> !v.getColumnType().equals(6));
        // ai巡检或检查项全为ai检查项的定时巡检
        if(TaskTypeEnum.PATROL_STORE_AI.getCode().equals(patrolType) || TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(patrolType)
                && !notAllAiCheck) {
            if (TaskTypeEnum.PATROL_STORE_AI.getCode().equals(patrolType) && notAllAiType) {
                throw new ServiceException(ErrorCodeEnum.NOT_ALL_AI_CHECK);
            }
            aiHandel(eid, tbPatrolStorePictureDOS, tbDataStaTableColumnDOS, tbMetaStaTableColumnDOS, recordDO, businessId, patrolType);
        }
        //自动抓拍处理默认场景图片
        if(storeSceneId == null){
            dealLeftStoreScene(eid, storeSceneIdList, businessId, false);
        }else {
            //手动抓拍图
            Long newStoreSceneId = storeSceneId;
            //若果没有抓拍场面就，默认去第一个
            if(newStoreSceneId == 0L && CollectionUtils.isNotEmpty(storeSceneIdList)){
                newStoreSceneId = storeSceneIdList.get(0);
            }
            List<Long> newList = new ArrayList<>();
            newList.add(newStoreSceneId);
            dealLeftStoreScene(eid, newList, businessId, true);

        }
    }

    public void aiHandel(String eid,List<TbPatrolStorePictureDO> tbPatrolStorePictureDOS,List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS,
                         List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS, TbPatrolStoreRecordDO recordDO,Long businessId,String patrolType){
        log.info("PATROL_STORE_AI start businessId:{} pic:{}",businessId,JSONObject.toJSONString(tbPatrolStorePictureDOS));

        Map<Long, TbMetaStaTableColumnDO> tableColumnDOMap = tbMetaStaTableColumnDOS.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data -> data, (a, b) -> a));

        //ai分析
        Map<Long, List<TbPatrolStorePictureDO>> mapByStoreSceneId = ListUtils.emptyIfNull(tbPatrolStorePictureDOS).stream().collect(Collectors.groupingBy(TbPatrolStorePictureDO::getStoreSceneId));

        TbDataTableDO tbDataTableDO = tbDataTableMapper.selectOneByBusinessId(eid, recordDO.getId(), PATROL_STORE);
        List<TbMetaColumnResultDO> tbMetaColumnResultDOS = tbMetaColumnResultMapper.selectByMetaTableId(eid, tbDataTableDO.getMetaTableId());
        Map<String, TbMetaColumnResultDO> resultDOMap = tbMetaColumnResultDOS.stream().collect(Collectors.toMap(data ->data.getMetaColumnId()+data.getMappingResult(), data -> data, (a, b) -> a));
        Map<Long, List<TbMetaColumnResultDO>> resultListMap = CollStreamUtil.groupByKey(tbMetaColumnResultDOS, TbMetaColumnResultDO::getMetaColumnId);

        List<Future<Boolean>> futureList = new ArrayList<>();
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        for (TbDataStaTableColumnDO dataStaTableColumnDO : tbDataStaTableColumnDOS) {
            futureList.add(executor.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                //默认不适用
                dataStaTableColumnDO.setCheckResult(CheckResultEnum.INAPPLICABLE.getCode());
                List<AiPictureResultMappingDTO> aiPictureResultMappingDTOS;
                TbMetaStaTableColumnDO metaStaTableColumnDO = tableColumnDOMap.get(dataStaTableColumnDO.getMetaColumnId());

                Long storeSceneId = Objects.nonNull(dataStaTableColumnDO.getStoreSceneId()) ? dataStaTableColumnDO.getStoreSceneId() : 0;
                List<TbPatrolStorePictureDO> pictureDOList = ListUtils.emptyIfNull(mapByStoreSceneId.get(storeSceneId));
                if (YesOrNoEnum.YES.getCode().equals(metaStaTableColumnDO.getIsAiCheck())) {
                    List<TbMetaColumnResultDO> columnResultDOList = resultListMap.get(metaStaTableColumnDO.getId());
                    aiPictureResultMappingDTOS = aiResolve(eid, pictureDOList, metaStaTableColumnDO, dataStaTableColumnDO, columnResultDOList);
                } else {
                    aiPictureResultMappingDTOS = aiAlgorithmResolve(eid, pictureDOList, dataStaTableColumnDO, metaStaTableColumnDO, resultDOMap);
                }
                if (CollectionUtils.isNotEmpty(aiPictureResultMappingDTOS)) {
                    //插入分析结果
                    List<AiPictureResultMappingDO> aiPictureResultMappingDOS = aiPictureResultMappingDTOS.stream()
                            .map(ConvertFactory::convertAiPictureResultMappingDTO2AiPictureResultMappingDO).collect(Collectors.toList());
                    aiPictureResultMappingDao.batchInsert(eid, aiPictureResultMappingDOS);
                    //设置checkPic
                    List<AiPictureResultMappingDTO> sortedList = aiPictureResultMappingDTOS.stream()
                            .sorted(Comparator.comparing(AiPictureResultMappingDTO::getSortNum)).collect(Collectors.toList());
                    StringBuilder checkPic = new StringBuilder();
                    for (int i = 0; i < sortedList.size() && i < Constants.UPLOAD_PIC_MAX_NUM; i++) {
                        checkPic.append(sortedList.get(i).getPicUrl()).append(Constants.COMMA);
                    }
                    dataStaTableColumnDO.setCheckPics(checkPic.substring(0, checkPic.length() - 1));
                }
                return true;
            }));
        }
        futureList.forEach(v -> {
            try {
                v.get();
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("AI分析异常", e.getCause());
                throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
            }
        });
        //更新结果
        //dataStaTableColumnMapper.batchUpdateResult(eid,tbDataStaTableColumnDOS);
        //签到，提交，签退
        aiPatrol(eid,tbDataStaTableColumnDOS,recordDO,businessId,patrolType);
        // 将dataTable的标记为置零
        if ((Constants.SUBMITSTATUS_EIGHT & tbDataTableDO.getSubmitStatus()) == Constants.SUBMITSTATUS_EIGHT) {
            tbDataTableMapper.updateSubmitStatus(eid, tbDataTableDO.getId(), tbDataTableDO.getSubmitStatus() & ~(Constants.SUBMITSTATUS_EIGHT));
        }
    }

    /**
     * AI检查项处理
     */
    private List<AiPictureResultMappingDTO> aiResolve(String eid, List<TbPatrolStorePictureDO> pictureDOList, TbMetaStaTableColumnDO metaStaTableColumnDO, TbDataStaTableColumnDO dataStaTableColumnDO, List<TbMetaColumnResultDO> columnResultDOList) {
        List<String> imageList = CollStreamUtil.toList(pictureDOList, TbPatrolStorePictureDO::getPicture);
        if(CollectionUtils.isEmpty(imageList)){
            return CollStreamUtil.toList(pictureDOList, v -> createAiPictureResultMappingDTO(v, metaStaTableColumnDO, CheckResultEnum.INAPPLICABLE.getCode()));
        }
        AiModelLibraryDO aiModelLibraryDO = aiModelLibraryService.getModelByCode(metaStaTableColumnDO.getAiModel());
        AIResolveDTO aiResolveDTO = aiService.aiPatrolResolve(eid, aiModelLibraryDO, imageList, metaStaTableColumnDO, columnResultDOList, AICommentStyleEnum.DETAIL.getStyle());
        dataStaTableColumnDO.setCheckScore(aiResolveDTO.getAiScore());
        dataStaTableColumnDO.setCheckText(aiResolveDTO.getAiComment());
        if (Objects.nonNull(aiResolveDTO.getColumnResult())) {
            dataStaTableColumnDO.setCheckResultId(aiResolveDTO.getColumnResult().getId());
            dataStaTableColumnDO.setCheckResultName(aiResolveDTO.getColumnResult().getResultName());
            dataStaTableColumnDO.setCheckResult(aiResolveDTO.getColumnResult().getMappingResult());
            return CollStreamUtil.toList(pictureDOList, v -> createAiPictureResultMappingDTO(v, metaStaTableColumnDO, aiResolveDTO.getColumnResult().getMappingResult()));
        }
        return CollStreamUtil.toList(pictureDOList, v -> createAiPictureResultMappingDTO(v, metaStaTableColumnDO, CheckResultEnum.INAPPLICABLE.getCode()));
    }

    /**
     * AI检查项算法处理
     */
    private List<AiPictureResultMappingDTO> aiAlgorithmResolve(String eid, List<TbPatrolStorePictureDO> pictureDOList, TbDataStaTableColumnDO dataStaTableColumnDO,
                                                               TbMetaStaTableColumnDO metaStaTableColumnDO, Map<String, TbMetaColumnResultDO> resultDOMap) {
        List<AiPictureResultMappingDTO> result = new ArrayList<>();
        for (TbPatrolStorePictureDO storePictureDO : pictureDOList) {
            String resultMapping = hikvisionAIService.aiDetection(eid, storePictureDO.getPicture(), metaStaTableColumnDO.getAiType());
            String aiResult = StringUtils.isNotBlank(resultMapping) ? resultMapping : CheckResultEnum.INAPPLICABLE.getCode();
            result.add(createAiPictureResultMappingDTO(storePictureDO, metaStaTableColumnDO, aiResult));
        }
        if (CollectionUtils.isNotEmpty(result)) {
            List<AiPictureResultMappingDTO> failList = result.stream().filter(e -> CheckResultEnum.FAIL.getCode().equals(e.getAiResult())).collect(Collectors.toList());
            List<AiPictureResultMappingDTO> passList = result.stream().filter(e -> CheckResultEnum.PASS.getCode().equals(e.getAiResult())).collect(Collectors.toList());
            //只要有一张图片不合格，结果就是不合格、只有全部图片不适用，结果才是不适用、部分图片合格和部分不适用，结果为合格
            if (CollectionUtils.isNotEmpty(failList)) {
                dataStaTableColumnDO.setCheckResult(CheckResultEnum.FAIL.getCode());
            } else if (CollectionUtils.isNotEmpty(passList)) {
                dataStaTableColumnDO.setCheckResult(CheckResultEnum.PASS.getCode());
                dataStaTableColumnDO.setCheckScore(metaStaTableColumnDO.getSupportScore());
            } else {
                dataStaTableColumnDO.setCheckResult(CheckResultEnum.INAPPLICABLE.getCode());
            }
            TbMetaColumnResultDO tbMetaColumnResultDO = resultDOMap.get(dataStaTableColumnDO.getMetaColumnId() + dataStaTableColumnDO.getCheckResult());
            if (tbMetaColumnResultDO != null) {
                dataStaTableColumnDO.setCheckResultId(tbMetaColumnResultDO.getId());
                dataStaTableColumnDO.setCheckResultName(tbMetaColumnResultDO.getResultName());
                dataStaTableColumnDO.setCheckScore(tbMetaColumnResultDO.getMaxScore());
            }
        }
        return result;
    }

    private AiPictureResultMappingDTO createAiPictureResultMappingDTO(TbPatrolStorePictureDO storePictureDO, TbMetaStaTableColumnDO metaStaTableColumnDO, String aiResult) {
        AiPictureResultMappingDTO aiPictureResultMappingDTO = new AiPictureResultMappingDTO();
        aiPictureResultMappingDTO.setPictureId(storePictureDO.getId());
        Date now = new Date();
        aiPictureResultMappingDTO.setMetaColumnId(metaStaTableColumnDO.getId());
        aiPictureResultMappingDTO.setPicUrl(storePictureDO.getPicture());
        aiPictureResultMappingDTO.setCreateTime(now);
        aiPictureResultMappingDTO.setUpdateTime(now);
        aiPictureResultMappingDTO.setAiResult(aiResult);
        return aiPictureResultMappingDTO;
    }

    private void aiPatrol(String eid, List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS,
                          TbPatrolStoreRecordDO recordDO, Long businessId, String patrolType){
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(eid);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(eid);

        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        //签到
        PatrolStoreSignInParam patrolStoreSignInParam = new PatrolStoreSignInParam();
        patrolStoreSignInParam.setSignInStatus(1);
        patrolStoreSignInParam.setStoreId(recordDO.getStoreId());
        patrolStoreSignInParam.setPatrolType(patrolType);
        patrolStoreSignInParam.setUserId(Constants.AI);
        patrolStoreSignInParam.setBusinessId(businessId);
        patrolStoreSignInParam.setDingCorpId(config.getDingCorpId());
        patrolStoreSignInParam.setAppType(config.getAppType());
        patrolStoreService.signIn(eid, patrolStoreSignInParam);

        //提交
        TbDataTableDO tbDataTableDO = tbDataTableMapper.selectOneByBusinessId(eid, businessId, PATROL_STORE);
        PatrolStoreSubmitParam patrolStoreSubmitParam = new PatrolStoreSubmitParam();
        patrolStoreSubmitParam.setBusinessId(businessId);
        patrolStoreSubmitParam.setDataTableId(tbDataTableDO.getId());
        patrolStoreSubmitParam.setSubmit(true);
        List<PatrolStoreSubmitParam.DataStaTableColumnParam> dataStaTableColumnParamList = new ArrayList<>();
        for (TbDataStaTableColumnDO tbDataStaTableColumnDO : tbDataStaTableColumnDOS) {
            PatrolStoreSubmitParam.DataStaTableColumnParam param = new PatrolStoreSubmitParam.DataStaTableColumnParam();
            param.setCheckResultId(tbDataStaTableColumnDO.getCheckResultId());
            param.setCheckScore(tbDataStaTableColumnDO.getCheckScore());
            param.setCheckResultName(tbDataStaTableColumnDO.getCheckResultName());
            param.setCheckResult(tbDataStaTableColumnDO.getCheckResult());
            param.setCheckPics(tbDataStaTableColumnDO.getCheckPics());
            param.setId(tbDataStaTableColumnDO.getId());
            param.setCheckText(tbDataStaTableColumnDO.getCheckText());
            dataStaTableColumnParamList.add(param);
        }
        patrolStoreSubmitParam.setDataStaTableColumnParamList(dataStaTableColumnParamList);
        patrolStoreService.submit(eid, patrolStoreSubmitParam, Constants.AI);

        //签退
        PatrolStoreSignOutParam param = new PatrolStoreSignOutParam();
        param.setBusinessId(businessId);
        param.setSignOutStatus(1);

        patrolStoreService.signOut(config.getDingCorpId(),eid,param,storeCheckSettingDO,Constants.AI,Constants.AI,config.getAppType(), enterpriseSettingDO);

        // 不允許先签退，后提交，如果为true则调一次结束巡店
        if(recordDO.getOpenSubmitFirst()){
            patrolStoreService.overPatrol(eid,businessId,Constants.AI,Constants.AI,config.getDingCorpId(),true,config.getAppType(), null,null, enterpriseSettingDO);
        }
    }

    private String getAIResult(List<DetectWorkwearResponseBody.DetectWorkwearResponseBodyDataElements> elements, PatrolAITypeEnum patrolAITypeEnum, BigDecimal threshold){

        if(CollectionUtils.isEmpty(elements)){
            return CheckResultEnum.INAPPLICABLE.getCode();
        }
        //所有合格才算合格
        for (DetectWorkwearResponseBody.DetectWorkwearResponseBodyDataElements element : elements) {
            if (patrolAITypeEnum.getType().equals(element.getType())) {
                for (DetectWorkwearResponseBody.DetectWorkwearResponseBodyDataElementsProperty elementsProperty : ListUtils.emptyIfNull(element.getProperty())) {
                    if (patrolAITypeEnum.getCode().equals(elementsProperty.getLabel())) {
                        //小于
                        if (BigDecimal.valueOf(elementsProperty.getProbability().getYes()).compareTo(threshold) < 0) {
                            return CheckResultEnum.FAIL.getCode();
                        }
                    }
                }
            }
        }
        return CheckResultEnum.PASS.getCode();
    }

    private String getAIResult(BigDecimal result, BigDecimal threshold){

        if(Objects.isNull(result)){
            return CheckResultEnum.INAPPLICABLE.getCode();
        }
        //小于
        if (result.compareTo(threshold) < 0) {
            return CheckResultEnum.FAIL.getCode();
        }
        return CheckResultEnum.PASS.getCode();
    }

    private List<String> getAIContent(List<DetectWorkwearResponseBody.DetectWorkwearResponseBodyDataElements> elements, PatrolAITypeEnum patrolAITypeEnum){
        List<String> aiContentList = new ArrayList<>();
        for (DetectWorkwearResponseBody.DetectWorkwearResponseBodyDataElements element : elements) {
            if(patrolAITypeEnum.getType().equals(element.getType())){
                aiContentList.add(JSONObject.toJSONString(element));
            }
        }
        return aiContentList;
    }

    private void setSort(AiPictureResultMappingDTO dto,String aiResult){
        if(CheckResultEnum.FAIL.getCode().equals(aiResult)){
            dto.setSortNum(Constants.INDEX_ONE);
        } else if(CheckResultEnum.PASS.getCode().equals(aiResult)){
            dto.setSortNum(Constants.INDEX_TWO);
        } else {
            dto.setSortNum(Constants.INDEX_THREE);
        }
    }

    @Override
    public void beginCapturePicture(String eid, TbPatrolStoreCapturePictureDTO param) {
        String key = redisConstantUtil.getCapturePicture(eid + "_" + param.getBusinessId()
                + "_" + (param.getStoreSceneId() == null ? 0 : param.getStoreSceneId()));
        String captureStatus = redisUtilPool.getString(key);
        //
        if(Constants.STRING_INDEX_ONE.equals(captureStatus)){
            throw new ServiceException("抓拍中，不能再次抓拍");
        }
        simpleMessageService.send(JSONObject.toJSONString(new PatrolStorePictureMsgDTO(eid, param.getBusinessId(), param.getStoreSceneId(),TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode())), RocketMqTagEnum.PATROL_MANUAL_STORE_CAPTURE_PICTURE_QUEUE);

    }

    @Override
    public TbPatrolStorePictureDO beginCapturePictureByDevice(String eid, TbPatrolStoreCapturePictureDTO param) {
        if(param.getDeviceChannelId() == null && param.getDeviceId() == null){
            throw new ServiceException("设备参数不能为空");
        }
        TbPatrolStorePictureDO tbPatrolStorePictureDO = null;
        TbPatrolStorePictureInfoVO tbPatrolStorePictureInfoVO = new TbPatrolStorePictureInfoVO();

        if(param.getDeviceChannelId() != null && param.getDeviceChannelId() != 0){
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelById(eid, param.getDeviceChannelId());
            if(deviceChannelDO == null){
                throw new ServiceException("设备不存在");
            }
            DeviceDO deviceDO = deviceMapper.getDeviceByDeviceId(eid, deviceChannelDO.getParentDeviceId());
            if(deviceDO == null){
                throw new ServiceException("设备不存在");
            }

            tbPatrolStorePictureDO = deviceService.beginCapture(eid, param.getBusinessId(), 0L, deviceChannelDO.getId(),
                    deviceChannelDO.getParentDeviceId(), deviceChannelDO.getChannelNo(), deviceChannelDO.getStoreSceneId(), YunTypeEnum.getByCode(deviceDO.getResource()), CapturePictureTypeEnum.TIMING);
            tbPatrolStorePictureInfoVO.setDeviceName(deviceChannelDO.getChannelName());
            tbPatrolStorePictureInfoVO.setChannelNo(deviceChannelDO.getChannelNo());
            tbPatrolStorePictureInfoVO.setDeviceNo(deviceChannelDO.getParentDeviceId());
            if(deviceChannelDO.getStoreSceneId() != null){
                tbPatrolStorePictureInfoVO.setStoreSceneName(storeSceneDAO.getStoreSceneName(eid, deviceChannelDO.getStoreSceneId()));
            }
        }

        if(param.getDeviceId() != null && param.getDeviceId() != 0){

            DeviceDO deviceDO = deviceMapper.getDeviceById(eid, param.getDeviceId());
            if(deviceDO == null){
                throw new ServiceException("设备不存在");
            }
            tbPatrolStorePictureDO = deviceService.beginCapture(eid, param.getBusinessId(), deviceDO.getId(), 0L,
                    deviceDO.getDeviceId(), null, deviceDO.getStoreSceneId(), YunTypeEnum.getByCode(deviceDO.getResource()),CapturePictureTypeEnum.TIMING);
            tbPatrolStorePictureInfoVO.setDeviceName(deviceDO.getDeviceName());
            tbPatrolStorePictureInfoVO.setDeviceNo(deviceDO.getDeviceId());
            tbPatrolStorePictureInfoVO.setStoreSceneName(storeSceneDAO.getStoreSceneName(eid, deviceDO.getStoreSceneId()));

        }
        if(tbPatrolStorePictureDO != null){
            tbPatrolStorePictureInfoVO.setId(tbPatrolStorePictureDO.getId());
            tbPatrolStorePictureInfoVO.setPicture(tbPatrolStorePictureDO.getPicture());
            tbPatrolStorePictureInfoVO.setStoreSceneId(tbPatrolStorePictureDO.getStoreSceneId());
            tbPatrolStorePictureInfoVO.setBusinessId(tbPatrolStorePictureDO.getBusinessId());
            tbPatrolStorePictureInfoVO.setDeviceId(tbPatrolStorePictureDO.getDeviceId());
            tbPatrolStorePictureInfoVO.setDeviceChannelId(tbPatrolStorePictureDO.getDeviceChannelId());
            tbPatrolStorePictureInfoVO.setRemark(tbPatrolStorePictureDO.getRemark());

        }
        return tbPatrolStorePictureDO;
    }

    /**
     * 给场景添加默认图片
     * @param eid
     * @param storeSceneIdList
     * @param businessId
     */
    private void dealLeftStoreScene(String eid, List<Long> storeSceneIdList, Long businessId, Boolean isManual){
        storeSceneIdList.remove(0L);
        // 一张图片没有
        Long id = tbPatrolStorePictureMapper.selectIdOne(eid, businessId);
        if(CollectionUtils.isEmpty(storeSceneIdList) && id != null && !isManual){
            return;
        }
        if(CollectionUtils.isEmpty(storeSceneIdList)){
            storeSceneIdList.add(0L);
        }
        storeSceneIdList.forEach(storeSceneId -> {
            Long pictureIdOne = tbPatrolStorePictureMapper.selectPictureIdOne(eid, businessId, storeSceneId);
            if(pictureIdOne != null && !isManual){
                log.info("不需要插入默认图片 : {}", storeSceneId);
                return;
            }
            TbPatrolStorePictureDO patrolStorePictureDO = new TbPatrolStorePictureDO();
            patrolStorePictureDO.setDeviceId(0L);
            patrolStorePictureDO.setDeviceChannelId(0L);
            patrolStorePictureDO.setBusinessId(businessId);
            patrolStorePictureDO.setCreateTime(new Date());
            patrolStorePictureDO.setUpdateTime(new Date());
            patrolStorePictureDO.setStoreSceneId(storeSceneId == null ? 0: storeSceneId);
            patrolStorePictureDO.setDeleted(false);
            patrolStorePictureDO.setCapturePictureType(CapturePictureTypeEnum.TIMING.getCode());
            //根据设备类型抓图
            String url = Constants.DEFAULT_PICTURE_URL;
            patrolStorePictureDO.setPicture(url);
            tbPatrolStorePictureMapper.insert(eid, patrolStorePictureDO);
        });

    }


    public void setScheduler(String enterpriseId, Long businessId, Date beginTime) {

        List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
        jobs.add(ScheduleCallBackUtil.getCallBack(schedulerCallbackTaskUrl + "/v2/" + enterpriseId + "/communication/capturePicture/" + businessId, ScheduleCallBackEnum.api.getValue()));

        Date afterTenSecond = DateUtil.offset(beginTime, DateField.SECOND, 10);
        String startTime = DateUtils.convertTimeToString(afterTenSecond.getTime(), "yyyy-MM-dd HH:mm:ss");

        ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime, jobs);
        fixedRequest.setTimes(1);
        String requestString = JSON.toJSONString(fixedRequest);
        log.info("单抓拍图片回调，开始调用定时器enterpriseId={},businessId={},开始调用参数={}", enterpriseId, businessId, requestString);
        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", requestString, ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);
        log.info("单抓拍图片回调迟回调，结束调用定时器enterpriseId={},businessId={},返回结果={}", enterpriseId, businessId, jsonObjectSchedule);
        String scheduleId = null;
        if (ObjectUtil.isNotEmpty(jsonObjectSchedule)) {
            scheduleId = jsonObjectSchedule.getString("scheduler_id");
        }
    }
}
