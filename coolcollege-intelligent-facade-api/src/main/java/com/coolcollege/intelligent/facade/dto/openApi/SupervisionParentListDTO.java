package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/5 16:32
 * @Version 1.0
 */
@Data
public class SupervisionParentListDTO {


    private String taskName;

    private Long startTime;

    private Long endTime;

    private List<Integer> statusList;

    private List<String> priorityList;

    private List<String> taskGroupingList;

    private List<String> tags;

    private Integer pageSize;

    private Integer pageNum;
}
