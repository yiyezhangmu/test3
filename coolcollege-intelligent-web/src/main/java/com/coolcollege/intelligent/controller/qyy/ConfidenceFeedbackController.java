package com.coolcollege.intelligent.controller.qyy;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackPageDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitConfidenceFeedbackDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.ConfidenceFeedbackService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackController
 * @Description:
 * @date 2023-04-07 10:40
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/confidence/feedback")
@Api(tags = "信心反馈")
@Slf4j
public class ConfidenceFeedbackController {

    @Resource
    private ConfidenceFeedbackService confidenceFeedbackService;

    @ApiOperation("提交信心反馈")
    @PostMapping("/submitConfidenceFeedback")
    public ResponseResult submitConfidenceFeedback(@PathVariable("enterprise-id") String enterpriseId, @RequestBody SubmitConfidenceFeedbackDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(confidenceFeedbackService.submitConfidenceFeedback(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName(), param));
    }

    @ApiOperation("获取信心反馈详情")
    @GetMapping("/getConfidenceFeedback")
    public ResponseResult<ConfidenceFeedbackDetailVO> getConfidenceFeedback(@PathVariable("enterprise-id") String enterpriseId, @RequestParam ("id")Long id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(confidenceFeedbackService.getConfidenceFeedback(enterpriseId, id));
    }

    @ApiOperation("信心反馈列表")
    @PostMapping("/getConfidenceFeedbackPage")
    public ResponseResult<PageInfo<ConfidenceFeedbackDetailVO>> getConfidenceFeedbackPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                          @RequestBody ConfidenceFeedbackPageDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(confidenceFeedbackService.getConfidenceFeedbackPage(enterpriseId, param));
    }


    @ApiOperation("我发出的信心反馈列表")
    @PostMapping("/getMyConfidenceFeedbackPage")
    public ResponseResult<PageInfo<ConfidenceFeedbackDetailVO>> getMyConfidenceFeedbackPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                          @RequestBody ConfidenceFeedbackPageDTO param){
        DataSourceHelper.changeToMy();
        param.setUserIds(Arrays.asList(UserHolder.getUser().getUserId()));
        return ResponseResult.success(confidenceFeedbackService.getConfidenceFeedbackPage(enterpriseId, param));
    }


    @ApiOperation("信心反馈列表导出")
    @PostMapping("/exportConfidenceFeedbackPage")
    public ResponseResult<ImportTaskDO> exportConfidenceFeedbackPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                     @RequestBody ConfidenceFeedbackPageDTO param){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(confidenceFeedbackService.exportConfidenceFeedbackPage(enterpriseId, param, user));
    }

    @ApiOperation("删除信心反馈")
    @PostMapping("/deleteConfidenceFeedback")
    public ResponseResult deleteConfidenceFeedback(@PathVariable("enterprise-id") String enterpriseId, @RequestBody IdDTO id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(confidenceFeedbackService.deleteConfidenceFeedback(enterpriseId, id.getId()));
    }

}
