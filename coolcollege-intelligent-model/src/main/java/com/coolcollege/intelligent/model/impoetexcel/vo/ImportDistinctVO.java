package com.coolcollege.intelligent.model.impoetexcel.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 邵凌志
 * @date 2020/12/14 10:43
 */
@Data
public class ImportDistinctVO {

    /**
     * 去重字段
     */
    @NotBlank(message = "去重字段不能为空 storeId,storeNum,storeName")
    private String uniqueField;

    /**
     * 是否覆盖
     */
    private boolean cover;

    /**
     * 上传文件类型
     */
    private String fileType;

    /**
     * 门店分组id
     */
    private String storeGroupId;
}
