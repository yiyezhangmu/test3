package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.GroupConversationVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ak.ExportTaskRecordVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ak.SendRecordInfoVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.scopeandScene.VO.OpGroupConversationScopeVO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.achievement.qyy.GroupConversationService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author wxp
 * @FileName: GroupConversationService
 * @Description: 群会话
 * @date 2023-04-19 10:38
 */
@Service
@Slf4j
public class GroupConversationServiceImpl implements GroupConversationService {

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    RegionService regionService;

    public static ExportTaskRecordVO toExportTaskRecordVO(ExportTaskRecordDTO res) {
        if (res == null) {
            return null;
        }
        ExportTaskRecordVO exportTaskRecordVO1 = new ExportTaskRecordVO();
        exportTaskRecordVO1.setId(res.getId());
        exportTaskRecordVO1.setGmtCreate(res.getGmtCreate());
        exportTaskRecordVO1.setGmtModified(res.getGmtModified());
        exportTaskRecordVO1.setOrgId(res.getOrgId());
        exportTaskRecordVO1.setFileName(res.getFileName());
        exportTaskRecordVO1.setFileType(res.getFileType());
        exportTaskRecordVO1.setIsImport(res.getIsImport());
        exportTaskRecordVO1.setStatus(res.getStatus());
        exportTaskRecordVO1.setSuccessNum(res.getSuccessNum());
        exportTaskRecordVO1.setTotalNum(res.getTotalNum());
        exportTaskRecordVO1.setFileUrl(res.getFileUrl());
        exportTaskRecordVO1.setCreatorId(res.getCreatorId());
        exportTaskRecordVO1.setRemark(res.getRemark());
        return exportTaskRecordVO1;
    }


    @Override
    public List<GroupConversationVO> listGroupConversation(String corpId, String appType, String conversationType, String conversationTitle) {
        List<OpGroupConversationDTO> groupConversationDTOList = null;
        try {
            groupConversationDTOList = enterpriseInitConfigApiService.listGroupConversation(corpId, appType, conversationType, conversationTitle);
        } catch (ApiException e) {
            log.error("获取群列表异常conversationType : {}", conversationType, e);
        }
        List<GroupConversationVO> groupConversationVOList = ListUtils.emptyIfNull(groupConversationDTOList).stream().map(groupConversationDTO -> {
            GroupConversationVO groupConversationVO = new GroupConversationVO();
            BeanUtils.copyProperties(groupConversationDTO, groupConversationVO);
            return groupConversationVO;
        }).collect(Collectors.toList());
        return groupConversationVOList;
    }

    @Override
    public OpGroupConversationScopeVO getScopeByOpenCidAndSceneCode(EnterpriseConfigDO enterpriseConfigDO, String appType, String openConversationId, String sceneCode) {
        String dingCorpId = enterpriseConfigDO.getDingCorpId();
        OpGroupConversationScopeVO opGroupConversationScopeVO = new OpGroupConversationScopeVO();
        OpGroupConversationScopeDTO opGroupConversationScopeDTO = new OpGroupConversationScopeDTO();
        try {
            opGroupConversationScopeDTO = enterpriseInitConfigApiService.getScopeByOpenCidAndSceneCode(dingCorpId, appType, openConversationId, sceneCode);
            log.info("getScopeByOpenCidAndSceneCode#opGroupConversationScopeDTO:{}", JSONObject.toJSONString(opGroupConversationScopeDTO));
            opGroupConversationScopeVO.setScopeId(opGroupConversationScopeDTO.getScopeId());
            opGroupConversationScopeVO.setGroupConversationType(opGroupConversationScopeDTO.getGroupConversationType());
            RegionDO regionBySynDingDeptId = regionService.getRegionBySynDingDeptId(enterpriseConfigDO.getEnterpriseId(), opGroupConversationScopeDTO.getScopeId());
            if (Objects.nonNull(opGroupConversationScopeDTO.getScopeId())
                    && !StringUtils.isBlank(regionBySynDingDeptId.getRegionType())) {
                opGroupConversationScopeVO.setRegionType(regionBySynDingDeptId.getRegionType());
            }
            log.info("getScopeByOpenCidAndSceneCode#opGroupConversationScopeVO:{}", JSONObject.toJSONString(opGroupConversationScopeVO));
        } catch (ApiException e) {
            log.error("获取业务范围异常openConversationId: {}，sceneCode: {}", openConversationId, sceneCode, e);
        }
        return opGroupConversationScopeVO;
    }

