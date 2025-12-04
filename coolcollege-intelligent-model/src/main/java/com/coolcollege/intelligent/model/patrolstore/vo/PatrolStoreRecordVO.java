package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PatrolStoreRecordVO {
    /**
     * 巡店记录
     */
    private TbPatrolStoreRecordDO tbPatrolStoreRecordDO;

    /**
     * 巡店人头像
     *
     */
    private String supervisorAvatar;

    /**
     * 总检查项数
     */
    private Integer allCount;

    /**
     * 不合格检查项数
     */
    private Integer unPassCount;

    /**
     * 不适用检查项数
     */
    private Integer inApplicableCount;

    /**
     * 能够发起问题工单
     */
    private Boolean canQuestion;

    /**
     * 处理人列表
     */
    private List<PersonDTO> personList;

    /**
     * 审核人列表
     */
    private List<PersonDTO> aduitPersonList;


    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 检查表属性
     */
    private Integer tableProperty;


    /**
     * 是否逾期
     */
    private Boolean overdue;

    /**
     * 检查表报表列表
     */
    private List<TbDataTableVO> dataTableList;

    private String params;

}
