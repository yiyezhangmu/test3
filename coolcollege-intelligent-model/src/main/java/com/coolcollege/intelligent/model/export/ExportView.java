package com.coolcollege.intelligent.model.export;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportView {

    private ExportParams exportParams;
    private List<?> dataList;
    private Class<?> cls;

}
