package com.coolcollege.intelligent.model.operationboard.dto;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.*;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/8 11:20
 * @Description 运营看板人员执行力汇总dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserStatisticsDTO {
    /**
     * 总人数
     */
    private int personCount;

    /**
     * 创建检查表数
     */
    private int createTableCount;

    /**
     * 检查门店数
     */
    private int patrolStoreNum;

    /**
     * 总问题数
     */
    private int totalQuestionNum;

    /**
     * 已解决的问题数
     */
    private int finishQuestionNum;

    /**
     * 总任务数
     */
    private int totalTaskNum;

    private List<SysRoleDO> defaultRoleList;
}
