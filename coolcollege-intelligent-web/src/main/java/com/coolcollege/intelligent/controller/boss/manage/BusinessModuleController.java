package com.coolcollege.intelligent.controller.boss.manage;

/**
 * 业务模块管理
 * @author ：xugangkun
 * @date ：2022/3/23 11:33
 */

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.boss.StandardStateEnum;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageModuleMappingDao;
import com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO;
import com.coolcollege.intelligent.model.bosspackage.dto.BusinessModuleDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.bosspackage.BusinessModuleService;
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

@Api(tags = "业务模块管理")
@RestController
@RequestMapping({"/boss/manage/businessModule"})
public class BusinessModuleController {

    @Autowired
    private BusinessModuleService businessModuleService;

    @Autowired
    private EnterprisePackageModuleMappingDao enterprisePackageModuleMappingDao;

    @ApiOperation("业务模块管理-查询列表（分页）")
    @GetMapping("/list")
    public ResponseResult<PageVO<BusinessModuleVO>> list(PageRequest pageRequest) {
        DataSourceHelper.reset();
        List<BusinessModuleVO> result = businessModuleService.getBusinessModuleList(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo(result)));
    }

    @ApiOperation("业务模块管理-业务模块管理详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "业务模块id", required = true)
    })
    @GetMapping("/detail")
    public ResponseResult<BusinessModuleDetailVO> detail(@RequestParam(value = "moduleId", required = true) Long moduleId) {
        DataSourceHelper.reset();
        return ResponseResult.success(businessModuleService.getBusinessModuleDetail(moduleId));
    }

    @ApiOperation("业务模块管理-获得有效的业务模块列表")
    @GetMapping("/validList")
    public ResponseResult<List<BusinessModuleVO>> validList() {
        DataSourceHelper.reset();
        List<BusinessModuleVO> result = businessModuleService.getValidModuleList();
        return ResponseResult.success(result);
    }

    @ApiOperation("业务模块管理-添加业务模块")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody BusinessModuleDTO businessModuleDTO) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        List<BusinessModuleDO> check = businessModuleService.selectByModuleName(businessModuleDTO.getModuleName());
        if (CollectionUtils.isNotEmpty(check)) {
            return ResponseResult.fail(ErrorCodeEnum.MODULE_NAME_IN_USE);
        }
        businessModuleService.addBusinessModule(businessModuleDTO, user);
        return ResponseResult.success();
    }

    @ApiOperation("业务模块管理-修改业务模块")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody BusinessModuleDTO businessModuleDTO) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        List<BusinessModuleDO> check = businessModuleService.selectByModuleName(businessModuleDTO.getModuleName());

        Boolean isRepeat = check.size() > Constants.INDEX_ONE ||
                (check.size() == Constants.INDEX_ONE && !check.get(0).getId().equals(businessModuleDTO.getModuleId()));
        if (isRepeat) {
            return ResponseResult.fail(ErrorCodeEnum.MODULE_NAME_IN_USE);
        }
        businessModuleService.updateBusinessModule(businessModuleDTO, user);
        return ResponseResult.success();
    }

    @ApiOperation("业务模块管理-重新使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块id", required = true)
    })
    @PostMapping("/revive")
    public ResponseResult revive(@RequestParam(value = "moduleId", required = true) Long moduleId) {
        DataSourceHelper.reset();
        businessModuleService.updateModuleStatus(StandardStateEnum.NORMAL.getCode(), moduleId);
        return ResponseResult.success();
    }

    @ApiOperation("业务模块管理-禁用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块id", required = true)
    })

    @PostMapping("/disable")
    public ResponseResult disable(@RequestParam(value = "moduleId", required = true) Long moduleId) {
        DataSourceHelper.reset();
        businessModuleService.updateModuleStatus(StandardStateEnum.FREEZE.getCode(), moduleId);
        return ResponseResult.success();
    }

    @ApiOperation("业务模块管理-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块id", required = true)
    })
    @DeleteMapping("/delete")
    public ResponseResult delete(@RequestParam(value = "moduleId", required = true) Long moduleId) {
        DataSourceHelper.reset();
        //校验业务模块是否使用中
        List<Long> packageIds = enterprisePackageModuleMappingDao.selectPackageIdsByModuleId(moduleId);
        if (CollectionUtils.isNotEmpty(packageIds)) {
            return ResponseResult.fail(ErrorCodeEnum.MODULE_IN_USE);
        }
        businessModuleService.deleteByPrimaryKey(moduleId);
        return ResponseResult.success();
    }

}
