package com.coolcollege.intelligent.facade.dto.openApi;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.facade.dto.openApi.vo.TaskSopDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/5 9:41
 * @Version 1.0
 */
@Data
public class AddSupervisionTaskParentDTO {


    private Long id ;
    private String taskName;
    private String remark;
    private Date taskStartTime;
    private Date taskEndTime;
    private String businessType;
    private String businessId;
    private SupervisionTaskPriorityEnum priority;
    private String tags;
    private List<String> checkStoreIds;
    private String handleWay;
    private String checkCode;
    private String desc;
    private String formId;
    private String executePersons;
    List<TaskSopDTO> sopVOList;
    private String createUserId;
}
