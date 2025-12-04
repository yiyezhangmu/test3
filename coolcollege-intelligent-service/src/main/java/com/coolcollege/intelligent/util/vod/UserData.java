package com.coolcollege.intelligent.util.vod;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Title UserData
 * @Description vod服务UserData参数
 * @Author zhucg
 */
@Data
public class UserData {

    /**
     * 回调设置
     */
    @JSONField(name = "MessageCallback")
    private MessageCallback messageCallback;

    public void setCallBack(String callBackUrl) {
        this.messageCallback = new MessageCallback();
        this.messageCallback.setCallbackURL(callBackUrl);
    }

    @Data
    public class MessageCallback {
        
        @JSONField(name = "CallbackURL")
        private String callbackURL;
    }
}
