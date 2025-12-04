package com.coolcollege.intelligent.model.device;
import lombok.Data;

/**
 *
 * @author byd
 * @date 2025-11-07 10:11
 */
@Data
public class YingShiCloudRecordingMessage {

    private Header header;

    private Body body;

    @Data
    public static class Header {

        /**
         * 消息类型：ys.open.cloud表示云录制消息
         */
        public String type;

        /**
         * 设备序列号
         */
        public String deviceId;

        /**
         * 设备通道号
         */
        public Integer channelNo;

        /**
         * 消息唯一ID
         */
        public String messageId;

        /**
         * 消息发送时间
         */
        public Long messageTime;
    }

    @Data
    public static class Body {

        /**
         * 设备上传的消息
         */
        public Object body;

        /**
         * 录制类型
         * 抽帧状态变更，抽到图片("video_frame_status_change")；
         * 抽帧结束("video_frame")
         */
        public String messageType;

        /**
         * 用户id
         */
        public String userId;

        /**
         * 任务id
         */
        public String taskId;

        /**
         * 项目Id
         */
        public String projectId;

        /**
         * 设备序列号
         */
        public String deviceSerial;

        /**
         * 通道号
         */
        public Integer channel;

        /**
         * 任务产生文件数量
         */
        public Long fileNum;

        /**
         * 任务产生文件总大小
         */
        public Long totalSize;

        /**
         * 返回码
         */
        public String errorCode;

        /**
         * 返回信息
         */
        public String errorMsg;

        /**
         * 任务状态
         * COMPLETE(0, "已完成"),
         * WAITING(1, "排队中"),
         * PROCESSING(2, "进行中"),
         * FINISHED(3, "已结束"),
         * EXCEPTION_FAILED(4, "异常结束"),
         * CANCEL(6,"已取消"),
         * NOT_START(7,"未开始")
         */
        public String taskStatus;
    }

    /**
     * 任务状态枚举（辅助类）
     */
    public enum TaskStatus {
        COMPLETE("0", "已完成"),
        WAITING("1", "排队中"),
        PROCESSING("2", "进行中"),
        FINISHED("3", "已结束"),
        EXCEPTION_FAILED("4", "异常结束"),
        CANCEL("6", "已取消"),
        NOT_START("7", "未开始");

        private final String code;
        private final String description;

        TaskStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TaskStatus getByCode(String code) {
            for (TaskStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 消息类型枚举（辅助类）
     */
    public enum MessageType {
        FRAME_STATUS_CHANGE("video_frame_status_change", "抽帧状态变更"),
        FRAME_END("video_frame", "抽帧结束");

        private final String code;
        private final String description;

        MessageType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    // 工具方法

    /**
     * 验证消息是否有效
     */
    public boolean isValid() {
        return header != null && body != null &&
                "ys.open.cloud".equals(header.getType());
    }

    /**
     * 判断是否是抽帧状态变更消息
     */
    public boolean isFrameStatusChange() {
        return body != null && "video_frame_status_change".equals(body.getMessageType());
    }

    /**
     * 判断是否是抽帧结束消息
     */
    public boolean isFrameEnd() {
        return body != null && "video_frame".equals(body.getMessageType());
    }

    /**
     * 判断任务是否已完成
     */
    public boolean isTaskCompleted() {
        return body != null && ("0".equals(body.getTaskStatus()) || "3".equals(body.getTaskStatus()));
    }

    /**
     * 判断任务是否异常结束
     */
    public boolean isTaskException() {
        return body != null && "4".equals(body.getTaskStatus());
    }

    /**
     * 判断任务是否已取消
     */
    public boolean isTaskCancelled() {
        return body != null && "6".equals(body.getTaskStatus());
    }
}
