package com.coolcollege.intelligent.service.supervison;

import com.coolcollege.intelligent.model.supervision.dto.SupervisionApproveCountDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveCountVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveDataVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

/**
 * @Author suzhuhong
 * @Date 2023/4/18 11:07
 * @Version 1.0
 */
public interface SupervisionApproveService {

    /**
     * 查询审批人是否有数据
     * @param enterpriseId
     * @param user
     * @return
     */
    Boolean getApproveData(String enterpriseId, CurrentUser user);


    /**
     * 审批人按人 按门店任务审批数据
     * @param enterpriseId
     * @param user
     * @return
     */
    SupervisionApproveCountVO getApproveCount(String enterpriseId, CurrentUser user,String taskName);

    /**
     * 审批人 督导按人任务
     * @param enterpriseId
     * @param taskName
     * @param type
     * @param user
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionApproveDataVO> getSupervisionTaskApproveData(String enterpriseId, String taskName, String type, CurrentUser user, Integer pageSize, Integer pageNum);


    /**
     * 审批人 督导按门店任务
     * @param enterpriseId
     * @param taskName
     * @param type
     * @param user
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionApproveDataVO> getSupervisionStoreTaskApproveData(String enterpriseId, String taskName, String type, CurrentUser user, Integer pageSize, Integer pageNum);



}
