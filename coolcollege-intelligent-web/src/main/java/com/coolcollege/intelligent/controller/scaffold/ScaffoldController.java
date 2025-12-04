package com.coolcollege.intelligent.controller.scaffold;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.request.StoreAcceptanceRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.StoreAcceptanceVO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 脚手架 前端控制器
 * </p>
 *
 * @author wangff
 * @since 2025/4/15
 */
@Api(tags = "脚手架")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/scaffold")
@RequiredArgsConstructor
public class ScaffoldController {
    private final StoreService storeService;
    private final PatrolStoreRecordsService patrolStoreRecordsService;

    @ApiOperation("根据门店编号查询门店id")
    @GetMapping("/store")
    public ResponseResult<String> trans(@PathVariable("enterprise-id") String enterpriseId,
                                        @NotEmpty(message = "门店编号不能为空") String storeNum) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getStoreIdByStoreNum(enterpriseId, storeNum));
    }

    @ApiOperation("门店验收记录")
    @GetMapping("/acceptanceRecords")
    public ResponseResult<List<StoreAcceptanceVO>> getStoreAcceptanceRecords(@PathVariable("enterprise-id") String enterpriseId,
                                                                             StoreAcceptanceRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.getStoreAcceptanceRecords(enterpriseId, request));
    }
}
