package com.coolcollege.intelligent.controller.aianalysis;

import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.group.InsertGroup;
import com.coolcollege.intelligent.common.group.UpdateGroup;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisReportQueryDTO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleDTO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleQueryDTO;
import com.coolcollege.intelligent.model.aianalysis.vo.*;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisReportService;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisRuleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * AI分析规则 前端控制器
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Api(tags = "AI分析店报")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/ai/analysis")
@RequiredArgsConstructor
public class AiAnalysisController {
    private final AiAnalysisRuleService aiAnalysisRuleService;
    private final AiAnalysisReportService aiAnalysisReportService;

    @ApiOperation("新增规则")
    @PostMapping("/rule/add")
    @SysLog(func = "新增规则", opModule = OpModuleEnum.AI_ANALYSIS_RULE, opType = OpTypeEnum.INSERT, resolve = false)
    public ResponseResult<Boolean> addAiAnalysisRule(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestBody @Validated(InsertGroup.class) AiAnalysisRuleDTO dto) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisRuleService.save(enterpriseId, dto));
    }

    @ApiOperation("编辑规则")
    @PostMapping("/rule/update")
    @SysLog(func = "编辑规则", opModule = OpModuleEnum.AI_ANALYSIS_RULE, opType = OpTypeEnum.EDIT, resolve = false)
    public ResponseResult<Boolean> updateAiAnalysisRule(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody @Validated(UpdateGroup.class) AiAnalysisRuleDTO dto) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisRuleService.update(enterpriseId, dto));
    }

    @ApiOperation("删除规则")
    @PostMapping("/rule/deleteBatch")
    @SysLog(func = "删除规则", opModule = OpModuleEnum.AI_ANALYSIS_RULE, opType = OpTypeEnum.DELETE, resolve = false)
    public ResponseResult<Boolean> deleteBatchAiAnalysisRule(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestBody List<Long> ids) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisRuleService.removeBatch(enterpriseId, ids));
    }

    @ApiOperation("规则分页查询")
    @PostMapping("/rule/getPage")
    public ResponseResult<PageInfo<AiAnalysisRuleSimpleVO>> getRulePage(@PathVariable("enterprise-id") String enterpriseId,
                                                                @RequestBody AiAnalysisRuleQueryDTO queryDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisRuleService.getPage(enterpriseId, queryDTO));
    }

    @ApiOperation("规则详情查询")
    @GetMapping("/rule/detail")
    public ResponseResult<AiAnalysisRuleVO> getRuleById(@PathVariable("enterprise-id") String enterpriseId, Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisRuleService.getById(enterpriseId, id));
    }

    @ApiOperation("AI模型列表")
    @GetMapping("/models")
    public ResponseResult<List<AiAnalysisModelVO>> getModelList(@PathVariable("enterprise-id") String enterpriseId) {
        return ResponseResult.success(aiAnalysisRuleService.getModelList());
    }

    @ApiOperation("报告分页查询")
    @PostMapping("/report/getPage")
    public ResponseResult<PageInfo<AiAnalysisReportSimpleVO>> getReportPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestBody AiAnalysisReportQueryDTO queryDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisReportService.getPage(enterpriseId, queryDTO));
    }

    @ApiOperation("报告详情")
    @GetMapping("/report/detail")
    public ResponseResult<AiAnalysisReportVO> getReportById(@PathVariable("enterprise-id") String enterpriseId,
                                                            @NotNull(message = "id不能为空") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiAnalysisReportService.getById(enterpriseId, id));
    }
}
