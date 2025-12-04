package com.coolcollege.intelligent.controller.aliyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.ListPageInfo;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.aliyun.request.AliyunStaticPersonAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.WebHookMessage;
import com.coolcollege.intelligent.model.aliyun.request.WebHookRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonTraceVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonVO;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsService;
import com.coolcollege.intelligent.util.DateFormatUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/13
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v3/enterprises/aliyun/vds")
public class AliyunVdsController {

    @Autowired
    private AliyunVdsService aliyunVdsService;

    @Autowired
    private AliyunService aliyunService;

    /**
     * 分页查询所有的corp的基本信息
     *
     * @param pageNum
     * @param pageNum
     * @param countTotalNum
     * @param type
     * @param name
     * @return
     */
    @GetMapping("/list")
    public ResponseResult listCorp(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "countTotalNum", required = false) Boolean countTotalNum,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return ResponseResult.success(aliyunService.paginateProject(name, type, countTotalNum, pageNum, pageSize));
    }

    @GetMapping("/list/corpByDevice")
    public ResponseResult listCorpByDeviceId(
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        return ResponseResult.success(aliyunService.listDeviceRelation(deviceId));
    }

    @GetMapping("/list/deviceByCorp")
    public ResponseResult deviceByCorp(
            @RequestParam(value = "corpId", required = false) String corpId,
            @RequestParam(value = "countTotalNum", required = false) Boolean countTotalNum,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return ResponseResult.success(aliyunService.paginateDevice(corpId, pageNum, pageSize, countTotalNum));
    }

    /**
     * vds动态人员列表
     *
     * @param enterpriseId
     * @param storeId
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/{enterprise-id}/list/person")
    public ResponseResult listPerson(@PathVariable(value = "enterprise-id") String enterpriseId,
                                     @RequestParam(value = "storeId", required = false) String storeId,
                                     @RequestParam(value = "startTime") Long startTime,
                                     @RequestParam(value = "endTime") Long endTime,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        long l = System.currentTimeMillis();
        ListPageInfo<AliyunVdsPersonVO> aliyunVdsPersonVOListPageInfo = aliyunVdsService.listPerson(enterpriseId, storeId, startTime, endTime, pageSize, pageNum);
        log.info("vds调用企业={},门店={},vds全部历史调用时间{}ms",enterpriseId,storeId,System.currentTimeMillis()-l);
        return ResponseResult.success(aliyunVdsPersonVOListPageInfo);
    }

    @GetMapping("/{enterprise-id}/list/person/history")
    public ResponseResult listPersonHistory(@PathVariable(value = "enterprise-id") String enterpriseId,
                                     @RequestParam(value = "storeId", required = false) String storeId,
                                     @RequestParam(value = "startTime") Long startTime,
                                     @RequestParam(value = "endTime") Long endTime,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        List<AliyunVdsPersonHistoryVO> historyVOList = aliyunVdsService.listPersonHistory(enterpriseId, storeId, startTime, endTime, pageNum, pageSize);
        if(CollectionUtils.isNotEmpty(historyVOList)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(historyVOList)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>())));
        }
    }

    @GetMapping("/{enterprise-id}/list/person/history/count")
    public ResponseResult personHistoryCount(@PathVariable(value = "enterprise-id") String enterpriseId,
                                            @RequestParam(value = "storeId", required = false) String storeId,
                                            @RequestParam(value = "startTime") Long startTime,
                                            @RequestParam(value = "endTime") Long endTime) {
        DataSourceHelper.changeToMy();
        Integer count =aliyunVdsService.personHistoryCount(enterpriseId, storeId, startTime, endTime);
        return ResponseResult.success(count==null?0:count);
    }


    @GetMapping("/{enterprise-id}/list/person/trace")
    public ResponseResult personTrace(@PathVariable(value = "enterprise-id") String enterpriseId,
                                            @RequestParam(value = "customerId") String customerId,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        List<AliyunPersonTraceVO> aliyunPersonTraceVOList = aliyunVdsService.listPersonTrace(enterpriseId, customerId, pageNum, pageSize);
        if(CollectionUtils.isNotEmpty(aliyunPersonTraceVOList)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(aliyunPersonTraceVOList)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>())));
        }
    }


    /**
     * vds回调
     *
     * @param request
     * @param customerId
     * @return
     */
    @PostMapping({"/{enterprise-id}/{customer-id}/webhook/callback","/{enterprise-id}/webhook/callback/{customer-id}"})
    public ResponseResult callBackWebhook(@RequestBody WebHookRequest request,
                                          @PathVariable(value = "enterprise-id", required = false) String eid,
                                          @PathVariable(value = "customer-id", required = false) String customerId) {
        log.info("vds回调通知日志：enterprisse={},customerId={},request={}", eid,customerId, JSON.toJSON(request));
        aliyunVdsService.callBackWebhook(eid,customerId,request);
        return ResponseResult.success(true);

    }

}
