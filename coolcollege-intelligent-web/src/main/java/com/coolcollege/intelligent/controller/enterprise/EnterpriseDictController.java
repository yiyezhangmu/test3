package com.coolcollege.intelligent.controller.enterprise;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDictDO;
import com.coolcollege.intelligent.model.enums.BusinessTypeEnum;
import com.coolcollege.intelligent.model.newstore.request.NsStoreTypeAddOrUpdateRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreTypeVO;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusDTO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseDictService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 用户人事状态
 *
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2022-03-02 10:31:57
 */
@Api(tags = "通用字典表")
@ErrorHelper
@RestController
@Slf4j
@RequestMapping("/v3/enterprises/{enterprise-id}/enterpriseDict")
public class EnterpriseDictController {

    @Autowired
    private EnterpriseDictService enterpriseDictService;

    /**
     * 列表
     */
    @GetMapping("/userStatus/list")
    public ResponseResult<List<UserPersonnelStatusVO>> list(@PathVariable(value = "enterprise-id") String eid) {
        DataSourceHelper.changeToMy();
        List<UserPersonnelStatusVO> userPersonnelStatusList = enterpriseDictService.selectAllByType(eid, BusinessTypeEnum.USER_PERSONNEL_STATUS.getCode());
        return ResponseResult.success(userPersonnelStatusList);
    }


    /**
     * 添加人事状态
     *
     * @param eid
     * @param userPersonnelStatusDTO
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @author: xugangkun
     * @date: 2022/3/2 14:57
     */
    @PostMapping("/userStatus/add")
    public ResponseResult userStatusAdd(@PathVariable(value = "enterprise-id") String eid,
                                        @RequestBody UserPersonnelStatusDTO userPersonnelStatusDTO) {
        DataSourceHelper.changeToMy();
        Boolean isAddSuccess = dictAdd(eid, BusinessTypeEnum.USER_PERSONNEL_STATUS.getCode(), userPersonnelStatusDTO.getStatusName(), null);
        if (!isAddSuccess) {
            return ResponseResult.fail(ErrorCodeEnum.USER_PERSONNEL_STATUS_EXIST);
        }
        return ResponseResult.success();
    }

    private Boolean dictAdd(String eid, String businessType, String businessValue, String remakes) {
        CurrentUser user = UserHolder.getUser();
        EnterpriseDictDO check = enterpriseDictService.selectByTypeAndValue(eid, businessType, businessValue);
        if (check != null) {
            return false;
        }
        EnterpriseDictDO enterpriseDictDO = new EnterpriseDictDO();
        enterpriseDictDO.setBusinessType(businessType);
        enterpriseDictDO.setBusinessValue(businessValue);
        enterpriseDictDO.setRemarks(remakes);
        enterpriseDictDO.setCreateUserId(user.getUserId());
        enterpriseDictDO.setCreateTime(new Date());
        enterpriseDictDO.setCreateUserName(user.getName());
        enterpriseDictService.save(eid, enterpriseDictDO);
        return true;
    }

    /**
     * 修改人事状态名称
     *
     * @param eid
     * @param userPersonnelStatusDTO
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @author: xugangkun
     * @date: 2022/3/2 15:00
     */
    @RequestMapping("/userStatus/update")
    public ResponseResult userStatusUpdate(@PathVariable(value = "enterprise-id") String eid,
                                           @RequestBody UserPersonnelStatusDTO userPersonnelStatusDTO) {
        DataSourceHelper.changeToMy();
        Boolean isUpdateSuccess = dictUpdate(eid, userPersonnelStatusDTO.getId(),
                BusinessTypeEnum.USER_PERSONNEL_STATUS.getCode(), userPersonnelStatusDTO.getStatusName(), null);
        if (!isUpdateSuccess) {
            return ResponseResult.fail(ErrorCodeEnum.USER_PERSONNEL_STATUS_EXIST);
        }
        return ResponseResult.success();
    }

