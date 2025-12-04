package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.ThirdDepartmentDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2024-04-02 04:42
 */
public interface ThirdDepartmentMapper {
    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-04-02 04:42
     */
    ThirdDepartmentDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);
    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-04-02 04:42
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    Integer deleteThirdDepartment(@Param("eid")String eid, @Param("departmentCodes") List<String> departmentCodes);

    void batchInsertOrUpdate(@Param("eid") String eid, @Param("thirdDepartments") List<ThirdDepartmentDO> thirdDepartments);

    List<ThirdDepartmentDO> listByDeptPrincipals(@Param("eid")String eid, @Param("deptPrincipals") List<String> deptPrincipals);

    List<ThirdDepartmentDO> listAllThirdDepartment(@Param("eid")String eid);

    ThirdDepartmentDO getByDepartmentCode(@Param("eid") String eid, @Param("departmentCode") String departmentCode);



}