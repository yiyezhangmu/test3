package com.coolcollege.intelligent.model.achievement.qyy.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackDetailVO
 * @Description:信心反馈导出
 * @date 2023-04-06 16:45
 */
@Data
public class ConfidenceFeedbackExportDTO {

    @Excel(name = "反馈人", width = 20, orderNum = "1")
    private String username;

    @Excel(name = "所属部门", width = 20, orderNum = "2")
    private String regionName;

    @Excel(name = "信心指数", width = 20, orderNum = "3")
    private BigDecimal score;

    @Excel(name = "保障举措", width = 20, orderNum = "4")
    private String measure;

    @Excel(name = "资源支持", width = 20, orderNum = "5")
    private String resourceSupport;

    @Excel(name = "填写时间", width = 20, format = "yyyy-MM-dd HH:mm", orderNum = "6")
    private Date createTime;

    public static ConfidenceFeedbackExportDTO convert(ConfidenceFeedbackDetailVO param){
        if(Objects.isNull(param)){
            return null;
        }
        ConfidenceFeedbackExportDTO result = new ConfidenceFeedbackExportDTO();
        result.setUsername(param.getUsername());
        result.setRegionName(param.getRegionName());
        result.setScore(param.getScore());
        result.setMeasure(param.getMeasure());
        result.setResourceSupport(param.getResourceSupport());
        result.setCreateTime(param.getCreateTime());
        return result;
    }

}
