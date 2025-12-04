package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PanasonicFindResponse {
    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("品类")
    private String category;
    @ApiModelProperty("中类")
    private String middleClass;
    @ApiModelProperty("型号")
    private String type;

    private String aId;
    private List<PanasonicFindResponse> child = new ArrayList<>();

    public PanasonicFindResponse() {
    }

    public PanasonicFindResponse(String storeId,
                                 String category,
                                 String middleClass,
                                 String type) {
        this.storeId = storeId;
        this.category = category;
        this.middleClass = middleClass;
        this.type = type;
        this.child = new ArrayList<>();
    }
}
