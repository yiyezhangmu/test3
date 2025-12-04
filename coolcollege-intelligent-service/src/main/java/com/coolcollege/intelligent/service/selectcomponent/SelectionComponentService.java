package com.coolcollege.intelligent.service.selectcomponent;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.department.dto.DeptChildDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserInfoDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.selectcomponent.*;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;

import java.util.List;
import java.util.Map;


/**
 * @author: xuanfeng
 * @date: 2021-10-27 14:45
 */
public interface SelectionComponentService {

    /**
     * 选择组件中常用联系人筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentUserVO> getSelectionUserByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean active, String currentUserId);

    /**
     * 选择组件中常用联系人筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentUserVO> getSelectionUserByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean active, String currentUserId,Boolean hasAuth);

    /**
     * 选择组件中岗位筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentPositionVO> getSelectionPositionByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 选择组件中门店筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentStoreVO> getSelectionStoreByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, List<String> storeStatusList);

    /**
     * 选择组件中部门筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentDepartmentVO> getSelectionDepartmentByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 填充以前接口选人组件中部门中的人员信息
     * @param eid
     * @param deptUserList
     * @return
     */
    List<DeptChildDTO> supplementDeptUserQueryResult(String eid, List<DeptChildDTO> deptUserList);

    /**
     * 填充以前接口选人组件中常用联系人的人员信息
     * @param eid
     * @param selectUserDTOS
     * @return
     */
    List<SelectUserDTO> supplementRecentUserQueryResult(String eid, List<SelectUserDTO> selectUserDTOS);

    /**
     * 填充以前接口选人组件中点击联系人弹出的人员信息
     * @param eid
     * @param selectUserInfoDTO
     * @return
     */
    SelectUserInfoDTO supplementClickUserQueryResult(String eid, SelectUserInfoDTO selectUserInfoDTO);

    /**
     * 填充以前接口选人组件中按照职位查询人员的信息
     * @param eid
     * @param userDTOS
     * @return
     */
    List<UserDTO> supplementSelectRoleUserQueryResult(String eid, List<UserDTO> userDTOS);

    /**
     * 选人组件 根据门店id查询门店下的用户
     * @param eid
     * @param storeId
     * @return
     */
    List<SelectComponentUserVO> getSelectUserByStoreId(String eid, String storeId, Boolean active);

    /**
     * 选人组件 根据门店id以及关键字查询人员
     * @param eid
     * @param storeId
     * @param keyword
     * @return
     */
    List<SelectComponentUserVO> getSelectUserByStoreIdAndKeyword(String eid, String storeId, String keyword);

    /**
     * 选店组件-常用门店筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param isByKeyword
     * @return
     */
    PageVO<SelectComponentStoreVO> getCommonStores(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean isByKeyword, List<String> storeStatusList);

    /**
     * 门店搜索
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentStoreVO> getStoresByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId, List<String> storeStatusList);

    /**
     * 选店组件-根据关键字搜索区域
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentRegionVO> getRegionsByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId);

    PageVO<SelectComponentRegionVO> getZxjpRegionsByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId);

    /**
     * 选店组件-按照区域选择重构方法
     * @param eid
     * @param parentId
     * @return
     */
    SelectComptRegionStoreVO getRegionAndStore(String eid, Long parentId, String userId, List<String> storeStatusList);

    SelectComptRegionStoreVO getRegion(String eid, Long parentId, String userId);

    /**
     * 选店组件-按照区域选择重构方法(全路径模式)
     * @param eid
     * @param parentId
     * @return
     */
    SelectComptRegionStoreVO getRegionAndStoreFullPath(String eid, Long parentId);


    /**
     * 根据区域id 获取该区域以及上级的区域链
     * @param eid
     * @param regionId
     * @return
     */
    SelectComponentRegionVO getParentRegionsByRegionId(String eid, String regionId, CurrentUser user);

    List<RegionDO> getRegionsByRegionPath(List<RegionDO> regionDOS, String eid);

    List<SelectComponentRegionVO> fetchSelectComponentRegionVO(List<String> regionIds, Map<String, RegionDO> regionDOSMap);
}
