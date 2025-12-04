package com.coolcollege.intelligent.controller.menu;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.MenuTypeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.base.ClassIdBase;
import com.coolcollege.intelligent.model.menu.request.*;
import com.coolcollege.intelligent.service.system.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * describe: boss系统获取的url
 *
 * @author zhouyiping
 * @date 2020/09/22
 */
@RestController
@RequestMapping("/boss/menu")
@BaseResponse
@Slf4j
public class MenuBossController {

    @Autowired
    private SysMenuService sysMenuService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    /**
     * 菜单添加
     * @param request
     * @return
     */
    @PostMapping(path = "/add")
    public ResponseResult addMenu(@RequestBody MenuAddRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.addMenuOrAuth(request.getParentId(),request.getName(),request.getPath(),null,
                request.getTarget(),request.getComponent(),request.getIcon(), MenuTypeEnum.MENU, request.getEnv(), request.getLabel()));
    }

    /**
     *菜单修改
     * @param request
     * @return
     */
    @PostMapping(path = "/modify")
    public ResponseResult modifyMenu(@RequestBody MenuModifyRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.modifyMenuOrAuth(request.getId(),request.getName(),request.getPath(),null,
                request.getTarget(),request.getComponent(),request.getIcon(), request.getEnv(),request.getCommonFunctionsIcon(), request.getLabel()));
    }

    /**
     * 菜单删除
     * @param request
     * @return
     */
    @PostMapping(path = "/delete")
    public ResponseResult deleteMenu(@RequestBody ClassIdBase request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.deleteMenuOrAuthById(request.getId()));
    }

    /**
     * 菜单权限添加
     * @param request
     * @return
     */
    @PostMapping(path = "/auth/add")
    public ResponseResult addMenuAuth(@RequestBody MenuAuthAddRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.addMenuOrAuth(request.getParentId(),request.getName(),null,request.getType() ,
                null,null,null, MenuTypeEnum.AUTH, request.getEnv(), null));
    }

    /**
     * 菜单权限修改
     * @param request
     * @return
     */
    @PostMapping(path = "/auth/modify")
    public ResponseResult modifyMenuAuth(@RequestBody MenuAuthModifyRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.modifyMenuOrAuth(request.getId(),request.getName(),null,request.getType(),
                null,null,null, request.getEnv(),null, null));
    }

    /**
     * 菜单删除
     * @param request
     * @return
     */
    @PostMapping(path = "/auth/delete")
    public ResponseResult deleteMenuAuth(@RequestBody ClassIdBase request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.deleteMenuOrAuthById(request.getId()));
    }

    @GetMapping(path = "/list")
    public ResponseResult menuList(){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.getAllMenus());
    }

    /**
     * 菜单下权限列表
     * @param id
     * @return
     */
    @GetMapping(path = "/auth/list")
    public ResponseResult menuList(@RequestParam("id")Long id){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.getMenuAuthList(id, PlatFormTypeEnum.PC.getCode()));
    }

    @PostMapping(path = "/move")
    public ResponseResult menuList(@RequestBody MenuMoveRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.moveMenu(request.getId(),request.getParentId()));
    }
    @PostMapping(path = "/sort")
    public ResponseResult sortList(@RequestBody MenuSortRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.sortMenu(request.getIdList()));
    }

    @PostMapping(path = "/app/add")
    public ResponseResult addAppMenu(@RequestBody AppMenuAddRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.addAppMenu(request));
    }
    @PostMapping(path = "/app/modify")
    public ResponseResult modifyAppMenu(@RequestBody AppMenuModifyRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.modifyAppMenu(request));
    }
    @PostMapping(path = "/app/delete")
    public ResponseResult delteAppMenu(@RequestBody AppMenuDeleteRequest request){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.deleteAppMenu(request));
    }
    @GetMapping(path = "/app/list")
    public ResponseResult listAppMenu(){

        DataSourceHelper.reset();
        return ResponseResult.success( sysMenuService.ListAppMenu());
    }

    /**
     * 菜单下权限列表(app)
     * @param id
     * @return
     */
    @GetMapping(path = "/app/auth/list")
    public ResponseResult menuAppAuthList(@RequestParam("id")Long id){

        DataSourceHelper.reset();
        return ResponseResult.success(  sysMenuService.getMenuAuthList(id,PlatFormTypeEnum.NEW_APP.getCode()));
    }




}
