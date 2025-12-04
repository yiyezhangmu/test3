package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtils;
import com.coolcollege.intelligent.model.ai.AiModelGroupVO;
import com.coolcollege.intelligent.model.ai.AiModelSceneDTO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.ai.AiModelSceneService;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author byd
 * @date 2025-09-26 16:14
 */
@Api(tags = "平台ai算法场景")
@RestController
@RequestMapping("/boss/manage/aiModelScene")
@Slf4j
@ErrorHelper
public class BossAiModelSceneController {

    @Autowired
    private AiModelSceneService aiModelSceneService;

    @Autowired
    private FileUploadService fileUploadService;

    @ApiOperation("获取ai模型场景列表")
    @GetMapping("/list")
    public ResponseResult<List<AiModelSceneVO>> list(Long groupId) {
        DataSourceHelper.reset();
        return ResponseResult.success(aiModelSceneService.list(groupId, null));
    }

    @ApiOperation("获取ai模型场景详情")
    @GetMapping("/detail")
    public ResponseResult<AiModelSceneVO> detail(Long id) {
        DataSourceHelper.reset();
        return ResponseResult.success(aiModelSceneService.detail(id));
    }

    @ApiOperation("更新ai模型场景")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody AiModelSceneDTO aiModelSceneDTO) {
        DataSourceHelper.reset();
        aiModelSceneService.updateAiScene(aiModelSceneDTO);
        return ResponseResult.success();
    }

    @ApiOperation("新增ai模型场景")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody AiModelSceneDTO aiModelSceneDTO) {
        DataSourceHelper.reset();
        aiModelSceneService.addAiScene(aiModelSceneDTO);
        return ResponseResult.success();
    }

    @ApiOperation("获取ai模型场景分组列表")
    @GetMapping("/groupList")
    public ResponseResult<List<AiModelGroupVO>> groupList() {
        DataSourceHelper.reset();
        return ResponseResult.success(aiModelSceneService.groupList());
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Object upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("文件为空");
        } else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}", file.getName(), file.getSize(), file.getOriginalFilename());
        }
        if (!FileUtils.checkFileSizeByInputStream(file.getInputStream(), 200)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "文件大小不能超过200M");
        }
        FileUploadParam fileUploadParam = fileUploadService.uploadFile(file, Constants.PLATFORM_PIC , UserHolder.getUser().getAppType());
        return fileUploadParam;
    }

}
