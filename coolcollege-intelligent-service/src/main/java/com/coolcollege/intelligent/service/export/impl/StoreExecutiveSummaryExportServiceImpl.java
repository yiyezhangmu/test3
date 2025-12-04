package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.storework.request.RegionSummaryDataStatisticRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataDetailVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStoreSummaryVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author byd
 */
@Service
@Slf4j
public class StoreExecutiveSummaryExportServiceImpl implements BaseExportService  {

    @Autowired
    private StoreWorkStatisticsService storeWorkStatisticsService;

    @Autowired
    private RegionService regionService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return 0L;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.STORE_EXECUTIVE_SUMMARY_LIST_REPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreWorkDataListRequest query = JSONObject.toJavaObject(request, StoreWorkDataListRequest.class);
        query.setPageNumber(pageNum);
        query.setPageSize(pageSize);
        PageInfo<StoreWorkStoreSummaryVO> pageInfo = storeWorkStatisticsService.storeExecutiveSummaryList(enterpriseId, query);
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            for (StoreWorkStoreSummaryVO storeSummaryVO : pageInfo.getList()) {
                Map<String, Object> params = new HashMap<>();
                params.put("storeName", storeSummaryVO.getStoreName());
                params.put("storeNum", storeSummaryVO.getStoreNum());
                params.put("fullRegionName", storeSummaryVO.getFullRegionName());
                if (CollectionUtils.isNotEmpty(storeSummaryVO.getDetailList())) {
                    for (StoreWorkDataDetailVO statisticsOverviewVO : storeSummaryVO.getDetailList()) {
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "finishPercent", statisticsOverviewVO.getFinishPercent());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "totalColumnNum", statisticsOverviewVO.getTotalColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "unFinishColumnNum", statisticsOverviewVO.getUnFinishColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "finishColumnNum", statisticsOverviewVO.getFinishColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "failColumnNum", statisticsOverviewVO.getFailColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "passColumnNum", statisticsOverviewVO.getPassColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "questionNum", statisticsOverviewVO.getQuestionNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgScore", statisticsOverviewVO.getAvgScore());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgPassRate", statisticsOverviewVO.getAvgPassRate());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgScoreRate", statisticsOverviewVO.getAvgScoreRate());
                    }
                }
                //10级区域
                List<String> regionNameList = regionService.getAllRegionName(enterpriseId, storeSummaryVO.getStoreRegionId()).getRegionNameList();
                if(CollectionUtils.isNotEmpty(regionNameList)){
                    int i = 0 ;
                    for(String regionName : regionNameList){
                        params.put(Constants.EXPORT_REGION_CODE + i, regionName);
                        i++;
                    }
                }
                mapList.add(params);
            }
        }
        return mapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        int orderNum = 1;
        List<ExcelExportEntity> beanList = new ArrayList<>();
        RegionSummaryDataStatisticRequest queryParam = JSONObject.toJavaObject(request, RegionSummaryDataStatisticRequest.class);
        List<String> dateList = DateUtils.getDayOfWeekWithinDateInterval(queryParam.getBeginStoreWorkDate(), queryParam.getEndStoreWorkDate(), queryParam.getWorkCycle());

        ExcelExportEntity storeName = new ExcelExportEntity("门店名称", "storeName");
        storeName.setOrderNum(orderNum++);
        beanList.add(storeName);
        ExcelExportEntity storeNum = new ExcelExportEntity("门店编号", "storeNum");
        storeNum.setOrderNum(orderNum++);
        beanList.add(storeNum);
        ExcelExportEntity fullRegionMame = new ExcelExportEntity("所属区域", "fullRegionName");
        fullRegionMame.setOrderNum(orderNum++);
        beanList.add(fullRegionMame);
        for (String date : dateList) {
            ExcelExportEntity finishPercent = new ExcelExportEntity("完成率", date + "-" + "finishPercent");
            finishPercent.setOrderNum(orderNum++);
            finishPercent.setGroupName(date);
            finishPercent.setNumFormat("#.##%");
            beanList.add(finishPercent);
            ExcelExportEntity totalColumnNum = new ExcelExportEntity("应完成检查项", date + "-" +"totalColumnNum");
            totalColumnNum.setOrderNum(orderNum++);
            totalColumnNum.setGroupName(date);
            beanList.add(totalColumnNum);

            ExcelExportEntity finishColumnNum = new ExcelExportEntity("已完成检查项", date + "-" +"finishColumnNum");
            finishColumnNum.setOrderNum(orderNum++);
            finishColumnNum.setGroupName(date);
            beanList.add(finishColumnNum);

            ExcelExportEntity unFinishColumnNum = new ExcelExportEntity("未完成检查项", date + "-" +"unFinishColumnNum");
            unFinishColumnNum.setOrderNum(orderNum++);
            unFinishColumnNum.setGroupName(date);
            beanList.add(unFinishColumnNum);


            ExcelExportEntity failColumnNum = new ExcelExportEntity("不合格项数", date + "-" +"failColumnNum");
            failColumnNum.setOrderNum(orderNum++);
            failColumnNum.setGroupName(date);
            beanList.add(failColumnNum);

            ExcelExportEntity passColumnNum = new ExcelExportEntity("合格项数", date + "-" +"passColumnNum");
            passColumnNum.setOrderNum(orderNum++);
            passColumnNum.setGroupName(date);
            beanList.add(passColumnNum);

            ExcelExportEntity questionNum = new ExcelExportEntity("工单数", date + "-" +"questionNum");
            questionNum.setOrderNum(orderNum++);
            questionNum.setGroupName(date);
            beanList.add(questionNum);

            ExcelExportEntity avgScore = new ExcelExportEntity("平均得分", date + "-" +"avgScore");
            avgScore.setOrderNum(orderNum++);
            avgScore.setGroupName(date);
            avgScore.setNumFormat("#.##");
            beanList.add(avgScore);

            ExcelExportEntity avgPassRate = new ExcelExportEntity("平均合格率", date + "-" +"avgPassRate");
            avgPassRate.setOrderNum(orderNum++);
            avgPassRate.setGroupName(date);
            avgPassRate.setNumFormat("#.##%");
            beanList.add(avgPassRate);

            ExcelExportEntity avgScoreRate = new ExcelExportEntity("平均得分率", date + "-" +"avgScoreRate");
            avgScoreRate.setOrderNum(orderNum++);
            avgScoreRate.setGroupName(date);
            avgScoreRate.setNumFormat("#.##%");
            beanList.add(avgScoreRate);
        }
        beanList.addAll(ExportUtil.getRegionExportEntityList());
        return beanList;
    }

    @Override
    public String getSheetName(ExportMsgSendRequest exportMsgSendRequest) {
        ImportTaskDO importTaskDO = exportMsgSendRequest.getImportTaskDO();
        return importTaskDO == null ? null : importTaskDO.getFileName();
    }
}
