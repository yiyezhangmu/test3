package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.department.dto.DeptChildDTO;
import com.coolcollege.intelligent.model.department.dto.DeptUserTreeDTO;
import com.coolcollege.intelligent.model.department.dto.QueryDeptChildDTO;
import com.coolcollege.intelligent.model.department.dto.SyncTreeNode;
import com.coolcollege.intelligent.model.enterprise.DeptIdDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserDeptDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface SysDepartmentMapper {

    /**
     * 批量插入部门
     * @param eid
     * @param sysDepartmentDOList
     */
    void batchInsertOrUpdate(@Param("list") List<SysDepartmentDO> sysDepartmentDOList, @Param("eid") String eid);

    void syncBatchInsertOrUpdate(@Param("list") List<SysDepartmentDO> sysDepartmentDOList, @Param("eid") String eid);

    /**
     * 插入部门
     * @param eid
     * @param department
     * @author: xugangkun
     * @return void
     * @date: 2021/6/9 17:35
     */
    void insertDept(@Param("eid") String eid, @Param("department") SysDepartmentDO department);

    /**
     * 更新部门
     * @param eid
     * @param department
     * @author: xugangkun
     * @return void
     * @date: 2021/6/9 17:35
     */
    void updateDeptName(@Param("eid") String eid, @Param("department") SysDepartmentDO department);

    /**
     * 获取部门列表
     * @param eid
     * @return
     */
    List<SysDepartmentDO> selectAll(@Param("eid") String eid);

    /**
     * 根据id获取部门信息
     * @param eid
     * @param id
     * @return
     */
    SysDepartmentDO selectById(@Param("eid") String eid, @Param("id") String id);

    /**
     * 根据id删除部门
     * @param sysDepartmentDOList
     * @param eid
     */
    void deleteByIds(@Param("list") List<String> sysDepartmentDOList, @Param("eid") String eid);

    /**
     * 根据id删除部门
     * @param deptIdList
     * @param eid
     */
    void deleteByNotInIds(@Param("list") List<String> deptIdList, @Param("eid") String eid);

    /**
     * 获取部门根据部门名称
     * @param eid
     * @param name
     * @param deptIdList
     * @return
     */
    List<DeptNode> getDepListByDepName(@Param("eid") String eid, @Param("name") String name, @Param("deptIdList") List<Long> deptIdList);

    /**
     * 删除用户和部门的关联信息
     * @param eip
     * @param userId
     * @return
     */
    Boolean deleteDepartmentUser(@Param("eip")String eip,@Param("userId")String userId);

    /**
     * 获取部门列表
     * @param eid
     * @return
     */
    List<DeptUserTreeDTO> getDeptList(@Param("eid") String eid);

    /**
     * 根据部门id获取多个部门
     * @param eid
     * @param ids
     * @return
     */
    List<SysDepartmentDO> getAllDepartmentList(@Param("eid")String eid,@Param("ids")List<String> ids);

    SysDepartmentDO getDepartmentByName(@Param("eid")String eid,@Param("name")String name);

    List<SysDepartmentDO> getDepartmentList(@Param("eid")String eid,@Param("ids")List<String> ids);

    /**
     * 获取所有的id和服id
     * @param eid
     * @return
     */
    List<DeptIdDTO> getAllIdAndParentId(@Param("eid") String eid, @Param("id") String id, @Param("recursion") boolean recursion);

    /**
     * 获取隐藏的部门
     * @param eid
     * @return
     */
    List<SysDepartmentDO> selectHideDept(@Param("eid") String eid);

    /**
     * 获取部门树列表
     * @param eid
     * @return
     */
    List<SyncTreeNode> getSyncDeptTreeList(@Param("eid") String eid, @Param("parentId") String parentId);

    /**
     * 获取部门子节点
     * @param eid
     * @param parentId
     * @return
     */
    List<DeptChildDTO> getDeptChildList(@Param("eid") String eid, @Param("pids") List<String> parentId);

    /**
     * 获取部门子节点
     * @param eid
     * @param idList
     * @return
     */
    List<DeptChildDTO> getDeptListById(@Param("eid") String eid, @Param("pids") List<String> idList);

    /**
     * 根据用户id获取部门子节点
     * @param eid
     * @param parentId
     * @return
     */
    List<QueryDeptChildDTO> getDeptChildListByParentId(@Param("eid") String eid, @Param("parentId") String parentId);

    /**
     * 获取部门子节点
     * @param eid
     * @param parentId
     * @return
     */
    List<DeptChildDTO> getDeptUserList(@Param("eid") String eid, @Param("pids") List<String> parentId);

    /**
     * 获取用户的部门信息
     * @param eid
     * @param userId
     * @return
     */
    List<SelectUserDeptDTO> selectUserDeptInfo(@Param("eid") String eid, @Param("userId") String userId);

    /**
     * 获得部门id列表
     * @param eid
     * @return: java.util.List<java.lang.Long>
     * @Author: xugangkun
     * @Date: 2021/3/27 19:46
     */
    List<String> selectIdList(@Param("eid") String eid);

    /**
     * 批量更新部门名
     * @param enterpriseId
     * @param depts
     * @return
     */
    Long batchUpdateDeptName(@Param("eid") String enterpriseId, @Param("list") List<SysDepartmentDO> depts);

    /**
     * 批量更新部门顺序值
     * @param enterpriseId
     * @param depts
     */
    void batchUpdateDeptOrder(@Param("eid") String enterpriseId, @Param("list") List<SysDepartmentDO> depts);



    /**
     * 获得用户主管部门id列表
     * @param eid
     * @return: java.util.List<java.lang.Long>
     * @Author: xugangkun
     * @Date: 2021/3/27 19:46
     */
    List<String> selectIdListByUserId(@Param("eid") String eid, @Param("userId") String userId);

    List<Long> listParentIdByIdList(@Param("eid") String eid, @Param("idList") List<Long> idList);

    Integer batchUpdateDept(@Param("enterpriseId") String enterpriseId, @Param("deptList") List<SysDepartmentDO> deptList);

    List<String> getAllSubDeptIdList(@Param("enterpriseId") String enterpriseId, @Param("deptId") String deptId);

    List<SysDepartmentDO> getAllSubDeptList(@Param("enterpriseId") String enterpriseId, @Param("deptId") String deptId);

    List<SysDepartmentDO> getDeptListBySyncId(@Param("enterpriseId") String enterpriseId, @Param("syncId") Long syncId);

}
