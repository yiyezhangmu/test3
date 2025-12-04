package com.coolcollege.intelligent.controller.user;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.device.vo.LastPatrolStoreVO;
import com.coolcollege.intelligent.model.store.dto.StoreIdDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.user.UserCollectStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserCollectStoreController
 * @Description:
 * @date 2022-12-20 14:58
 */
@Api(tags = "门店收藏")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}")
public class UserCollectStoreController {

    @Resource
    private UserCollectStoreService userCollectStoreService;

    @ApiOperation("添加收藏")
    @PostMapping("/addUserCollectStore")
    public ResponseResult<Boolean> addUserCollectStore(@PathVariable("enterprise-id") String enterpriseId, @Validated @RequestBody StoreIdDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(userCollectStoreService.addUserCollectStore(enterpriseId, UserHolder.getUser().getUserId(), param.getStoreId()));
    }

    @ApiOperation("取消收藏")
    @PostMapping("/deleteUserCollectStore")
    public ResponseResult<Boolean> deleteUserCollectStore(@PathVariable("enterprise-id") String enterpriseId, @Validated @RequestBody StoreIdDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(userCollectStoreService.deleteUserCollectStore(enterpriseId, UserHolder.getUser().getUserId(), param.getStoreId()));
    }

    @ApiOperation("获取用户收藏的门店")
    @GetMapping("/getUserCollectStore")
    public ResponseResult<List<LastPatrolStoreVO>> getUserCollectStore(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(userCollectStoreService.getUserCollectStore(enterpriseId, UserHolder.getUser().getUserId()));
    }

}
