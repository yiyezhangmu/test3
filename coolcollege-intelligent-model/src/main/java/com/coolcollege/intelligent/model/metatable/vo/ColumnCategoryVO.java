package com.coolcollege.intelligent.model.metatable.vo;

import lombok.Data;

import java.util.List;

@Data
public class ColumnCategoryVO {

    private Integer columnCount;

    private List<MetaStaColumnVO> columnList;

    private String columnName;
}
