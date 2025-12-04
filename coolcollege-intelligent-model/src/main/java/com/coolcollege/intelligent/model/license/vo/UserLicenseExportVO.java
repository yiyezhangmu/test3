package com.coolcollege.intelligent.model.license.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLicenseExportVO {

    @Excel(name = "门店名",needMerge = true)
    private String storeName;

    @Excel(name = "用户ID",needMerge = true,width = 30)
    private String userId;

    @Excel(name = "名称",needMerge = true)
    private String userName;

    @Excel(name = "用户职位",needMerge = true,width = 40)
    private String userPosition;

    @Excel(name = "证照类型",needMerge = true)
    private String licenseTypeName;

    @Excel(name = "证照状态",needMerge = true)
    private String licenseStatus;

    @Excel(name = "过期时间",needMerge = true,width = 30 ,exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @ExcelCollection(name = "证照图片")
    private List<LicenseImgExportVO> licenseImgUrl;


}
