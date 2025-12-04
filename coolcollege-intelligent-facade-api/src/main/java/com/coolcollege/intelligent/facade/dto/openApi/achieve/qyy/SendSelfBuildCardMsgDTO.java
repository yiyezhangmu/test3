package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author wxp
 * @FileName: SendSelfBuildCardMsgDTO
 * @Description: 自建卡片消息推送
 * @date 2023-06-07 16:14
 */
@Data
public class SendSelfBuildCardMsgDTO {

    /**
     * 卡片code
     */
    private String sceneCardCode;

    /**
     * 组织id
     */
    private String dingDeptId;

    /**
     * 卡片数据
     */
    private JSONObject cardData;


}
