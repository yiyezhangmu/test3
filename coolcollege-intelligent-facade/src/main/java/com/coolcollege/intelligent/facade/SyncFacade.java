package com.coolcollege.intelligent.facade;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.sync.vo.AuthMsg;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.sync.SyncUtils;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Created by wxp on 2021/3/20
 */

@Service
@Slf4j
public class SyncFacade {

    @Autowired
    private RedisUtilPool redisUtil;
    @Autowired
    public EnterpriseConfigService enterpriseConfigService;
    @Autowired
    @Lazy
    private UnifyTaskFcade unifyTaskFcade;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Autowired
    private EnterpriseInitService enterpriseInitService;

    /**
     * 同步钉钉组织架构
     *
     * @param corpId
     * @param flag
     */
    //@Async("taskExecutor")
    public void start(String corpId, String appType, Boolean flag) {

    }

    public void scopeChange(String corpId, String appType, Boolean flag, String permanentCode) {
        log.info("scopeChange start, corpId={}, appType={}", corpId, appType);
        appType = StrUtil.isEmpty(appType) ? AppTypeEnum.DING_DING.getValue() : appType;
        Boolean isDingType = AppTypeEnum.isDingType(appType);
        try {
            DataSourceHelper.reset();
            //获取企业配置，只做可见范围的变更的操作
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, appType);
            if (Objects.isNull(enterpriseConfig)) {
                //同步组织架构
                AuthMsg authMsg = new AuthMsg();
                authMsg.setCorpId(corpId);
                authMsg.setAppType(appType);
                authMsg.setPermanentCode(permanentCode);
                //发送rocketmq的消息
                simpleMessageService.send(JSONObject.toJSONString(authMsg), RocketMqTagEnum.ENTERPRISE_OPEN);
                return;
            }
            //同步人员，以及人员和部门，区域之间的关系
            enterpriseInitService.enterpriseInitUser(corpId, enterpriseConfig.getEnterpriseId(), AppTypeEnum.getAppType(appType), enterpriseConfig.getDbName(), true);
        } catch (Exception e) {
            log.error("Scope change sync error", e);
        } finally {
            if (flag) {
                String authKey = SyncUtils.getAuthKey(corpId);
                redisUtil.delKey(authKey);
            }
        }
    }


    public void initFirstDisplayTask(String eid, TbMetaTableDO tableDO, List<EnterpriseUserDO> enterpriseUserList, CurrentUser currentUser) {
        try {
            UnifyTaskBuildDTO task = new UnifyTaskBuildDTO();
            long createTime = System.currentTimeMillis();
            task.setBeginTime(createTime);
            task.setEndTime(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            task.setTaskName(MetaTableConstant.DISPLAY_TASK_NAME);
            task.setTaskType(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
            task.setTaskPattern(UnifyTaskPatternEnum.WORKFLOW.getCode());
            JSONObject jsonObject = new JSONObject();
            JSONObject tbDisplayDefined = new JSONObject();
            tbDisplayDefined.put("isSupportScore", true);
            tbDisplayDefined.put("isSupportPhoto", false);
            tbDisplayDefined.put("isCheckItem", false);
            tbDisplayDefined.put("filterStoresWthoutPersonnel", false);
            tbDisplayDefined.put("handleEndTime", DateUtils.convertTimeToString(task.getEndTime(), DateUtils.DATE_FORMAT_MINUTE));
            jsonObject.put("tbDisplayDefined", tbDisplayDefined);
            task.setTaskInfo(JSONObject.toJSONString(jsonObject));
            GeneralDTO store = new GeneralDTO();
            store.setType("store");
            store.setValue(Constants.DEFAULT_INIT_STORE_ID);
            task.setStoreIds(Collections.singletonList(store));
            task.setRunRule(TaskRunRuleEnum.ONCE.getCode());
            List<GeneralDTO> userList = new ArrayList<>();
            for(EnterpriseUserDO enterpriseUserDO : enterpriseUserList){
                if("a100000001".equals(enterpriseUserDO.getUserId())){
                    continue;
                }
                userList.add(new GeneralDTO("person", enterpriseUserDO.getUserId()));
            }
            TaskProcessDTO taskProcessDTO = new TaskProcessDTO();
            taskProcessDTO.setNodeNo("1");
            taskProcessDTO.setApproveType("any");
            taskProcessDTO.setUser(userList);
            TaskProcessDTO taskProcessDTO2 = new TaskProcessDTO();
            taskProcessDTO2.setNodeNo("2");
            taskProcessDTO2.setApproveType("any");
            taskProcessDTO2.setUser(userList);
            List<TaskProcessDTO> process = new ArrayList<>();
            process.add(taskProcessDTO);
            process.add(taskProcessDTO2);
            task.setProcess(process);
            task.setForm(Collections.singletonList(new GeneralDTO(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode(), String.valueOf(tableDO.getId()), tableDO.getTableName())));
            unifyTaskFcade.insertUnifyTask(eid, task, currentUser, createTime);
        } catch (Exception e) {
            log.error("initFirstDisplayTask error", e);
        }
    }

}
