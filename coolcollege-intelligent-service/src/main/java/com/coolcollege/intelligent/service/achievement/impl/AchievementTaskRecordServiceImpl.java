package com.coolcollege.intelligent.service.achievement.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.achievement.AchievementTaskRecordMapper;
import com.coolcollege.intelligent.dao.achievement.PanasonicMapper;
import com.coolcollege.intelligent.dao.activity.StoreSampleExtractionMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDetailDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTaskRecordDO;
import com.coolcollege.intelligent.model.achievement.entity.TaskModelsMappingDO;
import com.coolcollege.intelligent.model.activity.entity.StoreSampleExtractionDO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.AchievementTaskStoreSubmitDTO;
import com.coolcollege.intelligent.model.unifytask.dto.ProductInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskModelsDTO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementTaskStoreQuery;
import com.coolcollege.intelligent.service.achievement.AchievementTaskRecordService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新品上架/旧品下架
 *
 * @author byd
 * @date 2024-03-16 14:26
 */
@Slf4j
@Service
public class AchievementTaskRecordServiceImpl implements AchievementTaskRecordService {

    @Resource
    private AchievementTaskRecordMapper achievementTaskRecordMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private UserAuthMappingService userAuthMappingService;

    @Resource
    private RegionService regionService;
    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    private PanasonicMapper panasonicMapper;