    @Override
    public Boolean pushCardMessage(String corpId, String appType, OpenApiPushCardMessageDTO.MessageData param) {
        Boolean flag = null;
        try {
            EnterpriseConfigDO enterpriseConfig = new EnterpriseConfigDO();
            flag = enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, Arrays.asList(param));
        } catch (ApiException e) {
            log.error("推送消息异常：{} ", JSONObject.toJSONString(param), e);
        }
        return flag;
    }

    private List<SendRecordInfoVO> convert(List<SendRecordInfoDTO> sendRecordInfoDTOS) {
        List<SendRecordInfoVO> list = new ArrayList<>();
        List<SendRecordInfoDTO> sendRecordInfoDTOS1 = JSONArray.parseArray(JSONArray.toJSONString(sendRecordInfoDTOS), SendRecordInfoDTO.class);
        for (SendRecordInfoDTO sendRecordInfoDTO : sendRecordInfoDTOS1) {
            SendRecordInfoVO param = new SendRecordInfoVO();
            param.setConversationTitle(sendRecordInfoDTO.getConversationTitle());
            List<SendRecordInfoVO.CardDataDetailVO> detailList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sendRecordInfoDTO.getDetailList())) {
                for (SendRecordInfoDTO.CardDataDetailVO cardDataDetailVO : sendRecordInfoDTO.getDetailList()) {
                    SendRecordInfoVO.CardDataDetailVO cardDataDetailVO1 = new SendRecordInfoVO.CardDataDetailVO();
                    cardDataDetailVO1.setDeptName(cardDataDetailVO.getDeptName());
                    cardDataDetailVO.setReadStatusStr(cardDataDetailVO.getReadStatusStr());
                    cardDataDetailVO.setReadTime(cardDataDetailVO.getReadTime());
                    cardDataDetailVO.setRoleName(cardDataDetailVO.getRoleName());
                    cardDataDetailVO.setUserName(cardDataDetailVO.getUserName());
                    detailList.add(cardDataDetailVO1);
                }
            }

            param.setDetailList(detailList);
            param.setId(sendRecordInfoDTO.getId());
            param.setMemberNum(sendRecordInfoDTO.getMemberNum());
            param.setReadNum(sendRecordInfoDTO.getReadNum());
            param.setReadPercent(sendRecordInfoDTO.getReadPercent());
            param.setReceiveNum(sendRecordInfoDTO.getReceiveNum());
            param.setSendStatus(sendRecordInfoDTO.getSendStatus());
            param.setSendTime(sendRecordInfoDTO.getSendTime());
            list.add(param);
        }
        return list;
    }

    @Override
    public List<SendRecordInfoVO> listCardSendRecord(CardSendRecordListReq param) {
        try {
            List<SendRecordInfoDTO> sendRecordInfoDTOS = enterpriseInitConfigApiService.listCardSendRecord(param);
            List<SendRecordInfoDTO> jsonArraySendRecordInfoDTOS = JSONArray.parseArray(JSONArray.toJSONString(sendRecordInfoDTOS), SendRecordInfoDTO.class);
            log.info("listCardSendRecord impl :{}", JSONObject.toJSONString(jsonArraySendRecordInfoDTOS));
            List<SendRecordInfoVO> convert = convert(jsonArraySendRecordInfoDTOS);
            log.info("convert:{}", JSONObject.toJSONString(convert));
            return convert;
        } catch (ApiException e) {
            log.error("listCardSendRecord异常：{} ", JSONObject.toJSONString(param), e);
            return null;
        }
    }

    @Override
    public ExportTaskRecordVO exportCardDataList(CardDataDetailReq param) {
        try {
            ExportTaskRecordDTO exportTaskRecordDTO = enterpriseInitConfigApiService.exportCardDataList(param);
            ExportTaskRecordDTO result = JSONObject.parseObject(JSONObject.toJSONString(exportTaskRecordDTO), ExportTaskRecordDTO.class);
            ExportTaskRecordVO exportTaskRecordVO = toExportTaskRecordVO(result);
            return exportTaskRecordVO;
        } catch (ApiException e) {
            log.error("exportCardDataList异常：{} ", JSONObject.toJSONString(param), e);
            return null;
        }

    }




    @Override
    public ExportTaskRecordVO exportCardDataDetailList(CardDataDetailReq param) {
        try {
            ExportTaskRecordDTO exportTaskRecordDTO = enterpriseInitConfigApiService.exportCardDataDetailList(param);
            ExportTaskRecordDTO result = JSONObject.parseObject(JSONObject.toJSONString(exportTaskRecordDTO), ExportTaskRecordDTO.class);
            ExportTaskRecordVO exportTaskRecordVO = toExportTaskRecordVO(result);
            return exportTaskRecordVO;
        } catch (ApiException e) {
            log.error("exportCardDataDetailList异常：{} ", JSONObject.toJSONString(param), e);
            return null;
        }

    }


    @Override
    public List<ExportTaskRecordVO> listExportTaskRecord(PageReq param) {
        try {
            List<ExportTaskRecordDTO> exportTaskRecordDTOS = enterpriseInitConfigApiService.listExportTaskRecord(param);
            log.info("listExportTaskRecord exportTaskRecordDTOS:{}",JSONObject.toJSONString(exportTaskRecordDTOS));
            List<ExportTaskRecordDTO> exportTaskRecordVOS = JSONArray.parseArray(JSONArray.toJSONString(exportTaskRecordDTOS), ExportTaskRecordDTO.class);
            log.info("listExportTaskRecord exportTaskRecordDTOS:{}",JSONObject.toJSONString(exportTaskRecordDTOS));
            List<ExportTaskRecordVO> convert = convertExportTaskRecordVOS(exportTaskRecordVOS);
            log.info("convert:{}", JSONObject.toJSONString(convert));
            return convert;
        } catch (ApiException e) {
            log.error("listExportTaskRecord异常：{} ", JSONObject.toJSONString(param), e);
            return null;
        }
    }

    private List<ExportTaskRecordVO> convertExportTaskRecordVOS(List<ExportTaskRecordDTO> exportTaskRecordVOS) {
        List<ExportTaskRecordVO> result = new ArrayList<>();
        for (ExportTaskRecordDTO exportTaskRecordVO : exportTaskRecordVOS) {
            ExportTaskRecordVO entity = new ExportTaskRecordVO();
            entity.setTotalNum(exportTaskRecordVO.getTotalNum());
            entity.setSuccessNum(exportTaskRecordVO.getSuccessNum());
            entity.setStatus(exportTaskRecordVO.getStatus());
            entity.setRemark(exportTaskRecordVO.getRemark());
            entity.setOrgId(exportTaskRecordVO.getOrgId());
            entity.setIsImport(exportTaskRecordVO.getIsImport());
            entity.setId(exportTaskRecordVO.getId());
            entity.setCreatorId(exportTaskRecordVO.getCreatorId());
            entity.setGmtModified(exportTaskRecordVO.getGmtModified());
            entity.setGmtCreate(exportTaskRecordVO.getGmtCreate());
            entity.setFileUrl(exportTaskRecordVO.getFileUrl());
            entity.setFileType(exportTaskRecordVO.getFileType());
            entity.setFileName(exportTaskRecordVO.getFileName());
            result.add(entity);
        }
        return result;
    }


}
