package com.coolcollege.intelligent.model.device.response.tplink;

import lombok.Data;

import java.util.List;

@Data
public class TPSdCardInfoResponse {

    private List<TPSdCardInfo> cardInfoList;


    @Data
    public static class TPSdCardInfo{

        //SD卡索引
        private int index;

        //rw:可读写  w只写  r只读
        private String permission;

        //SD卡总空间
        private String totalSpace;

        //SD卡剩余空间
        private String freeSpace;

        //已录制视频的时长，单位为秒
        private String recordDuration;

        //最早录像时间，UNIX时间戳，精确到秒
        private String recordStartTime;

        //SD卡状态，枚举类型 none:不存在, unformatted:未格式化，只有格式化的磁盘才可使用 normal:正常 insufficient:SD卡容量太小
        private String status;

    }

}
