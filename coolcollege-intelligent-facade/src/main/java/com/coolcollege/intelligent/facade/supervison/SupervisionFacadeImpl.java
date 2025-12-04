package com.coolcollege.intelligent.facade.supervison;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionStoreTaskDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionTaskDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionTaskParentDao;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.request.supervison.SupervisionRemindRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import com.coolcollege.intelligent.model.msg.MessageDealDTO;
import com.coolcollege.intelligent.model.msg.SupervisionTaskMessageDTO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskParentDO;
import com.coolcollege.intelligent.model.supervision.dto.TimingInfoDTO;
import com.coolcollege.intelligent.model.supervision.dto.TimingInfoDetailInfoDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

/**
 * @author byd
 * @date 2023-04-13 11:20
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.SUPERVISION_UNIQUE_ID, interfaceType = SupervisionFacade.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class SupervisionFacadeImpl implements SupervisionFacade {

    @Resource
    private SupervisionStoreTaskDao supervisionStoreTaskDao;

    @Resource
    private SupervisionTaskDao supervisionTaskDao;

    @Resource
    private SupervisionTaskParentDao supervisionTaskParentDao;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    private SubordinateMappingDAO subordinateMappingDAO;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    public ResultDTO<Boolean> supervisionRemind(SupervisionRemindRequest supervisionRemindRequest) {
        Map<Long, Map<String, List<String>>> directSuperiorMap = new HashMap<>();
        Map<Long, SupervisionTaskParentDO> supervisionTaskParentMap = new HashMap<>();

        String eid = supervisionRemindRequest.getEnterpriseId();
        // 根据企业id切库
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(supervisionRemindRequest.getEnterpriseId());
        String dbName = enterpriseConfigDO.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //任务开始前提醒
        String nowDateStr = LocalDate.now().toString();
        //按人任务-执行人
        List<SupervisionTaskDO> supervisionTaskDOList = supervisionTaskDao.listReminderBeforeSupervisionTask(supervisionRemindRequest.getEnterpriseId(), nowDateStr);
        if (CollectionUtils.isNotEmpty(supervisionTaskDOList)) {
            supervisionTaskDOList.forEach(supervisionTaskDO -> {
                int businessType = 0;
                if(StringUtils.isNotBlank(supervisionTaskDO.getCheckObjectIds())){
                    businessType = 1;
                }
                deal(eid, supervisionTaskDO.getTaskParentId(), supervisionTaskDO.getId(), supervisionTaskDO.getSupervisionHandleUserId(),
                        directSuperiorMap, supervisionTaskParentMap, enterpriseConfigDO, businessType, false, supervisionTaskDO.getTaskState());
            });
        }


        //任务结束前提醒

        //按人任务-执行人
        List<SupervisionTaskDO> supervisionTaskAfterDOList = supervisionTaskDao.listReminderAfterSupervisionTask(supervisionRemindRequest.getEnterpriseId(), nowDateStr);
        if (CollectionUtils.isNotEmpty(supervisionTaskAfterDOList)) {
            supervisionTaskAfterDOList.forEach(supervisionTaskDO -> {
                int businessType = 0;
                if(StringUtils.isNotBlank(supervisionTaskDO.getCheckObjectIds())){
                    businessType = 1;
                }
                deal(eid, supervisionTaskDO.getTaskParentId(), supervisionTaskDO.getId(), supervisionTaskDO.getSupervisionHandleUserId(),
                        directSuperiorMap, supervisionTaskParentMap, enterpriseConfigDO, businessType, true, supervisionTaskDO.getTaskState());
            });
        }

        //上级人发通知
        if (MapUtils.isNotEmpty(directSuperiorMap)) {
            directSuperiorMap.forEach((taskParentId, superiorMap) -> {
                SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentMap.get(taskParentId);
                if (MapUtils.isNotEmpty(superiorMap)) {
                    superiorMap.forEach((directSuperior, handleUserIdList) -> {
                        List<String> userNameList = enterpriseUserDao.selectUserNamesByUserIds(eid, handleUserIdList);
                        String msg = StringUtils.join(userNameList, Constants.PAUSE);
                        String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                                "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                                "您管辖的人：" + msg + "未完成任务";
                        //发送工作通知
                        jmsTaskService.sendSupervisionTaskTextMessage(eid, taskParentId, Collections.singletonList(directSuperior),
                                MessageDealDTO.SUPERVISION_HANDLE_TITLE, content);
                    });
                }
            });
        }
        return ResultDTO.successResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> supervisionData(SupervisionRemindRequest supervisionRemindRequest) {
        log.info("supervisionData:{}",JSONObject.toJSONString(supervisionRemindRequest));
        String eid = supervisionRemindRequest.getEnterpriseId();
        // 根据企业id切库
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(supervisionRemindRequest.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource( enterpriseConfigDO.getDbName());
        supervisionTaskDao.updateHandleOverTimeData(eid);
        supervisionStoreTaskDao.updateHandleOverTimeData(eid);
        return ResultDTO.successResult(Boolean.TRUE);
    }

    private void deal(String eid, Long taskParentId, Long supervisionTaskId, String supervisionHandleUserId, Map<Long, Map<String, List<String>>> directSuperiorMap,
                      Map<Long, SupervisionTaskParentDO> supervisionTaskParentMap, EnterpriseConfigDO enterpriseConfigDO, Integer businessType, Boolean iaAfter,
                      Integer taskState) {
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(taskParentId, eid);


        if (StringUtils.isNotBlank(supervisionTaskParentDO.getTimingInfo())) {

            TimingInfoDTO timingInfoDTO = JSONObject.parseObject(supervisionTaskParentDO.getTimingInfo(), TimingInfoDTO.class);
            TimingInfoDetailInfoDTO afterStarting = timingInfoDTO.getAfterStarting();

            if (iaAfter) {
                afterStarting = timingInfoDTO.getBeforeTheEnd();
            }
            //提醒执行人
            if(afterStarting.getHandleFlag()){
                SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
                String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                        "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                        "您有一个任务还未完成，点击前往执行。";
                //发送工作通知
                SupervisionTaskMessageDTO taskMessageDTO = new SupervisionTaskMessageDTO();
                taskMessageDTO.setSupervisionTaskId(supervisionTaskId);
                taskMessageDTO.setHandleUserIdList(Collections.singletonList(supervisionHandleUserId));
                taskMessageDTO.setBusinessType(businessType);
                taskMessageDTO.setTaskName(supervisionTaskParentDO.getTaskName());
                taskMessageDTO.setHandleWay(handleWay);
                taskMessageDTO.setTitle(MessageDealDTO.SUPERVISION_HANDLE_TITLE);
                taskMessageDTO.setContent(content);
                taskMessageDTO.setTaskState(taskState);
                jmsTaskService.sendSupervisionTaskMessage(eid, taskMessageDTO);
            }

            //提醒上级
            if (afterStarting.getSuperiorFlag()) {
                //查询上级
                SubordinateMappingDO subordinateMappingDO = subordinateMappingDAO.selectByUserIdAndType(eid, supervisionHandleUserId);
                if (subordinateMappingDO == null) {
                    log.info("supervisionRemind#deal#没有上级不发通知,taskParentId:{},supervisionTaskId:{},eid:{},businessType：{}", taskParentId, supervisionTaskId, eid, businessType);
                    return;
                }
                //上级id
                String directSuperior = subordinateMappingDO.getPersonalId();
                supervisionTaskParentMap.put(supervisionTaskParentDO.getId(), supervisionTaskParentDO);
                Map<String, List<String>> listMap = directSuperiorMap.get(supervisionTaskParentDO.getId());
                if (listMap == null) {
                    listMap = new HashMap<>();
                }
                List<String> directSuperiorList = listMap.getOrDefault(directSuperior, new ArrayList<>());
                directSuperiorList.add(supervisionHandleUserId);
                listMap.put(directSuperior, directSuperiorList);
                directSuperiorMap.put(supervisionTaskParentDO.getId(), listMap);
            }
        }
    }
}
