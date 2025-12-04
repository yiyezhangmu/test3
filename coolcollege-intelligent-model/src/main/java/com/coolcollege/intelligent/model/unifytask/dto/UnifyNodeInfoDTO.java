package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 15:20
 */
@Data
public class UnifyNodeInfoDTO {

    private String nodeNo;
    /**
     * 审批方式
     * any：或签
     * all：并签
     */
    private String approveType;

}
