package com.coolcollege.intelligent.controller.picture;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.picture.PictureCenterStoreDO;
import com.coolcollege.intelligent.model.picture.query.PictureCenterQuery;
import com.coolcollege.intelligent.model.picture.vo.PictureCenterVO;
import com.coolcollege.intelligent.model.picture.vo.PictureQuestionCenterVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.picture.PictureCenterService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Api(tags = "图片中心")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/picture")
@BaseResponse
@Slf4j
public class PictureCenterController {
    @Autowired
    private PictureCenterService pictureCenterService;

    @PostMapping("/patrolStoreRecord")
    public ResponseResult<PageVO<PictureCenterVO>> patrolStoreRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestBody PictureCenterQuery query) {
        DataSourceHelper.changeToMy();
        query.setCurrentUser(UserHolder.getUser());
        return ResponseResult.success(PageHelperUtil.getPageVO(pictureCenterService.getRecordByTaskName(enterpriseId,query)));
    }
    @PostMapping("/displayRecord")
    public ResponseResult<PageVO<PictureCenterVO>> displayRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestBody PictureCenterQuery query) {
        DataSourceHelper.changeToMy();
        query.setCurrentUser(UserHolder.getUser());
        return ResponseResult.success(PageHelperUtil.getPageVO(pictureCenterService.getDisplayRecordByTaskName(enterpriseId,query)));
    }
    @PostMapping("/store")
    public ResponseResult<PageVO<PictureCenterStoreDO>> store(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                              @RequestBody PictureCenterQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo(pictureCenterService.getStorePicture(enterpriseId,query))));
    }

    @PostMapping("/patrolStorePictureList")
    public ResponseResult<PageVO<PictureCenterVO>> patrolStorePicture(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                     @RequestBody PictureCenterQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageVO(pictureCenterService.getPictureRecordByTaskName(enterpriseId,query)));
    }

    @ApiOperation("工单图片报表")
    @PostMapping("/taskQuestionRecord")
    public ResponseResult<PageVO<PictureQuestionCenterVO>> taskQuestionRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                              @RequestBody PictureCenterQuery query) {
        DataSourceHelper.changeToMy();
        query.setCurrentUser(UserHolder.getUser());
        return ResponseResult.success(PageHelperUtil.getPageVO(pictureCenterService.taskQuestionRecord(enterpriseId,query)));
    }

    @ApiOperation("图片稽核列表")
    @PostMapping(path = "/pictureCheckList")
    public ResponseResult<PageInfo<PictureCenterVO>> pictureCheckList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Valid PatrolStoreCheckQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(pictureCenterService.getCheckRecordByTaskName(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

}
