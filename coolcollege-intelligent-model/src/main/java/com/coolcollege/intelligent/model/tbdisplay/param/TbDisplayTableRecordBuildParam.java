package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 陈列记录初始化参数
 * 
 * @author wxp
 * @date 2021-03-03 15:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordBuildParam {
    /**
     * 父任务id
     */
    @NotNull(message = "父任务id不能为空")
    private Long unifyTaskId;

    /**
     * 是否支持单项评分 0 不支持  1 支持
     */
    private Boolean isSupportScore;

    /**
     * 是否支持拍照上传 0不支持  1支持 哈
     */
    private Boolean isSupportPhoto;

    /**
     * 附件地址
     */
    private String attachUrl;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 检查表元数据id
     */
    private Long metaTableId;

    /**
     * 子任务信息
     */
    private List<TbDisplayTableRecordSubBuildParam> subBuildParams;

    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 轮次开始时间
     */
    private Date subBeginTime;
    /**
     * 轮次结束时间
     */
    private Date subEndTime;
    /**
     * 任务处理截止时间
     */
    private Date handlerEndTime;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TbDisplayTableRecordSubBuildParam {
        /**
         * 子任务id
         */
        @NotNull(message = "子任务id不能为空")
        private Long subTaskId;

        /**
         * 门店id
         */
        @NotNull(message = "门店id不能为空")
        private String storeId;

        /**
         * 处理人id
         */
        @NotNull(message = "处理人id不能为空")
        private String handleUserId;

    }
}