    private Boolean dictUpdate(String eid, Long dictId, String businessType, String businessValue, String remakes) {
        CurrentUser user = UserHolder.getUser();
        EnterpriseDictDO check = enterpriseDictService.selectByTypeAndValue(eid, businessType, businessValue);
        if (check != null && !check.getId().equals(dictId)) {
            return false;
        }
        EnterpriseDictDO enterpriseDictDO = new EnterpriseDictDO();
        enterpriseDictDO.setId(dictId);
        enterpriseDictDO.setBusinessType(businessType);
        enterpriseDictDO.setBusinessValue(businessValue);
        enterpriseDictDO.setRemarks(remakes);
        enterpriseDictDO.setUpdateUserId(user.getUserId());
        enterpriseDictDO.setUpdateTime(new Date());
        enterpriseDictDO.setUpdateUserName(user.getName());
        enterpriseDictService.updateById(eid, enterpriseDictDO);
        return true;
    }

    /**
     * 删除人事状态
     *
     * @param eid
     * @param userPersonnelStatusDTO
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @author: xugangkun
     * @date: 2022/3/2 15:02
     */
    @PostMapping("/userStatus/delete")
    public ResponseResult delete(@PathVariable(value = "enterprise-id") String eid,
                                 @RequestBody UserPersonnelStatusDTO userPersonnelStatusDTO) {
        DataSourceHelper.changeToMy();
        enterpriseDictService.deleteById(eid, userPersonnelStatusDTO.getId());
        return ResponseResult.success();
    }


    @ApiOperation(value = "新店类型-添加")
    @PostMapping("/newStoreType/add")
    public ResponseResult newStoreTypeAdd(@PathVariable(value = "enterprise-id") String eid,
                                          @Validated @RequestBody NsStoreTypeAddOrUpdateRequest request) {
        DataSourceHelper.changeToMy();
        Boolean isAddSuccess = dictAdd(eid, BusinessTypeEnum.NEW_STORE_TYPE.getCode(), request.getNewStoreType(), null);
        if (!isAddSuccess) {
            return ResponseResult.fail(ErrorCodeEnum.NEW_STORE_TYPE_EXIST);
        }
        return ResponseResult.success();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "类型id", dataType = "String", required = true),
    })
    @ApiOperation(value = "新店类型-删除")
    @DeleteMapping("/newStoreType/delete")
    public ResponseResult newStoreTypeDelete(@PathVariable(value = "enterprise-id") String eid,
                                             @RequestParam(required = true, name = "id") Long id) {
        DataSourceHelper.changeToMy();
        enterpriseDictService.deleteById(eid, id);
        return ResponseResult.success();
    }

    @ApiOperation(value = "新店类型-更新")
    @PostMapping("/newStoreType/update")
    public ResponseResult newStoreTypeUpdate(@PathVariable(value = "enterprise-id") String eid,
											 @Validated @RequestBody NsStoreTypeAddOrUpdateRequest request) {
        DataSourceHelper.changeToMy();
        Boolean isUpdateSuccess = dictUpdate(eid, request.getId(),
                BusinessTypeEnum.NEW_STORE_TYPE.getCode(), request.getNewStoreType(), null);
        if (!isUpdateSuccess) {
            return ResponseResult.fail(ErrorCodeEnum.NEW_STORE_TYPE_EXIST);
        }
        return ResponseResult.success();
    }

    @ApiOperation(value = "新店类型-查询列表")
    @GetMapping("/newStoreType/list")
    public ResponseResult<List<NsStoreTypeVO>> newStoreTypeList(@PathVariable(value = "enterprise-id") String eid) {
        DataSourceHelper.changeToMy();
        List<NsStoreTypeVO> list = (List<NsStoreTypeVO>) enterpriseDictService.selectByType(eid, BusinessTypeEnum.NEW_STORE_TYPE);
        return ResponseResult.success(list);
    }

}
