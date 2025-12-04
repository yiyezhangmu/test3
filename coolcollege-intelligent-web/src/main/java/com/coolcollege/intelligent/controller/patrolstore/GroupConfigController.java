package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.request.AddGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.DeleteGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.UpdateGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.GroupConfigDetailVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.GroupConfigService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:27
 * @Description:
 */
@Api(tags = "群分享配置")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/group/config")
public class GroupConfigController {

    @Resource
    private GroupConfigService groupConfigService;

    @ApiOperation("新增配置")
    @PostMapping("/add")
    public ResponseResult<Long> addGroupConfig(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @Validated @RequestBody AddGroupConfigRequest param,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(groupConfigService.addGroupConfig(enterpriseId, currentUser.getUserId(), param));
    }

    @ApiOperation("编辑配置")
    @PostMapping("/update")
    public ResponseResult<Boolean> updateGroupConfig(@PathVariable(value = "enterprise-id") String enterpriseId,
                                               @Validated @RequestBody UpdateGroupConfigRequest param,
                                               BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(groupConfigService.updateGroupConfig(enterpriseId, currentUser.getUserId(), param));
    }

    @ApiOperation("删除配置")
    @PostMapping("/delete")
    public ResponseResult<Boolean> deleteGroupConfig(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                  @Validated @RequestBody DeleteGroupConfigRequest param,
                                                  BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(groupConfigService.deleteGroupConfig(enterpriseId, currentUser.getUserId(), param));
    }

    @ApiOperation("查询配置详情")
    @GetMapping("/detail")
    public ResponseResult<GroupConfigDetailVO> getGroupConfigDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                              @RequestParam("groupId") Long groupId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(groupConfigService.getGroupConfigDetail(enterpriseId, groupId));
    }

    @ApiOperation("配置列表")
    @PostMapping("/page")
    public ResponseResult<PageInfo<GroupConfigDetailVO>> getGroupConfigPage(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                            @RequestBody PageBaseRequest param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(groupConfigService.getGroupConfigPage(enterpriseId, param));
    }
}
