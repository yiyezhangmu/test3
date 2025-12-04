package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AchieveReportListReq {
    @ApiModelProperty("钉钉部门id")
    private String synDingDeptId;
    @ApiModelProperty("时间类型： month:月 week:周 day:天")
    private String timeType;
    @ApiModelProperty("业务日期 月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    private String timeValue;
    @ApiModelProperty("排序字段 业绩达成：gross_sales 单产达成：output  销量达成：sales_volume  客单达成：per_customer ")
    private String sortDesc;
}
