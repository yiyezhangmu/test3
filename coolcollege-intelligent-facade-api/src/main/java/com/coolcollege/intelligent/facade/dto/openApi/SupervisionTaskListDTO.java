package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/5 16:49
 * @Version 1.0
 */
@Data
public class SupervisionTaskListDTO {

    private Long parentId;

    private Long supervisionTaskId;

    private Integer pageSize;

    private Integer pageNum;

    private List<Integer> completeStatusList;

    Integer handleOverTimeStatus;
}
