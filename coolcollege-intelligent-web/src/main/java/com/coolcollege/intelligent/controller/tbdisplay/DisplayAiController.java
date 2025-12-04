package com.coolcollege.intelligent.controller.tbdisplay;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreBatchDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.DashScopeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author zhangchenbiao
 * @FileName: DisplayAiController
 * @Description:陈列ai
 * @date 2025-02-11 9:40
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/displayAi")
@Slf4j
public class DisplayAiController {

    @Resource
    private DashScopeService dashScopeService;
    @Resource
    private AIService aiService;

    @ApiOperation("获取ai标准描述")
    @PostMapping("/getAiCheckStdDesc")
    public ResponseResult<String> getAiCheckStdDesc(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AiCommentAndScoreDTO request){
        try {
            String imageStaDesc = dashScopeService.getAiCheckStdDesc(enterpriseId, Collections.singletonList(request.getImageUrl()), request.getStandardDesc());
            return ResponseResult.success(imageStaDesc);
        } catch (NoApiKeyException | UploadFileException e) {
            e.printStackTrace();
        }
        return ResponseResult.success(null);
    }

    @ApiOperation("获取Ai评论及分数")
    @PostMapping("/getAiCommentAndScore")
    public ResponseResult<AiCommentAndScoreVO> getAiCommentAndScore(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AiCommentAndScoreDTO request){
        try {
            return ResponseResult.success(dashScopeService.getAiCommentAndScore(enterpriseId, request));
        } catch (NoApiKeyException | UploadFileException e) {
            e.printStackTrace();
        }
        return ResponseResult.success();
    }

    @ApiOperation("多张图片获取ai标准描述")
    @PostMapping("/getAiCheckStdDescForImages")
    public ResponseResult<String> getAiCheckStdDescForImages(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AiCommentAndScoreBatchDTO request){
        try {
            return ResponseResult.success(aiService.getAiCheckStaDesc(enterpriseId, request.getAiModel(), request.getImageList(), request.getStandardDesc()));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.success();
    }

    @ApiOperation("多张图片获取Ai评论及分数")
    @PostMapping("/getAiCommentAndScoreForImages")
    public ResponseResult<AiCommentAndScoreVO> getAiCommentAndScoreForImages(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AiCommentAndScoreBatchDTO request){
        try {
            return ResponseResult.success(aiService.getPatrolAiCommentAndScore(enterpriseId, request));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.success(null);
    }

}
