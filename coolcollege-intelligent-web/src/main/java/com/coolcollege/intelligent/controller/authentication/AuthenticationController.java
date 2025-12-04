package com.coolcollege.intelligent.controller.authentication;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.OperationResultCodeEnum;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.authentication.AuthenticationDO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authentication.AuthenticationService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 角色基本操作
 *
 * @author Aaron
 * @ClassName AuthenticationController
 * @Description 角色基本操作
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/{enterprise-id}/roles")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * 角色查询
     *
     * @param map
     * @return map
     * @throws Exception
     * @Description 角色查询
     */
    @PostMapping("/get")
    public Map<String, Object> queryRoles(@PathVariable("enterprise-id") String enterpriseId,
        @RequestBody(required = false) Map<String, Object> map) {

        if (UserHolder.getUser().getIsAdmin()) {
            map.put("isAdmin", "1");
        }
        PageHelper.startPage(Integer.parseInt(map.get("page_num").toString()),
            Integer.parseInt(map.get("page_size").toString()));
        List<AuthenticationDO> list = authenticationService.queryRoles(enterpriseId, map);
        if (CollectionUtils.isEmpty(list)) {
            return PageHelperUtil.getPageInfo(new PageInfo());
        }
        return PageHelperUtil.getPageInfo(new PageInfo(list));
    }

    /**
     * 员工查询
     *
     * @param map
     * @return map
     * @throws Exception
     * @Description 员工查询
     */
    @PostMapping("/get_employee")
    public Map<String, Object> queryEmployee(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestBody(required = false) Map<String, Object> map) {

        DataSourceHelper.changeToMy();
        if (UserHolder.getUser().getIsAdmin()) {
            map.put("isAdmin", "1");
        } else {
            map.put("isAdmin", "0");
        }
        map.put("user_id", UserHolder.getUser().getUserId());
        PageHelper.startPage(Integer.parseInt(map.get("page_num").toString()),
            Integer.parseInt(map.get("page_size").toString()));
        List<AuthenticationDO> list = authenticationService.queryEmployee(enterpriseId, map);
        if (CollectionUtils.isEmpty(list)) {
            return PageHelperUtil.getPageInfo(new PageInfo());
        }
        return PageHelperUtil.getPageInfo(new PageInfo(list));
    }

    /**
     * 角色更新
     *
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     * @Description 角色更新
     */
    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新角色")
    public boolean updateRoles(@PathVariable("enterprise-id") String enterpriseId,
                               @RequestBody(required = false) Map<String, Object> map) {

        DataSourceHelper.changeToMy();
        int i = authenticationService.updateRoles(enterpriseId, map);
        if (i > 0) {
            return OperationResultCodeEnum.SUCCESS.getFlag();
        }
        return OperationResultCodeEnum.FAIL.getFlag();
    }

    /**
     * 角色批量更新
     *
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     * @Description 角色批量更新
     */
    @PostMapping("/batch_update")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量更新角色")
    public boolean batchUpdateRoles(@PathVariable("enterprise-id") String enterpriseId,
        @RequestBody(required = false) Map<String, Object> map) {
        DataSourceHelper.changeToMy();
        int i = authenticationService.batchUpdateRoles(enterpriseId, map);
        if (i > 0) {
            return OperationResultCodeEnum.SUCCESS.getFlag();
        }
        return OperationResultCodeEnum.FAIL.getFlag();
    }

    /**
     * 角色批量更新非管理员
     *
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     * @Description 角色批量更新非管理员
     */
    @PostMapping("/update_user")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "角色批量更新非管理员")
    public boolean batchUpdateUser(@PathVariable("enterprise-id") String enterpriseId,
        @RequestBody(required = false) Map<String, Object> map) {
        DataSourceHelper.changeToMy();
        int i = authenticationService.batchUpdateUser(enterpriseId, map);
        if (i > 0) {
            return OperationResultCodeEnum.SUCCESS.getFlag();
        }
        return OperationResultCodeEnum.FAIL.getFlag();
    }

}
