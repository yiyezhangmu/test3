package com.coolcollege.intelligent.model.impoetexcel.vo;

import com.coolcollege.intelligent.model.impoetexcel.dto.TaskStoreRangeDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 15:12
 * @Version 1.0
 */
@Data
public class TaskStoreRangeVO {

   List<TaskStoreRangeDTO> successList;

   List<TaskStoreRangeDTO> failList;
}
