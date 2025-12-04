package com.coolcollege.intelligent.controller.department;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.model.department.dto.DepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.DeptSearchRespDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolstore.base.enums.AppTypeEnum;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 部门
 * Created by ydw on 2020/6/16.
 */
@Api(tags = "部门相关接口类")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/departments", "/v3/enterprises/{enterprise-id}/departments"})
@BaseResponse
@Slf4j
public class SysDepartmentController {

    @Autowired
    public SysDepartmentService sysDepartmentService;
    @Autowired
    private SyncFacade syncFacade;

    /**
     * 获取子节点列表
     * @param eid
     * @param pid
     * @return
     */
    @GetMapping("childList")
    public Object getChildList(@PathVariable(value = "enterprise-id") String eid,
                               String pid,
                               @RequestParam(value = "hasUser", required = false, defaultValue = "false") boolean hasUser,
                               @RequestParam(value = "hasUserNum", required = false, defaultValue = "false") boolean hasUserNum,
                               @RequestParam(value = "hasAuth", required = false, defaultValue = "false") boolean hasAuth) {
        return sysDepartmentService.getDeptChild(eid, pid, hasUser, hasUserNum, hasAuth, UserHolder.getUser().getUserId());
    }

    /**
     * 部门/人员树
     *
     * @param eid
     * @param userType 用户类型：active/激活,unactive/未激活
     * @return
     */
    @GetMapping(path = "/dept_user_tree")
    public Object getDepUserTree(@PathVariable(value = "enterprise-id") String eid,
                             @RequestParam(name = "user_type", required = false, defaultValue = "active") String userType) {

        return sysDepartmentService.getDeptUserTree(eid, userType);
    }

    /**
     * 查询部门集合
     *
     * @param eid
     * @param departmentName
     * @return
     */
    @GetMapping(path = "")
    public Object getDepListByDepName(@PathVariable(value = "enterprise-id", required = true) String eid,
                                      @RequestParam(name = "department_name", required = false, defaultValue = "") String departmentName) {

        DataSourceHelper.changeToMy();
        return new DeptSearchRespDTO(sysDepartmentService.getDepListByDepName(eid, departmentName));
    }

    /**
     * 查询部门下人员列表
     *
     * @param eid
     * @param departmentId
     * @param userName
     * @return
     */
    @GetMapping(path = "/{department-id}/users")
    public Object getDepUsers(@PathVariable(value = "enterprise-id", required = true) String eid,
                              @PathVariable(value = "department-id", required = true) String departmentId,
                              @RequestParam(value = "page_num", required = false) Integer pageNum,
                              @RequestParam(value = "page_size", required = false) Integer pageSize,
                              @RequestParam(name = "user_name", required = false, defaultValue = "") String userName,
                              @RequestParam(value = "type",defaultValue = "true")Boolean type) {

        DepartmentQueryDTO departmentQueryDTO = new DepartmentQueryDTO();
        departmentQueryDTO.setKeyword(userName);
        departmentQueryDTO.setDeptId(departmentId);
        departmentQueryDTO.setPage_num(pageNum);
        departmentQueryDTO.setPage_size(pageSize);
        DataSourceHelper.changeToMy();
        return sysDepartmentService.getDepUsersByPage(eid, departmentQueryDTO,type);
    }

    /**
     * 同步钉钉组织架构
     * @param enterpeiseId
     * @return
     */
    @GetMapping("/syncInformation")
    public Object   syncInformation(@PathVariable(value = "enterprise-id",required = true)String enterpeiseId,
                                  @RequestParam(value = "corpId",required = false)String corpId ){

        if (StringUtils.isEmpty(corpId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "请传递corpId");
        }
        try {
            log.info("开始同步组织架构信息，corpId为{}", corpId);
            syncFacade.start(corpId, AppTypeEnum.DING_DING.getValue(), true);
            log.info("组织架构信息同步结束");
        } catch (Exception e) {
            log.error("同步钉钉组织失败{}", e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "同步钉钉组织失败");
        }
        return Boolean.TRUE;
    }
}
