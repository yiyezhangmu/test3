package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 脚手架门店验收记录VO
 * </p>
 *
 * @author wangff
 * @since 2025/4/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreAcceptanceVO {
    /**
     * 检查表名称
     */
    private String tableName;

    /**
     * 合格项数
     */
    private Integer passNum;

    /**
     * 总项数
     */
    private Integer totalNum;

    /**
     * 巡店结果 excellent:优秀 good:良好 eligible:合格 disqualification:不合格
     */
    private String checkResultLevel;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 巡店人姓名
     */
    private String supervisorName;

    /**
     * 巡店开始时间
     */
    private Date signStartTime;

    /**
     * 巡店结束时间
     */
    private Date signEndTime;

    public StoreAcceptanceVO(TbPatrolStoreRecordDO patrolStoreRecordDO, TbDataTableDO tbDataTableDO) {
        this.businessId = String.valueOf(patrolStoreRecordDO.getId());
        this.supervisorName = patrolStoreRecordDO.getSupervisorName();
        this.signStartTime = patrolStoreRecordDO.getSignStartTime();
        this.signEndTime = patrolStoreRecordDO.getSignEndTime();
        if (Objects.nonNull(tbDataTableDO)) {
            this.tableName = tbDataTableDO.getTableName();
            this.passNum = tbDataTableDO.getPassNum();
            this.totalNum = tbDataTableDO.getTotalCalColumnNum();
            this.checkResultLevel = tbDataTableDO.getCheckResultLevel();
        }
    }
}
