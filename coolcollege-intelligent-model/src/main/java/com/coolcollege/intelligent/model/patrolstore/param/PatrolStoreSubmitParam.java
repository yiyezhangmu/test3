package com.coolcollege.intelligent.model.patrolstore.param;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;

/**
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreSubmitParam {

    @NotNull(message = "巡店记录id不能为空")
    private Long businessId;

    @NotNull(message = "检查表id不能为空")
    private Long dataTableId;

    @ApiModelProperty("稽核数据表id")
    private Long checkDataTableId;

    private List<DataStaTableColumnParam> dataStaTableColumnParamList;

    private List<DataStaTableColumnParam> dataStaCheckTableColumnParamList;

    private List<DataDefTableColumnParam> dataDefTableColumnParamList;

    private List<DataDefTableColumnParam> dataDefCheckTableColumnParamList;

    /**
     * 是否提交
     */
    private Boolean submit;

    /**
     * 是否允许异步提交
     */
    private Boolean allowAsyncSubmit;

    /**
     * 停留时间
     */
    private String dwellTime;


    /**
     * 标准检查项数据
     */
    @ApiModel
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataStaTableColumnParam {
        /**
         * 标准检查项id
         */
        @NotNull(message = "标准检查项id不能为空")
        @ApiModelProperty("数据项id")
        private Long id;

        /**
         * 检查项结果
         */
        @ApiModelProperty("检查项结果")
        private String checkResult;

        /**
         * 检查项结果id
         */
        @ApiModelProperty("检查项结果id")
        private Long checkResultId;

        /**
         * 检查项结果名称
         */
        @ApiModelProperty("检查项结果名称")
        private String checkResultName;

        /**
         * 检查项上传的图片
         */
        @ApiModelProperty("检查项上传的图片")
        private String checkPics;

        /**
         * 检查项上传的视频
         */
        @ApiModelProperty("检查项上传的视频")
        private String checkVideo;

        /**
         * 检查项的描述信息
         */
        @ApiModelProperty("检查项填写描述信息")
        private String checkText;

        /**
         * 分值
         */
        @ApiModelProperty("分值")
        private BigDecimal checkScore;

        /**
         * 得分倍数
         */
        @ApiModelProperty("得分倍数")
        private BigDecimal scoreTimes;

        /**
         * 奖罚倍数
         */
        @ApiModelProperty("奖罚倍数")
        private BigDecimal awardTimes;

        /**
         * 不合格原因名称
         */
        @ApiModelProperty("不合格原因")
        private String checkResultReason;


        /**
         * 检查项是否已经上报
         */
        @ApiModelProperty("检查项是否已经上报  0：未提交  1:已提交")
        private Integer submitStatus;
    }


    /**
     * 自定义检查项数据
     *
     *
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDefTableColumnParam implements Serializable {
        /**
         * 自定义检查项id
         */
        @NotNull(message = "自定义检查项id不能为空")
        private Long id;

        /**
         * 值1
         */
        private String value1;

        /**
         * 值2
         */
        private String value2;

        /**
         * 视频
         */
        private String checkVideo;

        /**
         * 检查项是否已经上报
         */
        @ApiModelProperty("检查项是否已经上报  0：未提交  1:已提交")
        private Integer submitStatus;

    }

    public static List<TbDataStaTableColumnDO> convertDataColumnList(List<DataStaTableColumnParam> dataStaTableColumnParamList, String userId, boolean isUpdateHandleUserId){
        return ListUtils.emptyIfNull(dataStaTableColumnParamList).stream()
                .map(a -> {
                    TbDataStaTableColumnDO column = TbDataStaTableColumnDO.builder().id(a.getId()).checkResult(a.getCheckResult())
                            .checkResultId(a.getCheckResultId()).checkResultName(a.getCheckResultName()).supervisorId(userId)
                            .checkPics(a.getCheckPics()).checkVideo(a.getCheckVideo()).checkText(a.getCheckText())
                            .awardTimes(a.getAwardTimes()).scoreTimes(a.getScoreTimes()).submitStatus(a.getSubmitStatus())
                            .checkResultReason(a.getCheckResultReason()).checkScore(a.getCheckScore()).build();
                    if(isUpdateHandleUserId){
                        column.setHandlerUserId(userId);
                        column.setPatrolStoreTime(new Date());
                    }
                    return column;
                })
                .collect(Collectors.toList());
    }
}
