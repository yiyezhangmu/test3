package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/10 10:13
 */
@Data
public class BatchBuildDTO {
    private List<UnifyTaskBuildDTO> taskList;
}
