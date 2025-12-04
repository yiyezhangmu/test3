package com.coolcollege.intelligent.model.safetycheck.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 稽核主管审核请求
 * @author wxp
 */
@ApiModel
@Data
public class SafetyCheckAuditRequest {


    @NotNull(message = "巡店记录id不能为空")
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @NotBlank(message = "审核结果不能为空")
    @ApiModelProperty("审核结果 pass同意 reject拒绝")
    private String action;

    @ApiModelProperty("整体原因")
    @Length(max = 1000, message = "原因最多1000个字")
    private String remark;

    private List<DataColumnCommentParam> dataColumnCommentParamList;

    @ApiModel
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataColumnCommentParam {
        /**
         * 标准检查项id
         */
        @NotNull(message = "数据项id不能为空")
        @ApiModelProperty("数据项id")
        private Long dataColumnId;

        /**
         * 检查项的描述信息
         */
        @ApiModelProperty("点评备注")
        private String commentRemark;
        /**
         * 不合格原因名称
         */
        @ApiModelProperty("点评项[{\"id\":46,\"reasonName\":\"不合格原因1\",\"mappingResult\":\"FAIL\"}]")
        private String commentResult;
    }


}
