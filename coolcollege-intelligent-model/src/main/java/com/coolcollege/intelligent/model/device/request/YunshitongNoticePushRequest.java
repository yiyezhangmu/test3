package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

/**
 * <p>
 * 云视通（中维）消息推送请求体
 * </p>
 *
 * @author wangff
 * @since 2025/7/29
 */
@Data
public class YunshitongNoticePushRequest {

    /**
     * 事件类型
     */
    private String messageType;
    
    /**
     * 消息id（消息时间戳）
     */
    private Long messageId;
    
    /**
     * 报警时间，RFC3339格式
     */
    private String alarmTime;
    
    /**
     * 消息内容
     */
    private MessageData messageData;

    @Data
    public static class MessageData {
        /**
         * 设备序列号
         */
        private String deviceSn;
        
        /**
         * 设备名称
         */
        private String deviceName;
    }
}
