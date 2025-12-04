package com.coolcollege.intelligent.service.aianalysis.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.aianalysis.dao.*;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.dao.DeviceDao;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.ai.AIResultDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisPictureDO;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportDO;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportUserMappingDO;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisRuleDO;
import com.coolcollege.intelligent.model.aianalysis.dto.*;
import com.coolcollege.intelligent.model.aianalysis.entity.AiAnalysisCaptureTaskDO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisModelVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisRuleSimpleVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisRuleVO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.CapturePictureDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisRuleService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * <p>
 * AI分析规则 服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/6/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisRuleServiceImpl implements AiAnalysisRuleService {

    private final AiAnalysisRuleDAO aiAnalysisRuleDAO;
    private final AiAnalysisPictureDAO aiAnalysisPictureDAO;
    private final AiAnalysisCaptureTaskDAO aiAnalysisCaptureTaskDAO;
    private final AiAnalysisReportDAO aiAnalysisReportDAO;
    private final AiAnalysisReportUserMappingDAO aiAnalysisReportUserMappingDAO;
    private final EnterpriseConfigMapper enterpriseConfigMapper;
    private final StoreWorkService storeWorkService;
    private final DeviceDao deviceDao;
    private final DeviceChannelMapper deviceChannelMapper;
    private final VideoServiceApi videoServiceApi;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    private final RedisUtilPool redisUtilPool;
    private final UserPersonInfoService userPersonInfoService;
    private final AuthVisualService authVisualService;
    private final FileUploadService fileUploadService;
    private final JmsTaskService jmsTaskService;
    private final StoreDao storeDao;
    private final AIService aiService;
    private final AiModelLibraryService aiModelLibraryService;

    private static final String AI_MODEL_KEY = "ai_analysis_model";


    @Override
    public Boolean save(String enterpriseId, AiAnalysisRuleDTO dto) {
        AiAnalysisRuleDO aiAnalysisRuleDO = dto.convertToDO(false);
        return aiAnalysisRuleDAO.insert(enterpriseId, aiAnalysisRuleDO);
    }

    @Override
    public Boolean update(String enterpriseId, AiAnalysisRuleDTO dto) {
        AiAnalysisRuleDO aiAnalysisRuleDO = dto.convertToDO(true);
        return aiAnalysisRuleDAO.update(enterpriseId, aiAnalysisRuleDO);
    }

    @Override
    public Boolean removeBatch(String enterpriseId, List<Long> ids) {
        return aiAnalysisRuleDAO.deleteBatch(enterpriseId, ids);
    }

    @Override
    public PageInfo<AiAnalysisRuleSimpleVO> getPage(String enterpriseId, AiAnalysisRuleQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AiAnalysisRuleDO> list = aiAnalysisRuleDAO.getList(enterpriseId, query);
        PageInfo pageResult = new PageInfo<>(list);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        List<AiAnalysisRuleSimpleVO> result = CollStreamUtil.toList(list, v -> AiAnalysisRuleSimpleVO.builder()
                .id(v.getId())
                .ruleName(v.getRuleName())
                .validityPeriod(formatter.format(v.getStartTime()) + "-" + formatter.format(v.getEndTime()))
                .captureTimes(v.getCaptureTimes())
                .models(v.getModels())
                .storeRange(convertByCommonDTOStr(v.getStoreRange()))
                .reportPusher(convertByCommonDTOStr(v.getReportPusher()))
                .build());
        result.forEach(v -> {
            String aiModelNames = Arrays.stream(v.getModels().split(Constants.COMMA)).map(modelId -> redisUtilPool.hashGet(AI_MODEL_KEY, modelId)).collect(Collectors.joining(Constants.COMMA));
            v.setAiModelNames(aiModelNames);
        });
        pageResult.setList(result);
        return pageResult;
    }

    private String convertByCommonDTOStr(String str) {
        List<StoreWorkCommonDTO> list = JSONObject.parseArray(str, StoreWorkCommonDTO.class);
        List<String> nameList = CollStreamUtil.toList(list, StoreWorkCommonDTO::getName);
        return CollectionUtils.join(nameList, "、");
    }

    @Override
    public AiAnalysisRuleVO getById(String enterpriseId, Long id) {
        AiAnalysisRuleDO aiAnalysisRuleDO = aiAnalysisRuleDAO.getById(enterpriseId, id);
        AiAnalysisRuleVO vo = AiAnalysisRuleVO.convert(aiAnalysisRuleDO);
        String aiModelNames = Arrays.stream(vo.getModels().split(Constants.COMMA)).map(v -> redisUtilPool.hashGet(AI_MODEL_KEY, v)).collect(Collectors.joining(Constants.COMMA));
        vo.setAiModelNames(aiModelNames);
        return vo;
    }

    @Override
    public List<AiAnalysisModelVO> getModelList() {
        Map<String, String> aiAnalysisModel = redisUtilPool.hashGetAll("ai_analysis_model");
        return CollStreamUtil.toList(aiAnalysisModel.entrySet(), v -> new AiAnalysisModelVO(Long.valueOf(v.getKey()), v.getValue()));
    }

    @Override
    @Async("generalThreadPool")
    public void aiAnalysis(String enterpriseId, LocalDate date, List<Long> retryRuleIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        LocalDate reportDate = today.minusDays(1);
        // 获取今日抓拍任务结果
        getCapturePictureAndDown(enterpriseId, today);

        List<AiAnalysisRuleDO> ruleList = aiAnalysisRuleDAO.getListByPeriod(enterpriseId, today, retryRuleIds);
        // 过滤今天已经生成的规则
        List<Long> ruleIds = CollStreamUtil.toList(ruleList, AiAnalysisRuleDO::getId);
        Set<Long> existRuleIds = aiAnalysisReportDAO.getExistRule(enterpriseId, ruleIds, reportDate);
        ruleList = ruleList.stream().filter(v -> !existRuleIds.contains(v.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ruleList)) return;
        // 这里固定调火山的豆包模型，code用的是别名
        AiModelLibraryDO aiModelLibraryDO = aiModelLibraryService.getModelByCode("huoshan");
        aiModelLibraryDO.setCode("doubao-seed-1-6-250615");
        for (AiAnalysisRuleDO rule : ruleList) {
            try {
                log.info("开始生成AI分析报告，date：{}，ruleId：{}", today, rule.getId());
                // 拿到抓拍图片并按照门店分组
                List<AiAnalysisPictureDO> pictureList = aiAnalysisPictureDAO.getByRuleIdAndDate(enterpriseId, rule.getId(), today);
                Map<String, List<String>> pictureMap = CollStreamUtil.groupBy(pictureList, AiAnalysisPictureDO::getStoreId, Collectors.mapping(AiAnalysisPictureDO::getUrl, Collectors.toList()));
                // 获取门店
                List<String> storeIds = getStoreByStoreRangeCommonDTO(enterpriseId, rule.getStoreRange());
                String promptDimension = Arrays.stream(rule.getModels().split(Constants.COMMA)).map(v -> "● " + redisUtilPool.hashGet("ai_analysis_model", v)).collect(Collectors.joining(" "));
                log.info("ruleId:{}, promptDimension:{}", rule.getId(), promptDimension);
                List<Future<AiAnalysisResultDTO>> futureList = new ArrayList<>();
                for (String storeId : storeIds) {
                    List<String> pictures = pictureMap.get(storeId);
                    if (CollectionUtils.isEmpty(pictures)) {
                        log.info("报告生成失败，抓拍图片为空，ruleId:{}, storeId:{}", rule.getId(), storeId);
                        continue;
                    }
                    futureList.add(executor.submit(() -> {
                        AIResultDTO aiResultDTO = aiService.aiReportResolve(enterpriseId, aiModelLibraryDO, promptDimension, pictures);
                        log.info("ruleId:{}, AI分析结果：{}", rule.getId(), aiResultDTO.getAiResult());
                        return new AiAnalysisResultDTO(storeId, aiResultDTO.getAiResult());
                    }));
                }

                List<AiAnalysisResultDTO> aiResultList = new ArrayList<>();
                futureList.forEach(v -> {
                    try {
                        aiResultList.add(v.get());
                    } catch (Exception e) {
                        log.info("AI分析异常", e);
                    }
                });

                Map<String, List<String>> storeUserMap = getUserByStoreRangeCommonDTO(enterpriseId, rule.getReportPusher(), storeIds);

                for (AiAnalysisResultDTO aiAnalysisResultDTO : aiResultList) {
                    if (StringUtils.isBlank(aiAnalysisResultDTO.getResult())) {
                        log.info("AI结果为空，不生成店报");
                        continue;
                    }
                    // 生成AI报告
                    AiAnalysisReportDO reportDO = AiAnalysisReportDO.builder()
                            .ruleId(rule.getId())
                            .storeId(aiAnalysisResultDTO.getStoreId())
                            .reportPushTime(rule.getReportPushTime().atDate(today))
                            .reportDate(reportDate)
                            .aiResult(aiAnalysisResultDTO.getResult())
                            .build();
                    aiAnalysisReportDAO.insert(enterpriseId, reportDO);
                    // 报告和推送用户映射关系
                    List<String> userIds = storeUserMap.getOrDefault(aiAnalysisResultDTO.getStoreId(), Collections.emptyList());
                    List<AiAnalysisReportUserMappingDO> reportUserMappingList = CollStreamUtil.toList(userIds, v -> AiAnalysisReportUserMappingDO.builder()
                            .reportId(reportDO.getId()).userId(v).reportPushTime(reportDO.getReportPushTime()).reportDate(reportDate).storeId(reportDO.getStoreId()).build());
                    if (CollectionUtils.isNotEmpty(reportUserMappingList)) {
                        aiAnalysisReportUserMappingDAO.insertBatch(enterpriseId, reportUserMappingList);
                    }
                }

            } catch (Exception e) {
                log.info("AI分析报告生成失败，date：{}，ruleId：{}, errorMsg: {}", today, rule.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void deleteAndAiAnalysis(String enterpriseId, LocalDate date, List<Long> retryRuleIds) {
        if (CollectionUtils.isEmpty(retryRuleIds)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        LocalDate reportDate = today.minusDays(1);
        // 删除报告
        aiAnalysisReportDAO.removeByRuleIds(enterpriseId, retryRuleIds, reportDate);
        // 删除人员映射
        List<Long> reportIds = aiAnalysisReportDAO.getReportIdsByRuleIds(enterpriseId, retryRuleIds, reportDate);
        aiAnalysisReportUserMappingDAO.removeByReportIds(enterpriseId, reportIds);
        // 生成报告
        aiAnalysis(enterpriseId, today, retryRuleIds);
    }

    @Override
    @Async("generalThreadPool")
    public void reportPush(String enterpriseId, LocalDateTime pushTime) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        LocalDateTime now = Objects.nonNull(pushTime) ? pushTime : LocalDateTime.now();
        log.info("推送AI店报, now:{}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        LocalDateTime startTime = now.minusMinutes(30);
        LocalDateTime endTime = now.plusMinutes(30);
        // 查询当前应该通知的报告列表，前后冗余半小时
        List<AiAnalysisReportDO> reportList = aiAnalysisReportDAO.getListByPushTime(enterpriseId, startTime, endTime);
        if (CollectionUtils.isEmpty(reportList)) return;
        Set<String> storeIds = CollStreamUtil.toSet(reportList, AiAnalysisReportDO::getStoreId);
        Map<String, String> storeNamaMap = storeDao.getStoreNameMapByIds(enterpriseId, new ArrayList<>(storeIds));
        for (AiAnalysisReportDO reportDO : reportList) {
            String storeName = storeNamaMap.get(reportDO.getStoreId());
            if (Objects.isNull(storeName)) {
                log.info("未查询到营业门店, storeId:{}", reportDO.getStoreId());
                continue;
            }
            // 查询用户映射
            List<String> userIds = aiAnalysisReportUserMappingDAO.getUserIdsByReportId(enterpriseId, reportDO.getId());
            if (CollectionUtils.isNotEmpty(userIds)) {
                jmsTaskService.sendAiAnalysisReportMessage(enterpriseId, reportDO.getId(), reportDO.getStoreId(), storeName, reportDO.getReportDate(), userIds, configDO.getAppType(), configDO.getDingCorpId());
            }
        }
    }

    @Override
    public void reportUserMappingReset(String enterpriseId, LocalDate date, List<Long> ruleIds) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        LocalDate reportDate = today.minusDays(1);
        List<AiAnalysisRuleDO> ruleList = aiAnalysisRuleDAO.getListByPeriod(enterpriseId, today, ruleIds);
        Map<Long, List<AiAnalysisReportDO>> reportMap = aiAnalysisReportDAO.getReportMapByRuleIds(enterpriseId, ruleIds, reportDate);
        Map<Long, Set<String>> reportUserMap = aiAnalysisReportUserMappingDAO.getUserIdMapByReportIds(enterpriseId, reportMap.values().stream().flatMap(v -> v.stream().map(AiAnalysisReportDO::getId)).collect(Collectors.toList()));
        List<AiAnalysisReportUserMappingDO> insertReportUserMappingList = new ArrayList<>();
        for (AiAnalysisRuleDO rule : ruleList) {
            List<AiAnalysisReportDO> reports = reportMap.get(rule.getId());
            if (CollectionUtils.isEmpty(reports)) continue;
            List<String> storeIds = getStoreByStoreRangeCommonDTO(enterpriseId, rule.getStoreRange());
            Map<String, List<String>> storeUserMap = getUserByStoreRangeCommonDTO(enterpriseId, rule.getReportPusher(), storeIds);
            for (AiAnalysisReportDO report : reports) {
                List<String> expectPushUserIds = storeUserMap.getOrDefault(report.getStoreId(), Collections.emptyList());
                Set<String> pushedUserIds = reportUserMap.getOrDefault(report.getId(), Collections.emptySet());
                // 需要补发的用户ID
                List<String> needResendUserIds = expectPushUserIds.stream()
                        .filter(userId -> !pushedUserIds.contains(userId))
                        .collect(Collectors.toList());
                List<AiAnalysisReportUserMappingDO> reportUserMappingList = CollStreamUtil.toList(needResendUserIds, v -> AiAnalysisReportUserMappingDO.builder()
                        .reportId(report.getId()).userId(v).reportPushTime(report.getReportPushTime()).reportDate(reportDate).storeId(report.getStoreId()).build());
                insertReportUserMappingList.addAll(reportUserMappingList);
            }
        }
        aiAnalysisReportUserMappingDAO.insertBatch(enterpriseId, insertReportUserMappingList);
    }

    /**
     * 将图片上传到oss
     */
    private String pictureUpload(String enterpriseId, String url) {
        FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(url, enterpriseId, "");
        return fileUploadParam.getServer() + fileUploadParam.getFileNewName();
    }

    /**
     * 获取指定日期所有未得到结果的抓拍任务结果，并将抓拍图片入库
     * @param enterpriseId 企业id
     * @param date 日期
     */
    private void getCapturePictureAndDown(String enterpriseId, LocalDate date) {
        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 查询抓拍任务
        List<AiAnalysisCaptureTaskDO> captureTaskList = aiAnalysisCaptureTaskDAO.getNoResultListByDate(enterpriseId, today);
        // 设备
        Set<String> deviceIds = CollStreamUtil.toSet(captureTaskList, AiAnalysisCaptureTaskDO::getDeviceId);
        List<DeviceDO> deviceList = deviceDao.getDeviceByDeviceIdList(enterpriseId, new ArrayList<>(deviceIds));
        Map<String, List<String>> deviceStoreMap = CollStreamUtil.toMap(deviceList, DeviceDO::getDeviceId, v -> StringUtils.isNotBlank(v.getBindStoreIds()) ? Arrays.asList(v.getBindStoreIds().split(",")) : Collections.emptyList());
        List<AiAnalysisCaptureTaskDO> updateCaptureTaskList = new ArrayList<>();
        List<AiAnalysisPictureDO> insertPictureList = new ArrayList<>();
        for (AiAnalysisCaptureTaskDO captureTaskDO : captureTaskList) {
            // 这里不能直接查文件列表，因为文件列表接口调用成功并返回数据不代表所有时间点抓拍成功，因此先查任务状态
            try {
                VideoFileDTO fileDTO = videoServiceApi.getVideoFile(enterpriseId, captureTaskDO.getDeviceId(), captureTaskDO.getCaptureTaskId());
                if (Constants.INDEX_ZERO.equals(fileDTO.getStatus())) {
                    // 成功
                    captureTaskDO.setHasResult(true);
                    updateCaptureTaskList.add(captureTaskDO);
                    // 获取图片并入库
                    List<TaskFileDTO> taskFiles = videoServiceApi.getTaskFiles(enterpriseId, captureTaskDO.getDeviceId(), captureTaskDO.getCaptureTaskId());
                    // 设备可能绑定多个门店，每个门店都新增一个摄像头抓拍到的一批图片
                    deviceStoreMap.getOrDefault(captureTaskDO.getDeviceId(), Collections.emptyList()).forEach(storeId -> {
                        List<AiAnalysisPictureDO> pictureList = CollStreamUtil.toList(taskFiles, taskFile -> AiAnalysisPictureDO.builder()
                                .ruleId(captureTaskDO.getRuleId())
                                .storeId(storeId)
                                // 萤石云返回的图片url有过期时间，上传到oss
                                .url(pictureUpload(enterpriseId, taskFile.getUrl()))
                                .captureTime(LocalDateTime.parse(taskFile.getTimePoint(), formatter).toLocalTime())
                                .generateDate(captureTaskDO.getGenerateDate())
                                .deviceId(captureTaskDO.getDeviceId())
                                .channelNo(captureTaskDO.getChannelNo())
                                .storeSceneId(captureTaskDO.getStoreSceneId())
                                .build());
                        insertPictureList.addAll(pictureList);
                    });
                } else if (Constants.INDEX_TWO.equals(fileDTO.getStatus())) {
                    // 失败
                    captureTaskDO.setHasResult(true);
                    captureTaskDO.setErrorCode(fileDTO.getErrorCode());
                    captureTaskDO.setErrorMsg(fileDTO.getErrorMsg());
                    updateCaptureTaskList.add(captureTaskDO);
                }
            } catch (Exception e) {
                captureTaskDO.setHasResult(true);
                captureTaskDO.setErrorMsg("查询任务结果异常");
                updateCaptureTaskList.add(captureTaskDO);
                log.info("抓图任务结果查询异常，taskId：{}，errorMsg：{}", captureTaskDO.getCaptureTaskId(), e.getMessage());
            }
        }
        if (CollectionUtils.isNotEmpty(updateCaptureTaskList)) {
            aiAnalysisCaptureTaskDAO.updateBatch(enterpriseId, updateCaptureTaskList);
        }
        if (CollectionUtils.isNotEmpty(insertPictureList)) {
            aiAnalysisPictureDAO.insertBatch(enterpriseId, insertPictureList);
        }
    }

    @Override
    @Async("generalThreadPool")
    public void submitCaptureTask(String enterpriseId, LocalDate date, List<Long> retryRuleIds) {
        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        String yesterdayStr = today.minusDays(1).toString();
        log.info("开始执行AI分析, enterpriseId:{}, date:{}", enterpriseId, today);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        // 查询所有有效期内的规则
        List<AiAnalysisRuleDO> ruleList = aiAnalysisRuleDAO.getListByPeriod(enterpriseId, today, retryRuleIds);
        // 过滤今天已经生成抓图任务的规则
        List<Long> ruleIds = CollStreamUtil.toList(ruleList, AiAnalysisRuleDO::getId);
        Set<Long> existRuleIds = Collections.emptySet();
        // 重试时不校验是否已生成过抓图任务
        if (CollectionUtils.isEmpty(retryRuleIds)) {
            existRuleIds = aiAnalysisCaptureTaskDAO.getExistRule(enterpriseId, ruleIds, today);
        }
        final Set<Long> finalExistRuleIds = existRuleIds;
        ruleList = ruleList.stream().filter(v -> !finalExistRuleIds.contains(v.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ruleList)) {
            return;
        }
        for (AiAnalysisRuleDO rule : ruleList) {
            try {
                log.info("提交抓图任务，ruleId：{}", rule.getId());
                List<String> captureTimes = Optional.ofNullable(rule.getCaptureTimes()).map(v -> Arrays.asList(v.split(Constants.COMMA))).orElse(Collections.emptyList());
                if (CollectionUtils.isEmpty(captureTimes)) continue;
                // 取出门店
                List<String> storeIds = getStoreByStoreRangeCommonDTO(enterpriseId, rule.getStoreRange());
                if (CollectionUtils.isEmpty(storeIds)) {
                    log.info("门店为空不提交抓图任务");
                    continue;
                }
                // 取出所有需要抓拍的设备
                List<AiAnalysisDeviceDTO> deviceList = getDeviceInfo(enterpriseId, storeIds, rule);
                // yyyy-MM-dd HH:mm:ss
                List<String> captureTimeTrans = CollStreamUtil.toList(captureTimes, time -> (yesterdayStr + " " + time + ":00"));
                // 设备发起抓拍任务
                List<AiAnalysisCaptureTaskDO> captureTaskList = new ArrayList<>();
                List<AiAnalysisPictureCaptureDTO> captureDictList = new ArrayList<>();
                log.info("设备数量:{}", deviceList.size());
                for (AiAnalysisDeviceDTO deviceDTO : deviceList) {
                    try {
                        String taskId = videoServiceApi.captureByTime(enterpriseId, deviceDTO.getDeviceId(), deviceDTO.getChannelNo(), captureTimeTrans);
                        AiAnalysisCaptureTaskDO captureTaskDO = AiAnalysisCaptureTaskDO.builder()
                                .captureTaskId(taskId)
                                .ruleId(rule.getId())
                                .deviceId(deviceDTO.getDeviceId())
                                .channelNo(deviceDTO.getChannelNo())
                                .storeSceneId(deviceDTO.getStoreSceneId())
                                .hasResult(StringUtils.isBlank(taskId))
                                .errorMsg(StringUtils.isBlank(taskId) ? "抓拍任务创建失败" : null)
                                .generateDate(today)
                                .build();
                        captureTaskList.add(captureTaskDO);
                    } catch (UnsupportedOperationException e) {
                        List<CapturePictureDTO> captureDTOList = videoServiceApi.captureByTimeDict(enterpriseId, deviceDTO.getDeviceId(), deviceDTO.getChannelNo(), captureTimeTrans);
                        AiAnalysisCaptureTaskDO captureTaskDO = AiAnalysisCaptureTaskDO.builder()
                                .ruleId(rule.getId())
                                .deviceId(deviceDTO.getDeviceId())
                                .channelNo(deviceDTO.getChannelNo())
                                .storeSceneId(deviceDTO.getStoreSceneId())
                                .hasResult(true)
                                .errorMsg(getCaptureDictErrorMsg(captureDTOList))
                                .generateDate(today)
                                .build();
                        captureTaskList.add(captureTaskDO);
                        // 成功的直接入库
                        List<CapturePictureDTO> successList = captureDTOList.stream().filter(v -> StringUtils.isNotBlank(v.getUrl())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(successList)) {
                            captureDictList.add(new AiAnalysisPictureCaptureDTO(rule.getId(), deviceDTO.getDeviceId(), deviceDTO.getChannelNo(), deviceDTO.getStoreSceneId(), successList));
                        }
                    }
                }
                // 直接返回图片的入库
                saveCaptureDict(enterpriseId, captureDictList, today);
                aiAnalysisCaptureTaskDAO.insertBatch(enterpriseId, captureTaskList);
                log.info("抓图任务提交完成，ruleId:{}, 任务数量:{}, 直接获取抓图的设备数量:{}", rule.getId(), captureTaskList.size(), captureDictList.size());
            } catch (Exception e) {
                log.info("抓图任务提交失败，ruleId：{}, errorMsg：{}", rule.getId(), e.getMessage());
            }
        }
    }

    /**
     * 抓图入库
     * @param enterpriseId 企业id
     * @param captureDictList AI店报抓图DTO列表
     */
    private void saveCaptureDict(String enterpriseId, List<AiAnalysisPictureCaptureDTO> captureDictList, LocalDate generateDate) {
        if (CollectionUtils.isEmpty(captureDictList)) return;
        Set<String> deviceIds = CollStreamUtil.toSet(captureDictList, AiAnalysisPictureCaptureDTO::getDeviceId);
        List<DeviceDO> deviceList = deviceDao.getDeviceByDeviceIdList(enterpriseId, new ArrayList<>(deviceIds));
        Map<String, List<String>> deviceStoreMap = CollStreamUtil.toMap(deviceList, DeviceDO::getDeviceId, v -> StringUtils.isNotBlank(v.getBindStoreIds()) ? Arrays.asList(v.getBindStoreIds().split(",")) : Collections.emptyList());
        List<AiAnalysisPictureDO> insertPictureList = captureDictList.stream().flatMap(dto -> {
            List<String> storeIds = deviceStoreMap.getOrDefault(dto.getDeviceId(), Collections.emptyList());
            return dto.getCapturePictureList().stream().flatMap(pictureDTO -> storeIds.stream().map(storeId -> AiAnalysisPictureDO.builder()
                    .ruleId(dto.getRuleId())
                    .storeId(storeId)
                    .url(pictureUpload(enterpriseId, pictureDTO.getUrl()))
                    .captureTime(pictureDTO.getCaptureTime().toLocalTime())
                    .generateDate(generateDate)
                    .deviceId(dto.getDeviceId())
                    .channelNo(dto.getChannelNo())
                    .storeSceneId(dto.getStoreSceneId())
                    .build()));
        }).collect(Collectors.toList());
        aiAnalysisPictureDAO.insertBatch(enterpriseId, insertPictureList);
    }

    /**
     * 获取失败信息
     */
    private String getCaptureDictErrorMsg(List<CapturePictureDTO> captureDTOList) {
        if (CollectionUtils.isEmpty(captureDTOList)) {
            return "抓图结果为空";
        }
        return captureDTOList.stream().filter(v -> StringUtils.isNotBlank(v.getErrorCode()))
                .map(v -> MessageFormat.format("{0}:{1}:{2}:{3}", v.getDeviceId(), v.getChannelNo(), v.getCaptureTime().toLocalTime(), v.getErrorMsg()))
                .collect(Collectors.joining(","));
    }

    @Override
    public void deleteAndSubmitCaptureTask(String enterpriseId, LocalDate date, List<Long> retryRuleIds) {
        if (CollectionUtils.isEmpty(retryRuleIds)) {
            return;
        }
        LocalDate today = Objects.isNull(date) ? LocalDate.now() : date;
        LocalDate reportDate = today.minusDays(1);
        log.info("删除并重新提交抓图任务, enterpriseId:{}, date:{}", enterpriseId, today);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

        // 查询所有有效期内的规则
        List<AiAnalysisRuleDO> ruleList = aiAnalysisRuleDAO.getListByPeriod(enterpriseId, today, retryRuleIds);
        // 过滤今天已经生成的规则
        List<Long> ruleIds = CollStreamUtil.toList(ruleList, AiAnalysisRuleDO::getId);
        Set<Long> existRuleIds = aiAnalysisReportDAO.getExistRule(enterpriseId, ruleIds, reportDate);
        ruleList = ruleList.stream().filter(v -> !existRuleIds.contains(v.getId())).collect(Collectors.toList());

        List<Long> noGeneratedRuleIds = CollStreamUtil.toList(ruleList, AiAnalysisRuleDO::getId);
        // 删除任务
        aiAnalysisCaptureTaskDAO.removeByRuleIds(enterpriseId, noGeneratedRuleIds, today);
        // 删除图片
        aiAnalysisPictureDAO.removeByRuleIds(enterpriseId, noGeneratedRuleIds, today);
        // 重新生成
        submitCaptureTask(enterpriseId, today, noGeneratedRuleIds);
    }

    /**
     * 获取门店id
     */
    private List<String> getStoreByStoreRangeCommonDTO(String enterpriseId, String storeRangeStr) {
        List<StoreWorkCommonDTO> storeRange = JSONObject.parseArray(storeRangeStr, StoreWorkCommonDTO.class);
        List<SwStoreWorkRangeDO> rangeList = CollStreamUtil.toList(storeRange, range -> SwStoreWorkRangeDO.builder().mappingId(range.getValue()).type(range.getType()).build());
        List<StoreAreaDTO> storeAreaDTOList = storeWorkService.getStoreRange(enterpriseId, rangeList);
        return CollStreamUtil.toList(storeAreaDTOList, StoreAreaDTO::getStoreId);
    }

    /**
     * 获取门店用户
     */
    private Map<String, List<String>> getUserByStoreRangeCommonDTO(String enterpriseId, String reportPusher, List<String> storeIds) {
        List<StoreWorkCommonDTO> storeRange = JSONObject.parseArray(reportPusher, StoreWorkCommonDTO.class);
        List<String> allUserIds = userPersonInfoService.getUserIdListByCommonDTO(enterpriseId, storeRange);
        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(enterpriseId, storeIds, null);
        authStoreUserDTOS.forEach(v -> v.getUserIdList().retainAll(allUserIds));
        return CollStreamUtil.toMap(authStoreUserDTOS, AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList);
    }

    /**
     * 获取需要进行抓拍的IPC和NVR设备
     */
    private List<AiAnalysisDeviceDTO> getDeviceInfo(String enterpriseId, List<String> storeIds, AiAnalysisRuleDO rule) {
        boolean allScene = Constants.INDEX_ZERO.equals(rule.getCaptureDevice());
        Set<Long> sceneIds = Collections.emptySet();
        if (!allScene && StringUtils.isNotBlank(rule.getCaptureDeviceScene())) {
            sceneIds = CollStreamUtil.toSet(Arrays.asList(rule.getCaptureDeviceScene().split(Constants.COMMA)), Long::parseLong);
        }
        // 查询门店下所有设备
        List<DeviceDO> allDeviceList = new ArrayList<>();
        // 支持的设备来源
        List<String> supportDeviceSource = Arrays.asList(
                YunTypeEnum.YINGSHIYUN.getCode(),
                YunTypeEnum.YINGSHIYUN_GB.getCode(),
                YunTypeEnum.YUNSHITONG.getCode(),
                YunTypeEnum.JFY.getCode()
        );
        ListUtils.partition(storeIds, 200).forEach(batchStoreIds -> {
            allDeviceList.addAll(deviceDao.getDeviceByStoreIds(enterpriseId, batchStoreIds, null, supportDeviceSource));
        });
        // 区分NVR和IPC设备，并过滤场景
        List<DeviceDO> nvrDeviceList = new ArrayList<>();
        List<AiAnalysisDeviceDTO> result = new ArrayList<>();
        for (DeviceDO deviceDO : allDeviceList) {
            if (Boolean.TRUE.equals(deviceDO.getHasChildDevice())) {
                nvrDeviceList.add(deviceDO);
            } else if (allScene || sceneIds.contains(deviceDO.getStoreSceneId())) {
                result.add(new AiAnalysisDeviceDTO(deviceDO.getDeviceId(), "0", deviceDO.getStoreSceneId()));
            }
        }
        if (CollectionUtils.isNotEmpty(nvrDeviceList)) {
            // 查询所有通道
            List<String> parentDeviceIds = CollStreamUtil.toList(nvrDeviceList, DeviceDO::getDeviceId);
            List<DeviceChannelDO> channelList = deviceChannelMapper.getByParentDeviceIds(enterpriseId, parentDeviceIds, new ArrayList<>(sceneIds));
            if (CollectionUtils.isNotEmpty(channelList)) {
                result.addAll(CollStreamUtil.toList(channelList, channel -> new AiAnalysisDeviceDTO(channel.getParentDeviceId(), channel.getChannelNo(), channel.getStoreSceneId())));
            }
        }
        return result;
    }
}
