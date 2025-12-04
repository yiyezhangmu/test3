package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/30 20:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyStoreDTO {

    /**
     * 任务ID
     */
    private Long unifyTaskId;

    private String storeId;

    private String storeName;

    private String type;

    private String filterRegionId;
}
