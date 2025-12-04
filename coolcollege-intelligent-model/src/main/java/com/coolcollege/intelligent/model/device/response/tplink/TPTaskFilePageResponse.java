package com.coolcollege.intelligent.model.device.response.tplink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * tp获取任务文件列表
 */
@Data
public class TPTaskFilePageResponse {

    @ApiModelProperty("云录制任务下的文件（File）总数")
    private int total;

    @ApiModelProperty("云录制任务下的文件（File）信息列表")
    private List<TaskFileDetail> list;


    @Data
    public static class TaskFileDetail{

        @ApiModelProperty("云录制文件索引")
        private String fileId;

        @ApiModelProperty("云录制任务索引")
        private String taskId;

        @ApiModelProperty("文件个数")
        private int segmentCount;

        @ApiModelProperty("文件总字节数")
        private long totalBytes;

        @ApiModelProperty("文件下载地址列表")
        private List<String> urls;

        @ApiModelProperty("文件创建时间")
        private String createTime;

        @ApiModelProperty("文件过期时间")
        private String expireTime;
    }

}
