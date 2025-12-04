package com.coolcollege.intelligent.model.impoetexcel.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author byd
 */
@Data
public class ImportStoreDistinctVO {

    /**
     * 去重字段
     */
    @NotBlank(message = "去重字段不能为空 storeId,storeNum,storeName")
    private String uniqueField;
}
