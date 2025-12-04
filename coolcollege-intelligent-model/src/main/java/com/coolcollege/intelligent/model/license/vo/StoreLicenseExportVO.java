package com.coolcollege.intelligent.model.license.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreLicenseExportVO {

    @Excel(name = "名称",needMerge = true)
    private String name;

    @Excel(name = "证照类型",needMerge = true)
    private String licenseTypeName;

    @Excel(name = "证照状态",needMerge = true)
    private String licenseStatus;

    @Excel(name = "过期时间",needMerge = true)
    private String endDate;

    @ExcelCollection(name = "证照图片")
    private List<LicenseImgExportVO> licenseImgUrl;


}
