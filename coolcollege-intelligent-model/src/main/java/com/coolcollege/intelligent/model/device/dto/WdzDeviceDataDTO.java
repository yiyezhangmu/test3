package com.coolcollege.intelligent.model.device.dto;

import lombok.Data;

import java.util.List;

/**
 * describe: 万店掌设备列表data数据
 *
 * @author wangff
 * @date 2024/10/16
 */
@Data
public class WdzDeviceDataDTO {

    /**
     * 结果集
     */
    private List<WdzDeviceInfo> list;

    @Data
    public static class WdzDeviceInfo {

        /**
         * 门店id
         */
        private Integer id;
        
        /**
         * 门店名称
         */
        private String name;

        /**
         * 设备详情
         */
        private List<WdzDeviceDetail> devices;
    }

    /**
     * 设备详情
     */
    @Data
    public static class WdzDeviceDetail {
        /**
         * 设备名称
         */
        private String name;

        /**
         * 设备在线状态（1：在线,0：离线）
         */
        private Integer online;

        /**
         * 设备id
         */
        private Integer id;

        /**
         * 流媒体服务器端口
         */
        private String mediaServerPort;

        /**
         * 是否能云端控制
         */
        private Integer ptzEnable;

        /**
         * 是否可以对讲
         */
        private Integer audioCallEnable;

        /**
         * puid
         */
        private String puid;

        /**
         * 管道id
         */
        private Integer channel_id;

        /**
         * 辐流管道id
         */
        private Integer slaveChannel_id;

        /**
         * 流媒体服务器ip
         */
        private String mediaServerIp;

        /**
         * 音频id
         */
        private Integer video_id;

        /**
         * 收音机machineId
         */
        private String machineId;
    }
}
