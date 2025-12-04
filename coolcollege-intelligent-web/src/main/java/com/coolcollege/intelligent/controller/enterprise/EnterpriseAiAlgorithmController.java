package com.coolcollege.intelligent.controller.enterprise;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.ai.AiModelGroupVO;
import com.coolcollege.intelligent.model.ai.EnterpriseModelAlgorithmDTO;
import com.coolcollege.intelligent.service.ai.AiModelSceneService;
import com.coolcollege.intelligent.service.ai.EnterpriseModelAlgorithmService;
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
 * @author byd
 * @date 2025-09-26 16:14
 */
@Api(tags = "AI巡检场景")
@RestController
@RequestMapping("/v3/enterprise/{enterprise-id}/aiAlgorithm")
@Slf4j
@ErrorHelper
public class EnterpriseAiAlgorithmController {

    @Autowired
    private EnterpriseModelAlgorithmService enterpriseModelAlgorithmService;

    @Autowired
    private AiModelSceneService aiModelSceneService;

    @ApiOperation("获取企业ai模型场景列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "分组id", required = false, dataTypeClass = Long.class)
    })
    public ResponseResult<List<EnterpriseModelAlgorithmDTO>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestParam(value = "groupId", required = false) Long groupId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseModelAlgorithmService.enterpriseAlgorithmList(enterpriseId, groupId));
    }

    @ApiOperation("获取企业ai模型场景详情")
    @GetMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "ai场景id", required = true, dataTypeClass = Long.class)
    })
    public ResponseResult<EnterpriseModelAlgorithmDTO> detail(@PathVariable("enterprise-id") String enterpriseId,
                                                              @RequestParam("sceneId") Long sceneId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseModelAlgorithmService.detail(enterpriseId, sceneId));
    }

    @ApiOperation("更新企业ai模型场景")
    @PostMapping("/update")
    public ResponseResult update(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestBody EnterpriseModelAlgorithmDTO modelAlgorithmDTO) {
        DataSourceHelper.reset();
        modelAlgorithmDTO.setEnterpriseId(enterpriseId);
        enterpriseModelAlgorithmService.update(modelAlgorithmDTO);
        return ResponseResult.success();
    }

    @ApiOperation("获取ai模型场景分组列表")
    @GetMapping("/groupList")
    public ResponseResult<List<AiModelGroupVO>> groupList() {
        DataSourceHelper.reset();
        return ResponseResult.success(aiModelSceneService.groupList());
    }

}
