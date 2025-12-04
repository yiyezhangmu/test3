package com.coolcollege.intelligent.model.yunda;

import lombok.Data;

import java.util.List;

@Data
public class AoKangExtendDTO {

    private Boolean is_to_all;

    private String media_id;

    private String msg_type;

    private String uuid;

    private String text_content;

    private String userid_list;

    private MsgBody msg_body;

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
