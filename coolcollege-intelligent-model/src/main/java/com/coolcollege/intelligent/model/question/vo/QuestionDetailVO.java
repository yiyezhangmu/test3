package com.coolcollege.intelligent.model.question.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskSubReportVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/31 14:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDetailVO {

    private String storeId;
    private String storeName;
    /**
     * 工单状态
     */
    private String status;
    /**
     * 工单任务相关信息
     */
    private String taskInfo;
    /**
     * 整改人
     */
    private String handleUserId;
    private String handleUserName;
    /**
     * 截止日期
     */
    private Long endTime;
    /**
     * 任务描述（备注）
     */
    private String taskDesc;
    /**
     * 关联检查项信息
     */
    List<TbMetaStaTableColumnDO> columnList;
    /**
     * 历史
     */
    List<TaskSubReportVO> history;
}
