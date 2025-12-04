package com.coolcollege.intelligent.controller.authorityregion;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.authorityregion.request.AddAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.request.AuthorityRegionPageRequest;
import com.coolcollege.intelligent.model.authorityregion.request.DeleteAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.request.UpdateAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.vo.AuthorityRegionVO;
import com.coolcollege.intelligent.model.authorityregion.vo.MyAuthorityRegionVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authorityregion.AuthorityRegionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:22
 * @Description: 授权区域配置
 */
@Api(tags = "授权区域配置")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/authority/region")
public class AuthorityRegionController {
    @Resource
    private AuthorityRegionService authorityRegionService;

    @ApiOperation("新增授权区域")
    @PostMapping("/add")
    public ResponseResult<Long> addAuthorityRegion(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @Validated @RequestBody AddAuthorityRegionRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(authorityRegionService.addAuthorityRegion(enterpriseId, param, currentUser));
    }

    @ApiOperation("更新授权区域")
    @PostMapping("/update")
    public ResponseResult<Long> updateAuthorityRegion(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                   @Validated @RequestBody UpdateAuthorityRegionRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(authorityRegionService.updateAuthorityRegion(enterpriseId, param, currentUser));
    }

    @ApiOperation("删除授权区域")
    @PostMapping("/delete")
    public ResponseResult<Integer> deleteAuthorityRegion(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                    @Validated @RequestBody DeleteAuthorityRegionRequest param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(authorityRegionService.deleteAuthorityRegion(enterpriseId, param.getAuthorityRegionId()));
    }
    @ApiOperation("授权区域详情")
    @PostMapping("/detail")
    public ResponseResult<AuthorityRegionVO> detailAuthorityRegion(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("authorityRegionId") Long authorityRegionId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(authorityRegionService.detailAuthorityRegion(enterpriseId, authorityRegionId));
    }

    @ApiOperation("授权区域分页列表")
    @PostMapping("/page")
    public ResponseResult<PageInfo<AuthorityRegionVO>> getAuthorityRegionPage(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody AuthorityRegionPageRequest param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(authorityRegionService.getAuthorityRegionPage(enterpriseId, param));
    }

    @ApiOperation("获取我的授权区域")
    @PostMapping("/getMyAuthorityRegion")
    public ResponseResult<MyAuthorityRegionVO> getMyAuthorityRegion(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(authorityRegionService.getMyAuthorityRegion(enterpriseId, currentUser.getUserId()));
    }
}
