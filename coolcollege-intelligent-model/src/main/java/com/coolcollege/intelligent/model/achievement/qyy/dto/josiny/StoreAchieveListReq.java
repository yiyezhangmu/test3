package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StoreAchieveListReq {

    @ApiModelProperty("dingding部门id")
    private String synDingDeptId;
    @ApiModelProperty("时间类型： month:月 week:周 day:天")
    private String timeType;
    @ApiModelProperty("业务日期 月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    private String timeValue;
    @ApiModelProperty("排序字段 业绩：gross_sales   销量：sales_volume    客单：per_customer")
    private String sortDesc;
}
