package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/07/07
 */
@Data
public class StoreBaseInfoExportErrorDTO extends StoreBaseInfoExportDTO {
    @Excel(name = "描述", width = 30)
    private String dec;
}
