package com.coolcollege.intelligent.controller.brand;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.group.InsertGroup;
import com.coolcollege.intelligent.common.group.UpdateGroup;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandQueryRequest;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandUpdateRequest;
import com.coolcollege.intelligent.model.brand.vo.EnterpriseBrandVO;
import com.coolcollege.intelligent.service.brand.EnterpriseBrandService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 品牌 前端控制器
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
@Api(tags = "品牌")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/brand")
@ErrorHelper
@RequiredArgsConstructor
public class EnterpriseBrandController {
    private final EnterpriseBrandService brandService;

    @ApiOperation("新增品牌")
    @PostMapping("/add")
    public ResponseResult<Long> insert(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestBody @Validated(InsertGroup.class) EnterpriseBrandUpdateRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.insert(enterpriseId, request));
    }

    @ApiOperation("修改品牌")
    @PostMapping("/update")
    public ResponseResult<Long> update(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestBody @Validated(UpdateGroup.class) EnterpriseBrandUpdateRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.update(enterpriseId, request));
    }

    @ApiOperation("批量删除品牌")
    @PostMapping("/removeBatch")
    public ResponseResult<Boolean> removeBatch(@PathVariable("enterprise-id") String enterpriseId,
                                               @RequestBody List<Long> ids) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.removeBatch(enterpriseId, ids));
    }

    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public ResponseResult<EnterpriseBrandVO> getById(@PathVariable("enterprise-id") String enterpriseId,
                                                     @PathVariable("id") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.getVOById(enterpriseId, id));
    }

    @ApiOperation("列表查询")
    @GetMapping("/list")
    public ResponseResult<List<EnterpriseBrandVO>> getList(@PathVariable("enterprise-id") String enterpriseId,
                                                           EnterpriseBrandQueryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.getVOList(enterpriseId, request));
    }

    @ApiOperation("分页查询")
    @GetMapping("/getPage")
    public ResponseResult<PageInfo<EnterpriseBrandVO>> getPage(@PathVariable("enterprise-id") String enterpriseId,
                                                               EnterpriseBrandQueryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(brandService.getVOPage(enterpriseId, request));
    }
}
