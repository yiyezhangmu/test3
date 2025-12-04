package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableDataColumnVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableDataContentVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021/10/11 16:20
 */
@Service
public class DisplayRecordDetailListExportService implements BaseExportService {

    @Resource
    private TbDisplayService tbDisplayService;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    private RegionService regionService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_DISPLAY_RECORD_DETAIL_LIST;
    }

    @Override
    public List<Map<String, Object>> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        //不分页
        if(pageNum > 1){
            return null;
        }
        TbDisplayReportQueryParam queryParam = JSONObject.toJavaObject(request, TbDisplayReportQueryParam.class);;
        TbDisplayTableRecordVO result = tbDisplayService.detailByTaskIdAndStoreIdAndLoopCount(queryParam.getEid(), queryParam.getUnifyTaskId(), queryParam.getStoreId(), queryParam.getLoopCount());
        if (CollectionUtils.isEmpty(result.getTbDisplayDataColumnVOList()) && CollectionUtils.isEmpty(result.getTbDisplayDataContentList())) {
            return null;
        }
        List<TbDisplayTableDataColumnVO> tbDisplayDataColumnVOList = result.getTbDisplayDataColumnVOList();

        List<TbDisplayTableDataContentVO> tbDisplayDataContentList = result.getTbDisplayDataContentList();


        List<Map<String, Object>> mapList = new ArrayList<>();
        List<String> storeIdList = ListUtils.emptyIfNull(tbDisplayDataColumnVOList)
                .stream()
                .map(TbDisplayTableDataColumnVO::getStoreId)
                .collect(Collectors.toList());

        List<StorePathDTO> storePathList = ListUtils.emptyIfNull(tbDisplayDataColumnVOList)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO=new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());

        List<StoreDO> storeList= storeMapper.getStoreByStoreIdList(enterpriseId, storeIdList);
        Map<String, String> storeNumMap = ListUtils.emptyIfNull(storeList)
                .stream()
                .filter(data-> StringUtils.isNotBlank(data.getStoreNum()))
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum, (a, b) -> a));
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathList);
        for(TbDisplayTableDataColumnVO displayTableDataColumnVO : tbDisplayDataColumnVOList){
            Map<String, Object> params = new HashMap<>();
            params.put("storeName", result.getStoreName());
            params.put("storeNum",storeNumMap.get(result.getStoreId()));
            params.put("storeGroupName", result.getStoreGroupName());
            params.put("storeAreaName", fullRegionNameMap.get(result.getStoreId()));
            params.put("taskName", result.getTaskName());
            params.put("tableName", result.getTableName());
            params.put("handerUserName", result.getHandleUserName());
            params.put("handerUserTime", result.getHandlerDuration());
            params.put("approveUserName", result.getApproveUserName());
            params.put("approveUserTime", result.getApproveDuration());
            params.put("recheckUserName", result.getRecheckUserName());
            params.put("score", result.getScore());
            params.put("metaColumnName", displayTableDataColumnVO.getColumnName());
            params.put("storeScore", displayTableDataColumnVO.getScore());
            String videos = displayTableDataColumnVO.getCheckVideo();
            List<String> videoList = new ArrayList<>();
            if (StringUtils.isNotBlank(videos)) {
                SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);
                videoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                        .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
            }
            List<String> picList = getPhotoList(displayTableDataColumnVO.getPhotoArray());
            if(CollectionUtils.isNotEmpty(videoList)){
                picList.addAll(videoList);
            }
            params.put("storePicture", StringUtils.join(picList, ","));
            params.put("metaColumnRemark", displayTableDataColumnVO.getRemark());
            params.put("metaPicture", displayTableDataColumnVO.getStandardPic());
            params.put("metaScore", displayTableDataColumnVO.getMetaScore());
            params.put("dataDesc", displayTableDataColumnVO.getDescription());

            if(CollectionUtils.isNotEmpty(tbDisplayDataContentList)){
                for(TbDisplayTableDataContentVO dataContentVO : tbDisplayDataContentList){
                    String contentVideos = dataContentVO.getCheckVideo();
                    List<String> contentVideoList = new ArrayList<>();
                    if (StringUtils.isNotBlank(contentVideos)) {
                        SmallVideoInfoDTO videoInfoDTO = JSONObject.parseObject(contentVideos, SmallVideoInfoDTO.class);
                        contentVideoList = CollectionUtils.emptyIfNull(videoInfoDTO.getVideoList())
                                .stream().map(SmallVideoDTO::getVideoUrl).collect(Collectors.toList());
                    }
                    List<String> contentPicList = getPhotoList(dataContentVO.getPhotoArray());
                    if(CollectionUtils.isNotEmpty(contentVideoList)){
                        contentPicList.addAll(contentVideoList);
                    }
                    params.put("storePic_" + dataContentVO.getMetaContentId(), StringUtils.join(contentPicList, ","));
                    params.put("dataRemark_" + dataContentVO.getMetaContentId(), dataContentVO.getRemark());
                    params.put("metaPicture_" + dataContentVO.getMetaContentId(), dataContentVO.getStandardPic());
                    params.put("standDesc_" + dataContentVO.getMetaContentId(), dataContentVO.getDescription());
                }
            }
            mapList.add(params);
        }

        return mapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        int orderNum = 1;
        List<ExcelExportEntity> beanList = new ArrayList<>();
        TbDisplayReportQueryParam queryParam = JSONObject.toJavaObject(request, TbDisplayReportQueryParam.class);;

        TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(
                queryParam.getEid(), queryParam.getUnifyTaskId(), queryParam.getStoreId(), queryParam.getLoopCount());
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(queryParam.getEid(), tbDisplayTableRecordDO.getMetaTableId());

        ExcelExportEntity storeName = new ExcelExportEntity("门店名称", "storeName");
        storeName.setOrderNum(orderNum++);
        beanList.add(storeName);
        ExcelExportEntity storeNum = new ExcelExportEntity("门店编号", "storeNum");
        storeNum.setOrderNum(orderNum++);
        beanList.add(storeNum);
        ExcelExportEntity storeGroupName = new ExcelExportEntity("门店分组", "storeGroupName");
        storeGroupName.setOrderNum(orderNum++);
        beanList.add(storeGroupName);
        ExcelExportEntity storeAreaName = new ExcelExportEntity("门店区域", "storeAreaName");
        storeAreaName.setOrderNum(orderNum++);
        beanList.add(storeAreaName);
        ExcelExportEntity taskName = new ExcelExportEntity("任务名称", "taskName");
        taskName.setOrderNum(orderNum++);
        beanList.add(taskName);
        ExcelExportEntity tableName = new ExcelExportEntity("检查表", "tableName");
        tableName.setOrderNum(orderNum++);
        beanList.add(tableName);

        ExcelExportEntity handerUserName = new ExcelExportEntity("处理人", "handerUserName");
        handerUserName.setOrderNum(orderNum++);
        beanList.add(handerUserName);

        ExcelExportEntity handerUserTime = new ExcelExportEntity("处理用时", "handerUserTime");
        handerUserTime.setOrderNum(orderNum++);
        beanList.add(handerUserTime);

        ExcelExportEntity approveUserName = new ExcelExportEntity("审批人", "approveUserName");
        approveUserName.setOrderNum(orderNum++);
        beanList.add(approveUserName);

        ExcelExportEntity approveUserTime = new ExcelExportEntity("审批时长", "approveUserTime");
        approveUserTime.setOrderNum(orderNum++);
        beanList.add(approveUserTime);

        ExcelExportEntity recheckUserName = new ExcelExportEntity("复审人", "recheckUserName");
        recheckUserName.setOrderNum(orderNum++);
        beanList.add(recheckUserName);

        ExcelExportEntity score = new ExcelExportEntity("总分", "score");
        score.setOrderNum(orderNum++);
        beanList.add(score);

        ExcelExportEntity metaColumnName = new ExcelExportEntity("检查项名称", "metaColumnName");
        metaColumnName.setOrderNum(orderNum++);
        beanList.add(metaColumnName);
        if(tbDisplayTableRecordDO.getIsSupportScore()){
            ExcelExportEntity storeScore = new ExcelExportEntity(metaTableDO.getTableProperty()==1 ? "检查项分数" : "门店得分", "storeScore");
            storeScore.setOrderNum(orderNum++);
            beanList.add(storeScore);
        }
        if(metaTableDO.getTableProperty()!=1){
            ExcelExportEntity storePicture = new ExcelExportEntity("陈列图片/视频", "storePicture");
            storePicture.setOrderNum(orderNum++);
            beanList.add(storePicture);

            ExcelExportEntity metaColumnRemark = new ExcelExportEntity("检查项备注", "metaColumnRemark");
            metaColumnRemark.setOrderNum(orderNum++);
            beanList.add(metaColumnRemark);

            ExcelExportEntity metaPicture = new ExcelExportEntity("标准图", "metaPicture");
            metaPicture.setOrderNum(orderNum++);
            beanList.add(metaPicture);

            ExcelExportEntity metaScore = new ExcelExportEntity("满分", "metaScore");
            metaScore.setOrderNum(orderNum++);
            beanList.add(metaScore);

            ExcelExportEntity dataDesc = new ExcelExportEntity("检查项描述", "dataDesc");
            dataDesc.setOrderNum(orderNum++);
            beanList.add(dataDesc);
        }

        if(metaTableDO.getTableProperty()==1){
            List<TbMetaDisplayTableColumnDO> metaContentList = tbMetaDisplayTableColumnMapper.listByMetaTableId(queryParam.getEid(), tbDisplayTableRecordDO.getMetaTableId());
            metaContentList = metaContentList.stream().filter(e -> Constants.INDEX_ONE.equals(e.getCheckType())).collect(Collectors.toList());
            int num = 0;
            for(TbMetaDisplayTableColumnDO metaContent : metaContentList){
                num++;
                ExcelExportEntity storePic = new ExcelExportEntity("陈列图片/视频", "storePic_" + metaContent.getId());
                storePic.setOrderNum(orderNum++);
                storePic.setGroupName(metaContent.getColumnName() + "(" + num + ")");
                storePic.setNeedMerge(true);
                storePic.setMergeVertical(true);
                beanList.add(storePic);

                ExcelExportEntity dataRemark = new ExcelExportEntity("检查内容备注", "dataRemark_" + metaContent.getId());
                dataRemark.setOrderNum(orderNum++);
                dataRemark.setGroupName(metaContent.getColumnName() + "(" + num + ")");
                dataRemark.setNeedMerge(true);
                dataRemark.setMergeVertical(true);
                beanList.add(dataRemark);

                ExcelExportEntity metaPicture = new ExcelExportEntity("标准图", "metaPicture_" + metaContent.getId());
                metaPicture.setOrderNum(orderNum++);
                metaPicture.setGroupName(metaContent.getColumnName() + "(" + num + ")");
                metaPicture.setNeedMerge(true);
                metaPicture.setMergeVertical(true);
                beanList.add(metaPicture);

                ExcelExportEntity standDesc = new ExcelExportEntity("检查内容描述", "standDesc_" + metaContent.getId());
                standDesc.setOrderNum(orderNum++);
                standDesc.setGroupName(metaContent.getColumnName() + "(" + num + ")");
                standDesc.setNeedMerge(true);
                standDesc.setMergeVertical(true);
                beanList.add(standDesc);
            }
        }

        return beanList;
    }

    public List<String> getPhotoList(String photoArray){
        if(StringUtils.isBlank(photoArray)){
            return new ArrayList<>();
        }
        List<String> picList = new ArrayList<>();
        List<cn.hutool.json.JSONObject> array = JSONArray.parseArray(photoArray, cn.hutool.json.JSONObject.class);
        for(cn.hutool.json.JSONObject jsonObject : array){
            String picUrl = jsonObject.getStr("handleUrl");
            if(StringUtils.isNotBlank(picUrl)){
                picList.add(picUrl);
            }
        }
        if(CollectionUtils.isEmpty(picList)){
            return new ArrayList<>();
        }
        return picList;
    }
}
