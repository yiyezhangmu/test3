package com.coolcollege.intelligent.model.license;

import lombok.Data;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/20 15:12
 */
@Data
public class LcLicenseTypeExtendFieldVO {
    private Long id;
    //证照扩展字段名称
    private String name;
    //证照扩展字段类型
    private String type;
    //证照扩展字段选项
    private String caseItems;

}
