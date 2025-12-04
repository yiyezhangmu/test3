package com.coolcollege.intelligent.service.sync.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.QuestionActionKeyEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.http.CoolHttpClientResult;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 单个实例详情
 * https://open.dingtalk.com/document/isvapp/obtains-the-details-of-a-single-approval-instance-pop
 * @author wxp
 * @date 2023/11/30 15:14
 */
@Slf4j
public class OaPluginEvent extends BaseEvent {

    protected String bizData;

    private String eventType;

    public OaPluginEvent(String corpId, String bizData, String appType, String eventType) {
        this.corpId = corpId;
        this.bizData = bizData;
        this.appType = appType;
        this.eventType = eventType;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public void doEvent() {
        log.info("OaPluginEvent=====bizData===={}", bizData);
        JSONObject jsonObject = JSON.parseObject(bizData);
        if(WORKFLOW_INSTANCE_CHANGE_DIRECTED.equals(eventType)){
            return;
        }
        String processInstanceId = jsonObject.getString("processInstanceId");
        DataSourceHelper.reset();
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());

        UnifyTaskService unifyTaskService = SpringContextUtil.getBean("unifyTaskService", UnifyTaskService.class);
        TaskParentDO taskParentDO = unifyTaskService.getByExtraParam(config.getEnterpriseId(),  processInstanceId);
        log.info("getByExtraParam taskParentDO : {}", JSONObject.toJSONString(taskParentDO));
        JSONObject instancesDetail = getInstancesDetail(corpId, appType, processInstanceId);
        // 发起人的userId
        String originatorUserId = instancesDetail.getString("originatorUserId");
        JSONArray formComponentArray = instancesDetail.getJSONArray("formComponentValues");
        JSONArray tasks = instancesDetail.getJSONArray("tasks");
        String status = instancesDetail.getString("status");
        RedisUtilPool redisUtilPool = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
        String lockKey = "buildQuestionByOaPluginEvent:" + processInstanceId;
        boolean lock = redisUtilPool.setNxExpire(lockKey, processInstanceId, CommonConstant.NORMAL_LOCK_TIMES);
        try {
            if(WORKFLOW_TASK_CHANGE_DIRECTED.equals(eventType) && taskParentDO == null && lock){
                QuestionTaskInfoDTO taskInfo = new QuestionTaskInfoDTO();
                List<TaskProcessDTO> process = Lists.newArrayList();
                List<String> storeIdList = Lists.newArrayList();
                String taskName = "";
                Date endTime = new Date();
                String taskDesc = "";
                String image = "";
                for (Object obj  : formComponentArray) {
                    JSONObject formComponent = (JSONObject) obj;
                    String  bizAlias = formComponent.getString("bizAlias");
                    String  value = formComponent.getString("value");
                    String  extValue = formComponent.getString("extValue");
                    if("orderName".equals(bizAlias)){
                        taskName = value;
                    }
                    if("checkItem".equals(bizAlias)){
                        JSONObject extValueObject = JSONObject.parseObject(extValue);
                        if(extValueObject != null){
                            taskInfo.setMetaColumnId(extValueObject.getLong("key"));
                        }
                    }
                    if("store".equals(bizAlias)){
                        JSONArray extValueArray = JSONArray.parseArray(extValue);
                        storeIdList = extValueArray.stream().map(s -> {
                            JSONObject storeObj = JSONObject.parseObject(JSON.toJSONString(s));
                            return storeObj.getString("key");
                        }).collect(Collectors.toList());
                    }
                    if("endTime".equals(bizAlias) && StringUtils.isNotBlank(value)){
                        endTime = DateUtils.transferString2Date(value);
                    }
                    if("desc".equals(bizAlias)){
                        taskDesc = value;
                    }
                    if("image".equals(bizAlias) && StringUtils.isNotBlank(value)){
                        image = value;
                        String stringWithoutBrackets = image.replaceAll("\\[|\\]|\"", "").trim();
                        // 使用逗号和空格分隔字符串，得到字符串数组
                        String[] imageArray = stringWithoutBrackets.split(",");
                        List<String> photos = Arrays.asList(imageArray);
                        List<String> picUrls = Lists.newArrayList();
                        List<String> videoUrls = Lists.newArrayList();
                        ListUtils.emptyIfNull(photos).forEach(photo -> {
                            if (photo.contains(Constants.TRANSCODE_VIDEO)) {
                                videoUrls.add(photo);
                            }else {
                                picUrls.add(photo);
                            }
                        });
                        if (CollectionUtils.isNotEmpty(picUrls)) {
                            taskInfo.setPhotos(picUrls);
                        }
                        if (CollectionUtils.isNotEmpty(videoUrls)) {
                            SmallVideoInfoDTO smallVideoInfo = new SmallVideoInfoDTO();
                            List<SmallVideoDTO> videoList  = videoUrls.stream().map(videoUrl -> {
                                SmallVideoDTO smallVideoDTO = new SmallVideoDTO();
                                smallVideoDTO.setVideoUrl(videoUrl);
                                return smallVideoDTO;
                            }).collect(Collectors.toList());
                            smallVideoInfo.setVideoList(videoList);
                            smallVideoInfo.setSoundRecordingList(null);
                            taskInfo.setVideos(JSONObject.toJSONString(smallVideoInfo));
                        }
                    }
                }

                JSONObject task = tasks.getJSONObject(0);
                String handlerUserId = task.getString("userId");
                TaskProcessDTO handlerUserPerson = getTaskProcessDTO(UnifyNodeEnum.FIRST_NODE.getCode(), handlerUserId);
                TaskProcessDTO approveUserPerson = getTaskProcessDTO(UnifyNodeEnum.SECOND_NODE.getCode(), originatorUserId);
                process.add(handlerUserPerson);
                process.add(approveUserPerson);

                QuestionParentInfoService questionParentInfoService = SpringContextUtil.getBean("questionParentInfoService", QuestionParentInfoService.class);
                BuildQuestionRequest buildQuestionRequest = new BuildQuestionRequest();
                buildQuestionRequest.setTaskName(taskName);
                buildQuestionRequest.setQuestionType("common");
                String finalTaskName = taskName;
                String finalTaskDesc = taskDesc;
                Date finalEndTime = endTime;
                List<BuildQuestionDTO> questionList = ListUtils.emptyIfNull(storeIdList).stream().map(e -> {
                    BuildQuestionDTO buildQuestionDTO = new BuildQuestionDTO();
                    buildQuestionDTO.setTaskName(finalTaskName);
                    buildQuestionDTO.setTaskDesc(finalTaskDesc);
                    buildQuestionDTO.setEndTime(finalEndTime);
                    buildQuestionDTO.setStoreId(e);
                    buildQuestionDTO.setTaskInfo(taskInfo);
                    buildQuestionDTO.setProcess(process);
                    return buildQuestionDTO;
                }).collect(Collectors.toList());
                buildQuestionRequest.setQuestionList(questionList);
                buildQuestionRequest.setExtraParam(processInstanceId);
                questionParentInfoService.buildQuestion(config.getEnterpriseId(), buildQuestionRequest, originatorUserId, Boolean.FALSE,null);
            }else {
                if("COMPLETED".equals(status) && taskParentDO != null){
                    EnterpriseUserService enterpriseUserService = SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class);
                    List<String> userIds = Lists.newArrayList();
                    // 审批人审批通过/拒绝
                    if(tasks != null && tasks.size() >= 1){
                        // 升序
                        tasks.sort(Comparator.comparing(obj -> ((JSONObject) obj).getDate("createTime")));
                        // 处理人直接拒绝
                        JSONObject handleObj = tasks.getJSONObject(0);
                        String handleUserId = handleObj.getString("userId");
                        String handleResult = handleObj.getString("result");
                        String handleTimeStr = handleObj.getString("finishTime");
                        Date handleTime = DateUtils.transferString2Date(handleTimeStr);
                        String handleActionKey = "";
                        if("AGREE".equals(handleResult)){
                            handleActionKey = QuestionActionKeyEnum.RECTIFIED.getCode();
                        }else if("REFUSE".equals(handleResult)){
                            handleActionKey = QuestionActionKeyEnum.UNNEEDED.getCode();
                        }
                        Date approveTime = null;
                        String approveUserId = null;
                        String approveActionKey = null;
                        if(tasks.size() > 1){
                            JSONObject approveObj = tasks.getJSONObject(1);
                            approveUserId = approveObj.getString("userId");
                            String approveResult = approveObj.getString("result");
                            String approveStr = approveObj.getString("finishTime");
                            approveTime = DateUtils.transferString2Date(approveStr);
                            if("AGREE".equals(approveResult)){
                                approveActionKey = QuestionActionKeyEnum.PASS.getCode();
                            }else if("REFUSE".equals(approveResult)){
                                approveActionKey = QuestionActionKeyEnum.REJECT.getCode();
                            }
                        }
                        if(StringUtils.isNotBlank(handleUserId)){
                            userIds.add(handleUserId);
                        }
                        if(StringUtils.isNotBlank(approveUserId)){
                            userIds.add(approveUserId);
                        }
                        Map<String, String> userNameMap = enterpriseUserService.getUserNameMap(config.getEnterpriseId(), userIds);
                        String approveUserName = userNameMap.get(approveUserId);
                        String handleUserName = userNameMap.get(handleUserId);
                        unifyTaskService.updateQuestionRecordFinish(config.getEnterpriseId(), approveTime, approveUserId, approveUserName ,approveActionKey,
                                handleTime, handleUserId, handleUserName, handleActionKey, taskParentDO.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("buildQuestionByOaPluginEvent error" , e);
        } finally {
            redisUtilPool.delKey(lockKey);
        }
    }

    private TaskProcessDTO getTaskProcessDTO(String nodeNo, String handlerUserId) {
        TaskProcessDTO processDTO = new TaskProcessDTO();
        processDTO.setNodeNo(nodeNo);
        List<GeneralDTO> handlerUserList = Lists.newArrayList();
        handlerUserList.add(new GeneralDTO(UnifyTaskConstant.PersonType.PERSON, handlerUserId, ""));
        processDTO.setUser(handlerUserList);
        processDTO.setApproveType(UnifyTaskConstant.ApproveType.ANY);
        return processDTO;
    }


    public JSONObject getInstancesDetail(String corpId, String appType, String processInstanceId)  {
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }
        EnterpriseInitConfigApiService enterpriseInitConfigApiService = SpringContextUtil.getBean("enterpriseInitConfigApiService", EnterpriseInitConfigApiService.class);
        String accessToken = null;
        try {
            accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
        } catch (ApiException e) {
            log.error("getInstancesDetail 获取token失败！",e);
        }
        String url =  "https://api.dingtalk.com/v1.0/workflow/processInstances";
        Map<String,String> headMap=new HashMap<>();
        headMap.put("x-acs-dingtalk-access-token",accessToken);
        Map<String, String> paramMap=new HashMap<>();
        paramMap.put("processInstanceId",processInstanceId);
        try {
            CoolHttpClientResult coolHttpClientResult = CoolHttpClient.doGet(url, headMap, paramMap);
            log.info("getInstancesDetail response : {}", JSONObject.toJSONString(coolHttpClientResult));
            String content = coolHttpClientResult.getContent();
            JSONObject jsonObject = JSONObject.parseObject(content);
            if(jsonObject.getJSONObject("result")!=null){
                return jsonObject.getJSONObject("result");
            }
        } catch (Exception e) {
            log.error("获取实例详情错误！",e);
            return null;
        }
        return null;
    }

    /*public static void main(String[] args) {
        String processInstanceId = "6OdXsaU6TNSrdtgl-SWDiQ02641701948279";
        String accessToken = "7fed9c731eac3c3baa42ec35154dc300";
        String url =  "https://api.dingtalk.com/v1.0/workflow/processInstances";
        Map<String,String> headMap=new HashMap<>();
        headMap.put("x-acs-dingtalk-access-token",accessToken);
        Map<String, String> paramMap=new HashMap<>();
        paramMap.put("processInstanceId",processInstanceId);
        try {
            CoolHttpClientResult coolHttpClientResult = CoolHttpClient.doGet(url, headMap, paramMap);
            log.info("getInstancesDetail response : {}", JSONObject.toJSONString(coolHttpClientResult));
            String content = coolHttpClientResult.getContent();
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONObject instancesDetail = jsonObject.getJSONObject("result");
            JSONArray tasks = instancesDetail.getJSONArray("tasks");

            if(tasks != null && tasks.size() >= 1) {
                // 升序
              //   tasks.sort(Comparator.comparing(obj -> ((JSONObject) obj).getDate("createTime")));
                tasks.sort(Comparator.comparing(obj -> ((JSONObject) obj).getDate("createTime")).reversed());


                // 处理人直接拒绝
                JSONObject handleObj = tasks.getJSONObject(0);
                System.out.println(JSONObject.toJSONString(handleObj));
            }
        } catch (Exception e) {
            log.error("获取实例详情错误！",e);
        }
    }*/
}
