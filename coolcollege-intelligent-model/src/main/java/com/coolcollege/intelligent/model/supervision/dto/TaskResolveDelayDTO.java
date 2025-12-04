package com.coolcollege.intelligent.model.supervision.dto;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 20:09
 * @Version 1.0
 */
@Data
public class TaskResolveDelayDTO {

    private String enterpriseId;

    private Long parentId;

    private CurrentUser currentUser;

    private Long taskStartTime;


}
