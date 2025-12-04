package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.ThirdDepartmentMapper;
import com.coolcollege.intelligent.model.enterprise.ThirdDepartmentDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * describe:
 * @author wxp
 * @date 2020/09/17
 */
@Service
public class ThirdDepartmentDao {

   @Resource
   private ThirdDepartmentMapper thirdDepartmentMapper;

    /**
     * 删除
     * @param eid
     * @param departmentCodes
     */
    public void deleteThirdDepartment(String eid, List<String> departmentCodes){
        if (CollectionUtils.isEmpty(departmentCodes)) {
            return ;
        }
        thirdDepartmentMapper.deleteThirdDepartment(eid, departmentCodes);
    }

    /**
     * 批量新增三方部门
     * @param eid
     * @param thirdDepartments
     */
    public void batchInsertOrUpdate(String eid, List<ThirdDepartmentDO> thirdDepartments) {
        if (CollectionUtils.isEmpty(thirdDepartments)) {
            return;
        }
        List<ThirdDepartmentDO> distinctData = thirdDepartments.stream()
                .distinct()
                .collect(Collectors.toList());
        thirdDepartmentMapper.batchInsertOrUpdate(eid, distinctData);
    }

    public List<ThirdDepartmentDO> listByDeptPrincipals(String eid, List<String> deptPrincipals) {
        if (CollectionUtils.isEmpty(deptPrincipals)) {
            return new ArrayList<>();
        }
        return thirdDepartmentMapper.listByDeptPrincipals(eid, deptPrincipals);
    }

    public List<ThirdDepartmentDO> listAllThirdDepartment(String eid) {
        return thirdDepartmentMapper.listAllThirdDepartment(eid);
    }

    public ThirdDepartmentDO getByDepartmentCode(String eid, String departmentCode) {
        if(StringUtils.isBlank(departmentCode)) {
            return null;
        }
        return thirdDepartmentMapper.getByDepartmentCode(eid, departmentCode);
    }

}
