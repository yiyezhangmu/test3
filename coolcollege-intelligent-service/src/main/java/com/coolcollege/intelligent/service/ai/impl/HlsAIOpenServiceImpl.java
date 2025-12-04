package com.coolcollege.intelligent.service.ai.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.ai.dao.AiModelLibraryDAO;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.HlsAIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("hlsAIOpenServiceImpl")
public class HlsAIOpenServiceImpl implements AIOpenService {

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private HuoshanAIOpenServiceImpl huoshanAIOpenService;
    @Resource
    private AiModelLibraryDAO aiModelLibraryDAO;

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        return huoshanAIOpenService.aiResolve(enterpriseId, aiCommonPromptDTO, imageList, aiModel);
    }

    @Override
    public AIResolveDTO asyncAiResolve(String enterpriseId, AiResolveBusinessTypeEnum businessType, AIResolveRequestDTO request) {
        TbDataStaTableColumnDO dataColumn = request.getDataColumn();
        TbMetaStaTableColumnDO staTableColumn = request.getMetaStaTableColumnDO();
        DataSourceHelper.reset();
        AiModelLibraryDO aiModel = aiModelLibraryDAO.getModelByCode(staTableColumn.getAiModel());
        if(Objects.isNull(aiModel)){
            log.info("ai model not exist {}", staTableColumn.getAiModel());
            throw new ServiceException(ErrorCodeEnum.AI_MODEL_NOT_EXIST);
        }
        JSONObject extendInfoObj = JSONObject.parseObject(aiModel.getExtendInfo());
        String routingKey = extendInfoObj.getString("routingKey");
        HlsAIResolveRequestDTO hlsAIResolveRequestDTO = new HlsAIResolveRequestDTO(dataColumn.getId(), dataColumn.getTaskId(), dataColumn.getStoreId(), dataColumn.getRegionId(),
                dataColumn.getMetaColumnName(), aiModel.getCode(), dataColumn.getCheckPics(), CheckResultEnum.PASS.getCode().equals(dataColumn.getCheckResult()));
        hlsAIResolveRequestDTO.setEnterpriseId(enterpriseId);
        hlsAIResolveRequestDTO.setRoutingKey(routingKey);
        simpleMessageService.send(JSONObject.toJSONString(hlsAIResolveRequestDTO), RocketMqTagEnum.HLS_PATROL_AI_DEAL);
        return null;
    }

}
