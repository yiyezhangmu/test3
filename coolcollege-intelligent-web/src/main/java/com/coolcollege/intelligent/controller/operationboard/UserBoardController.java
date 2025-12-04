package com.coolcollege.intelligent.controller.operationboard;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.operationboard.dto.PatrolTypeStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TaskStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import org.apache.commons.collections4.CollectionUtils;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import org.springframework.web.bind.annotation.*;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.operationboard.dto.UserStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.query.UserDetailStatisticsQuery;
import com.coolcollege.intelligent.model.operationboard.query.UserStatisticsQuery;
import com.coolcollege.intelligent.service.operationboard.UserBoardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/1/8 11:29
 * @Description 运营看板controller
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/operationboard/userboard")
@BaseResponse
public class UserBoardController {
    @Resource
    private UserBoardService userBoardService;
    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 移动端人员执行力汇总
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("userStatistics")
    public ResponseResult userStatistics(@PathVariable("enterprise-id") String enterpriseId,@RequestBody @Valid UserStatisticsQuery query){
        DataSourceHelper.changeToMy();
        List<SysRoleDO> roleList = new ArrayList<>();
        if(CollectionUtils.isEmpty(query.getRoleIdList())&&CollectionUtils.isEmpty(query.getUserIdList())){
            SysRoleDO adminRole = sysRoleMapper.getAdminRole(enterpriseId);
            if(adminRole == null){
                UserStatisticsDTO userStatisticsDTO = UserStatisticsDTO.builder()
                    .createTableCount(0).finishQuestionNum(0)
                    .patrolStoreNum(0)
                    .personCount(0)
                    .totalQuestionNum(0)
                    .totalTaskNum(0)
                    .defaultRoleList(new ArrayList<>()).build();
            return ResponseResult.success(userStatisticsDTO);
            }
            roleList.add(adminRole);
            List<Long> idList = roleList.stream().map(data -> data.getId()).collect(Collectors.toList());
            query.setRoleIdList(idList);
//            UserStatisticsDTO userStatisticsDTO = UserStatisticsDTO.builder()
//                    .createTableCount(0).finishQuestionNum(0)
//                    .patrolStoreNum(0)
//                    .personCount(0)
//                    .totalQuestionNum(0)
//                    .totalTaskNum(0)
//                    .defaultRoleList(new ArrayList<>()).build();
//            return ResponseResult.success(userStatisticsDTO);
        }
        UserStatisticsDTO userStatisticsDTO = userBoardService.userStatistics(enterpriseId,query);
        userStatisticsDTO.setDefaultRoleList(roleList);
        return ResponseResult.success(userStatisticsDTO);
    }

    /**
     * 个人巡店详情统计
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("userDetailStatistics")
    public ResponseResult userDetailStatistics(@PathVariable("enterprise-id") String enterpriseId,@RequestBody @Valid UserDetailStatisticsQuery query){
        DataSourceHelper.changeToMy();
        List<SysRoleDO> roleList = new ArrayList<>();
        if(CollectionUtils.isEmpty(query.getRoleIdList())&&CollectionUtils.isEmpty(query.getUserIdList())){
            SysRoleDO  adminRole = sysRoleMapper.getAdminRole(enterpriseId);
            if(adminRole == null){
                return ResponseResult.success(new PageInfo<>());
            }
            roleList.add(adminRole);
            List<Long> idList = roleList.stream().map(data -> data.getId()).collect(Collectors.toList());
            query.setRoleIdList(idList);
        }
        query.setDefaultRoleList(roleList);
        PageInfo<UserDetailStatisticsDTO> pageInfo = userBoardService.userDetailStatistics(enterpriseId,query);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    /**
     * 任务状态统计
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("taskStatistics")
    public ResponseResult taskStatistics(@PathVariable("enterprise-id") String enterpriseId,@RequestBody @Valid UserStatisticsQuery query){
        DataSourceHelper.changeToMy();
        List<SysRoleDO> roleList = new ArrayList<>();
        if(CollectionUtils.isEmpty(query.getRoleIdList())&&CollectionUtils.isEmpty(query.getUserIdList())){
            SysRoleDO adminRole = sysRoleMapper.getAdminRole(enterpriseId);
            if(adminRole == null){
                return ResponseResult.success(new TaskStatisticsDTO());
            }
            roleList.add(adminRole);
            List<Long> idList = roleList.stream().map(data -> data.getId()).collect(Collectors.toList());
            query.setRoleIdList(idList);
//            TaskStatisticsDTO taskStatisticsDTO = new TaskStatisticsDTO();
//            taskStatisticsDTO.setDefaultRoleList(new ArrayList<>());
//            return ResponseResult.success(taskStatisticsDTO);
        }
        TaskStatisticsDTO result = userBoardService.taskStatistics(enterpriseId,query);
        result.setDefaultRoleList(roleList);
        return ResponseResult.success(result);
    }

    /**
     * 巡店方式统计
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("patrolTypeStatistics")
    public ResponseResult patrolTypeStatistics(@PathVariable("enterprise-id") String enterpriseId,@RequestBody @Valid UserStatisticsQuery query){
        DataSourceHelper.changeToMy();
        List<SysRoleDO> roleList = new ArrayList<>();
        if(CollectionUtils.isEmpty(query.getRoleIdList())&&CollectionUtils.isEmpty(query.getUserIdList())){
            SysRoleDO adminRole = sysRoleMapper.getAdminRole(enterpriseId);
            if(adminRole == null){
                return ResponseResult.success(new PatrolTypeStatisticsDTO());
            }
            roleList.add(adminRole);
            List<Long> idList = roleList.stream().map(data -> data.getId()).collect(Collectors.toList());
            query.setRoleIdList(idList);
//            PatrolTypeStatisticsDTO patrolTypeStatisticsDTO = new PatrolTypeStatisticsDTO();
//            patrolTypeStatisticsDTO.setDefaultRoleList(new ArrayList<>());
//            return ResponseResult.success(patrolTypeStatisticsDTO);
        }
        PatrolTypeStatisticsDTO result = userBoardService.patrolTypeStatistics(enterpriseId,query);
        result.setDefaultRoleList(roleList);
        return ResponseResult.success(result);
    }
}
