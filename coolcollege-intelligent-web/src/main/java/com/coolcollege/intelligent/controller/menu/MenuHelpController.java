package com.coolcollege.intelligent.controller.menu;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * describe: 帮助文档获取到的url
 *
 * @author zhouyiping
 * @date 2020/09/18
 */
@RestController
@RequestMapping("/v2")
@BaseResponse
@Slf4j
public class MenuHelpController {

    @Autowired
    private SysMenuService sysMenuService;
    /**
     * 获取用户的模块权限
     * @return
     */
    @GetMapping(path = "/getMenus")
    public Object  getMenus(){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.getAllMenus());
    }
}
