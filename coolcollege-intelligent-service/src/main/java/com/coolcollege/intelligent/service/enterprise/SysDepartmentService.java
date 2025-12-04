package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.department.dto.DepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.SyncTreeNode;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.qywx.dto.ImportUserDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SysDepartmentService {

    /**
     * 批量插入企业部门
     *
     * @param sysDepartments
     * @param eid
     */
    void batchInsertOrUpdate(List<SysDepartmentDO> sysDepartments, String eid);

    /**
     * 查询企业所有部门
     *
     * @param eid
     * @return
     */
    List<SysDepartmentDO> selectAllDepts(String eid);

    /**
     * 删除企业部门
     *
     * @param sysDepartmentDOList
     * @param eid
     */
    void deleteByIds(List<String> sysDepartmentDOList, String eid);

    /**
     * @param eid
     * @return
     */
    TwoResultTuple<Set<String>, Map<String, String>> getAllDeptInfo(String eid);

    /**
     * 获得部门建档信息
     * @param eid
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.department.dto.SyncTreeNode>
     * @date: 2021/10/28 14:55
     */
    List<SyncTreeNode> getSyncDeptTreeList(String eid, String parentId);

    /**
     * 根据名字查询部门节点集合
     *
     * @param eid
     * @param name
     * @return
     */
    List<DeptNode> getDepListByDepName(String eid, String name);

    /**
     * 查询部门下人员列表
     *
     * @param enterpriseId
     * @param departmentQueryDTO
     * @return
     */
    Object getDepUsersByPage(String enterpriseId, DepartmentQueryDTO departmentQueryDTO,Boolean type);

//    /**
//     * 获取企业下的管理员、主管
//     * @param accessToken
//     * @return
//     */
//    List<String> getAdminList(String accessToken);

    /**
     * 获取部门人员树形结构
     * @param eid
     * @param userType
     * @return
     */
    Object getDeptUserTree(String eid, String userType);

    /**
     * 根据id获取部门信息
     * @param eid
     * @param id
     * @return
     */
    SysDepartmentDO selectById(String eid, String id);

    /**
     * 获取父节点下子部门id
     * @param eid
     * @param id
     * @param recursion 是否递归查询
     * @return
     */
    List<String> getChildDeptIdList(String eid, String id, boolean recursion);

    /**
     * 获取部门子节点
     * @param eid
     * @param pid
     * @param hasUser
     * @return
     */
    Object getDeptChild(String eid, String pid, boolean hasUser, boolean hasUserNum, Boolean hasAuth, String userId);

    /**
     * 获得部门id列表
     * @param eid
     * @return: java.util.List<java.lang.Long>
     * @Author: xugangkun
     * @Date: 2021/3/27 19:46
     */
    List<String> selectIdList(String eid);

    /**
     * 初始化企业微信部门
     * @param eid 企业id
     * @param importUserDTO 用户部门信息
     * @author: xugangkun
     * @return void
     * @date: 2021/6/9 16:22
     */
    void initWeComDepartment(String eid, ImportUserDTO importUserDTO);


    void batchUpdateDeptName(String enterpriseId, List<SysDepartmentDO> depts);

    List<Long> listParentIdByIdList(String eid, List<Long> idList);
}
