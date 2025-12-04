package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/22 15:14
 */
@Data
public class SubQuery {
    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店任务id
     */
    private Long taskStoreId;
}
