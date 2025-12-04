package com.coolcollege.intelligent.model.yunda;

import lombok.Data;

import java.util.List;

/**
 * @author: wxp
 * @date: 2023-07-06 11:29
 * 韵达服务窗消息封装
 */
@Data
public class YunDaActionCardMsgDTO extends AoKangExtendDTO{
    /**
     * 消息发送给的用户列表
     */
    private List<String> userIds;

    private String mediaId;

    private String unionid;

    private String msgType;

    private String textContent;

    private Boolean isToAll = false;
    /**
     * 消息体
     */
    private MsgBody msgBody;

    @Data
    public static class MsgBody {
        private ActionCard action_card;
        @Data
        public static class ActionCard {
            private String single_url;
            private String single_title;
            private String title;
            private String btn_orientation;
            private List button_list;
            private String markdown;
        }
    }

}
