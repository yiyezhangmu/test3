package com.coolcollege.intelligent.controller.qyy;

import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.ConversationTypeEnum;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.AddRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateRecommendStyleDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.RecommendStyleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: RecommendStyleController
 * @Description:主推款
 * @date 2023-04-07 10:35
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/recommend/style")
@Api(tags = "主推款")
@Slf4j
public class RecommendStyleController {

    @Resource
    private RecommendStyleService recommendStyleService;

    @ApiOperation("主推款列表 移动端")
    @GetMapping("/getH5RecommendStyleList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "conversationId", value = "群id"),
            @ApiImplicitParam(name = "conversationType", value = "其他群: other, 分子公司群: region, 门店群: store")
    })
    public ResponseResult<List<H5RecommendStyleListVO>> getH5RecommendStyleList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestParam("conversationId") String conversationId,
                                                                                @RequestParam(value = "conversationType") ConversationTypeEnum conversationType){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.getH5RecommendStyleList(enterpriseId, conversationId, conversationType));
    }

    @ApiOperation("主推款详情 移动端")
    @GetMapping("/getH5RecommendStyleDetail")
    public ResponseResult<H5RecommendStyleDetailVO> getRecommendStyleDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam("id") Long id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.getRecommendStyleDetail(enterpriseId, id));
    }

    @ApiOperation("主推款列表 PC端")
    @GetMapping("/getPCRecommendStylePage")
    public ResponseResult<PageInfo<PCRecommendStyleListVO>> getPCRecommendStylePage(@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestParam(value = "name",required = false) String name,
                                                            @RequestParam(value = "pageNum") Integer pageNum,
                                                            @RequestParam(value = "pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.getPCRecommendStylePage(enterpriseId, name, pageNum, pageSize));
    }

    @ApiOperation("主推款详情 PC端")
    @GetMapping("/getPCRecommendStyleDetail")
    public ResponseResult<PCRecommendStyleDetailVO> getPCRecommendStyleDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                              @RequestParam("id") Long id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.getPCRecommendStyleDetail(enterpriseId, id));
    }

    @ApiOperation("新增主推款")
    @PostMapping("/addRecommendStyle")
    @OperateLog(operateModule = CommonConstant.Function.RECOMMEND_STYLE, operateType = CommonConstant.LOG_ADD, operateDesc = "新增主推款")
    public ResponseResult addRecommendStyle(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AddRecommendStyleDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.addRecommendStyle(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName(), param));
    }

    @ApiOperation("更新主推款")
    @PostMapping("/updateRecommendStyle")
    @OperateLog(operateModule = CommonConstant.Function.RECOMMEND_STYLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新主推款")
    public ResponseResult updateRecommendStyle(@PathVariable("enterprise-id") String enterpriseId, @RequestBody UpdateRecommendStyleDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.updateRecommendStyle(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName(), param));
    }

    @ApiOperation("删除主推款")
    @PostMapping("/deleteRecommendStyle")
    @OperateLog(operateModule = CommonConstant.Function.RECOMMEND_STYLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除主推款")
    public ResponseResult deleteRecommendStyle(@PathVariable("enterprise-id") String enterpriseId, @RequestBody IdDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.deleteRecommendStyle(enterpriseId, param.getId()));
    }

    @ApiOperation("商品搜索")
    @GetMapping("/searchGoods")
    public ResponseResult<List<RecommendStyleGoodsVO>> searchGoods(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("goodsIds")String goodsIds){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(recommendStyleService.searchGoods(enterpriseId, goodsIds));
    }

}