    @Resource
    private StoreSampleExtractionMapper storeSampleExtractionMapper;
    @Autowired
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public boolean addRecord(TaskMessageDTO taskMessageDTO, TaskSubDO taskSubDO) {

        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, unifyTaskId);
        if (Objects.isNull(taskParentDO)) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(taskMessageDTO.getTaskInfo());
        String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
        List<ProductInfoDTO> productInfoDTOS = JSONObject.parseArray(taskInfoStr, ProductInfoDTO.class);
        TaskSubDO newTaskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, taskSubDO.getId());
        String userName = enterpriseUserDao.selectNameByUserId(enterpriseId, newTaskSubDO.getCreateUserId());
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, newTaskSubDO.getUnifyTaskId(), newTaskSubDO.getStoreId(), newTaskSubDO.getLoopCount());
        AchievementTaskRecordDO achievementTaskRecordDO = AchievementTaskRecordDO.builder()
                .createTime(new Date())
                .editTime(new Date())
                .subBeginTime(new Date(newTaskSubDO.getSubBeginTime()))
                .subEndTime(new Date(newTaskSubDO.getSubEndTime()))
                .unifyTaskId(newTaskSubDO.getUnifyTaskId())
                .taskName(taskParentDO.getTaskName())
                .loopCount(1L)
                .storeId(newTaskSubDO.getStoreId())
                .storeName(newTaskSubDO.getStoreName())
                .regionId(newTaskSubDO.getRegionId())
                .regionPath(taskStoreDO.getRegionWay())
                .createUserId(newTaskSubDO.getCreateUserId())
                .createUserName(userName)
                .deleted(false)
                .status(0)
                .taskType(taskParentDO.getTaskType())
                .taskStoreId(taskStoreDO.getId())
                .build();
        achievementTaskRecordMapper.insertSelective(achievementTaskRecordDO, enterpriseId);

        List<TaskModelsMappingDO> dos=new ArrayList<>();
        for (ProductInfoDTO productInfoDTO : productInfoDTOS) {
            TaskModelsMappingDO mappingDO = TaskModelsMappingDO.builder()
                    .taskStoreId(taskStoreDO.getId())
                    .productModel(productInfoDTO.getType())
                    .categoryCode(productInfoDTO.getCategoryCode())
                    .categoryName(productInfoDTO.getCategoryName())
                    .middleClassCode(productInfoDTO.getMiddleCategoryCode())
                    .middleClassName(productInfoDTO.getMiddleCategoryName())
                    .smallCategoryCode(productInfoDTO.getSmallCategoryCode())
                    .smallCategoryName(productInfoDTO.getSmallCategoryName())
                    .build();
            dos.add(mappingDO);
        }
        Integer i = panasonicMapper.batchInsertTaskModelsMapping(dos);
        log.info("松下插入任务型号信息：{},{}",newTaskSubDO.getStoreId(), i);
        return true;
    }

    @Override
    public boolean addRecord(String enterpriseId, TaskParentDO taskParent, TaskSubDO taskSubDO) {
        if (Objects.isNull(taskParent)) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(taskParent.getTaskInfo());
        String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
        List<ProductInfoDTO> productInfoDTOS = JSONObject.parseArray(taskInfoStr, ProductInfoDTO.class);
        TaskSubDO newTaskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, taskSubDO.getId());
        String userName = enterpriseUserDao.selectNameByUserId(enterpriseId, newTaskSubDO.getCreateUserId());
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, newTaskSubDO.getUnifyTaskId(), newTaskSubDO.getStoreId(), newTaskSubDO.getLoopCount());
        AchievementTaskRecordDO achievementTaskRecordDO = AchievementTaskRecordDO.builder()
                .createTime(new Date())
                .editTime(new Date())
                .subBeginTime(new Date(newTaskSubDO.getSubBeginTime()))
                .subEndTime(new Date(newTaskSubDO.getSubEndTime()))
                .unifyTaskId(newTaskSubDO.getUnifyTaskId())
                .taskName(taskParent.getTaskName())
                .loopCount(1L)
                .storeId(newTaskSubDO.getStoreId())
                .storeName(newTaskSubDO.getStoreName())
                .regionId(newTaskSubDO.getRegionId())
                .regionPath(taskStoreDO.getRegionWay())
                .createUserId(newTaskSubDO.getCreateUserId())
                .createUserName(userName)
                .deleted(false)
                .status(0)
                .taskType(taskParent.getTaskType())
                .taskStoreId(taskStoreDO.getId())
                .build();
        achievementTaskRecordMapper.insertSelective(achievementTaskRecordDO, enterpriseId);

        List<TaskModelsMappingDO> dos=new ArrayList<>();
        for (ProductInfoDTO productInfoDTO : productInfoDTOS) {
            TaskModelsMappingDO mappingDO = TaskModelsMappingDO.builder()
                    .taskStoreId(taskStoreDO.getId())
                    .productModel(productInfoDTO.getType())
                    .categoryCode(productInfoDTO.getCategoryCode())
                    .categoryName(productInfoDTO.getCategoryName())
                    .middleClassCode(productInfoDTO.getMiddleCategoryCode())
                    .middleClassName(productInfoDTO.getMiddleCategoryName())
                    .smallCategoryCode(productInfoDTO.getSmallCategoryCode())
                    .smallCategoryName(productInfoDTO.getSmallCategoryName())
                    .build();
            dos.add(mappingDO);
        }
        Integer i = panasonicMapper.batchInsertTaskModelsMapping(dos);
        log.info("松下插入任务型号信息：{},{}",newTaskSubDO.getStoreId(), i);
        return true;
    }

    @Override
    public boolean delRecord(String enterpriseId, Long taskId) {
        achievementTaskRecordMapper.deleteByTaskId(taskId, enterpriseId);
        return false;
    }

    @Override
    public PageInfo<AchievementTaskRecordDTO> achievementStoreTaskList(String enterpriseId, AchievementTaskStoreQuery query, String userId) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AchievementTaskRecordDO> taskRecordDOList = achievementTaskRecordMapper.selectList(enterpriseId, query);
        PageInfo pageInfo = new PageInfo(taskRecordDOList);
        if(CollectionUtils.isEmpty(taskRecordDOList)){
            return pageInfo;
        }
        List<Long> taskStoreIdList = taskRecordDOList.stream().map(AchievementTaskRecordDO::getTaskStoreId).collect(Collectors.toList());
        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.taskStoreListByIdList(enterpriseId, taskStoreIdList);
        Map<String, List<PersonDTO>> personMap = unifyTaskStoreService.getTaskPerson(enterpriseId, taskStoreDOList);
        List<AchievementTaskRecordDTO> recordDTOList = new ArrayList<>();
        taskRecordDOList.forEach(taskRecordDO -> {
            AchievementTaskRecordDTO achievementTaskRecordDTO = AchievementTaskRecordDTO.builder()
                        .id(taskRecordDO.getId())
                        .subBeginTime(taskRecordDO.getSubBeginTime())
                        .subEndTime(taskRecordDO.getSubEndTime())
                        .unifyTaskId(taskRecordDO.getUnifyTaskId())
                        .taskName(taskRecordDO.getTaskName())
                        .loopCount(taskRecordDO.getLoopCount())
                        .storeId(taskRecordDO.getStoreId())
                        .storeName(taskRecordDO.getStoreName())
                        .regionId(taskRecordDO.getRegionId())
                        .status(taskRecordDO.getStatus())
                        .taskType(taskRecordDO.getTaskType())
                        .taskStoreId(taskRecordDO.getTaskStoreId())
                        .report(taskRecordDO.getPlanDelistTime() != null)
                       .handleUser(personMap.get(taskRecordDO.getUnifyTaskId()+"#"+taskRecordDO.getStoreId() + "#" + UnifyNodeEnum.FIRST_NODE.getCode() + "#" + taskRecordDO.getLoopCount()))
                        .build();
            List<PersonDTO> handleList = ListUtils.emptyIfNull(achievementTaskRecordDTO.getHandleUser());
            achievementTaskRecordDTO.setHandle(handleList.stream().anyMatch(personDTO -> personDTO.getUserId().equals(userId)));
            recordDTOList.add(achievementTaskRecordDTO);
        });
        pageInfo.setList(recordDTOList);
        return pageInfo;
    }

    @Override
    public AchievementTaskRecordDetailDTO storeTaskDetail(String enterpriseId, Long taskId, String storeId) {
        AchievementTaskRecordDO achievementTaskRecordDO = achievementTaskRecordMapper.selectDetail(enterpriseId, taskId, storeId);
        if(achievementTaskRecordDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }

        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.taskStoreListByIdList(enterpriseId, Arrays.asList(achievementTaskRecordDO.getTaskStoreId()));
        Map<String, List<PersonDTO>> personMap = unifyTaskStoreService.getTaskPerson(enterpriseId, taskStoreDOList);


        //查询任务下型号
        List<TaskModelsMappingDO> models = panasonicMapper.selectTaskModelsMapping(achievementTaskRecordDO.getTaskStoreId());
        for (TaskModelsMappingDO model : models) {
            String storeId1 = achievementTaskRecordDO.getStoreId();

            //查物理门店
            String physicalNum=panasonicMapper.selectPhysicalNum(storeId1);
            if (StringUtils.isNotBlank(physicalNum)){
                //查询出样表
                StoreSampleExtractionDO storeSampleExtractionDO = storeSampleExtractionMapper.selectByphysicalNumAndModel(physicalNum, model.getProductModel());
                if (storeSampleExtractionDO != null){
                    model.setRemainNum(storeSampleExtractionDO.getGoodsNum());
                }
            }

        }
        AchievementTaskRecordDetailDTO detailDTO = AchievementTaskRecordDetailDTO.builder()
                .id(achievementTaskRecordDO.getId())
                .subBeginTime(achievementTaskRecordDO.getSubBeginTime())
                .subEndTime(achievementTaskRecordDO.getSubEndTime())
                .unifyTaskId(achievementTaskRecordDO.getUnifyTaskId())
                .taskName(achievementTaskRecordDO.getTaskName())
                .loopCount(achievementTaskRecordDO.getLoopCount())
                .storeId(achievementTaskRecordDO.getStoreId())
                .storeName(achievementTaskRecordDO.getStoreName())
                .regionId(achievementTaskRecordDO.getRegionId())
                .status(achievementTaskRecordDO.getStatus())
                .taskType(achievementTaskRecordDO.getTaskType())
                .taskStoreId(achievementTaskRecordDO.getTaskStoreId())
                .report(achievementTaskRecordDO.getPlanDelistTime() != null)
                .produceUserId(achievementTaskRecordDO.getProduceUserId())
                .produceUserName(achievementTaskRecordDO.getProduceUserName())
                .deleted(achievementTaskRecordDO.getDeleted())
                .planDelistTime(achievementTaskRecordDO.getPlanDelistTime())
                .submitTime(achievementTaskRecordDO.getSubmitTime())
                .handleUser(personMap.get(achievementTaskRecordDO.getUnifyTaskId()+"#"+achievementTaskRecordDO.getStoreId() + "#" + UnifyNodeEnum.FIRST_NODE.getCode() + "#" + achievementTaskRecordDO.getLoopCount()))
                .planGoodTime(achievementTaskRecordDO.getPlanGoodTime())
                .delistNum(achievementTaskRecordDO.getGoodsNum())
                .taskModels(models)
                .build();
        return detailDTO;
    }

    @Override
    public PageInfo<AchievementTaskRecordDTO> achievementMyStoreTaskList(String enterpriseId, AchievementTaskStoreQuery query, String userId) {
        // 判断是否是管理员
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if(!isAdmin){
            List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, userId, null);
            if(CollectionUtils.isEmpty(regionPathDTOList)){
                return new PageInfo<>();
            }
            List<String> regionPathList = regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            query.setRegionPathList(regionPathList);
        }
        return this.achievementStoreTaskList(enterpriseId, query, userId);
    }

    @Override
    @Transactional
    public boolean submitTask(String enterpriseId, AchievementTaskStoreSubmitDTO storeSubmitDTO, String userId, String userName) {
        String storeId = storeSubmitDTO.getStoreId();
        Integer i = panasonicMapper.selectStoreByStoreId(storeId);
        if(i==0){
            throw new ServiceException("门店不存在！");
        }
        AchievementTaskRecordDO achievementTaskRecordDO = achievementTaskRecordMapper.selectDetail(enterpriseId, storeSubmitDTO.getUnifyTaskId(), storeId);
        if(achievementTaskRecordDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if(achievementTaskRecordDO.getStatus() == 1){
            throw new ServiceException("任务已提交！");
        }
        List<TaskModelsDTO> taskModelsDTOList = storeSubmitDTO.getTaskModelsDTOList();
        if (CollectionUtils.isEmpty(taskModelsDTOList)){
            throw new ServiceException("任务型号数据不能为空！");
        }
        List<String> imgs = taskModelsDTOList.stream().map(c -> c.getPicture()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(imgs)){
            throw new ServiceException("图片不能为空！");
        }
        List<Long> finalIds = taskModelsDTOList.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<TaskModelsMappingDO> taskModelsMappingDOS = panasonicMapper.selectTaskModelsMappingByIds(finalIds);
        if(CollectionUtils.isEmpty(taskModelsMappingDOS)){
            throw new ServiceException("任务型号数据不存在！");
        }
        Map<Long, TaskModelsMappingDO> mappingDOMap = taskModelsMappingDOS.stream().collect(Collectors.toMap(TaskModelsMappingDO::getId, c -> c, (v1, v2) -> v1));
        achievementTaskRecordDO.setSubmitTime(new Date());
        achievementTaskRecordDO.setProduceUserId(userId);
        achievementTaskRecordDO.setProduceUserName(userName);
        achievementTaskRecordDO.setStatus(1);

        String physicalNum = panasonicMapper.selectPhysicalNum(storeId);
        if(TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(achievementTaskRecordDO.getTaskType())){
            for (TaskModelsDTO taskModelsDTO : taskModelsDTOList) {
                achievementTaskRecordDO.setPlanGoodTime(new Date(taskModelsDTO.getPlanDelistTime()));

                TaskModelsMappingDO taskModelsMappingDO = mappingDOMap.get(taskModelsDTO.getId());
                //出样记录表更新 先查
                StoreSampleExtractionDO storeSampleExtractionDO = storeSampleExtractionMapper.selectByStoreIdAndModel(storeId, taskModelsMappingDO.getProductModel());

                if(storeSampleExtractionDO == null){
                    StoreSampleExtractionDO build = StoreSampleExtractionDO.builder()
                            .storeId(achievementTaskRecordDO.getStoreId())
                            .actualStoreName(achievementTaskRecordDO.getStoreName())
                            .categoryName(taskModelsMappingDO.getCategoryName())
                            .categoryCode(taskModelsMappingDO.getCategoryCode())
                            .goodsNum(taskModelsDTO.getGoodNum())
                            .sampleExtractionAmount(taskModelsDTO.getGoodNum())
                            .picture(taskModelsDTO.getPicture())
                            .productModel(taskModelsMappingDO.getProductModel())
                            .status(1)
                            .sampleExtractionTime(new Date(taskModelsDTO.getPlanDelistTime()))
                            .createTime(new Date())
                            .createUserId(userId)
                            .updateTime(new Date())
                            .updateUserId(userId)
                            .sampleUserId(userId)
                            .sampleUserName(userName)
                            .physicalStoreNum(physicalNum)
                            .build();
                    storeSampleExtractionMapper.insertSelective(build);
                }else {
                    //不为空就更新
                    storeSampleExtractionDO.setStatus(1);
                    storeSampleExtractionDO.setGoodsNum(taskModelsDTO.getGoodNum()+Optional.ofNullable(storeSampleExtractionDO.getGoodsNum()).orElse(0));
                    storeSampleExtractionDO.setSampleExtractionAmount(taskModelsDTO.getGoodNum()+Optional.ofNullable(storeSampleExtractionDO.getSampleExtractionAmount()).orElse(0));
                    storeSampleExtractionDO.setPicture(taskModelsDTO.getPicture());
                    storeSampleExtractionDO.setSampleExtractionTime(new Date(taskModelsDTO.getPlanDelistTime()));
                    storeSampleExtractionDO.setUpdateTime(new Date());
                    storeSampleExtractionDO.setUpdateUserId(userId);
                    storeSampleExtractionDO.setSampleUserId(userId);
                    storeSampleExtractionDO.setSampleUserName(userName);
                    storeSampleExtractionMapper.updateByPrimaryKeySelective(storeSampleExtractionDO);
                }
                taskModelsMappingDO.setPlanGoodTime(new Date(taskModelsDTO.getPlanDelistTime()));
                taskModelsMappingDO.setGoodsNum(taskModelsDTO.getGoodNum());
                taskModelsMappingDO.setPicture(taskModelsDTO.getPicture());

                panasonicMapper.updateTaskModelsMappingById(taskModelsMappingDO);
            }
        }else {
            for (TaskModelsDTO taskModelsDTO : taskModelsDTOList) {
                TaskModelsMappingDO taskModelsMappingDO = mappingDOMap.get(taskModelsDTO.getId());
                //更新撤样时间
                achievementTaskRecordDO.setPlanDelistTime(new Date(taskModelsDTO.getPlanDelistTime()));
                //出样记录表更新 先查
                StoreSampleExtractionDO storeSampleExtractionDO = storeSampleExtractionMapper.selectByStoreIdAndModel(storeId, taskModelsMappingDO.getProductModel());
                //撤样减库存
                Integer goodsNum = storeSampleExtractionDO.getGoodsNum();
                if (goodsNum - taskModelsDTO.getGoodNum() <0){
                    throw new ServiceException("库存不够或未出样！");
                }else if (goodsNum - taskModelsDTO.getGoodNum() == 0){
                    storeSampleExtractionDO.setStatus(0);
                }
                storeSampleExtractionDO.setGoodsNum(goodsNum - Optional.ofNullable(taskModelsDTO.getGoodNum()).orElse(0));
                storeSampleExtractionDO.setWithdrawSampleAmount(taskModelsDTO.getGoodNum()+Optional.ofNullable(storeSampleExtractionDO.getWithdrawSampleAmount()).orElse(0));
                storeSampleExtractionDO.setUpdateTime(new Date());
                storeSampleExtractionDO.setUpdateUserId(userId);
                storeSampleExtractionMapper.updateByPrimaryKeySelective(storeSampleExtractionDO);

                taskModelsMappingDO.setPlanDelistTime(new Date(taskModelsDTO.getPlanDelistTime()));
                taskModelsMappingDO.setGoodsNum(taskModelsDTO.getGoodNum());
                taskModelsMappingDO.setPicture(taskModelsDTO.getPicture());
                panasonicMapper.updateTaskModelsMappingById(taskModelsMappingDO);
            }

        }
        //同一批次同一节点的同一的必是已完成
        taskSubMapper.updateSubStatusComplete(enterpriseId, achievementTaskRecordDO.getUnifyTaskId(), achievementTaskRecordDO.getStoreId(), achievementTaskRecordDO.getLoopCount());
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, achievementTaskRecordDO.getTaskStoreId());
        taskStoreDO.setSubStatus(UnifyStatus.COMPLETE.getCode());
        taskStoreDO.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        taskStoreDO.setHandleTime(new Date());
        taskStoreMapper.updateByPrimaryKey(enterpriseId, taskStoreDO);
        //查询是否有未完成的门店任务，没有就改父任务
        List<TaskStoreDO> taskStoreDOS = taskStoreMapper.selectByUnifyTaskId(enterpriseId, achievementTaskRecordDO.getUnifyTaskId());
        List<TaskStoreDO> notOverTask = taskStoreDOS.stream().filter(taskStoreDO1 -> !UnifyStatus.COMPLETE.getCode().equals(taskStoreDO1.getSubStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notOverTask)){
            taskParentMapper.updateParentStatusByTaskId(enterpriseId,UnifyStatus.COMPLETE.getCode(), achievementTaskRecordDO.getUnifyTaskId());
        }
        achievementTaskRecordMapper.updateByPrimaryKeySelective(achievementTaskRecordDO, enterpriseId);

        return true;
    }

    @Override
    public boolean sendRemindMsg(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        String endTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<AchievementTaskRecordDO> list = achievementTaskRecordMapper.selectRemindList(enterpriseId, DateUtils.convertTimeToString(System.currentTimeMillis(),
                DateUtils.DATE_FORMAT_SEC), endTime);
        if(CollectionUtils.isEmpty(list)){
            log.info("sendRemindMsg没有需要提醒的任务");
            return false;
        }
        list.forEach(achievementTaskRecordDO -> {
            List<TaskSubDO> taskSubDOList = taskSubMapper.getTaskSubDOListForSend(enterpriseId, achievementTaskRecordDO.getUnifyTaskId(),
                    achievementTaskRecordDO.getStoreId(), null, null);
            Set<String> handleUserId = taskSubDOList.stream().map(TaskSubDO::getHandleUserId).collect(Collectors.toSet());
            jmsTaskService.sendUnifyTaskJms(achievementTaskRecordDO.getTaskType(), new ArrayList<>(handleUserId),
                    "product_remind", enterpriseId, achievementTaskRecordDO.getStoreName(), System.currentTimeMillis(), "【" + achievementTaskRecordDO.getProductCategoryName() + "】、" + "【" + achievementTaskRecordDO.getProductMiddleClassName() + "】、" + "【" + achievementTaskRecordDO.getProductType() + "】", achievementTaskRecordDO.getPlanDelistTime().getTime(),
                    "样机撤样",
                    false, achievementTaskRecordDO.getSubBeginTime().getTime(), achievementTaskRecordDO.getStoreId(), null, false, achievementTaskRecordDO.getUnifyTaskId(), null);
        });
        return true;
    }

    private List<RegionPathDTO> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList){
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(regionIdList)){
            regionPathList = regionService.getRegionPathByList(enterpriseId, regionIdList);
        }
        return regionPathList;
    }
}
