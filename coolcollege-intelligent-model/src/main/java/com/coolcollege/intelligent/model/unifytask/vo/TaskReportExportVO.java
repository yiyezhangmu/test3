package com.coolcollege.intelligent.model.unifytask.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author wxp
 * @date 2021/6/23 10:00
 */
@Data
public class TaskReportExportVO extends  TaskReportExportBaseVO{

    private static final long serialVersionUID = 1L;

    @Excel(name = "复审人", orderNum = "10")
    private String recheckPerson;

    @Excel(name = "平均得分", orderNum = "18")
    private Integer averageScore;


}
