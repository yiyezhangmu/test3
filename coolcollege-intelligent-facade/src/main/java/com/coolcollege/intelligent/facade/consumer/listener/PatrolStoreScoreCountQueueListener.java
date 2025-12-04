package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.QuestionCreateTypeEnum;
import com.coolcollege.intelligent.common.enums.fsGroup.FsSceneEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.fsGroup.*;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolStorePlanDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dto.EnterpriseQuestionSettingsDTO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionListDTO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.fsGroup.FsGroupCardDO;
import com.coolcollege.intelligent.model.fsGroup.FsGroupDO;
import com.coolcollege.intelligent.model.fsGroup.FsGroupSceneMappingDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePlanDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.PatrolStoreScoreMsgDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.fsGroup.FsGroupService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCheckService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_DAY;
import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_5;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.STORE_SELF_CHECK;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * 巡店计算分数
 *
 * @author chenyupeng
 * @since 2022/3/3
 */
@Slf4j
@Service
public class PatrolStoreScoreCountQueueListener implements MessageListener {

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private TaskSopService taskSopService;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private QuestionParentInfoService questionParentInfoService;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private UserPersonInfoService userPersonInfoService;
    @Resource
    private StoreService storeService;
    @Resource
    private TbPatrolStorePlanDao patrolStorePlanDao;

    @Resource
    private FsGroupMapper fsGroupMapper;

    @Resource
    private FsGroupSceneMapper fsGroupSceneMapper;

    @Resource
    private FsGroupSceneMappingMapper fsGroupSceneMappingMapper;

    @Resource
    private FsGroupCardMapper fsGroupCardMapper;



    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private SendCardService sendCardService;

    @Resource
    private FsService fsService;

    @Resource
    private FsGroupService fsGroupService;

    @Resource
    private PatrolStoreCheckService patrolStoreCheckService;

