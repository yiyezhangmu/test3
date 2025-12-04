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
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStatisticsOverviewListVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStatisticsOverviewVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
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
public class RegionExecutiveSummaryExportServiceImpl implements BaseExportService  {

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
        return ExportServiceEnum.REGION_EXECUTIVE_SUMMARY_LIST_REPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        if (pageNum > 1) {
            return new ArrayList<>();
        }
        StoreWorkDataListRequest query = JSONObject.toJavaObject(request, StoreWorkDataListRequest.class);
        List<StoreWorkStatisticsOverviewListVO> list = storeWorkStatisticsService.regionExecutiveSummaryList(enterpriseId, query);
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (StoreWorkStatisticsOverviewListVO overviewListVO : list) {
                Map<String, Object> params = new HashMap<>();
                params.put("regionName", overviewListVO.getRegionName());
                params.put("fullRegionName", overviewListVO.getFullRegionName());
                if (CollectionUtils.isNotEmpty(overviewListVO.getStatisticsOverviewVOList())) {
                    for (StoreWorkStatisticsOverviewVO statisticsOverviewVO : overviewListVO.getStatisticsOverviewVOList()) {
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "finishPercent", statisticsOverviewVO.getFinishPercent());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "totalStoreNum", statisticsOverviewVO.getTotalStoreNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "unFinishStoreNum", statisticsOverviewVO.getUnFinishStoreNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "finishStoreNum", statisticsOverviewVO.getFinishStoreNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "failColumnNum", statisticsOverviewVO.getFailColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "passColumnNum", statisticsOverviewVO.getPassColumnNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "questionNum", statisticsOverviewVO.getQuestionNum());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgScore", statisticsOverviewVO.getAvgScore());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgPassRate", statisticsOverviewVO.getAvgPassRate());
                        params.put(statisticsOverviewVO.getStoreWorkDate() + "-" + "avgScoreRate", statisticsOverviewVO.getAvgScoreRate());
                    }
                }
                //10级区域
                List<String> regionNameList = regionService.getAllRegionName(enterpriseId, overviewListVO.getRegionId()).getRegionNameList();
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

        ExcelExportEntity regionName = new ExcelExportEntity("区域名称", "regionName");
        regionName.setOrderNum(orderNum++);
        beanList.add(regionName);
        ExcelExportEntity fullRegionMame = new ExcelExportEntity("所属区域", "fullRegionName");
        fullRegionMame.setOrderNum(orderNum++);
        beanList.add(fullRegionMame);
        for (String date : dateList) {
            ExcelExportEntity finishPercent = new ExcelExportEntity("完成率", date + "-" + "finishPercent");
            finishPercent.setOrderNum(orderNum++);
            finishPercent.setGroupName(date);
            finishPercent.setNumFormat("#.##%");
            beanList.add(finishPercent);

            ExcelExportEntity totalStoreNum = new ExcelExportEntity("应完成门店数", date + "-" + "totalStoreNum");
            totalStoreNum.setOrderNum(orderNum++);
            totalStoreNum.setGroupName(date);
            beanList.add(totalStoreNum);

            ExcelExportEntity unFinishStoreNum = new ExcelExportEntity("未完成门店数", date + "-" + "unFinishStoreNum");
            unFinishStoreNum.setOrderNum(orderNum++);
            unFinishStoreNum.setGroupName(date);
            beanList.add(unFinishStoreNum);

            ExcelExportEntity finishStoreNum = new ExcelExportEntity("已完成门店数", date + "-" + "finishStoreNum");
            finishStoreNum.setOrderNum(orderNum++);
            finishStoreNum.setGroupName(date);
            beanList.add(finishStoreNum);

            ExcelExportEntity failColumnNum = new ExcelExportEntity("不合格项数", date + "-" + "failColumnNum");
            failColumnNum.setOrderNum(orderNum++);
            failColumnNum.setGroupName(date);
            beanList.add(failColumnNum);

            ExcelExportEntity passColumnNum = new ExcelExportEntity("合格项数", date + "-" + "passColumnNum");
            passColumnNum.setOrderNum(orderNum++);
            passColumnNum.setGroupName(date);
            beanList.add(passColumnNum);

            ExcelExportEntity questionNum = new ExcelExportEntity("工单数", date + "-" + "questionNum");
            questionNum.setOrderNum(orderNum++);
            questionNum.setGroupName(date);
            beanList.add(questionNum);

            ExcelExportEntity avgScore = new ExcelExportEntity("平均得分", date + "-" + "avgScore");
            avgScore.setOrderNum(orderNum++);
            avgScore.setGroupName(date);
            avgScore.setNumFormat("#.##");
            beanList.add(avgScore);

            ExcelExportEntity avgPassRate = new ExcelExportEntity("平均合格率", date + "-" + "avgPassRate");
            avgPassRate.setOrderNum(orderNum++);
            avgPassRate.setGroupName(date);
            avgPassRate.setNumFormat("#.##%");
            beanList.add(avgPassRate);

            ExcelExportEntity avgScoreRate = new ExcelExportEntity("平均得分率", date + "-" + "avgScoreRate");
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
