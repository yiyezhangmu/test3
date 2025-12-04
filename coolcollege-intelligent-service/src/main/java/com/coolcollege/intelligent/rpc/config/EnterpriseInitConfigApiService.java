package com.coolcollege.intelligent.rpc.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateUserAuthDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.rpc.api.ConfigServiceApi;
import com.coolcollege.intelligent.rpc.api.EnterpriseConfigServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author: xuanfeng
 * @date: 2022-01-26 15:25
 */
@Slf4j
@Service
public class EnterpriseInitConfigApiService {
    @SofaReference(uniqueId = ConfigConstants.CONFIG_FACADE_UNIQUE_ID,
            interfaceType = ConfigServiceApi.class,
            binding = @SofaReferenceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE, timeout = 600000))
    ConfigServiceApi configServiceApi;

    @SofaReference(uniqueId = ConfigConstants.ENTERPRISE_CONFIG_API_FACADE_UNIQUE_ID,
            interfaceType = EnterpriseConfigServiceApi.class,
            binding = @SofaReferenceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE, timeout = 120000))
    EnterpriseConfigServiceApi enterpriseConfigServiceApi;

    @Autowired
    private RedisUtilPool redisUtilPool;

    /**
     * 获取授权信息
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param appType
     * @return com.coolcollege.intelligent.dto.AuthInfoDTO
     */
    public AuthInfoDTO getAuthInfo(String corpId, String appType) throws ApiException {
        log.info("rpc getAuthInfo param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<AuthInfoDTO> authInfo = configServiceApi.getAuthInfo(corpId, appType);
        log.info("rpc getAuthInfo response : {}", JSONObject.toJSONString(authInfo));
        if (authInfo.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(authInfo.getResultCode()),authInfo.getMessage());
        }
        return authInfo.getData();
    }

    /**
     * 获取部门
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param appType
     * @param parentId
     * @return java.util.List<com.coolcollege.intelligent.dto.SysDepartmentDTO>
     */
    public List<SysDepartmentDTO> getDepartments(String corpId, String appType, String parentId) throws ApiException {
        log.info("rpc getDepartments param : corpId: {}, appType:{},parentId:{}", corpId, appType,parentId);
        BaseResultDTO<List<SysDepartmentDTO>> departments = configServiceApi.getDepartments(corpId, appType, parentId);
        log.info("rpc getDepartments response : {}", JSONObject.toJSONString(departments));
        if (departments.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(departments.getResultCode()),departments.getMessage());
        }
        return departments.getData();
    }

    /**
     * 获取子部门
     * @param corpId
     * @param appType
     * @param parentId
     * @return
     * @throws ApiException
     */
    public List<SysDepartmentDTO> getSubDepartments(String corpId, String appType, String parentId,Boolean fetchChild) throws ApiException {
        log.info("rpc getSubDepartments param : corpId: {}, appType:{},parentId:{}", corpId, appType,parentId);
        BaseResultDTO<List<SysDepartmentDTO>> departments = configServiceApi.getSubDepartments(corpId, appType, parentId,fetchChild);
        log.info("rpc getSubDepartments response : {}", JSONObject.toJSONString(departments));
        if (departments.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(departments.getResultCode()),departments.getMessage());
        }
        return departments.getData();
    }

    /**
     * 获取通讯录授权范围
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param appType
     * @return com.coolcollege.intelligent.dto.AuthScopeDTO
     */
    public AuthScopeDTO getAuthScope(String corpId, String appType) throws ApiException {
        log.info("rpc getAuthScope param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<AuthScopeDTO> authScope = configServiceApi.getAuthScope(corpId, appType);
        log.info("rpc getAuthScope response : {}", JSONObject.toJSONString(authScope));
        if (authScope.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(authScope.getResultCode()),authScope.getMessage());
        }
        return authScope.getData();
    }

    /**
     * 获取部门下的用户 企微/钉钉
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param deptId
     * @param appType
     * @return java.util.List<com.coolcollege.intelligent.dto.EnterpriseUserDTO>
     */
    public List<EnterpriseUserDTO> getDepartmentUsers(String corpId, String deptId, String appType) throws ApiException {
        log.info("rpc getDepartmentUsers param : corpId: {}, appType:{},deptId:{}", corpId, appType,deptId);
        BaseResultDTO<List<EnterpriseUserDTO>> departmentUsersQw = configServiceApi.getDepartmentUsers(corpId, deptId, appType);
        log.info("rpc getDepartmentUsers response deptId:{}: {}", deptId, JSONObject.toJSONString(departmentUsersQw));
        if (departmentUsersQw.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(departmentUsersQw.getResultCode()),departmentUsersQw.getMessage());
        }
        return departmentUsersQw.getData();
    }

    /**
     * 获取部门下的用户分页 钉钉
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param deptId
     * @param cursor
     * @param size
     * @param appType
     * @return com.coolcollege.intelligent.dto.DepartmentUserDTO
     */
    public DepartmentUserDTO getDingTalkDepartmentUsersPage(String corpId, Long deptId, Long cursor, Long size, String appType) throws ApiException {
        log.info("rpc getDingTalkDepartmentUsersPage param : corpId: {}, appType:{},cursor:{},size:{}", corpId, appType,cursor,size);
        BaseResultDTO<DepartmentUserDTO> dingTalkDepartmentUsers = configServiceApi.getDingTalkDepartmentUsersPage(corpId, deptId, cursor, size, appType);
        log.info("rpc getDingTalkDepartmentUsersPage response : {}", JSONObject.toJSONString(dingTalkDepartmentUsers));
        if (dingTalkDepartmentUsers.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(dingTalkDepartmentUsers.getResultCode()),dingTalkDepartmentUsers.getMessage());
        }
        return dingTalkDepartmentUsers.getData();
    }

    /**
     * 获取单个用户
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param userId
     * @param appType
     * @return com.coolcollege.intelligent.dto.EnterpriseUserDTO
     */
    public EnterpriseUserDTO getUserDetailByUserId(String corpId, String userId, String appType) throws ApiException {
        log.info("rpc getUserDetailByUserId param : corpId: {}, appType:{},userId:{}", corpId, appType,userId);
        BaseResultDTO<EnterpriseUserDTO> userDetailByUserId = configServiceApi.getUserDetailByUserId(corpId, userId, appType);
        log.info("rpc getUserDetailByUserId response : {}", JSONObject.toJSONString(userDetailByUserId));
        if (userDetailByUserId.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(userDetailByUserId.getResultCode()),userDetailByUserId.getMessage());
        }
        return userDetailByUserId.getData();
    }

    /**
     * 获取多个用户
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param userIds
     * @param appType
     * @return java.util.List<com.coolcollege.intelligent.dto.EnterpriseUserDTO>
     */
    public List<EnterpriseUserDTO> getUserDetailByUserIds(String corpId, List<String> userIds, String appType) throws ApiException {
        log.info("rpc getUserDetailByUserId param : corpId: {}, appType:{},userIds:{}", corpId, appType,userIds);
        BaseResultDTO<List<EnterpriseUserDTO>> userDetailByUserIds = configServiceApi.getUserDetailByUserIds(corpId, userIds, appType);
        log.info("rpc getUserDetailByUserId response : {}", JSONObject.toJSONString(userDetailByUserIds));
        if (userDetailByUserIds.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(userDetailByUserIds.getResultCode()),userDetailByUserIds.getMessage());
        }
        return userDetailByUserIds.getData();
    }

    /**
     * 获取管理与那
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param appType
     * @return java.util.List<java.lang.String>
     */
    public List<String> getAdminUserList(String corpId ,String appType) throws ApiException {
        String adminUserListKey = "adminUserListKey:" + corpId + ":" + appType;
        String adminList = redisUtilPool.getString(adminUserListKey);
        if(StringUtils.isNotBlank(adminList)){
            return JSONObject.parseArray(adminList, String.class);
        }
        BaseResultDTO<List<String>> adminUserList = configServiceApi.getAdminUserList(corpId, appType);
        log.info("rpc getAdminUserList response : {}", JSONObject.toJSONString(adminUserList));
        if (adminUserList.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(adminUserList.getResultCode()),adminUserList.getMessage());
        }
        redisUtilPool.setString(adminUserListKey, JSONObject.toJSONString(adminUserList.getData()), 60 * 60 * 24);
        return adminUserList.getData();
    }

    /**
     * 个人版开通获取主应用cropId
     * @author chenyupeng
     * @date 2022/1/27
     * @param corpId
     * @param appType
     * @return java.util.List<java.lang.String>
     */
    public String getMainCorpId(String corpId ,String userId, String appType) throws ApiException {
        log.info("rpc getMainCorpId param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<String> mainCorpId = configServiceApi.getMainCorpId(corpId, userId, appType);
        log.info("rpc getMainCorpId response : {}", JSONObject.toJSONString(mainCorpId));
        if (mainCorpId.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(mainCorpId.getResultCode()),mainCorpId.getMessage());
        }
        return mainCorpId.getData();
    }

    /**
     * 获取部门详情
     * @param corpId
     * @param deptId
     * @param appType
     * @return
     * @throws ApiException
     */
    public SysDepartmentDTO getDepartmentDetail(String corpId ,String deptId, String appType) throws ApiException {
        log.info("rpc getDepartmentDetail param : corpId: {}, appType:{}, deptId:{}", corpId, appType, deptId);
        BaseResultDTO<SysDepartmentDTO> baseResultDTO = configServiceApi.getDepartmentDetail(corpId, deptId, appType);
        log.info("rpc getDepartmentDetail response : {}", JSONObject.toJSONString(baseResultDTO));
        if (baseResultDTO.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(baseResultDTO.getResultCode()),baseResultDTO.getMessage());
        }
        return baseResultDTO.getData();
    }

    public void appEnterpriseOpen(AppEnterpriseOpenDto dto) {
        log.info("rpc appEnterpriseOpen param :{}", JSON.toJSONString(dto));
        //调用app企业开通
        configServiceApi.appEnterpriseOpen(dto);
    }

    /**
     * 根据code获取用户信息
     * @param corpId
     * @param appType
     * @param code
     * @return
     * @throws ApiException
     */
    public EnterpriseUserDTO getUserDetailByCode(String corpId, String appType, String code) throws ApiException {
        log.info("rpc getUserDetailByCode param : corpId: {}, appType:{},code:{}", corpId, appType,code);
        BaseResultDTO<EnterpriseUserDTO> response = configServiceApi.getUserDetailByCode(corpId, appType, code);
        log.info("rpc getUserDetailByCode response : {}", JSONObject.toJSONString(response));
        if (response.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(response.getResultCode()),response.getMessage());
        }
        return response.getData();
    }

    /**
     * 获取应用token
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public String getAccessToken(String corpId, String appType) throws ApiException {
        BaseResultDTO<String> accessToken = configServiceApi.getAccessToken(corpId, appType);
        if (accessToken.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            log.info("rpc getAccessToken param : error:{}", JSONObject.toJSONString(accessToken));
            throw new ApiException(String.valueOf(accessToken.getResultCode()),accessToken.getMessage());
        }
        //log.info("rpc getAccessToken param : corpId: {}, appType:{}, response:{}", corpId, appType, accessToken.getData());
        return accessToken.getData();
    }

    /**
     * 门店通-获取通讯录信息
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public OpContactInfoDTO getContactInfo(String corpId, String appType) throws ApiException {
        log.info("rpc getContactInfo param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<OpContactInfoDTO> contactInfo = configServiceApi.getContactInfo(corpId, appType);
        log.info("rpc getContactInfo response : {}", JSONObject.toJSONString(contactInfo));
        if (contactInfo.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(contactInfo.getResultCode()),contactInfo.getMessage());
        }
        return contactInfo.getData();
    }

    /**
     * 门店通-根据nodeid获取下级门店和区域
     * @param corpId
     * @param appType
     * @param code
     * @param nodeId
     * @return
     * @throws ApiException
     */
    public List<OpStoreAndRegionDTO> getSubStoreAndRegion(String corpId, String appType, String code, String nodeId) throws ApiException {
        log.info("rpc getSubStoreAndRegion param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<List<OpStoreAndRegionDTO>> result = null;
        try {
            result = configServiceApi.getSubStoreAndRegion(corpId, appType, code, nodeId);
        } catch (Exception e) {
            log.error("getSubStoreAndRegion,根据nodeid获取下级门店和区域nodeId{} ", nodeId, e);
            result = configServiceApi.getSubStoreAndRegion(corpId, appType, code, nodeId);
        }
        log.info("rpc getSubStoreAndRegion response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 门店通-获取角色列表
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public List<OpRoleDTO> getRoles(String corpId, String appType) throws ApiException {
        log.info("rpc getRoles param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<List<OpRoleDTO>> result = configServiceApi.getRoles(corpId, appType);
        log.info("rpc getRoles response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 门店通-获取门店分组
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public List<OpGroupDTO> getGroups(String corpId, String appType) throws ApiException {
        log.info("rpc getGroups param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<List<OpGroupDTO>> result = configServiceApi.getGroups(corpId, appType);
        log.info("rpc getGroups response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 门店通-获取门店分组详情
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public OpGroupDTO getGroupDetail(String corpId, Long groupId, String appType) throws ApiException {
        log.info("rpc getGroupDetail param : corpId: {}, groupId:{}, appType:{}", corpId, groupId, appType);
        BaseResultDTO<OpGroupDTO> result = null;
        try {
            result = configServiceApi.getGroupDetail(corpId, groupId, appType);
        } catch (Exception e) {
            log.error("rpc getGroupDetail error", e);
            throw new ApiException("调用远程服务失败");
        }
        log.info("rpc getGroupDetail response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 门店通-根据nodeid获取门店和区域
     * @param corpId
     * @param appType
     * @param code
     * @param nodeId
     * @return
     * @throws ApiException
     */
    public OpStoreAndRegionDTO getStoreAndRegion(String corpId, String appType, String code, String nodeId) throws ApiException {
        log.info("rpc getStoreAndRegion param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<OpStoreAndRegionDTO> result = configServiceApi.getStoreAndRegion(corpId, appType, code, nodeId);
        log.info("rpc getStoreAndRegion response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 获取门店通套餐
     * @param corpId
     * @param appType
     * @return
     * @throws ApiException
     */
    public OpPackageDTO getPackage(String corpId, String appType) throws ApiException {
        log.info("rpc getPackage param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<OpPackageDTO> result = configServiceApi.getPackage(corpId, appType);
        log.info("rpc getPackage response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    public AuthPersonLeaveInfoDTO getAuthPersonLeaveInfo(String corpId, String userId, String appType, String authCode) throws ApiException {
        log.info("rpc getAuthPersonLeaveInfo param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<AuthPersonLeaveInfoDTO> result = configServiceApi.getAuthPersonLeaveInfo(corpId, userId, appType, authCode);
        log.info("rpc getAuthPersonLeaveInfo response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    public Boolean updateUseRoleAndAuth(String corpId, OpenApiUpdateUserAuthDTO param) throws ApiException{
        if(Objects.isNull(param) || CollectionUtils.isEmpty(param.getUpdateUserList())){
            return false;
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ListUtils.partition(param.getUpdateUserList(), Constants.FIFTY_INT).forEach((data)->{
            OpenApiUpdateUserAuthDTO batchParam = new OpenApiUpdateUserAuthDTO();
            batchParam.setUpdateUserList(data);
            String jsonString = JSONObject.toJSONString(batchParam);
            log.info("循环轮次：{}，rpc updateUseRoleAndAuth param : corpId: {}, param:{}", atomicInteger.addAndGet(Constants.INDEX_ONE), corpId, jsonString);
            OpenApiConfigUpdateUserAuthDTO configParam = JSONObject.parseObject(jsonString, OpenApiConfigUpdateUserAuthDTO.class);
            BaseResultDTO<Boolean> result = configServiceApi.updateUseRoleAndAuth(corpId, configParam);
            log.info("循环轮次：{}，rpc updateUseRoleAndAuth response : {}", atomicInteger.get(), JSONObject.toJSONString(result));
            if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
                log.info("rpc updateUseRoleAndAuth response error: {}");
            }
        });
        return true;
    }

    /**
     * 根据群类型查询群列表
     * @param corpId
     * @param appType
     * @param conversationType
     * @param conversationTitle
     * @return
     * @throws ApiException
     */
    public List<OpGroupConversationDTO> listGroupConversation(String corpId, String appType, String conversationType, String conversationTitle) throws ApiException {
        log.info("rpc listGroupConversation param : corpId: {}, appType:{}，conversationType: {}, conversationTitle:{}", corpId, appType, conversationType, conversationTitle);
        BaseResultDTO<List<OpGroupConversationDTO>> result = configServiceApi.listGroupConversation(corpId, appType, conversationType, conversationTitle);
        log.info("rpc listGroupConversation response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 通过群id和子场景code查找业务范围
     * @param corpId
     * @param appType
     * @param openConversationId
     * @param sceneCode
     * @return
     * @throws ApiException
     */
    public OpGroupConversationScopeDTO getScopeByOpenCidAndSceneCode(String corpId, String appType, String openConversationId, String sceneCode) throws ApiException {
        log.info("rpc getScopeByOpenCidAndSceneCode param : corpId: {}, appType:{}, openConversationId: {}, sceneCode:{}", corpId, appType, openConversationId, sceneCode);
        BaseResultDTO<OpGroupConversationScopeDTO> result = configServiceApi.getScopeByOpenCidAndSceneCode(corpId, appType, openConversationId, sceneCode);
        log.info("rpc getScopeByOpenCidAndSceneCode response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return result.getData();
    }

    /**
     * 推送卡片消息数据
     * @param messageDataList
     * @return
     * @throws ApiException
     */
    public Boolean pushCardMessage(EnterpriseConfigDO enterpriseConfig, List<OpenApiPushCardMessageDTO.MessageData> messageDataList) throws ApiException{
        log.info("rpc pushCardMessage param : corpId: {}, appType:{}, messageDataList:{}", enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), JSONObject.toJSONString(messageDataList));
        BaseResultDTO<Boolean> result = null;
        try {
            BaseResultDTO<EnterpriseConfigExtendInfoDTO> enterpriseExtendInfo = enterpriseConfigServiceApi.getEnterpriseExtendInfo(enterpriseConfig.getEnterpriseId());
            log.info("pushCardMessage enterpriseExtendInfo :{}",enterpriseExtendInfo);
            EnterpriseConfigExtendInfoDTO data = JSONObject.parseObject(JSONObject.toJSONString(enterpriseExtendInfo.getData()), EnterpriseConfigExtendInfoDTO.class);
            String callbackKey = data.getCallbackKey();
            log.info("pushCardMessage callbackKey -> {}",callbackKey);
            if (!StringUtils.isEmpty(callbackKey)){
                for (OpenApiPushCardMessageDTO.MessageData messageData : messageDataList){
                    messageData.setCallbackKey(callbackKey);
                }
            }
            OpenApiPushCardMessageDTO param = new OpenApiPushCardMessageDTO();
            param.setMessageDataList(messageDataList);
            log.info("pushCardMessage param:{}", JSONObject.toJSONString(param));
            result = configServiceApi.pushCardMessage(enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), param);
        } catch (Exception e) {
            log.error("pushCardMessage,推送消息异常", e);
            throw new ApiException("调用远程服务失败");
        }
        log.info("rpc pushCardMessage response : {}", JSONObject.toJSONString(result));
        if (result.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(result.getResultCode()),result.getMessage());
        }
        return true;
    }

    public List<SendRecordInfoDTO> listCardSendRecord(CardSendRecordListReq param) throws ApiException {
        BaseResultDTO<List<SendRecordInfoDTO>> result = null;
        String jsonString = JSONObject.toJSONString(param);
        com.coolcollege.intelligent.dto.CardSendRecordListReq cardSendRecordListReq = JSONObject.parseObject(jsonString, com.coolcollege.intelligent.dto.CardSendRecordListReq.class);
        result = configServiceApi.listCardSendRecord(cardSendRecordListReq);
        log.info("result.getData():{}",JSONObject.toJSONString(result.getData()));
        return result.getData();
    }


    public ExportTaskRecordDTO exportCardDataList(CardDataDetailReq param) throws ApiException {
        BaseResultDTO<ExportTaskRecordDTO> result = null;
        String jsonString = JSONObject.toJSONString(param);
        com.coolcollege.intelligent.dto.CardDataDetailReq cardSendRecordListReq = JSONObject.parseObject(jsonString, com.coolcollege.intelligent.dto.CardDataDetailReq.class);
        result = configServiceApi.exportCardDataList(cardSendRecordListReq);
        log.info("result:{}",JSONObject.toJSONString(result));
        BaseResultDTO baseResultDTO = JSONObject.parseObject(JSONObject.toJSONString(result), BaseResultDTO.class);
        ExportTaskRecordDTO exportTaskRecordDTO = JSONObject.parseObject(JSONObject.toJSONString(baseResultDTO.getData()), ExportTaskRecordDTO.class);
        return exportTaskRecordDTO;
    }

    public ExportTaskRecordDTO exportCardDataDetailList(CardDataDetailReq param) throws ApiException {
        BaseResultDTO<ExportTaskRecordDTO> result = null;
        String jsonString = JSONObject.toJSONString(param);
        com.coolcollege.intelligent.dto.CardDataDetailReq cardSendRecordListReq = JSONObject.parseObject(jsonString, com.coolcollege.intelligent.dto.CardDataDetailReq.class);
        result = JSONObject.parseObject(JSONObject.toJSONString(configServiceApi.exportCardDataDetailList(cardSendRecordListReq)),BaseResultDTO.class);
        BaseResultDTO baseResultDTO = JSONObject.parseObject(JSONObject.toJSONString(result), BaseResultDTO.class);
        log.info("result:{}",JSONObject.toJSONString(result));
        ExportTaskRecordDTO exportTaskRecordDTO = JSONObject.parseObject(JSONObject.toJSONString(baseResultDTO.getData()), ExportTaskRecordDTO.class);
        return exportTaskRecordDTO;
    }

    public List<ExportTaskRecordDTO> listExportTaskRecord(PageReq param) throws ApiException {
        BaseResultDTO<List<ExportTaskRecordDTO>> result = null;
        String jsonString = JSONObject.toJSONString(param);
        com.coolcollege.intelligent.dto.PageReq cardSendRecordListReq = JSONObject.parseObject(jsonString, com.coolcollege.intelligent.dto.PageReq.class);
        result = configServiceApi.listExportTaskRecord(cardSendRecordListReq);
        log.info("result:{}",JSONObject.toJSONString(result));
        BaseResultDTO baseResultDTO = JSONObject.parseObject(JSONObject.toJSONString(result), BaseResultDTO.class);
        List<ExportTaskRecordDTO> exportTaskRecordDTOS = JSONArray.parseArray(JSONArray.toJSONString(baseResultDTO.getData()), ExportTaskRecordDTO.class);
        return exportTaskRecordDTOS;
    }



}
