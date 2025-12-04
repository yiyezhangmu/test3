package com.coolcollege.intelligent.controller.homePageTemplate;

import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.CommonFunctionsDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateRoleMappingDTO;
import com.coolcollege.intelligent.model.homeTemplate.VO.HomeTemplateVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 11:11
 * @Version 1.0
 */
@Slf4j
@Api(tags = "首页模板")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/homepage")
public class HomePageTemplateController {

    @Autowired
    HomeTemplateService homeTemplateService;

    @Autowired
    EnterpriseConfigMapper configMapper;

    @ApiOperation("模板发布")
    @PostMapping(path = "/publishHomeTemplate")
    public ResponseResult<Boolean> addHelpDesc(@PathVariable("enterprise-id") String enterpriseId,
                                      @RequestBody HomeTemplateDTO homeTemplateDTO) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.publishHomeTemplate(enterpriseId,homeTemplateDTO,user));
    }

    @ApiOperation("根据KEY获取首页模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key可能为key，可能为ID", dataType = "String", required = true),
            @ApiImplicitParam(name = "checkType", value = "查询类型 未发布传key 发布之后传id", dataType = "String", required = true),
    })
    @GetMapping(path = "/getHomeTemplateByKey")
    public ResponseResult<List<HomeTemplateVO>> getHomeTemplateByKey(@PathVariable("enterprise-id") String enterpriseId,
                                                                     @RequestParam(value = "key",required = true) String key,
                                                                     @RequestParam(value = "checkType") String checkType) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        com.coolcollege.intelligent.util.datasource.DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(homeTemplateService.selectByKey(enterpriseId,key,checkType, user));
    }

    @ApiOperation("获取模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "条数", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "String", required = false),
            @ApiImplicitParam(name = "templateName", value = "模板名称", dataType = "String", required = false),
            @ApiImplicitParam(name = "roleId", value = "角色ID", dataType = "String", required = false),
    })
    @GetMapping(path = "/getAllHomeTemplate")
    public ResponseResult<PageInfo<HomeTemplateVO>> getAllHomeTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                                                       @RequestParam(value = "pageSize", defaultValue = "10",required = false) Integer pageSize,
                                                                       @RequestParam(value = "pageNum", defaultValue = "1",required = false) Integer pageNum,
                                                                       @RequestParam(value = "templateName",required = false) String templateName,
                                                                       @RequestParam(value = "roleId",required = false) Long roleId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.listHomeTemplateVO(enterpriseId,pageSize,pageNum,roleId,templateName));
    }

    @ApiOperation("根据ID删除首页模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模板ID", dataType = "Integer", required = true),
    })
    @GetMapping(path = "/deletedHomeTemplateById")
    public ResponseResult<Boolean> deletedHomeTemplateById(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestParam(value = "id",required = true) Integer id) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.deletedById(enterpriseId,id,user));
    }


    @ApiOperation("模板预览")
    @PostMapping(path = "/previewHomeTemplate")
    public ResponseResult<String> previewHomeTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                      @RequestBody HomeTemplateDTO homeTemplateDTO) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.previewHomeTemplate(enterpriseId,homeTemplateDTO,user));
    }

    @ApiOperation("根据ID查询首页模板详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模板ID", dataType = "Integer", required = true),
    })
    @GetMapping(path = "/getHomeTemplateById")
    public ResponseResult<HomeTemplateVO> getHomeTemplateById(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestParam(value = "id") Integer id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.selectById(enterpriseId,id));
    }

    @ApiOperation("模板立即使用")
    @PostMapping(path = "/useImmediately")
    @SysLog(func = "编辑", subFunc = "首页模板", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.EDIT_HOME_TEMPLATE)
    public ResponseResult<Boolean> useImmediately(@PathVariable("enterprise-id") String enterpriseId,
                               @RequestBody HomeTemplateRoleMappingDTO homeTemplateRoleMappingDTO){
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.useImmediately(enterpriseId,homeTemplateRoleMappingDTO,user));
    }


    @ApiOperation("查询当前用户模板(用户最高优先级角色)")
    @GetMapping(path = "/getCurrentUserHomeTemplate")
    public ResponseResult<HomeTemplateVO> getCurrentUserHomeTemplate(@PathVariable("enterprise-id") String enterpriseId) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.getCurrentUserHomeTemplate(enterpriseId,user));
    }


    @ApiOperation("保存当前用户常用功能")
    @PostMapping(path = "/saveCurrentUserCommonFunctions")
    public ResponseResult<Boolean> saveCurrentUserCommonFunctions(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestBody CommonFunctionsDTO commonFunctionsDTO){
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.saveCurrentUserCommonFunctions(enterpriseId,commonFunctionsDTO,user));
    }

    @ApiOperation("获取当前用户常用功能")
    @GetMapping(path = "/getCurrentUserCommonFunctions")
    public ResponseResult<CommonFunctionsDTO> getCurrentUserCommonFunctions(@PathVariable("enterprise-id") String enterpriseId){
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(homeTemplateService.getCurrentUserCommonFunctions(enterpriseId,user));
    }



}
