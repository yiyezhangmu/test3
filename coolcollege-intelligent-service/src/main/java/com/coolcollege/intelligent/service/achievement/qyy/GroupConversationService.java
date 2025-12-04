package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.GroupConversationVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ak.ExportTaskRecordVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ak.SendRecordInfoVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.scopeandScene.VO.OpGroupConversationScopeVO;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * @author wxp
 * @FileName: GroupConversationService
 * @Description: 群会话
 * @date 2023-04-19 10:38
 */
public interface GroupConversationService {

    List<GroupConversationVO> listGroupConversation(String corpId, String appType, String conversationType, String conversationTitle);

    OpGroupConversationScopeVO getScopeByOpenCidAndSceneCode(EnterpriseConfigDO enterpriseConfigDO, String appType, String openConversationId, String sceneCode);

    Boolean pushCardMessage(String corpId, String appType, OpenApiPushCardMessageDTO.MessageData param);

    /**
     * 卡片发送记录列表
     * @param param
     * @return
     * @throws ApiException
     */
    List<SendRecordInfoVO> listCardSendRecord(CardSendRecordListReq param);

    /**
     * 导出群列表
     * @param param
     * @return
     */
    ExportTaskRecordVO exportCardDataList(CardDataDetailReq param);

    /**
     * 导出明细
     */
    ExportTaskRecordVO exportCardDataDetailList(CardDataDetailReq  param);

    /**
     * 导出记录和url
     * @param param
     * @return
     */
    List<ExportTaskRecordVO> listExportTaskRecord(PageReq param);



}
