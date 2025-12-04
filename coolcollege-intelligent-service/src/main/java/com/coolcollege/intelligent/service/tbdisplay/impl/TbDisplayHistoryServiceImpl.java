package com.coolcollege.intelligent.service.tbdisplay.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayHistoryColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayHistoryMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayHistoryUserVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayHistoryVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayHistoryService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @date 2021-03-02 19:49
 */
@Service
@Slf4j
public class TbDisplayHistoryServiceImpl implements TbDisplayHistoryService {

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private TbDisplayHistoryMapper tbDisplayHistoryMapper;

    @Resource
    private TbDisplayHistoryColumnMapper tbDisplayHistoryColumnMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Autowired
    private EnterpriseUserDao enterpriseUserDao;

    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Override
    public List<TbDisplayHistoryVO> listHistoryByTaskSubId(String enterpriseId, Long taskSubId) {
        TaskSubDO taskSubDO = taskSubMapper.getSimpleTaskSubDOListById(enterpriseId, taskSubId);
        if(taskSubDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的子任务");
        }
        TaskParentDO taskParentDO =  taskParentMapper.selectParentTaskByTaskId(enterpriseId, taskSubDO.getUnifyTaskId());
        if (taskParentDO != null && Constants.SYSTEM_USER_ID.equals(taskParentDO.getCreateUserId())) {
            taskParentDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }
        TbDisplayTableRecordDO tableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        List<TbDisplayHistoryDO> historyDOList = tbDisplayHistoryMapper.listByRecordId(enterpriseId, tableRecordDO.getId());
        List<Long> historyIdList = historyDOList.stream().map(TbDisplayHistoryDO::getId).collect(Collectors.toList());
        // 操作用户id集合
        List<String> operateUserIdList = historyDOList.stream().map(TbDisplayHistoryDO::getOperateUserId).collect(Collectors.toList());
        if(CollUtil.isEmpty(operateUserIdList)){
            operateUserIdList = Lists.newArrayList();
        }
        operateUserIdList.add(taskParentDO.getCreateUserId());
        Map<String, EnterpriseUserDO>  userIdMap = enterpriseUserDao.getUserMap(enterpriseId, operateUserIdList);
        Map<Long,List<TbDisplayHistoryColumnDO>> tbDisplayHistoryColumnDOMap = Maps.newHashMap();
        if(CollUtil.isNotEmpty(historyIdList)){
            List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnDOList = tbDisplayHistoryColumnMapper.getListByHistoryId(enterpriseId, historyIdList);
            tbDisplayHistoryColumnDOMap = tbDisplayHistoryColumnDOList.stream().collect(Collectors.groupingBy(TbDisplayHistoryColumnDO::getHistoryId));
        }
        List<TbDisplayHistoryVO> tbDisplayHistoryVOList = Lists.newArrayList();
        List<Long> taskIdList = new ArrayList<>();
        taskIdList.add(taskSubDO.getUnifyTaskId());
        // List<UnifyPersonDTO> unifyPersonDTOS = taskMappingMapper.selectPersonInfoByTaskList(enterpriseId, taskIdList, taskSubDO.getStoreId(), null);
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(taskSubDO.getUnifyTaskId()), Collections.singletonList(taskSubDO.getStoreId()), taskSubDO.getLoopCount());
        Map<String, List<TbDisplayHistoryUserVO>> personMap = unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                        Collectors.mapping(s -> new TbDisplayHistoryUserVO(s.getNode(), s.getUserId(),s.getUserName(), s.getAvatar(),null), Collectors.toList())));

        TbDisplayHistoryVO createHistory = new TbDisplayHistoryVO();
        createHistory.setCreateTime(new Date(taskParentDO.getCreateTime()));
        createHistory.setOperateType("create");
        createHistory.setOperateUserId(taskParentDO.getCreateUserId());
        createHistory.setOperateUserName(taskParentDO.getCreateUserName());
        createHistory.setAvatar("");
        if (userIdMap.get(taskParentDO.getCreateUserId()) != null) {
            createHistory.setAvatar(userIdMap.get(taskParentDO.getCreateUserId()).getAvatar());
        }
        createHistory.setNodeNo(UnifyNodeEnum.ZERO_NODE.getCode());
        tbDisplayHistoryVOList.add(createHistory);

        Map<Long, List<TbDisplayHistoryColumnDO>> finalTbDisplayHistoryColumnDOMap = tbDisplayHistoryColumnDOMap;
        Map<String, EnterpriseUserDO> finalUserIdMap = userIdMap;
        historyDOList.stream().map(a -> {
            TbDisplayHistoryVO tbDisplayHistoryVO = new TbDisplayHistoryVO();
            BeanUtils.copyProperties(a, tbDisplayHistoryVO);
            List<TbDisplayHistoryColumnDO> tbDisplayHistoryColumnDOListTemp = finalTbDisplayHistoryColumnDOMap.get(a.getId());
            tbDisplayHistoryVO.setApprovalDataNew(tbDisplayHistoryColumnDOListTemp);
            tbDisplayHistoryVO.setHistoryUserVOList(personMap.get(a.getNodeNo()));
            if(finalUserIdMap.get(a.getOperateUserId()) != null ){
                tbDisplayHistoryVO.setAvatar(finalUserIdMap.get(a.getOperateUserId()).getAvatar());
            } else {
                if (Constants.SYSTEM_USER_ID.equals(a.getOperateUserId())) {
                    tbDisplayHistoryVO.setOperateUserName(Constants.SYSTEM_USER_NAME);
                }
            }
            tbDisplayHistoryVO.setScore(a.getScore());
            JSONObject extendInfo = JSONObject.parseObject(a.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                JSONArray imageList = extendInfo.getJSONArray(CommonConstant.ExtendInfo.DISPLAY_APPROVE_IMAGE_LIST);
                if (Objects.nonNull(imageList)) {
                    tbDisplayHistoryVO.setApproveImageList(imageList.toJavaList(String.class));
                }
            }
            tbDisplayHistoryVOList.add(tbDisplayHistoryVO);
            return tbDisplayHistoryVO;
        }).collect(Collectors.toList());
        return tbDisplayHistoryVOList;
    }
}
