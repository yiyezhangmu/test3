package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AchieveReportDDListReq {
    @ApiModelProperty("钉钉部门id")
    private String synDingDeptId;
    @ApiModelProperty("时间类型： month:月 week:周 day:天")
    private String timeType;
    @ApiModelProperty("业务日期 月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    private String timeValue;
    @ApiModelProperty("排序字段 完成率：finish_rate  业绩同比：achieve_yoy  单产：output   单产同比：output_yoy")
    private String sortDesc;
}
