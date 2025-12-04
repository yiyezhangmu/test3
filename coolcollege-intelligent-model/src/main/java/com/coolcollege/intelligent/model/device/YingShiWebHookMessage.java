package com.coolcollege.intelligent.model.device;

import lombok.Data;


@Data
public class YingShiWebHookMessage {

    private Header header;

    private Body body;

    @Data
    public static class Header{

        /**
         * 消息类型:ys.onoffline
         */
        public String type;

        /**
         * 设备序列号
         */
        public String deviceId;
        /**
         * 设备通道号
         */
        public Long channelNo;
        /**
         * 消息唯-ID
         */
        public String messageId;
    }

    @Data
    public static class Body{

        /**
         * 设备类型
         */
        public String devType;

        /**
         * 设备上一次注册时间，格式:yyyy-MM-dd HH:mm:ss
         */
        public String regTime;
        /**
         * 设备外网IP
         */
        public String natIp;
        /**
         * 消息类型:OFFLINE-设备离线消息，ONLINE-设备上线消息
         */
        public String msgType;
        /**
         * 设备序列号
         */
        public String subSerial;
        /**
         * 设备上线(离线)时间，格式:yyyy-MM-dd HH:mm:ss
         */
        public String occurTime;
        /**
         * 设备名称
         */
        public String deviceName;
        /**
         * 设备是否是快速重连，0-表示设备快速重连，1-表示非快速重连
         */
        public Integer isCleanSession;
    }


}
