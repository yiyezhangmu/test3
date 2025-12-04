package com.coolcollege.intelligent.service.supervison.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionApproveDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionStoreTaskDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionTaskDao;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.SupervisionApproveDO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionApproveCountDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveCountVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveDataVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.supervison.SupervisionApproveService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2023/4/18 11:08
 * @Version 1.0
 */
@Service
public class SupervisionApproveServiceImpl implements SupervisionApproveService {


    @Resource
    private SupervisionApproveDao supervisionApproveDao;
    @Resource
    private SupervisionTaskServiceImpl supervisionTaskService;

    @Resource
    private SupervisionTaskDao supervisionTaskDao;

    @Resource
    private SupervisionStoreTaskDao supervisionStoreTaskDao;




    @Override
    public Boolean getApproveData(String enterpriseId, CurrentUser user) {
        //查询是否有审批数据
        int count = supervisionApproveDao.selectApproveDataByUserId(enterpriseId, user.getUserId());
        return count>0?Boolean.TRUE:Boolean.FALSE;
    }

    @Override
    public SupervisionApproveCountVO getApproveCount(String enterpriseId, CurrentUser user,String taskName) {
        //查询数据
        SupervisionApproveCountDTO supervisionApproveCountDTO = supervisionApproveDao.getApproveCountByUserId(enterpriseId, user.getUserId(),taskName);
        SupervisionApproveCountVO supervisionApproveCountVO = new SupervisionApproveCountVO();
        supervisionApproveCountVO.setStoreApproveCount(supervisionApproveCountDTO.getStoreTaskCount());
        supervisionApproveCountVO.setPersonApproveCount(supervisionApproveCountDTO.getPersonTaskCount());
        return supervisionApproveCountVO;
    }



    @Override
    public PageInfo<SupervisionApproveDataVO> getSupervisionTaskApproveData(String enterpriseId, String taskName, String type, CurrentUser user, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionApproveDO> supervisionApproveData = supervisionApproveDao.getSupervisionApproveData(enterpriseId, user.getUserId(), type, taskName);
        PageInfo pageInfo = new PageInfo<>(supervisionApproveData);
        if (CollectionUtils.isEmpty(supervisionApproveData)){
            return pageInfo;
        }
        List<Long> taskIdList = supervisionApproveData.stream().map(SupervisionApproveDO::getTaskId).collect(Collectors.toList());
        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listByIds(enterpriseId, taskIdList);
        Map<Long, SupervisionTaskDO> taskDOMap = supervisionTaskDOS.stream().collect(Collectors.toMap(SupervisionTaskDO::getId, data -> data));

        List<SupervisionApproveDataVO> supervisionApproveDataVOS = new ArrayList<>();
        supervisionApproveData.forEach(x->{
            SupervisionApproveDataVO supervisionApproveDataVO = new SupervisionApproveDataVO();
            supervisionApproveDataVO.setId(x.getTaskId());
            supervisionApproveDataVO.setTaskName(x.getTaskName());
            SupervisionTaskDO supervisionTaskDO = taskDOMap.get(x.getTaskId());
            if (supervisionTaskDO!=null){
                supervisionApproveDataVO.setSupervisionHandleUserId(supervisionTaskDO.getSupervisionHandleUserId());
                supervisionApproveDataVO.setSupervisionHandleUserName(supervisionTaskDO.getSupervisionHandleUserName());
                supervisionApproveDataVO.setSubmitTime(supervisionTaskDO.getSubmitTime());
                supervisionApproveDataVO.setPriority(supervisionTaskDO.getPriority());
                supervisionApproveDataVO.setTransferReassignFlag(supervisionTaskDO.getTransferReassignFlag());
                supervisionApproveDataVO.setTaskState(supervisionTaskDO.getTaskState());
                Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(null, supervisionTaskDO.getCancelStatus(), supervisionTaskDO.getTaskState());
                supervisionApproveDataVO.setTaskStateStr(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
            }
            supervisionApproveDataVOS.add(supervisionApproveDataVO);
        });
        pageInfo.setList(supervisionApproveDataVOS);
        return pageInfo;
    }

    @Override
    public PageInfo<SupervisionApproveDataVO> getSupervisionStoreTaskApproveData(String enterpriseId, String taskName, String type, CurrentUser user, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionApproveDO> supervisionApproveData = supervisionApproveDao.getSupervisionApproveData(enterpriseId, user.getUserId(), type, taskName);
        PageInfo pageInfo = new PageInfo<>(supervisionApproveData);
        if (CollectionUtils.isEmpty(supervisionApproveData)){
            return pageInfo;
        }
        List<Long> taskIdList = supervisionApproveData.stream().map(SupervisionApproveDO::getTaskId).collect(Collectors.toList());
        List<SupervisionStoreTaskDO> supervisionTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTask(enterpriseId,taskIdList);
        Map<Long, SupervisionStoreTaskDO> taskDOMap = supervisionTaskDOS.stream().collect(Collectors.toMap(SupervisionStoreTaskDO::getId, data -> data));

        List<SupervisionApproveDataVO> supervisionApproveDataVOS = new ArrayList<>();
        supervisionApproveData.forEach(x->{
            SupervisionApproveDataVO supervisionApproveDataVO = new SupervisionApproveDataVO();

            supervisionApproveDataVO.setId(x.getTaskId());
            supervisionApproveDataVO.setTaskName(x.getTaskName());
            SupervisionStoreTaskDO supervisionStoreTaskDO = taskDOMap.get(x.getTaskId());
            if (supervisionStoreTaskDO!=null){
                supervisionApproveDataVO.setSupervisionHandleUserId(supervisionStoreTaskDO.getSupervisionHandleUserId());
                supervisionApproveDataVO.setSupervisionHandleUserName(supervisionStoreTaskDO.getSupervisionHandleUserName());
                supervisionApproveDataVO.setSubmitTime(supervisionStoreTaskDO.getSubmitTime());
                supervisionApproveDataVO.setTransferReassignFlag(supervisionStoreTaskDO.getTransferReassignFlag());
                supervisionApproveDataVO.setTaskState(supervisionStoreTaskDO.getTaskState());
                supervisionApproveDataVO.setPriority(supervisionStoreTaskDO.getPriority());
                supervisionApproveDataVO.setStoreId(supervisionStoreTaskDO.getStoreId());
                supervisionApproveDataVO.setStoreName(supervisionStoreTaskDO.getStoreName());
                Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(null, supervisionStoreTaskDO.getCancelStatus(), supervisionStoreTaskDO.getTaskState());
                supervisionApproveDataVO.setTaskStateStr(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
            }
            supervisionApproveDataVOS.add(supervisionApproveDataVO);
        });
        pageInfo.setList(supervisionApproveDataVOS);
        return pageInfo;
    }
}
