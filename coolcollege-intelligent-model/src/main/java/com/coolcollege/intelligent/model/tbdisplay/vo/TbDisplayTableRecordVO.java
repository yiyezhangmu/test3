package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wxp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordVO extends TbDisplayTableRecordDO {

    private List<TbDisplayTableDataColumnVO> tbDisplayDataColumnVOList;

    private List<TbDisplayTableDataContentVO> tbDisplayDataContentList;

    private String storeAreaName;

    private String storeName;

    private String storeNum;

    private String jobNum;

    private String shopNo;

    private String shopName;

    private String taskName;

    private String tableName;

    private String taskType;

    private String taskDesc;

    private String handleUserName;

    private String approveUserName;

    private String recheckUserName;

    private String avatar;

    private String storeGroupName;

    private String handlerDuration;

    private String approveDuration;

    /**
     * 处理超时是否继续处理任务
     */
    private Boolean handlerOvertimeTaskContinue;

    /**
     * 审批超时是否继续处理任务
     */
    private Boolean approveOvertimeTaskContinue;

    private Long subTaskId;


    private String thirdApproveUserName;

    private String fourApproveUserName;

    private String fiveApproveUserName;

    private boolean overdueTaskContinue;
}
