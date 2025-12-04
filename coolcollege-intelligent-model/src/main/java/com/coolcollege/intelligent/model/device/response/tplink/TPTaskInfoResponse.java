package com.coolcollege.intelligent.model.device.response.tplink;

import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class TPTaskInfoResponse {

    public static final Map<Integer, String> TP_LINK_VIDEO_ERROR_CODES = new HashMap<>();

    static {
        TP_LINK_VIDEO_ERROR_CODES.put(0, "成功");
        TP_LINK_VIDEO_ERROR_CODES.put(-88303, "套餐用量超限");
        TP_LINK_VIDEO_ERROR_CODES.put(-82414, "系统异常");
        TP_LINK_VIDEO_ERROR_CODES.put(-80327, "设备离线");
        TP_LINK_VIDEO_ERROR_CODES.put(-80301, "设备不存在");
        TP_LINK_VIDEO_ERROR_CODES.put(-87035, "获取设备端数据超时");
        TP_LINK_VIDEO_ERROR_CODES.put(-87036, "录像数据不存在");
        TP_LINK_VIDEO_ERROR_CODES.put(-87037, "超过设备并发上限");
        TP_LINK_VIDEO_ERROR_CODES.put(-87038, "设备推流异常");
    }

    @ApiModelProperty("云录制任务索引")
    private String taskId;

    @ApiModelProperty("抓拍类型 1实时抓图  2回放抓图 102回访录像")
    private int type;

    @ApiModelProperty("任务状态 0待执行 1执行中 10成功结束 11异常结束")
    private int state;

    @ApiModelProperty("任务下文件个数")
    private int fileCount;

    @ApiModelProperty("文件总字节数")
    private long totalBytes;

    @ApiModelProperty("任务创建时间")
    private String createTime;

    @ApiModelProperty("错误码，任务结束后返回")
    private int error_code;

    @ApiModelProperty("错误描述，任务结束后返回")
    private String errorMsg;

    public static VideoFileDTO convert(TPTaskInfoResponse response){
        if(Objects.isNull(response)){
            return null;
        }
        VideoFileDTO result = new VideoFileDTO();
        result.setFileId(response.getTaskId());
        int status = 1;
        if(response.getState() == 10){
            status = 0;
        }
        if(response.getState() == 11){
            status = 2;
        }
        result.setStatus(response.getState() == 10 ?  0 : 1);
        result.setFileCount(response.getFileCount());
        result.setFileSize(response.getTotalBytes());
        result.setDuration(response.getTotalBytes());
        result.setCreateTime(response.getCreateTime());
        result.setErrorCode(String.valueOf(response.getError_code()));
        result.setErrorMsg(response.getErrorMsg());
        if(StringUtils.isBlank(response.getErrorMsg())){
            result.setErrorMsg(TP_LINK_VIDEO_ERROR_CODES.get(response.getError_code()));
        }
        return result;
    }

}
