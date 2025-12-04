package com.coolcollege.intelligent.model.license.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenseImgExportVO {
    @Excel(name = "图片",type = 2 ,width = 20 , height = 20,imageType = 2,mergeVertical = true)
    private byte[] img;
}
