package com.coolcollege.intelligent.model.patrolstore.vo;

import java.util.Date;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import lombok.Data;
import lombok.ToString;

/**
 * @author shuchang.wei
 * @date 2021/1/8 15:41
 */
@Data
@ToString
public class QuestionListVO {
    /**
     * 工单检查项
     */
    private TbDataStaTableColumnDO columnDO;
    /**
     * 问题工单列表
     */
    private TaskParentDO taskParentDO;

    /**
     * 门店名称
     */
    private StoreDO store;

    /**
     * 发起人姓名
     */
    private EnterpriseUserDO user;

    /**
     * 完成时间
     */
    private Date doneTime;

    /**
     * 工单子任务
     */
    private TaskStoreDO taskStoreDO;

    private TbMetaStaTableColumnDO metaColumn;
}
