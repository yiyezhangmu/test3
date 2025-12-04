package com.coolcollege.intelligent.controller.menu;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * describe:企业获取的api
 *
 * @author zhouyiping
 * @date 2020/09/22
 */
@RestController
@RequestMapping("/v2/enterprises/{enterprise-id}/users")
@BaseResponse
@Slf4j
public class MenuEnterpriseController {

    @Autowired
    public SysMenuService sysMenuService;
    /**
     * 获取用户的模块权限
     * @param enterpriseId
     * @param userId
     * @return
     */
    @GetMapping(path = "/getEnterpriseUseMenus")
    public ResponseResult  getEnterpriseUseMenusOld(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestParam(value = "userId",required = false) String userId){
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getUserMenus(enterpriseId,userId,user,true));
    }
    @GetMapping(path = "/getEnterpriseUseMenus/v2")
    public ResponseResult  getEnterpriseUseMenus(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestParam(value = "userId",required = false) String userId){
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getUserMenus(enterpriseId,userId,user,false));
    }
    @GetMapping(path = "/getEnterpriseUseMenus/all")
    public ResponseResult  getEnterpriseUseMenusAll(@PathVariable(value = "enterprise-id", required = false) String enterpriseId){
        return ResponseResult.success(sysMenuService.getEnterpriseMenus(enterpriseId));
    }


    @GetMapping(path = "/getAppAuthList")
    public ResponseResult  getAppAuthList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestParam(value = "userId",required = false) String userId){
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getAppAuthList(enterpriseId,userId,user,true));
    }

    @GetMapping(path = "/getAppAuthList/v2")
    public ResponseResult  getAppAuthListV2(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestParam(value = "userId",required = false) String userId){
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getAppAuthList(enterpriseId,userId,user,false));
    }

    @GetMapping(path = "/getEnterpriseMenus/allMenu")
    public ResponseResult  getEnterpriseMenusAll(@PathVariable(value = "enterprise-id", required = false) String enterpriseId){
        return ResponseResult.success(sysMenuService.getEnterpriseAllMenus(enterpriseId));
    }
}
