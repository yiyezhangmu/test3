package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.SceneTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.controller.common.vo.EnumVO;
import com.coolcollege.intelligent.model.patrolstore.param.MetaTableConfigParam;
import com.coolcollege.intelligent.model.pictureInspection.query.StoreSceneQueryParam;
import com.coolcollege.intelligent.model.pictureInspection.query.StoreSceneRequest;
import com.coolcollege.intelligent.service.pictureInspection.StoreSceneService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 18:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolstore/pictureInspection")
@BaseResponse
@Slf4j
public class PictureInspectionController {

    @Autowired
    StoreSceneService storeSceneService;


    /**
     * 门店场景新增
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/add")
    public ResponseResult add(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody StoreSceneRequest request) {
        DataSourceHelper.changeToMy();
        return  ResponseResult.success(storeSceneService.insert(enterpriseId, request));
    }

    /**
     * 门店场景更新
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseResult updateById(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody StoreSceneRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSceneService.updateSceneById(enterpriseId, request));
    }

    /**
     * 门店场景删除
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/delete")
    public ResponseResult deleteById(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody StoreSceneRequest request) {
        DataSourceHelper.changeToMy();
        storeSceneService.deleteById(enterpriseId, request.getId());
        return ResponseResult.success(true);
    }

    /**
     * 门店场景列表
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/getStoreSceneList")
    public ResponseResult getStoreSceneList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeSceneService.getStoreSceneList(enterpriseId));
    }


    /**
     * 场景类型列表
     * @return
     */
    @GetMapping(path = "/sceneType")
    public ResponseResult<List<EnumVO>> sceneType() {
        List<EnumVO> enumVOList = Arrays.stream(SceneTypeEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getMsg());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }

}