    @Resource
    private TbDataTableMapper dataTableMapper;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if (StringUtils.isBlank(text)) {
            log.info("消息体为空,tag:{},messageId:{}", message.getTag(), message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "PatrolStoreCapturePictureQueueListener:" + message.getMsgID();
        boolean lock = redisUtil.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if (lock) {
            try {
                countScore(text);
            } catch (Exception e) {
                log.error("PatrolStoreScoreCountQueueListener consume error", e);
                return Action.ReconsumeLater;
            } finally {
                redisUtil.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}", message.getTag(), message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void countScore(String msg) {
        log.info("patrol_store_score_count_queue:{}", msg);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        PatrolStoreScoreMsgDTO dto = JSONObject.parseObject(msg, PatrolStoreScoreMsgDTO.class);
        String enterpriseId = dto.getEid();
        Long businessId = dto.getBusinessId();
        String supervisorId = dto.getSupervisorId();
        Boolean needAgainSendProblem = dto.getNeedAgainSendProblem() == null ? false : dto.getNeedAgainSendProblem();
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        if(!needAgainSendProblem){
            //发送巡店报告
            sendNoticeReport(enterpriseId, businessId, dto.getSubTaskId());
            //需要复审
            needReCheck(enterpriseId, businessId, supervisorId);
            //巡店计划
            overPlanCheck(enterpriseId, businessId, supervisorId);
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        // 不合格检查项list
        List<TbDataStaTableColumnDO> failDataStaColumnList =
                tbDataStaTableColumnMapper.selectFailByBusinessId(enterpriseId, businessId, PATROL_STORE);
        if (CollectionUtils.isNotEmpty(failDataStaColumnList) && storeCheckSettingDO.getAutoSendProblem()) {
            // 自动发起问题工单
            Set<Long> failedMetaStaColumnIds =
                    failDataStaColumnList.stream().map(TbDataStaTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
            List<TbMetaStaTableColumnDO> metaStaColumnList =
                    tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(failedMetaStaColumnIds));
            Map<Long, TbMetaStaTableColumnDO> idMetaStaColumnMap = metaStaColumnList.stream()
                    .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
            List<StaColumnDTO> failStaColumnList = failDataStaColumnList.stream()
                    .map(a -> StaColumnDTO.builder().tbDataStaTableColumnDO(a)
                            .tbMetaStaTableColumnDO(idMetaStaColumnMap.get(a.getMetaColumnId())).build())
                    .collect(Collectors.toList());

            log.info("failStaColumnList :{}", JSONUtil.toJsonStr(failStaColumnList));
            //门店通临时取消工单发起
            this.autoQuestionOrder(enterpriseId, failStaColumnList, businessId, supervisorId);
        }
        Integer questionNum=CollectionUtils.isEmpty(failDataStaColumnList)?0:failDataStaColumnList.size();
        //发送飞书巡店卡片
        log.info("发送飞书巡店卡片：{}:{}:{}",enterpriseId,businessId,questionNum);
        try {
            sendFsCardMsg(config,enterpriseId,businessId,questionNum);
        }catch (Exception e){
            log.error("发送飞书巡店卡片失败：{}:{}:{}",enterpriseId,businessId,questionNum,e);
        }
    }


    public void sendFsCardMsg(EnterpriseConfigDO config,String enterpriseId,Long businessId,Integer questionNum){
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String feiShuCardEnterprise = redisUtil.hashGet(CommonConstant.FEI_SHU_SEND_CARD_ENTERPRISE, enterpriseId);
        //判断是否飞书类型企业
        if (config.getAppType().equals(AppTypeEnum.FEI_SHU.getValue()) && StringUtils.isNotBlank(feiShuCardEnterprise)) {
            //判断是否有本门店群
            TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            String regionWay = recordDO.getRegionWay();
            if (StringUtils.isBlank(regionWay)){
                log.info("没有门店群不发送消息:{}:{}",enterpriseId,regionWay);
                return;
            }
            List<String> ids = Lists.newArrayList(StringUtils.split(regionWay, "/"));
            if (CollectionUtils.isEmpty(ids)){
                log.info("没有门店群不发送消息:{}:{}",enterpriseId,regionWay);
                return;
            }
            String regionId=ids.get(ids.size() - 1);
            List<FsGroupDO> storeGroups = fsGroupMapper.selectByRegionId(enterpriseId, regionId);
            if (CollectionUtils.isEmpty(storeGroups)){
                log.info("没有门店群不发送消息:{}:{}",enterpriseId,regionId);
                return;
            }
            //根据code查询场景id
            Long sceneId = fsGroupSceneMapper.queryByCode(enterpriseId, FsSceneEnum.REPORT.getCode());
            if (Objects.isNull(sceneId)){
                log.info("没有开启门店场景不发送消息:{}:{}",enterpriseId,regionId);
                return;
            }
            List<String> chatIds = storeGroups.stream().map(FsGroupDO::getChatId).collect(Collectors.toList());
            //根据chatId查群配置
            List<FsGroupSceneMappingDO> mappingDOS = fsGroupSceneMappingMapper.queryByChatIds(enterpriseId, chatIds);
            //留下为对应场景id的chatIds
            List<String> hasConfigChatIds = mappingDOS.stream().filter(a -> a.getSceneId().equals(sceneId)).map(FsGroupSceneMappingDO::getChatId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(hasConfigChatIds)){
                log.info("没有开启配置的门店群:{}:{}",enterpriseId,regionId);
                return;
            }
            //查询该场景之下有哪些卡片
            List<FsGroupCardDO> cardDOS = fsGroupCardMapper.queryBySceneId(enterpriseId, sceneId);
            if (CollectionUtils.isEmpty(cardDOS)){
                log.info("没有配置卡片:{}:{}",enterpriseId,regionId);
                return;
            }
            //获取飞书token
            String token = fsService.getAccessToken(config.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
            for (FsGroupCardDO cardDO : cardDOS) {
                for (FsGroupDO groupDO: storeGroups) {
                    String sendRole = cardDO.getSendRole();
                    //计算值
                    Map<String, String> params = Maps.newHashMap();
                    params.put("storeName", recordDO.getStoreName().toString());
                    params.put("checkScore",recordDO.getScore().toString());
                    params.put("todoQuestionNum",questionNum.toString());
                    params.put("totalColumnNum",recordDO.getTotalCalColumnNum().toString());
                    params.put("passNum",recordDO.getPassNum().toString());
                    params.put("failNum",recordDO.getFailNum().toString());
                    params.put("handleUserName",recordDO.getSupervisorName().toString());
                    params.put("patrolStoreTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(recordDO.getSignEndTime()));
                    if(StringUtils.isBlank(sendRole)){
                        log.info("卡片没有配置角色，发送群内全部成员");
                        String content = StringUtil.formatFsCard(cardDO.getCardTemplate(),params);
                        fsGroupService.sendFsMsg(token,"chat_id",groupDO.getChatId(),"interactive",content);
                        return;
                    }
                    log.info("巡店报告发送指定人");
                    List<String> roleIds = Lists.newArrayList(StringUtils.split(sendRole, ","));
                    String chatId = groupDO.getChatId();
                    try {
                        //根据chatId查询门店下成员
                        String bindRegionIds = groupDO.getBindRegionIds();
                        List<String> regionIds = Lists.newArrayList(StringUtils.split(bindRegionIds, ","));
                        //查询区域下所属人员
                        List<String> userIds = enterpriseUserDao.getUserIdsByRegionIdList(enterpriseId, regionIds);
                        if (CollectionUtils.isEmpty(userIds)){
                            log.info("没有配置区域下成员:{}:{}:{}",enterpriseId,chatId,regionIds);
                            continue;
                        }
                        //查出目标人员
                        List<String> targetUserIds = enterpriseUserRoleMapper.getUserIdsByRoleIds(enterpriseId, roleIds, userIds);

                        for (String openId : targetUserIds) {
                            //发送卡片消息
                            sendCardService.sendFsGroupTargetUserCardMsg(enterpriseId,token,chatId,openId, cardDO.getCardTemplate(),params);
                        }
                    }catch (Exception e){
                        log.error("飞书巡店卡片发送失败chatId:{}",chatId,e);
                    }
                }
            }
        }
        log.info("发送飞书群卡片完毕");
    }



    private void autoQuestionOrder(String enterpriseId, List<StaColumnDTO> failStaColumnList, Long businessId, String userId) {
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        BuildQuestionRequest buildQuestionRequest = new BuildQuestionRequest();
        buildQuestionRequest.setQuestionType(QuestionTypeEnum.PATROL_STORE.getCode());
        if (TaskTypeEnum.PATROL_STORE_AI.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            buildQuestionRequest.setQuestionType(QuestionTypeEnum.PATROL_STORE.getCode());
        }else if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            buildQuestionRequest.setQuestionType(QuestionTypeEnum.SAFETY_CHECK.getCode());
        }else if (TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            buildQuestionRequest.setQuestionType(QuestionTypeEnum.MYSTERIOUS_GUEST.getCode());
        }
        long createTime = System.currentTimeMillis();
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, userId);
        List<BuildQuestionDTO> taskList = new ArrayList<>();
        String questionName = tbPatrolStoreRecordDO.getStoreName() + " ";
        if (tbPatrolStoreRecordDO.getTaskId() > 0) {
            questionName = questionName + tbPatrolStoreRecordDO.getTaskName() + "的工单";
        } else {
            Date signEndTime = tbPatrolStoreRecordDO.getSignEndTime();
            if (signEndTime == null) {
                signEndTime = new Date();
            }
            questionName = questionName + com.coolcollege.intelligent.common.util.DateUtils.
                    convertTimeToString(signEndTime.getTime(), DATE_FORMAT_SEC_5) + "的工单";
        }
        if (questionName.length() > Constants.COLUMN_NAME_MAX_LENGTH) {
            questionName = questionName.substring(0, Constants.COLUMN_NAME_MAX_LENGTH - 1);
        }
        buildQuestionRequest.setTaskName(questionName);
        EnterpriseQuestionSettingsDTO questionSettingsDTO = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        Boolean autoQuestionStudyFirst = questionSettingsDTO.getAutoQuestionStudyFirst();
        failStaColumnList.forEach(item -> {
            TbDataStaTableColumnDO data = item.getTbDataStaTableColumnDO();
            TbMetaStaTableColumnDO column = item.getTbMetaStaTableColumnDO();
            BuildQuestionDTO task = new BuildQuestionDTO();
            task.setTaskDesc(data.getCheckText());
            // 先获取检查项中的自动工单有效期设置，不存在的情况下再使用工单设置中的有效期
            JSONObject extendInfo = JSONObject.parseObject(column.getExtendInfo());
            Integer autoQuestionTaskValidity = questionSettingsDTO.getAutoQuestionTaskValidity();
            if (Objects.nonNull(extendInfo)) {
                Boolean isSetAutoQuestionTaskValidity = extendInfo.getBoolean(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY);
                Integer autoQuestionValidity = extendInfo.getInteger(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY);
                if (Objects.nonNull(isSetAutoQuestionTaskValidity) && isSetAutoQuestionTaskValidity && Objects.nonNull(autoQuestionValidity) && autoQuestionValidity > 0) {
                    autoQuestionTaskValidity = autoQuestionValidity;
                }

            }
            task.setEndTime(DateUtils.addHours(new Date(createTime), autoQuestionTaskValidity));
            task.setTaskName(column.getColumnName());
            task.setStoreId(tbPatrolStoreRecordDO.getStoreId());
            List<TaskProcessDTO> process = Lists.newArrayList();
            //整改人
            TaskProcessDTO handPerson = new TaskProcessDTO();
            handPerson.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
            List<GeneralDTO> handUserList = Lists.newArrayList();
            String handleId = column.getQuestionHandlerId();
            String handleType = column.getQuestionHandlerType();
            //如果没有处理人 ai巡店的工单处理人是发起人
            if (TaskTypeEnum.PATROL_STORE_AI.getCode().equals(tbPatrolStoreRecordDO.getPatrolType()) && StringUtils.isEmpty(handleId)) {
                handleId = tbPatrolStoreRecordDO.getCreateUserId();
                handleType = UnifyTaskConstant.PersonType.PERSON;
            }
            dealUsers(userId, process, handPerson, handUserList, handleId, handleType, enterpriseId);
            //审核人
            TaskProcessDTO checkPerson = new TaskProcessDTO();
            checkPerson.setNodeNo(UnifyNodeEnum.SECOND_NODE.getCode());
            List<GeneralDTO> checkUserList = Lists.newArrayList();
            String reCheckId = column.getQuestionRecheckerId();
            if (StringUtils.isNotBlank(reCheckId)) {
                String reCheckType = column.getQuestionRecheckerType();
                dealUsers(userId, process, checkPerson, checkUserList, reCheckId, reCheckType, enterpriseId);
            }
            if(column.getCreateUserApprove() != null && column.getCreateUserApprove()){
                checkUserList.add(new GeneralDTO(UnifyTaskConstant.PersonType.PERSON, userId, createUserName));
                if (StringUtils.isBlank(reCheckId)) {
                    checkPerson.setUser(checkUserList);
                    checkPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                    process.add(checkPerson);
                }
            }

            //二级、以及三级审核人处理
            if (StringUtils.isNotBlank(column.getQuestionApproveUser())) {
                JSONArray jsonArray = JSONUtil.parseArray(column.getQuestionApproveUser());
                List<PersonPositionListDTO> questionApproveUserList = JSONUtil.toList(jsonArray, PersonPositionListDTO.class);
                if (CollectionUtils.isNotEmpty(questionApproveUserList)) {
                    int i = 3;
                    for (PersonPositionListDTO personPositionListDTO : questionApproveUserList) {
                        TaskProcessDTO approvePerson = new TaskProcessDTO();
                        approvePerson.setNodeNo(String.valueOf(i));
                        List<GeneralDTO> appUserList = Lists.newArrayList();
                        for (PersonPositionDTO ccUser : personPositionListDTO.getPeopleList()) {
                            appUserList.add(new GeneralDTO(ccUser.getType().replace("user", "person"), ccUser.getId(), ccUser.getName()));
                        }
                        if(personPositionListDTO.getCreateUserApprove() != null && personPositionListDTO.getCreateUserApprove()){
                            appUserList.add(new GeneralDTO(UnifyTaskConstant.PersonType.PERSON, userId, createUserName));
                        }
                        if (CollectionUtils.isNotEmpty(appUserList)) {
                            approvePerson.setUser(appUserList);
                            approvePerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                            process.add(approvePerson);
                        }
                        i++;
                    }
                }
            }

            //抄送人
            String questionCcId = column.getQuestionCcId();
            if (StringUtils.isNotBlank(questionCcId)) {
                cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(questionCcId);
                List<PersonPositionDTO> ccIdList = JSONUtil.toList(jsonArray, PersonPositionDTO.class);

                TaskProcessDTO ccPerson = new TaskProcessDTO();
                ccPerson.setNodeNo(UnifyNodeEnum.CC.getCode());
                List<GeneralDTO> ccUserList = Lists.newArrayList();
                for (PersonPositionDTO ccUser : ccIdList) {
                    ccUserList.add(new GeneralDTO(ccUser.getType(), ccUser.getId(), ccUser.getName()));
                }
                if (CollectionUtils.isNotEmpty(ccUserList)) {
                    ccPerson.setUser(ccUserList);
                    ccPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                    process.add(ccPerson);
                }
            }
            log.info("questionCcId :{}", questionCcId);

            task.setProcess(process);

            log.info("task :{}", JSONUtil.toJsonStr(task));
            QuestionTaskInfoDTO info = new QuestionTaskInfoDTO();
            // checkPicList
            String checkPics = data.getCheckPics();
            List<String> checkPicList = new ArrayList<>();
            if (StringUtils.isNotBlank(checkPics)) {
                if(checkPics.contains("http")){
                    checkPicList = Arrays.asList(checkPics.split(",(?=http)"));
                }else{
                    checkPicList = Arrays.asList(checkPics.split(","));
                }
            }
            info.setPhotos(checkPicList);
            info.setVideos(checkVideoHandel(data));
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(info.getVideos(), SmallVideoInfoDTO.class);
            smallVideoInfo.setSoundRecordingList(null);
            info.setVideos(JSONObject.toJSONString(smallVideoInfo));
            info.setDataColumnId(data.getId());
            info.setBusinessId(data.getBusinessId());
            info.setMetaColumnId(data.getMetaColumnId());
            info.setMetaColumnName(data.getMetaColumnName());
            info.setContentLearnFirst(autoQuestionStudyFirst);
            info.setSoundRecordingList(checkSoundLostHandel(data.getCheckVideo()));
            info.setCreateType(QuestionCreateTypeEnum.AUTOMATIC.getCode());
            List<CoolCourseVO> courseList = new ArrayList<>();
            if (StringUtils.isNotBlank(column.getCoolCourse())) {
                CoolCourseVO coolCourseVO = JSONObject.parseObject(column.getCoolCourse(), CoolCourseVO.class);
                coolCourseVO.setCourseType(1);
                courseList.add(coolCourseVO);
            }
            if (StringUtils.isNotBlank(column.getFreeCourse())) {
                CoolCourseVO coolCourseVO = JSONObject.parseObject(column.getFreeCourse(), CoolCourseVO.class);
                coolCourseVO.setCourseType(3);
                courseList.add(coolCourseVO);
                ;
            }
            if (column.getSopId() != null && column.getSopId() > 0) {
                TaskSopVO taskSopVO = taskSopService.getSopById(enterpriseId, column.getSopId());
                if (taskSopVO != null) {
                    info.setAttachUrl(JSONObject.toJSONString(taskSopVO));
                }
            }
            info.setCourseList(courseList);
            task.setTaskInfo(info);
            taskList.add(task);
        });
        buildQuestionRequest.setQuestionList(taskList);
        questionParentInfoService.buildQuestion(enterpriseId, buildQuestionRequest, userId, Boolean.TRUE,!STORE_SELF_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType()));
    }

    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     *
     * @param request
     * @return void
     * @author chenyupeng
     * @date 2021/10/14
     */
    public String checkVideoHandel(TbDataStaTableColumnDO request) {

        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(request.getCheckVideo(), SmallVideoInfoDTO.class);
        if (smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())) {
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成就不处理，直接修改
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    return JSONObject.toJSONString(smallVideoInfo);
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    }
                }
            }
        }
        return smallVideoInfo == null ? "{\"videoList\":[]}" : JSONObject.toJSONString(smallVideoInfo);

    }

    private void dealUsers(String userId, List<TaskProcessDTO> process, TaskProcessDTO checkPerson, List<GeneralDTO> checkUserList, String reCheckId, String reCheckType, String enterpriseId) {
        if (StringUtils.isBlank(reCheckId)) {
            reCheckId = userId;
            reCheckType = UnifyTaskConstant.PersonType.PERSON;
        }
        String recheckType = reCheckType;
        String[] reCheckIdArray = reCheckId.split(",");
        Map<String, String> nameMap = changName(enterpriseId, reCheckIdArray, recheckType);
        Arrays.stream(reCheckId.split(",")).forEach(id -> {
            checkUserList.add(new GeneralDTO(recheckType, id, nameMap.get(id)));

        });
        checkPerson.setUser(checkUserList);
        checkPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
        process.add(checkPerson);
    }

    /**
     * 处理音频
     *
     * @param checkVideo 音频
     * @return
     */
    public List<String> checkSoundLostHandel(String checkVideo) {
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(checkVideo, SmallVideoInfoDTO.class);
        return smallVideoInfo == null || CollectionUtils.isEmpty(smallVideoInfo.getSoundRecordingList()) ?
                new ArrayList<>() : smallVideoInfo.getSoundRecordingList();

    }

    private Map<String, String> changName(String enterpriseId, String[] reCheckIdArray, String recheckType) {
        Map<String, String> nameMap = new HashMap<>();
        try {
            //写入名称
            if (StringUtils.isNotBlank(recheckType) && reCheckIdArray.length > 0) {
                switch (recheckType) {
                    case UnifyTaskConstant.PersonType.PERSON:
                        nameMap = enterpriseUserDao.getUserNameMap(enterpriseId, Arrays.asList(reCheckIdArray));
                        break;
                    case UnifyTaskConstant.PersonType.POSITION:
                        List<String> roleIdStrList = Arrays.asList(reCheckIdArray);
                        List<Long> roleIdList = roleIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
                        List<SysRoleDO> roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, roleIdList);
                        nameMap = roleList.stream()
                                .filter(a -> a.getId() != null && a.getRoleName() != null)
                                .collect(Collectors.toMap(data -> String.valueOf(data.getId()), SysRoleDO::getRoleName, (a, b) -> a));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("question#changName,eid:{},recheckType:{},reCheckIdArray:{}", enterpriseId, recheckType, reCheckIdArray, e);
        }
        return nameMap;
    }

    private void sendNoticeReport(String enterpriseId, Long businessId, Long subTaskId) {
        try {
            log.info("巡店报告,businessId:{}", businessId);
            //任务完成给报告通知人发送通知
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            Long taskId = tbPatrolStoreRecordDO.getTaskId();
            if (taskId != null && taskId > 0) {
                TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
                if (taskParentDO != null) {
                    List<TaskProcessDTO> taskProcessDTOList = JSONObject.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
                    taskProcessDTOList = taskProcessDTOList.stream().filter(taskProcessDTO -> UnifyNodeEnum.NOTICE.getCode().equals(taskProcessDTO.getNodeNo())).collect(Collectors.toList());
                    List<String> queryUserIdList = userPersonInfoService.getUserIdListByTaskProcess(enterpriseId, taskProcessDTOList);
                    log.info("sendNoticeReport queryUserIdList：{}",JSONObject.toJSONString(queryUserIdList));
                    List<AuthStoreUserDTO> authStoreUserDTOList = storeService.getStorePositionUserList(enterpriseId, Collections.singletonList(tbPatrolStoreRecordDO.getStoreId()), null,
                            queryUserIdList, null, null, taskParentDO.getCreateUserId(), false);
                    log.info("sendNoticeReport authStoreUserDTOList：{}",JSONObject.toJSONString(authStoreUserDTOList));
                    log.info("sendNoticeReport,subTaskId:{}", subTaskId);
                    Map<String, List<String>> storeUserMap = authStoreUserDTOList.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId,
                            AuthStoreUserDTO::getUserIdList, (a, b) -> a));
                    queryUserIdList = storeUserMap.get(tbPatrolStoreRecordDO.getStoreId());
                    if (CollectionUtils.isNotEmpty(queryUserIdList)) {
                        log.info("开始发送门店报告");
                        String content = tbPatrolStoreRecordDO.getSupervisorName() + "已经完成" + tbPatrolStoreRecordDO.getStoreName() + "的门店巡店工作，巡店日期："
                                + DateUtil.format(tbPatrolStoreRecordDO.getSignEndTime(), com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_6) + "，任务名称：" +
                                tbPatrolStoreRecordDO.getTaskName() + "，您快去查看吧～";
                        //发送完成工作通知
                        if (CollectionUtils.isNotEmpty(queryUserIdList)){
                            queryUserIdList = queryUserIdList.stream().distinct().collect(Collectors.toList());
                        }
                        jmsTaskService.sendNoticeUnifyTask(taskParentDO.getTaskType(), queryUserIdList, UnifyNodeEnum.END_NODE.getCode(),
                                enterpriseId, tbPatrolStoreRecordDO.getStoreName(), subTaskId,
                                tbPatrolStoreRecordDO.getSubEndTime().getTime(), tbPatrolStoreRecordDO.getSubBeginTime().getTime(),
                                tbPatrolStoreRecordDO.getStoreId(), taskParentDO.getId(), content, taskParentDO.getTaskName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("sendNoticeReport,eid:{},businessId:{}", enterpriseId, businessId, e);
        }
    }

    /**
     * 校验巡店是否需要进行复审
     *
     * @param enterpriseId
     * @param businessId
     */
    private void needReCheck(String enterpriseId, Long businessId, String supervisorId) {
        log.info("巡店复审,businessId:{}", businessId);
        try {
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                log.info("稽核巡店不需要复审,businessId:{}", businessId);
                return;
            }
            if (!BusinessCheckType.PATROL_STORE.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())) {
                log.info("非巡店不需要复审,businessId:{}", businessId);
                return;
            }

            if (tbPatrolStoreRecordDO.getTaskId() == 0) {
                log.info("非任务巡店不需要复审,businessId:{}", businessId);
                return;
            }

            // 是否需要稽核
            patrolStoreCheckService.patrolCheck(enterpriseId, businessId, supervisorId);

        } catch (Exception e) {
            log.error("needReCheck报错", e);
        }
    }

    /**
     * 完成巡店计划
     *
     * @param enterpriseId
     * @param businessId
     */
    private void overPlanCheck(String enterpriseId, Long businessId, String userId) {
        try {
            log.info("巡店计划,businessId:{}", businessId);
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            if (!BusinessCheckType.PATROL_STORE.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())) {
                log.info("非巡店不需要完成巡店计划,businessId:{}", businessId);
                return;
            }

            TbPatrolStorePlanDO patrolStorePlanDO = patrolStorePlanDao.getPlanByUserId(enterpriseId, userId, DateUtil.format(new Date(), DATE_FORMAT_DAY), tbPatrolStoreRecordDO.getStoreId());
            if (patrolStorePlanDO != null && patrolStorePlanDO.getStatus() == 0) {
                //
                TbPatrolStorePlanDO planDO = new TbPatrolStorePlanDO();
                planDO.setId(patrolStorePlanDO.getId());
                planDO.setStatus(1);
                patrolStorePlanDao.updateByPrimaryKeySelective(enterpriseId, planDO);
            }
        } catch (Exception e) {
            log.error("overPlanCheck报错", e);
        }
    }
}
