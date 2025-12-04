package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreCapturePictureDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePictureDTO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStorePictureService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author byd
 * @date 2021-08-27 13:46
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolstore/patrolStorePicture")
@BaseResponse
@Slf4j
public class PatrolStorePictureController {

    @Autowired
    private PatrolStorePictureService patrolStorePictureService;


    /**
     * 上传场景图片
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/uploadPicture")
    public ResponseResult uploadPicture(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestBody @Valid TbPatrolStorePictureDTO param) {
        DataSourceHelper.changeToMy();
        patrolStorePictureService.uploadPicture(enterpriseId, param);
        return ResponseResult.success();
    }

    /**
     * 查询场景图片列表
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/getStoreScenePictureList")
    public ResponseResult getStoreScenePictureList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestParam("businessId") Long businessId,
                                                   @RequestParam(value = "storeSceneId", required = false) Long storeSceneId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStorePictureService.getStoreScenePictureList(enterpriseId, businessId, storeSceneId));
    }


    /**
     * 开始抓拍场景图片
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/beginCapturePicture")
    public ResponseResult beginCapturePicture(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Valid TbPatrolStoreCapturePictureDTO param) {
        DataSourceHelper.changeToMy();
        patrolStorePictureService.beginCapturePicture(enterpriseId, param);
        return ResponseResult.success();
    }

    /**
     * 根据设备或者
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/beginCapturePictureByDevice")
    public ResponseResult beginCapturePictureByDevice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestBody @Valid TbPatrolStoreCapturePictureDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStorePictureService.beginCapturePictureByDevice(enterpriseId, param));
    }

}
