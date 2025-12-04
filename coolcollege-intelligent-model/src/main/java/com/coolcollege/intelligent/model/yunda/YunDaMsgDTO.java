package com.coolcollege.intelligent.model.yunda;

import lombok.Data;

import java.util.List;

/**
 * @author: wxp
 * @date: 2023-05-04 11:29
 * 韵达消息封装
 */
@Data
public class YunDaMsgDTO {
    /**
     * 消息发送给的用户列表
     */
    private List<String> userIds;
    /**
     * 消息参数
     */
    private MsgParam msgParam;

    private String time;

    private String sign;

    @Data
    public static class MsgParam {
        private String title;
        private String text;
        private String singleTitle;
        private String singleURL;
    }


}
