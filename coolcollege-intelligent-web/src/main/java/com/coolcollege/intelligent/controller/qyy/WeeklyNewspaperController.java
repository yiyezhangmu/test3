package com.coolcollege.intelligent.controller.qyy;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.WeeklyNewspaperDataDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.QyyWeeklyListDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitWeeklyNewspaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.WeeklyPaperDetailDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.StoreListVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.qyy.QyyNewspaperAchieveDO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyCountDO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.achievement.qyy.WeeklyNewspaperService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangchenbiao
 * @FileName: WeeklyNewspaperController
 * @Description:周报
 * @date 2023-04-07 10:37
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/weekly/newspaper")
@Api(tags = "周报")
@Slf4j
public class WeeklyNewspaperController {

    @Resource
    private WeeklyNewspaperService weeklyNewspaperService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @ApiModelProperty("获取门店列表")
    @GetMapping("/getUserAuthStoreList")
    public ResponseResult<List<StoreListVO>> getUserAuthStoreList(@PathVariable("enterprise-id") String enterpriseId, @RequestParam(value = "storeName", required = false) String storeName) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String userId = Optional.ofNullable(user).map(CurrentUser::getUserId).orElse(null);
        return ResponseResult.success(weeklyNewspaperService.getUserAuthStoreList(enterpriseId, userId, storeName));
    }


    @ApiOperation("写周报")
    @PostMapping("/submitWeeklyNewspaper")
    public ResponseResult submitWeeklyNewspaper(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody SubmitWeeklyNewspaperDTO param) throws UnsupportedEncodingException {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        String username = UserHolder.getUser().getName();
        return ResponseResult.success(weeklyNewspaperService.submitWeeklyNewspaper(enterpriseId, userId, username, param));
    }

    @ApiOperation("获取周报缓存数据")
    @GetMapping("/getWeeklyNewspaperCache")
    public ResponseResult<SubmitWeeklyNewspaperDTO> getWeeklyNewspaperCache(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam("storeId") String storeId,
                                                                            @RequestParam("mondayOfWeek") String mondayOfWeek,
                                                                            @RequestParam("conversationId") String conversationId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperCache(enterpriseId, storeId, mondayOfWeek, conversationId, user));
    }

    @ApiOperation("店长周报列表")
    @PostMapping("/getWeeklyNewspaperPage")
    public ResponseResult<PageInfo<WeeklyNewspaperPageVO>> getWeeklyNewspaperPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestBody QyyWeeklyListDTO param) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperPage(enterpriseId, param.getBeginDate(), param.getEndDate(), null, param.getPageNum(), param.getPageSize(), param.getConversationId(),param.getRegionId(),param.getStoreName(),param.getStoreId(),"store"));
    }

    @ApiOperation("我发出的列表")
    @PostMapping("/getMyWeeklyNewspaper")
    public ResponseResult<PageInfo<WeeklyNewspaperPageVO>> getMyWeeklyNewspaper(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestBody QyyWeeklyListDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperPage(enterpriseId, param.getBeginDate(), param.getEndDate(), UserHolder.getUser().getUserId(), param.getPageNum(), param.getPageSize(), param.getConversationId(),param.getRegionId(),param.getStoreName(),param.getStoreId(),"me"));
    }

    @ApiOperation("周报详情")
    @GetMapping("/getWeeklyNewspaperDetail")
    public ResponseResult<WeeklyNewspaperDetailVO> getWeeklyNewspaperDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("id") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperDetail(enterpriseId, id, null));
    }

    @ApiOperation("卓诗尼周报详情")
    @PostMapping("/josiny/getWeeklyNewspaperDetail")
    public ResponseResult<WeeklyNewspaperDetailVO> getJosinyWeeklyNewspaperDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestBody WeeklyPaperDetailDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperDetail(enterpriseId, param.getId(), param.getType()));
    }

    @ApiOperation("卓诗尼删除周报")
    @PostMapping("/josiny/deleteWeeklyNewspaper")
    public ResponseResult deleteWeeklyNewspaper(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody WeeklyPaperDetailDTO id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.deleteWeeklyNewspaper(enterpriseId, id.getId()));
    }

    @ApiOperation("卓诗尼群运营门店周报列表")
    @PostMapping("/storeWeeklyNewsPaperByPage")
    public ResponseResult<PageInfo<QyyWeeklyNewspaperDO>> storeWeeklyNewsPaperByPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                     @RequestBody StoreNewsPaperDTO paperDTO) {
        DataSourceHelper.changeToMy();
        log.info("storeWeeklyNewsPaperByPage#enterpriseId:{},paperDTO:{}", enterpriseId, JSONObject.toJSONString(paperDTO));
        return ResponseResult.success(weeklyNewspaperService.storeWeeklyNewsPaperByPage(enterpriseId, paperDTO));
    }


    @ApiOperation("周报数据")
    @PostMapping("/pushWeeklyNewspaperDate")
    public ResponseResult pushWeeklyNewspaperDate(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestBody WeeklyNewspaperDataDTO weeklyNewspaperDataDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.pushWeeklyNewspaperDate(enterpriseId, weeklyNewspaperDataDTO));
    }

    @ApiOperation("卓诗尼获取周报数据")
    @PostMapping("/getWeeklyNewspaperDate")
    public ResponseResult<QyyNewspaperAchieveDO> getWeeklyNewspaperDate(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestBody WeeklyNewspaperDataDTO param) {
        DataSourceHelper.changeToMy();
        log.info("getWeeklyNewspaperDate#enterpriseId:{},WeeklyNewspaperDataDTO:{}", enterpriseId, JSONObject.toJSONString(param));
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperDate(enterpriseId, param));
    }

    @ApiOperation("卓诗尼导出列表")
    @PostMapping("/downloadExcel")
    public ResponseResult<ImportTaskDO> downloadExcel(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.downloadExcel(UserHolder.getUser(),enterpriseId));
    }


    @ApiOperation("卓诗尼周报已读人员")
    @PostMapping("/readPeople")
    public ResponseResult<List<EnterpriseUserDO>> readPeople(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestBody WeeklyPaperDetailDTO id) {
        DataSourceHelper.changeToMy();
        log.info("readPeople#enterpriseId:{},id:{}", enterpriseId, id);
        return ResponseResult.success(weeklyNewspaperService.readPeople(enterpriseId, String.valueOf(id.getId())));
    }


    @ApiOperation("卓诗尼周报统计")
    @GetMapping("/countWeeklyNewspaper")
    public ResponseResult<QyyWeeklyCountDO> countWeeklyNewspaper(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestParam("synDeptId") String synDingDeptId,
                                                                 @RequestParam("type") String type){
        DataSourceHelper.changeToMy();
        log.info("readPeople#enterpriseId:{},synDeptId:{}", enterpriseId, synDingDeptId);
        return ResponseResult.success(weeklyNewspaperService.countWeeklyNewspaper(enterpriseId,synDingDeptId,type));
    }



    @ApiOperation("测试周报api")
    @GetMapping("/pullNewsPaperList")
    public ResponseResult pullNewsPaperList(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestParam("createTime") String time){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(weeklyNewspaperService.getWeeklyNewspaperList(enterpriseId,time));
    }



}
