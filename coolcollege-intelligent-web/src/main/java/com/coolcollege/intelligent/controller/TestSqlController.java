package com.coolcollege.intelligent.controller;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserQueryDTO;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping({"/test/sql"})
@Slf4j
public class TestSqlController {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @GetMapping("/selectUsersByUserIds")
    public Object selectUsersByUserIds(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIds);
    }

    @GetMapping("/selectActiveUsersByUserIds")
    public Object selectActiveUsersByUserIds(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectActiveUsersByUserIds(enterpriseId, userIds);
    }


    @GetMapping("/fuzzyUsersByUserIdsAndUserName")
    public Object fuzzyUsersByUserIdsAndUserName(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds
            , @RequestParam(value = "userName", required = false) String userName
            , @RequestParam(value = "userStatus", required = false) Integer userStatus) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.fuzzyUsersByUserIdsAndUserName(enterpriseId, userIds, userName, userStatus);
    }

    @GetMapping("/getDeptUser")
    public Object getDeptUser(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userType", required = false) String userType) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.getDeptUser(enterpriseId, userType);
    }

    @GetMapping("/selectUserRegionIdsByUserList")
    public Object selectUserRegionIdsByUserList(@RequestParam("enterpriseId")String enterpriseId
            , @RequestParam(value = "userIds", required = false) List<String> userIds, @RequestParam(value = "filterActive", required = false)Boolean filterActive) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectUserRegionIdsByUserList(enterpriseId, userIds, filterActive);
    }

    @GetMapping("/selectUserByKeyword")
    public Object selectUserByKeyword(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds,
                                      @RequestParam(value = "keyword", required = false)String keyword,
                                      @RequestParam(value = "userStatus", required = false)Integer userStatus,
                                      @RequestParam(value = "active", required = false)Boolean active) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectUserByKeyword(enterpriseId, keyword, userStatus,userIds,active);
    }

    @GetMapping("/selectByUserIdsAndStatus")
    public Object selectByUserIdsAndStatus(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds,
                                           @RequestParam(value = "userStatus", required = false)Integer userStatus) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectByUserIdsAndStatus(enterpriseId, userIds, userStatus);
    }

    @GetMapping("/selectUserByEid")
    public Object selectUserByEid(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "regionId", required = false) Long regionId) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.selectUserByEid(enterpriseId, regionId);
    }

    @GetMapping("/listUserIdByDepartmentIdList")
    public Object listUserIdByDepartmentIdList(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.listUserIdByDepartmentIdList(enterpriseId, userIds);
    }

    @GetMapping("/usersByUserIdList")
    public Object usersByUserIdList(@RequestParam("enterpriseId")String enterpriseId, @RequestParam(value = "userIds", required = false) List<String> userIds) {
        DataSourceHelper.changeToMy();
        return enterpriseUserMapper.usersByUserIdList(enterpriseId, userIds);
    }

}
