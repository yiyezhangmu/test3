package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaDefColumnDetailVO extends TbMetaColumnDetailVO  implements Serializable {


//    @Excel(name = "检查结果1", orderNum = "14")
    private String value1;

    @Excel(name = "检查结果", orderNum = "15")
    private String value2;

    private static final long serialVersionUID = 1L;

}