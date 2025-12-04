package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/9 15:36
 */
@Data
@Builder
@NoArgsConstructor
public class TaskDisplayMappingDO {

    /**
     * 任务ID
     */
    private Long unifyTaskId;
    /**
     * 陈列快照cid
     */
    private String displayId;
    /**
     * 陈列名
     */
    private String displayName;

    public TaskDisplayMappingDO(Long unifyTaskId, String displayCid, String displayName) {
        this.unifyTaskId = unifyTaskId;
        this.displayId = displayCid;
        this.displayName = displayName;
    }
}
