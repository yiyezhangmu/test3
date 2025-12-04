package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ExportImageDomainReplaceCodeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayHistoryColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataContentMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordPageVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.MyFileLoader;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021/10/12 16:20
 */
@Service
@Slf4j
public class DisplayRecordListExportService implements BaseExportService {

    @Resource
    private MyFileLoader myFileLoader;

    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;


    @Resource
    private TaskMappingMapper taskMappingMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;

    @Resource
    private TbDisplayTableDataContentMapper tbDisplayTableDataContentMapper;

    @Autowired
    private RegionService regionService;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private TbDisplayHistoryColumnMapper tbDisplayHistoryColumnMapper;

    private static final String OSS_URL = "oss.coolcollege.cn";

    private static final String OSS_PROCESSOR_URL = "oss-processor.coolcollege.cn";



    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_DISPLAY_RECORD_LIST;
    }

    @Override
    public List<Map<String, Object>> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        log.info("DisplayRecordListExportService exportList,enterpriseId:{},request:{}",enterpriseId,JSONObject.toJSONString(request));
        TbDisplayReportQueryParam queryParam = JSONObject.toJavaObject(request, TbDisplayReportQueryParam.class);;
        queryParam.setPageNumber(pageNum);
        queryParam.setPageSize(pageSize);
        TbDisplayTableRecordPageVO result = tbDisplayTableRecordService.tableRecordList(enterpriseId, queryParam);
        log.info("DisplayRecordListExportService result：{}",JSONObject.toJSONString(result));
        if (CollectionUtils.isEmpty(result.getRecordInfo().getList())) {
            log.info("result.getRecordInfo().getList() IS NULL");
            return null;
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<TbDisplayTaskDataVO> list = result.getRecordInfo().getList();

        List<TbMetaDisplayTableColumnDO> metaColumnList = result.getColumnList();

        Map<Long, TbMetaDisplayTableColumnDO> metaColumnMap = new HashMap<>();

        metaColumnMap  = ListUtils.emptyIfNull(metaColumnList).stream()
                .collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, data -> data, (a, b) -> a));

        Map<Long, TbMetaDisplayTableColumnDO> metaContentMap = new HashMap<>();


        List<TbMetaDisplayTableColumnDO> metaContentList = result.getContentList();

        metaContentMap  = ListUtils.emptyIfNull(metaContentList).stream()
                .collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, data -> data, (a, b) -> a));
        POICacheManager.setFileLoader(myFileLoader);
        List<String> storeIdList = ListUtils.emptyIfNull(list)
                .stream()
                .map(TbDisplayTaskDataVO::getStoreId)
                .collect(Collectors.toList());

        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(list)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO =new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        List<StoreDO> storeList= storeMapper.getStoreByStoreIdList(enterpriseId, storeIdList);
        Map<String, String> storeNumMap = ListUtils.emptyIfNull(storeList)
                .stream()
                .filter(data->StringUtils.isNotBlank(data.getStoreNum()))
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum, (a, b) -> a));
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);
        for (TbDisplayTaskDataVO tbDisplayTaskDataVO : list) {
            Map<String, Object> vo = new HashMap<>();
            vo.put("storeName", tbDisplayTaskDataVO.getStoreName());
            vo.put("storeNum",storeNumMap.get(tbDisplayTaskDataVO.getStoreId()));
            vo.put("storeAreaName", fullRegionNameMap.get(tbDisplayTaskDataVO.getStoreId()));
            vo.put("storeGroupName", tbDisplayTaskDataVO.getStoreGroupName());
            vo.put("tableName", tbDisplayTaskDataVO.getTableName());
            vo.put("metaColumnNum", tbDisplayTaskDataVO.getMetaColumnNum());
            vo.put("score", tbDisplayTaskDataVO.getScore());
            vo.put("remark", tbDisplayTaskDataVO.getRemark());

            List<TbDisplayTableDataColumnDO> dataDableColumnList = tbDisplayTaskDataVO.getDataColumnList();

            List<TbDisplayTableDataContentDO> dataTableContentList = tbDisplayTaskDataVO.getDataContentList();

            List<TbDisplayHistoryColumnDO> historyColumnDOList = tbDisplayHistoryColumnMapper.getListByRecordIdList(enterpriseId, Collections.singletonList(tbDisplayTaskDataVO.getId()));
            Map<Long, List<TbDisplayHistoryColumnDO>> approveHistoryColumnMap = new HashMap<>();
            Map<Long, List<TbDisplayHistoryColumnDO>> approveHistoryContentMap = new HashMap<>();
            //审核记录
            if(CollectionUtils.isNotEmpty(historyColumnDOList)){
                List<TbDisplayHistoryColumnDO> columnList = historyColumnDOList.stream().filter(a -> a.getCheckType() == 0).collect(Collectors.toList());
                //检查项 历史记录
                approveHistoryColumnMap = ListUtils.emptyIfNull(columnList).stream().collect(Collectors.groupingBy(TbDisplayHistoryColumnDO::getDataColumnId));
                //检查内容 历史数据
                List<TbDisplayHistoryColumnDO> contentList = historyColumnDOList.stream().filter(a -> a.getCheckType() == 1).collect(Collectors.toList());
                //检查项 历史记录
                approveHistoryContentMap = ListUtils.emptyIfNull(contentList).stream().collect(Collectors.groupingBy(TbDisplayHistoryColumnDO::getDataColumnId));
            }

            boolean isOpenHigh = CollectionUtils.isNotEmpty(dataTableContentList);

            if (!isOpenHigh) {
                for (TbDisplayTableDataColumnDO dataTableColumnDO : dataDableColumnList) {
                    TbMetaDisplayTableColumnDO metaColumnDO = metaColumnMap.get(dataTableColumnDO.getMetaColumnId());
                    vo.put("dataScore_" + dataTableColumnDO.getMetaColumnId(), dataTableColumnDO.getScore());
                    if(queryParam.getPicture()){
                        List<String> photoList = getPhotoList(dataTableColumnDO.getPhotoArray(), queryParam.getPicture());
                        int i = 0;
                        for(String picture : photoList){
                            i++;
                            vo.put("dataHanderPicture_" + dataTableColumnDO.getMetaColumnId() + "_" + i, StringUtils.isBlank(picture) ? null : picture.split("\\?")[0]);
                        }
                    }else {
                        String videos = dataTableColumnDO.getCheckVideo();
                        List<String> videoList = new ArrayList<>();
                        if (StringUtils.isNotBlank(videos)) {
                            SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);
                            videoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                                    .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                        }
                        List<String> picList = getPhotoList(dataTableColumnDO.getPhotoArray(), queryParam.getPicture());
                        if(CollectionUtils.isNotEmpty(videoList)){
                            picList.addAll(videoList);
                        }
                        vo.put("dataHanderPicture_" + dataTableColumnDO.getMetaColumnId() , StringUtils.join(picList, ","));
                    }

                    List<TbDisplayHistoryColumnDO> historyColumnApproveList = approveHistoryColumnMap.get(dataTableColumnDO.getId());
                    Map<String, String> approveRemarkMap = new HashMap<>();
                    if(CollectionUtils.isNotEmpty(historyColumnApproveList)){
                        historyColumnApproveList.forEach(approveRemarkDO -> approveRemarkMap.put(approveRemarkDO.getOperateType(), approveRemarkDO.getRemark()));
                    }
                    String approveRemark = "一级审批人：" + approveRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE, "") + "\n" +
                            "二级审批人：" + approveRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.RECHECK, "") + "\n" +
                            "三级审批人：" + approveRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.THIRD_APPROVE, "") + "\n" +
                            "四级审批人：" + approveRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.FOUR_APPROVE, "") + "\n" +
                            "五级审批人：" + approveRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.FIVE_APPROVE, "") + "\n";
                    vo.put("dataApproveRemark_" + dataTableColumnDO.getMetaColumnId(), approveRemark);
                    vo.put("dataRemark_" + dataTableColumnDO.getMetaColumnId(), dataTableColumnDO.getRemark());


                    if(metaColumnDO != null){
                        if(queryParam.getPicture()){
                            String picUrl = metaColumnDO.getStandardPic();
                            if(StringUtils.isNotBlank(picUrl)){
                                picUrl = picUrl.split(Constants.COMMA)[0];
                            }
                            vo.put("standardPic_" + dataTableColumnDO.getMetaColumnId(),
                                    StringUtils.isBlank(picUrl) ? null : ExportImageDomainReplaceCodeEnum.replaceCode(picUrl));

                        }else {
                            vo.put("standardPic_" + dataTableColumnDO.getMetaColumnId(), metaColumnDO.getStandardPic());
                        }
                        vo.put("description_" + dataTableColumnDO.getMetaColumnId(), metaColumnDO.getDescription());
                        vo.put("metaScore_" + dataTableColumnDO.getMetaColumnId(), metaColumnDO.getScore());

                    }
                }
            } else {
                for (TbDisplayTableDataColumnDO dataTableColumnDO : dataDableColumnList) {
                    TbMetaDisplayTableColumnDO metaColumnDO = metaColumnMap.get(dataTableColumnDO.getMetaColumnId());
                    vo.put("dataScore_" + dataTableColumnDO.getMetaColumnId(), dataTableColumnDO.getScore());
                    if(metaColumnDO != null){
                        vo.put("metaScore_" + dataTableColumnDO.getMetaColumnId(), metaColumnDO.getScore());
                    }

                }

                for (TbDisplayTableDataContentDO dataContentDO : dataTableContentList) {
                    TbMetaDisplayTableColumnDO metaContentDO = metaContentMap.get(dataContentDO.getMetaContentId());
                    if(queryParam.getPicture()){
                        List<String> photoList = getPhotoList(dataContentDO.getPhotoArray(), queryParam.getPicture());
                        int i = 0;
                        for(String picture : photoList){
                            i++;
                            vo.put("dataPicture_" + dataContentDO.getMetaContentId() + "_" + i, StringUtils.isBlank(picture) ? null : picture.split("\\?")[0]);
                        }
                    }else {
                        String videos = dataContentDO.getCheckVideo();
                        List<String> videoList = new ArrayList<>();
                        if (StringUtils.isNotBlank(videos)) {
                            SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);
                            videoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                                    .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                        }
                        List<String> picList = getPhotoList(dataContentDO.getPhotoArray(), queryParam.getPicture());
                        if(CollectionUtils.isNotEmpty(videoList)){
                            picList.addAll(videoList);
                        }
                        vo.put("dataPicture_" + dataContentDO.getMetaContentId(), StringUtils.join(picList, ","));
                    }
                    List<TbDisplayHistoryColumnDO> historyContentApproveList = approveHistoryContentMap.get(dataContentDO.getId());
                    Map<String, String> approveContentRemarkMap = new HashMap<>();
                    if(CollectionUtils.isNotEmpty(historyContentApproveList)){
                        historyContentApproveList.forEach(approveRemarkDO -> approveContentRemarkMap.put(approveRemarkDO.getOperateType(), approveRemarkDO.getRemark()));
                    }
                    String approveRemark = "一级审批人：" + approveContentRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE, "") + "\n" +
                            "二级审批人：" + approveContentRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.RECHECK, "") + "\n" +
                            "三级审批人：" + approveContentRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.THIRD_APPROVE, "") + "\n" +
                            "四级审批人：" + approveContentRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.FOUR_APPROVE, "") + "\n" +
                            "五级审批人：" + approveContentRemarkMap.getOrDefault(TbDisplayConstant.TbDisplayRecordStatusConstant.FIVE_APPROVE, "") + "\n";
                    vo.put("dataApproveRemark_" + dataContentDO.getMetaContentId(), approveRemark);
                    vo.put("dataRemark_" + dataContentDO.getMetaContentId(), dataContentDO.getRemark());
                    if(metaContentDO != null){
                        if(queryParam.getPicture()){
                            String picUrl = metaContentDO.getStandardPic();
                            if(StringUtils.isNotBlank(picUrl)){
                                picUrl = picUrl.split(Constants.COMMA)[0];
                            }
                            vo.put("metaPicture_" + dataContentDO.getMetaContentId(), StringUtils.isBlank(picUrl) ? null : ExportImageDomainReplaceCodeEnum.replaceCode(picUrl));
                        }else {
                            vo.put("metaPicture_" + dataContentDO.getMetaContentId(), metaContentDO.getStandardPic());
                        }
                        vo.put("metaDesc_" + dataContentDO.getMetaContentId(), metaContentDO.getDescription());
                    }
                }
            }
            vo.put("handleUserName", tbDisplayTaskDataVO.getHandleUserName());
            vo.put("handlerDuration", tbDisplayTaskDataVO.getHandlerDuration());
            vo.put("latestHandlerTime", DateUtil.format(tbDisplayTaskDataVO.getLatestHandlerTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.put("approveUserName", tbDisplayTaskDataVO.getApproveUserName());
            vo.put("approveDuration", tbDisplayTaskDataVO.getApproveDuration());
            vo.put("latestApproveTime", DateUtil.format(tbDisplayTaskDataVO.getLatestApproveTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.put("recheckUserName", tbDisplayTaskDataVO.getRecheckUserName());
            vo.put("doneTime", tbDisplayTaskDataVO.getDoneTime());
            vo.put("checkTime", tbDisplayTaskDataVO.getCheckTime());
            vo.put("handlerOverdue", tbDisplayTaskDataVO.getHandlerOverdue());
            vo.put("taskName", tbDisplayTaskDataVO.getTaskName());
            vo.put("taskDesc", tbDisplayTaskDataVO.getTaskDesc());
            vo.put("validTime", tbDisplayTaskDataVO.getValidTime());
            vo.put("completeOverdue", tbDisplayTaskDataVO.getCompleteOverdue());
            vo.put("status", tbDisplayTaskDataVO.getStatus());
            vo.put("taskStatus", tbDisplayTaskDataVO.getTaskStatus());
            String storeAreaName = MapUtils.getString(vo, "storeAreaName");
            if (StringUtils.isNotBlank(storeAreaName)) {
                String[] areaNameList = storeAreaName.split("-");
                int index = 0;
                for (String areaName : areaNameList) {
                    vo.put(Constants.EXPORT_REGION_CODE + index++, areaName);
                }
            }
            mapList.add(vo);
        }
        log.info("DisplayRecordListExportService exportList mapList：{}",JSONObject.toJSONString(mapList));
        return mapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        TbDisplayReportQueryParam recordExportRequest = JSONObject.toJavaObject(request, TbDisplayReportQueryParam.class);
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(recordExportRequest.getEid(), recordExportRequest.getUnifyTaskId());

        List<Long> metaTableIds = unifyFormDataDTOList.stream().map(m -> Long.parseLong(m.getOriginMappingId())).collect(Collectors.toList());

        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByTableIdList(recordExportRequest.getEid(), metaTableIds);

        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnList = tbMetaDisplayTableColumnDOList.stream().filter(s -> 0 == s.getCheckType()).collect(Collectors.toList());

        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentList = tbMetaDisplayTableColumnDOList.stream().filter(s -> 1 == s.getCheckType()).collect(Collectors.toList());
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(recordExportRequest.getEid(), recordExportRequest.getUnifyTaskId());
        JSONObject taskInfo = JSONObject.parseObject(taskParentDO.getTaskInfo());
        Boolean isSupportScore = false;
        if(taskInfo != null){
            JSONObject tbdisplaydefindObj = taskInfo.getJSONObject("tbDisplayDefined");
            if(tbdisplaydefindObj != null){
                isSupportScore = tbdisplaydefindObj.getBoolean("isSupportScore");
            }
        }
        if(isSupportScore == null){
            isSupportScore = false;
        }
        int orderNum = 1;
        List<ExcelExportEntity> beanList = new ArrayList<>();
        ExcelExportEntity storeName = new ExcelExportEntity("门店名称", "storeName");
        storeName.setOrderNum(orderNum++);
        beanList.add(storeName);
        ExcelExportEntity storeNum = new ExcelExportEntity("门店编号", "storeNum");
        storeNum.setOrderNum(orderNum++);
        beanList.add(storeNum);
        ExcelExportEntity storeAreaName = new ExcelExportEntity("门店区域", "storeAreaName");
        storeAreaName.setOrderNum(orderNum++);
        beanList.add(storeAreaName);
        ExcelExportEntity storeGroupName = new ExcelExportEntity("门店分组", "storeGroupName");
        storeGroupName.setOrderNum(orderNum++);
        beanList.add(storeGroupName);
        ExcelExportEntity tableName = new ExcelExportEntity("检查表", "tableName");
        tableName.setOrderNum(orderNum++);
        beanList.add(tableName);

        ExcelExportEntity metaColumnNum = new ExcelExportEntity("总检查项数量", "metaColumnNum");
        metaColumnNum.setOrderNum(orderNum++);
        beanList.add(metaColumnNum);
        ExcelExportEntity score = new ExcelExportEntity("门店总分", "score");
        score.setOrderNum(orderNum++);
        beanList.add(score);
        ExcelExportEntity remark = new ExcelExportEntity("门店评价", "remark");
        remark.setOrderNum(orderNum++);
        beanList.add(remark);

        boolean isOpenHigh = CollectionUtils.isNotEmpty(tbMetaDisplayTableContentList);
        int pageSize = Constants.PAGE_SIZE;
        long pages = (Constants.MAX_EXPORT_SIZE + pageSize - 1) / pageSize;

        Map<Long, Long> mapSize = new HashMap<>();
        if(isOpenHigh){
            List<TbDisplayTableDataContentDO> tableDataContentList = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageHelper.startPage(pageNum, pageSize);
                //检查内容
                tableDataContentList = tbDisplayTableDataContentMapper.listByUnifyTaskIdAndLoopCount(recordExportRequest.getEid(),
                        recordExportRequest.getUnifyTaskId(), recordExportRequest.getLoopCount());
                if(CollectionUtils.isEmpty(tableDataContentList)){
                    break;
                }
                for(TbDisplayTableDataContentDO dataContentDO : tableDataContentList){
                    int size = getPhotoList(dataContentDO.getPhotoArray(), recordExportRequest.getPicture()).size();
                    Long columnSize = mapSize.get(dataContentDO.getMetaContentId());
                    if(columnSize == null){
                        columnSize = 1L;
                    }
                    columnSize = columnSize > size ? columnSize : size;
                    mapSize.put(dataContentDO.getMetaContentId(), columnSize);
                }
            }
        }else {
            //检查图片
            List<TbDisplayTableDataColumnDO> tableDataColumnList = null;

            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PageHelper.startPage(pageNum, pageSize);
                //检查图片
                tableDataColumnList = tbDisplayTableDataColumnMapper.listByUnifyTaskIdAndLoopCount(recordExportRequest.getEid(),
                        recordExportRequest.getUnifyTaskId(), recordExportRequest.getLoopCount());
                if(CollectionUtils.isEmpty(tableDataColumnList)){
                    break;
                }
                for(TbDisplayTableDataColumnDO tTableDataColumnDO : tableDataColumnList){
                    int size = getPhotoList(tTableDataColumnDO.getPhotoArray(), recordExportRequest.getPicture()).size();
                    Long columnSize = mapSize.get(tTableDataColumnDO.getMetaColumnId());
                    if(columnSize == null){
                        columnSize = 1L;
                    }
                    columnSize = columnSize > size ? columnSize : size;
                    mapSize.put(tTableDataColumnDO.getMetaColumnId(), columnSize);
                }
            }
        }

        if (!isOpenHigh) {
            int num = 0;
            for (TbMetaDisplayTableColumnDO metaDisplayTableColumnDO : tbMetaDisplayTableColumnList) {
                num++;
                if(isSupportScore){
                    ExcelExportEntity dataScore = new ExcelExportEntity("门店得分", "dataScore_" + metaDisplayTableColumnDO.getId());
                    dataScore.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                    dataScore.setOrderNum(orderNum++);
                    beanList.add(dataScore);
                }
                if(recordExportRequest.getPicture()){
                    Long size = mapSize.get(metaDisplayTableColumnDO.getId());
                    size = size == null ? 1 : size;
                    for(int i = 1; i <= size; i++){
                        ExcelExportEntity picture = new ExcelExportEntity("陈列图片/视频", "dataHanderPicture_" + metaDisplayTableColumnDO.getId() + "_" + i);
                        picture.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                        picture.setOrderNum(orderNum++);
                        picture.setType(BaseEntityTypeConstants.IMAGE_TYPE);
                        picture.setHeight(25);
                        picture.setExportImageType(1);
                        beanList.add(picture);
                    }
                }else {
                    ExcelExportEntity dataHanderPicture = new ExcelExportEntity("陈列图片/视频", "dataHanderPicture_" + metaDisplayTableColumnDO.getId());
                    dataHanderPicture.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                    dataHanderPicture.setOrderNum(orderNum++);
                    beanList.add(dataHanderPicture);
                }
                ExcelExportEntity dataRemark = new ExcelExportEntity("检查项备注", "dataRemark_" + metaDisplayTableColumnDO.getId());
                dataRemark.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                dataRemark.setOrderNum(orderNum++);
                dataRemark.setWrap(true);
                beanList.add(dataRemark);
                ExcelExportEntity dataApproveRemark = new ExcelExportEntity("审批人备注", "dataApproveRemark_" + metaDisplayTableColumnDO.getId());
                dataApproveRemark.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                dataApproveRemark.setOrderNum(orderNum++);
                dataApproveRemark.setWrap(true);
                beanList.add(dataApproveRemark);
                ExcelExportEntity standardPic = new ExcelExportEntity("标准图", "standardPic_" + metaDisplayTableColumnDO.getId());
                standardPic.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                if(recordExportRequest.getPicture()){
                    standardPic.setType(BaseEntityTypeConstants.IMAGE_TYPE);
                    standardPic.setHeight(25);
                    standardPic.setExportImageType(1);
                }
                standardPic.setOrderNum(orderNum++);
                beanList.add(standardPic);
                ExcelExportEntity description = new ExcelExportEntity("检查项描述", "description_" + metaDisplayTableColumnDO.getId());
                description.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                description.setOrderNum(orderNum++);
                beanList.add(description);
                ExcelExportEntity metaScore = new ExcelExportEntity("满分", "metaScore_" + metaDisplayTableColumnDO.getId());
                metaScore.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                metaScore.setOrderNum(orderNum++);
                beanList.add(metaScore);
            }
        } else {
            int num = 0;
            for (TbMetaDisplayTableColumnDO metaDisplayTableColumnDO : tbMetaDisplayTableColumnList) {
                num++;
                if(isSupportScore){
                    ExcelExportEntity dataScore = new ExcelExportEntity("门店得分", "dataScore_" + metaDisplayTableColumnDO.getId());
                    dataScore.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                    dataScore.setOrderNum(orderNum++);
                    beanList.add(dataScore);
                }
                ExcelExportEntity metaScore = new ExcelExportEntity("满分", "metaScore_" + metaDisplayTableColumnDO.getId());
                metaScore.setOrderNum(orderNum++);
                metaScore.setGroupName(metaDisplayTableColumnDO.getColumnName() + "(" + num +")");
                beanList.add(metaScore);
            }

            for (TbMetaDisplayTableColumnDO metaDisplayTableContent : tbMetaDisplayTableContentList) {
                num++;
                if(!recordExportRequest.getPicture()){
                    ExcelExportEntity dataPicture= new ExcelExportEntity("陈列图片/视频", "dataPicture_" + metaDisplayTableContent.getId());
                    dataPicture.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                    dataPicture.setOrderNum(orderNum++);
                    beanList.add(dataPicture);
                }
                if(recordExportRequest.getPicture()){
                    Long size = mapSize.get(metaDisplayTableContent.getId());
                    size = size == null ? 1 : size;
                    for(int i = 1; i <= size; i++){
                        ExcelExportEntity picture = new ExcelExportEntity("陈列图片/视频", "dataPicture_" + metaDisplayTableContent.getId() + "_" + i);
                        picture.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                        picture.setOrderNum(orderNum++);
                        picture.setType(BaseEntityTypeConstants.IMAGE_TYPE);
                        picture.setHeight(25);
                        picture.setExportImageType(1);
                        beanList.add(picture);
                    }
                }

                ExcelExportEntity metaScore = new ExcelExportEntity("检查内容备注", "dataRemark_" + metaDisplayTableContent.getId());
                metaScore.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                metaScore.setOrderNum(orderNum++);
                beanList.add(metaScore);
                ExcelExportEntity dataApproveRemark = new ExcelExportEntity("审批人备注", "dataApproveRemark_" + metaDisplayTableContent.getId());
                dataApproveRemark.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                dataApproveRemark.setOrderNum(orderNum++);
                dataApproveRemark.setWrap(true);
                beanList.add(dataApproveRemark);
                ExcelExportEntity metaPicture = new ExcelExportEntity("标准图", "metaPicture_" + metaDisplayTableContent.getId());
                metaPicture.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                if(recordExportRequest.getPicture()){
                    metaPicture.setType(BaseEntityTypeConstants.IMAGE_TYPE);
                    metaPicture.setHeight(25);
                    metaPicture.setExportImageType(1);
                }
                metaPicture.setOrderNum(orderNum++);
                beanList.add(metaPicture);
                ExcelExportEntity metaDesc = new ExcelExportEntity("检查内容描述", "metaDesc_" + metaDisplayTableContent.getId());
                metaDesc.setGroupName(metaDisplayTableContent.getColumnName() + "(" + num +")");
                metaDesc.setOrderNum(orderNum++);
                beanList.add(metaDesc);
            }
        }
        ExcelExportEntity handleUserName = new ExcelExportEntity("处理人", "handleUserName");
        handleUserName.setOrderNum(orderNum++);
        beanList.add(handleUserName);
        ExcelExportEntity handlerDuration = new ExcelExportEntity("处理用时", "handlerDuration");
        handlerDuration.setOrderNum(orderNum++);
        beanList.add(handlerDuration);
        ExcelExportEntity latestHandlerTime = new ExcelExportEntity("最新处理时间", "latestHandlerTime");
        latestHandlerTime.setOrderNum(orderNum++);
        beanList.add(latestHandlerTime);
        ExcelExportEntity approveUserName  = new ExcelExportEntity("审批人", "approveUserName");
        approveUserName.setOrderNum(orderNum++);
        beanList.add(approveUserName);
        ExcelExportEntity approveDuration = new ExcelExportEntity("审批用时", "approveDuration");
        approveDuration.setOrderNum(orderNum++);
        beanList.add(approveDuration);
        ExcelExportEntity latestApproveTime = new ExcelExportEntity("最新审批时间", "latestApproveTime");
        latestApproveTime.setOrderNum(orderNum++);
        beanList.add(latestApproveTime);
        ExcelExportEntity recheckUserName = new ExcelExportEntity("复审人", "recheckUserName");
        recheckUserName.setOrderNum(orderNum++);
        beanList.add(recheckUserName);
        ExcelExportEntity doneTime = new ExcelExportEntity("实际结束时间", "doneTime");
        doneTime.setOrderNum(orderNum++);
        doneTime.setFormat(DateUtils.DATE_FORMAT_SEC_5);
        beanList.add(doneTime);
        ExcelExportEntity checkTime = new ExcelExportEntity("任务完成时长", "checkTime");
        checkTime.setOrderNum(orderNum++);
        beanList.add(checkTime);
        ExcelExportEntity handlerOverdue = new ExcelExportEntity("是否处理超时", "handlerOverdue");
        handlerOverdue.setOrderNum(orderNum++);
        beanList.add(handlerOverdue);
        ExcelExportEntity taskName = new ExcelExportEntity("任务名称", "taskName");
        taskName.setOrderNum(orderNum++);
        beanList.add(taskName);
        ExcelExportEntity taskDesc = new ExcelExportEntity("任务说明", "taskDesc");
        taskDesc.setOrderNum(orderNum++);
        beanList.add(taskDesc);
        ExcelExportEntity validTime = new ExcelExportEntity("有效期", "validTime");
        validTime.setOrderNum(orderNum++);
        beanList.add(validTime);
        ExcelExportEntity completeOverdue = new ExcelExportEntity("是否过期完成", "completeOverdue");
        completeOverdue.setOrderNum(orderNum++);
        beanList.add(completeOverdue);
        ExcelExportEntity status = new ExcelExportEntity("流程状态", "status");
        status.setOrderNum(orderNum++);
        beanList.add(status);
        ExcelExportEntity taskStatus = new ExcelExportEntity("任务状态", "taskStatus");
        taskStatus.setOrderNum(orderNum++);
        beanList.add(taskStatus);
        return beanList;
    }

    private List<String> getPhotoList(String photoArray, Boolean pictureExport){
        if(StringUtils.isBlank(photoArray)){
            return new ArrayList<>();
        }
        List<String> picList = new ArrayList<>();
        List<cn.hutool.json.JSONObject> array = JSONArray.parseArray(photoArray, cn.hutool.json.JSONObject.class);
        for(cn.hutool.json.JSONObject jsonObject : array){
            String picUrl = jsonObject.getStr("handleUrl");
            if(StringUtils.isNotBlank(picUrl) && pictureExport){
                String ext = picUrl.substring(picUrl.lastIndexOf(".") + 1);
                if (StringUtils.isBlank(ext)) {
                    continue;
                }
                picUrl = ExportImageDomainReplaceCodeEnum.replaceCode(picUrl);
                picList.add(picUrl);
            }else {
                picList.add(picUrl);
            }
        }
        if(CollectionUtils.isEmpty(picList)){
            return new ArrayList<>();
        }
        return picList;
    }
}
