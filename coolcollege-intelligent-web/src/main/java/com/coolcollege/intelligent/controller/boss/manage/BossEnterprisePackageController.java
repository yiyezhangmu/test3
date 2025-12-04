package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.boss.StandardStateEnum;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.dto.EnterprisePackageDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.CurrentPackageDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.bosspackage.EnterprisePackageService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业套餐管理
 * @author ：xugangkun
 * @date ：2022/3/24 11:06
 */
@Api(tags = "企业套餐管理")
@RestController
@RequestMapping({"/boss/manage/enterprisePackage"})
public class BossEnterprisePackageController {

    @Autowired
    private EnterprisePackageService enterprisePackageService;

    @Autowired
    private EnterpriseConfigDao enterpriseConfigDao;

    @ApiOperation("企业套餐管理-查询列表（分页）")
    @GetMapping("/list")
    public ResponseResult<PageVO<EnterprisePackageVO>> list(PageRequest pageRequest) {
        DataSourceHelper.reset();
        List<EnterprisePackageVO> result = enterprisePackageService.selectAllEnterprisePackage(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo(result)));
    }

    @ApiOperation("企业套餐管理-获得有效的企业套餐列表")
    @GetMapping("/validList")
    public ResponseResult<List<EnterprisePackageVO>> validList() {
        DataSourceHelper.reset();
        List<EnterprisePackageVO> result = enterprisePackageService.getPackageList(StandardStateEnum.NORMAL.getCode());
        return ResponseResult.success(result);
    }


    @ApiOperation("企业套餐管理-添加企业套餐")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody EnterprisePackageDTO enterprisePackageDTO) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        List<EnterprisePackageDO> check = enterprisePackageService.selectByPackageName(enterprisePackageDTO.getPackageName());
        if (CollectionUtils.isNotEmpty(check)) {
            return ResponseResult.fail(ErrorCodeEnum.PACKAGES_NAME_IN_USE);
        }
        enterprisePackageService.addEnterprisePackage(enterprisePackageDTO, user);
        return ResponseResult.success();
    }


    @ApiOperation("企业套餐管理-修改企业套餐")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody EnterprisePackageDTO enterprisePackageDTO) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        List<EnterprisePackageDO> check = enterprisePackageService.selectByPackageName(enterprisePackageDTO.getPackageName());
        Boolean isRepeat = check.size() > Constants.INDEX_ONE ||
                (check.size() == Constants.INDEX_ONE && !check.get(0).getId().equals(enterprisePackageDTO.getPackageId()));
        if (isRepeat) {
            return ResponseResult.fail(ErrorCodeEnum.PACKAGES_NAME_IN_USE);
        }
        enterprisePackageService.updateEnterprisePackage(enterprisePackageDTO, user);
        return ResponseResult.success();
    }

    @ApiOperation("企业套餐管理-套餐详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "packageId", value = "套餐id", required = true)
    })
    @GetMapping("/detail")
    public ResponseResult<CurrentPackageDetailVO> detail(@RequestParam(value = "packageId", required = true) Long packageId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterprisePackageService.getEnterprisePackageDetail(packageId));
    }

    @ApiOperation("企业套餐管理-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "packageId", value = "套餐id", required = true)
    })
    @DeleteMapping("/delete")
    public ResponseResult delete(@RequestParam(value = "packageId", required = true) Long packageId) {
        DataSourceHelper.reset();
        //删除前添加校验，如果已有企业使用该套餐，不允许删除
        List<EnterpriseConfigDO> configs = enterpriseConfigDao.selectByCurrentPackage(packageId);
        if (CollectionUtils.isNotEmpty(configs)) {
            return ResponseResult.fail(ErrorCodeEnum.PACKAGES_IN_USE);
        }
        enterprisePackageService.deleteByPrimaryKey(packageId);
        return ResponseResult.success();
    }

}
