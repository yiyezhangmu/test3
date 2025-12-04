package com.coolcollege.intelligent.controller.enterprise;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.request.AppMenuCustomizeRequest;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserAppMenuService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@RestController
@RequestMapping("/v3/enterprise/{enterprise-id}/user")
@BaseResponse
public class EnterpriseUserAppMenuController {

    @Autowired
    private EnterpriseUserAppMenuService enterpriseUserAppMenuService;
    @GetMapping("/menu/app")
    public ResponseResult userMenuApp(@PathVariable("enterprise-id")String eid){
        DataSourceHelper.changeToMy();
       return ResponseResult.success(enterpriseUserAppMenuService.getUserAppMenu(eid));
    }
    @PostMapping("/menu/app/update")
    public ResponseResult updateUserMenuApp(@PathVariable("enterprise-id")String eid,
                                            @RequestBody AppMenuCustomizeRequest reqest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserAppMenuService.updateUserAppMenu(eid,reqest, Constants.INDEX_TWO));
    }

    /**
     * 老的二级采单
     * @param eid
     * @return
     */
    @GetMapping("/defined/menu/app")
    public ResponseResult getUserDefinedMenuApp(@PathVariable("enterprise-id")String eid){
        CurrentUser user = UserHolder.getUser();
        SysRoleDO sysRoleDO = user.getSysRoleDO();
        if(sysRoleDO==null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户没有分配职位！");
        }
        return ResponseResult.success(enterpriseUserAppMenuService.getUserDefinedMenuAppNew(eid, user,true,Constants.INDEX_TWO));
    }
    /**
     * 新的二级采单
     * @param eid
     * @return
     */
    @GetMapping("/defined/menu/app/v2")
    public ResponseResult getUserDefinedMenuAppV2(@PathVariable("enterprise-id")String eid){
        CurrentUser user = UserHolder.getUser();
        SysRoleDO sysRoleDO = user.getSysRoleDO();
        if(sysRoleDO==null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户没有分配职位！");
        }
        return ResponseResult.success(enterpriseUserAppMenuService.getUserDefinedMenuAppNew(eid, user,false,Constants.INDEX_TWO));
    }

    /**
     * 一级采单查看
     * @param eid
     * @return
     */
    @GetMapping("/defined/firstLevelMenu/app/v2")
    public ResponseResult getUserDefinedFirstMenuAppV2(@PathVariable("enterprise-id")String eid){
        CurrentUser user = UserHolder.getUser();
        SysRoleDO sysRoleDO = user.getSysRoleDO();
        if(sysRoleDO==null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户没有分配职位！");
        }
        return ResponseResult.success(enterpriseUserAppMenuService.getUserDefinedMenuApp(eid, user,false,Constants.INDEX_ONE));
    }

    /**
     * 一级采单 排序操作
     * @param eid
     * @param reqest
     * @return
     */
    @PostMapping("/menu/app/moveSortAppFirstMenu")
    public ResponseResult moveSortAppFirstMenu(@PathVariable("enterprise-id")String eid,
                                            @RequestBody AppMenuCustomizeRequest reqest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserAppMenuService.updateUserAppMenu(eid,reqest, Constants.INDEX_ONE));
    }
}
