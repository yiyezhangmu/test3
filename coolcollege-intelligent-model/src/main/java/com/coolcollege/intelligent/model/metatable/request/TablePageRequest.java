package com.coolcollege.intelligent.model.metatable.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TablePageRequest {

    private String name;
    private String tableType;
    private Integer pageNumber;
    private Integer pageSize;
    private List<Long> tableIdList;
    private Boolean isAll;
    private Boolean isResultPerson;
    private Boolean bothPerson;
    private String tableProperty;
    private String statusFilterCondition;
    private Long groupId;

    private List<String> tableTypeList;

    @ApiModelProperty("有权限的表id")
    private List<String> authTableIds;

    @ApiModelProperty("表属性列表")
    private List<String> tablePropertyList;
}
