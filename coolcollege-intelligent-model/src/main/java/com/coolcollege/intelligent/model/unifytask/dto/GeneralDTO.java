package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/19 17:06
 */
@Data
public class GeneralDTO {
    /**
     *
     */
    private String type;
    /**
     *
     */
    private String value;
    /**
     *
     */
    private String name;
    /**
     * 权限
     */
    private Boolean valid;

    /**
     * 分组管理区域id
     */
    private String filterRegionId;

    @ApiModelProperty("是否需要稽核 ")
    private Boolean checkTable;

    @ApiModelProperty("AI审批 ")
    private Boolean aiAudit;

    public GeneralDTO(){}

    public GeneralDTO(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public GeneralDTO(String type, String value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }
}
