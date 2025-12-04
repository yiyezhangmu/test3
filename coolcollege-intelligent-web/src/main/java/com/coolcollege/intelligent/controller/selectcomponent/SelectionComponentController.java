package com.coolcollege.intelligent.controller.selectcomponent;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @desc: 选择组件，选人/选店组件
 * @author: xuanfeng
 * @date: 2021-10-27 14:40
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/selection", "/v3/enterprises/{enterprise-id}/selection"})
@BaseResponse
@Slf4j
public class SelectionComponentController {

    @Autowired
    private SelectionComponentService selectionComponentService;

    @Autowired
    private SubordinateMappingService subordinateMappingService;

    /**
     * 选择组件中常用联系人筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getSelectionUserByKeyword")
    public ResponseResult getSelectionUserByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                    @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                    @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                                    @RequestParam(value = "active",  required = false) Boolean active,
                                                    @RequestParam(value = "hasAuth", required = false, defaultValue = "true") boolean hasAuth) {

        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(selectionComponentService.getSelectionUserByKeyword(enterpriseId, keyword, pageNum, pageSize, active, currentUser.getUserId(),hasAuth));
    }

    /**
     * 选择组件中岗位筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getSelectionPositionByKeyword")
    public ResponseResult getSelectionPositionByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                    @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                    @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getSelectionPositionByKeyword(enterpriseId, keyword, pageNum, pageSize));
    }

    /**
     * 选择组件中门店筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getSelectionStoreByKeyword")
    public ResponseResult getSelectionStoreByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                        @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                        @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                        @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                                     @RequestParam(value = "storeStatusList",required = false) List<String> storeStatusList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getSelectionStoreByKeyword(enterpriseId, keyword, pageNum, pageSize, storeStatusList));
    }

    /**
     * 选择组件中部门筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getSelectionDepartmentByKeyword")
    public ResponseResult getSelectionDepartmentByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                          @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                          @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                          @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getSelectionDepartmentByKeyword(enterpriseId, keyword, pageNum, pageSize));
    }

    /**
     * 选人组件 根据门店id查询人员
     * @param enterpeiseId
     * @param storeId
     * @return
     */
    @GetMapping("/getSelectUserByStoreId")
    public Object getPersonByStoreId(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                     @RequestParam(value = "store_id", required = false) String storeId,
                                     @RequestParam(value = "active", required = false) Boolean active) {
        DataSourceHelper.changeToMy();
        List<SelectComponentUserVO> selectComponentUserVOList = selectionComponentService.getSelectUserByStoreId(enterpeiseId, storeId, active);
        CurrentUser currentUser = UserHolder.getUser();
        Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(enterpeiseId, currentUser.getUserId());
        List<String> userSubordinateList = Lists.newArrayList();
        if(!haveAllSubordinateUser){
            userSubordinateList = subordinateMappingService.getSubordinateUserIdList(enterpeiseId, currentUser.getUserId(),Boolean.TRUE);
        }
        List<String> finalUserSubordinateList = userSubordinateList;
        selectComponentUserVOList.forEach(f -> {
            if(haveAllSubordinateUser){
                f.setSelectFlag(true);
            }else {
                f.setSelectFlag(finalUserSubordinateList.contains(f.getUserId()));
            }
        });
        return ResponseResult.success(selectComponentUserVOList);
    }

    /**
     * 选人组件 根据门店id以及关键字查询人员
     * @param enterpeiseId
     * @param storeId
     * @return
     */
    @GetMapping("/getSelectUserByStoreIdAndKeyword")
    public Object getSelectUserByStoreIdAndKeyword(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                     @RequestParam(value = "store_id", required = false) String storeId,
                                     @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getSelectUserByStoreIdAndKeyword(enterpeiseId, storeId, keyword));
    }

    /**
     * 选店组件-常用门店筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getCommonStores")
    public ResponseResult getCommonStores(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                          @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                          @RequestParam(value = "isByKeyword", defaultValue = "false", required = false) Boolean isByKeyword,
                                          @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getCommonStores(enterpriseId, keyword, pageNum, pageSize, isByKeyword, storeStatusList));
    }

    /**
     * 选店组件-区域根据关键字搜索
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getRegionsByKeyword")
    public ResponseResult getRegionsByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                              @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                              @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                              @RequestParam(value = "authUserId",required = false) String authUserId) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        return ResponseResult.success(selectionComponentService.getRegionsByKeyword(enterpriseId, keyword, pageNum, pageSize, userId));
    }
    /**
     * zxjp选店组件-区域根据关键字搜索
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getZxjpRegionsByKeyword")
    public ResponseResult getZxjpRegionsByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                              @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                              @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                              @RequestParam(value = "authUserId",required = false) String authUserId) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        return ResponseResult.success(selectionComponentService.getZxjpRegionsByKeyword(enterpriseId, keyword, pageNum, pageSize, userId));
    }

    /**
     * 选店组件-按照区域筛选
     * @param eid
     * @param parentId
     * @return
     */
    @GetMapping("/getRegionAndStore")
    public ResponseResult getRegionAndStore(@PathVariable(value = "enterprise-id") String eid,
                                            @RequestParam(value = "parentId", required = false) Long parentId,
                                            @RequestParam(value = "authUserId",required = false) String authUserId,
                                            @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        return ResponseResult.success(selectionComponentService.getRegionAndStore(eid, parentId, userId, storeStatusList));
    }

    /**
     * zxjp区域组件
     * @param eid
     * @param parentId
     * @return
     */
    @GetMapping("/getRegion")
    public ResponseResult getRegion(@PathVariable(value = "enterprise-id") String eid,
                                            @RequestParam(value = "parentId", required = false) Long parentId,
                                            @RequestParam(value = "authUserId",required = false) String authUserId) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        return ResponseResult.success(selectionComponentService.getRegion(eid, parentId, userId));
    }




    /**
     * 选店组件-按照区域筛选（全路径模式）
     * @param eid
     * @param parentId
     * @return
     */
    @GetMapping("/getRegionAndStore/fullPath")
    public ResponseResult getRegionAndStoreFullPath(@PathVariable(value = "enterprise-id") String eid,
                                            @RequestParam(value = "parentId", required = false) Long parentId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getRegionAndStoreFullPath(eid, parentId));
    }

    /**
     * 选店组件-搜索门店
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getStoresByKeyword")
    public ResponseResult getCommonStores(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                          @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                          @RequestParam(value = "authUserId",required = false) String authUserId,
                                          @RequestParam(value = "storeStatusList",required = false) List<String> storeStatusList) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        return ResponseResult.success(selectionComponentService.getStoresByKeyword(enterpriseId, keyword, pageNum, pageSize, userId, storeStatusList));
    }

    /**
     * 根据regionId获取上级区域调用链
     * @param enterpriseId
     * @param regionId
     * @return
     */
    @GetMapping("/getParentRegionsByRegionId")
    public ResponseResult getRegionByRegionId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "region_id", defaultValue = "", required = false) String regionId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(selectionComponentService.getParentRegionsByRegionId(enterpriseId, regionId, UserHolder.getUser()));
    }
}
