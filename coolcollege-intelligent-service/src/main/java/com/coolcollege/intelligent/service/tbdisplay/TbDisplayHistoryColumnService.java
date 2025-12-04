package com.coolcollege.intelligent.service.tbdisplay;


import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayApproveParam;

/**
 *
 * @author byd
 */
public interface TbDisplayHistoryColumnService {

    /**
     * 单个记录的处理
     * @param enterpriseId
     * @param displayApproveParam
     */
    void approveDisplayHistoryColumn(String enterpriseId, TbDisplayApproveParam displayApproveParam);
}
