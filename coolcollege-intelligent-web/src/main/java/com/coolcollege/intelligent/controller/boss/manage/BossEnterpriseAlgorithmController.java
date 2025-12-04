package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.ai.AIResultDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreBatchDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.model.ai.EnterpriseModelAlgorithmDTO;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.EnterpriseModelAlgorithmService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2025-09-26 16:14
 */
@Api(tags = "企业ai算法场景")
@RestController
@RequestMapping("/boss/manage/enterpriseAlgorithm")
@Slf4j
@ErrorHelper
public class BossEnterpriseAlgorithmController {

    @Autowired
    private EnterpriseModelAlgorithmService enterpriseModelAlgorithmService;

    @Resource
    private AIService aiService;


    @ApiOperation("获取企业ai模型场景列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业Id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "groupId", value = "分组id", required = false, dataTypeClass = Long.class)
    })
    public ResponseResult<List<EnterpriseModelAlgorithmDTO>> list(@RequestParam("enterpriseId") String enterpriseId,
                                                                  @RequestParam(value = "groupId", required = false) Long groupId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseModelAlgorithmService.list(enterpriseId, groupId));
    }

    @ApiOperation("获取企业ai模型场景详情")
    @GetMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业Id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sceneId", value = "ai场景id", required = true, dataTypeClass = Long.class)
    })
    public ResponseResult<EnterpriseModelAlgorithmDTO> detail(@RequestParam("enterpriseId") String enterpriseId,
                                                              @RequestParam("sceneId") Long sceneId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseModelAlgorithmService.detail(enterpriseId, sceneId));
    }

    @ApiOperation("更新企业ai模型场景")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody EnterpriseModelAlgorithmDTO modelAlgorithmDTO) {
        DataSourceHelper.reset();
        enterpriseModelAlgorithmService.update(modelAlgorithmDTO);
        return ResponseResult.success();
    }

    @ApiOperation("禁用企业AI算法场景")
    @GetMapping("/disable")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业Id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sceneId", value = "ai场景id", required = true, dataTypeClass = Long.class)
    })
    public ResponseResult disable(@RequestParam("enterpriseId") String enterpriseId,
                                  @RequestParam("sceneId") Long sceneId) {
        DataSourceHelper.reset();
        enterpriseModelAlgorithmService.disable(enterpriseId, sceneId);
        return ResponseResult.success();
    }

    @ApiOperation("启用企业AI算法场景")
    @GetMapping("/enable")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业Id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sceneId", value = "ai场景id", required = true, dataTypeClass = Long.class)
    })
    public ResponseResult enable(@RequestParam("enterpriseId") String enterpriseId,
                                 @RequestParam("sceneId") Long sceneId) {
        DataSourceHelper.reset();
        enterpriseModelAlgorithmService.enable(enterpriseId, sceneId);
        return ResponseResult.success();
    }


    @ApiOperation("获取ai模型测试结果")
    @PostMapping("/getAiCommentAndScoreForImages")
    public ResponseResult<AiCommentAndScoreVO> getAiCommentAndScoreForImages(@RequestParam("enterpriseId") String enterpriseId,
                                                                             @RequestBody AiCommentAndScoreBatchDTO request){
        try {
            AIResultDTO aiResultDTO = aiService.aiInspectionResolveTest(enterpriseId, null, request.getImageList(), request.getStandardDesc(), request.getAiModel());
            AiCommentAndScoreVO aiCommentAndScoreVO = new AiCommentAndScoreVO(null, aiResultDTO.getAiResult(), null);
            return ResponseResult.success(aiCommentAndScoreVO);
        } catch (ServiceException e) {
            log.error("获取ai模型测试结果异常", e);
            throw e;
        } catch (Exception e) {
            log.error("获取ai模型测试结果异常", e);
        }
        return ResponseResult.success(null);
    }

}
