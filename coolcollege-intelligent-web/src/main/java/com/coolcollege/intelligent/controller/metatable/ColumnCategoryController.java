package com.coolcollege.intelligent.controller.metatable;

import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.metatable.request.ColumnCategoryRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaColumnCategoryVO;
import com.coolcollege.intelligent.service.metatable.ColumnCategoryService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
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
 * @Date 2022/4/2 10:16
 * @Version 1.0
 */
@Api(tags = "检查项分类")
@Slf4j
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/metaColumnCategory"})
public class ColumnCategoryController {

    @Autowired
    ColumnCategoryService columnCategoryService;


    @ApiOperation("获取检查项分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "项分类名称", dataType = "String")
    })
    @GetMapping("/getMetaColumnCategoryList")
    public ResponseResult<List<TbMetaColumnCategoryVO>> getMetaColumnCategoryList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestParam(value="categoryName",required = false) String categoryName) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(columnCategoryService.getMetaColumnCategoryList(enterpriseId, categoryName));
    }


    @ApiOperation("新增检查项分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "检查项分类请求BODY", dataType = "ColumnCategoryRequest")
    })
    @PostMapping("/addMetaColumnCategory")
    @SysLog(func = "新增分类", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.INSERT_GROUP)
    public ResponseResult<Long> addMetaColumnCategory(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestBody ColumnCategoryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(columnCategoryService.addMetaColumnCategory(enterpriseId, request));
    }

    @ApiOperation("编辑检查项分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "检查项分类请求BODY", dataType = "ColumnCategoryRequest")
    })
    @PostMapping("/updateMetaColumnCategory")
    @SysLog(func = "编辑分类", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.UPDATE_GROUP)
    public ResponseResult<Boolean> updateMetaColumnCategory(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestBody ColumnCategoryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(columnCategoryService.updateMetaColumnCategory(enterpriseId, request));
    }

    @ApiOperation("删除检查项分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "检查项分类请求BODY", dataType = "ColumnCategoryRequest")
    })
    @PostMapping("/deletedMetaColumnCategory")
    @SysLog(func = "删除分类", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.DELETE_GROUP)
    public ResponseResult<Boolean> deletedMetaColumnCategory(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestBody ColumnCategoryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(columnCategoryService.deletedMetaColumnCategory(enterpriseId, request.getId()));
    }

    @ApiOperation("检查项分类排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestList", value = "检查项分类请求BODY", dataType = "List<ColumnCategoryRequest>")
    })
    @PostMapping("/metaColumnCategorySort")
    public ResponseResult<Boolean> metaColumnCategorySort(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestBody ColumnCategoryRequest param) {
        DataSourceHelper.changeToMy();
        //新增service
        return ResponseResult.success(columnCategoryService.metaColumnCategorySort(enterpriseId, param.getIds()));
    }
}
