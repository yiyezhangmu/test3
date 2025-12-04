package com.coolcollege.intelligent.model.video.platform.imou.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/29
 */
@NoArgsConstructor
@Data
public class ImouDeviceInfoResponse {

    private List<DeviceListDTO> deviceList;

    @NoArgsConstructor
    @Data
    public static class DeviceListDTO {
        private String accessType;
        private String owner;
        private List<ChannelsDTO> channels;
        private String channelNum;
        private String catalog;
        private Integer encryptMode;
        private String name;
        private String deviceModel;
        private String ability;
        private String deviceId;
        private String version;
        private String status;

        @NoArgsConstructor
        @Data
        public static class ChannelsDTO {
            private String picUrl;
            private String shareFunctions;
            private String remindStatus;
            private String channelName;
            private String ability;
            private String channelId;
            private String status;
        }
    }
}
