package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaDefColumnDetailExtVO extends TbMetaColumnDetailVO  implements Serializable {


//    @Excel(name = "检查结果1", orderNum = "14")
    private String value1;

    @Excel(name = "检查结果", orderNum = "15")
    private String value2;

    @Excel(name = "巡店记录创建时间", orderNum = "7", format = "yyyy.MM.dd HH:mm")
    private Date createTime;

    @Excel(name = "巡店人名称",orderNum = "16")
    private String supervisorName;

    @Excel(name = "巡店人职位",orderNum = "16")
    private String supervisorPositionName;

    private static final long serialVersionUID = 1L;

}