package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserDepartmentMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/17
 */
@Service
public class EnterpriseUserDepartmentDao {

   @Resource
   private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;

    /**
     * 删除部门用户的关系
     * @param eid
     * @param userIds
     */
    public void deleteMapping(String eid, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)) {
            return ;
        }
        List<String> distinctData = userIds.stream()
                .distinct()
                .collect(Collectors.toList());
        enterpriseUserDepartmentMapper.deleteMapping(eid, distinctData);
    }

    /**
     * 删除部门用户主管的关系
     * @param eid
     * @param userIds
     */
    public void deleteUserDepartmentAuth(String eid, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)) {
            return ;
        }
        List<String> distinctData = userIds.stream()
                .distinct()
                .collect(Collectors.toList());
        enterpriseUserDepartmentMapper.deleteUserDepartmentAuth(eid, distinctData);
    }

    /**
     * p批量新增部门和用户的关系
     * @param eid
     * @param deptUsers
     */
    public void batchInsert(String eid, List<EnterpriseUserDepartmentDO> deptUsers) {
        if (CollectionUtils.isEmpty(deptUsers)) {
            return;
        }
        List<EnterpriseUserDepartmentDO> distinctData = deptUsers.stream()
                .distinct()
                .collect(Collectors.toList());
        enterpriseUserDepartmentMapper.batchInsert(eid, distinctData);
    }

    /**
     * 根据用户的id 获取映射关系部门的id
     * @param eid
     * @param userIds
     * @return
     */
    public List<EnterpriseUserDepartmentDO> selectDeptIdByUserIds(String eid, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return enterpriseUserDepartmentMapper.selectDeptIdByUserIds(eid, userIds);
    }

    /**
     * 根据用户的id 获取映射关系权限部门的id
     * @param eid
     * @param userIds
     * @return
     */
    public List<EnterpriseUserDepartmentDO> selectDeptAuthByUserIds(String eid, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return enterpriseUserDepartmentMapper.selectDeptAuthByUserIds(eid, userIds);
    }
}
