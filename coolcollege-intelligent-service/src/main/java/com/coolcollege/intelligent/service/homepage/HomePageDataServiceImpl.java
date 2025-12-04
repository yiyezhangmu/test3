package com.coolcollege.intelligent.service.homepage;

import cn.hutool.core.util.NumberUtil;
import com.cool.store.rpc.model.*;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRecordDao;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.homepage.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.storework.dto.PageHomeStoreWorkStatisticsDTO;
import com.coolcollege.intelligent.rpc.datareport.DataServiceApiImpl;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: HomePageDataServiceImpl
 * @Description: 首页数据
 * @date 2022-06-23 16:09
 */
@Slf4j
@Service
public class HomePageDataServiceImpl implements HomePageDataService{

    @Resource
    private DataServiceApiImpl dataServiceApi;
    @Lazy
    @Resource
    private RegionService regionService;
    @Resource
    private UserAuthMappingService userAuthMappingService;
    @Resource
    private SwStoreWorkRecordDao swStoreWorkRecordDao;

    @Override
    public PatrolRegionDataVO getPatrolDataStatistic(AuthDataStatisticRpcRequestDTO queryParam) {
        try {
            DataSourceHelper.changeToMy();
            List<PatrolRegionDataDTO> patrolDataStatistic = dataServiceApi.getPatrolDataStatistic(queryParam);
            PatrolRegionDataVO result = null;
            if(CollectionUtils.isNotEmpty(patrolDataStatistic)){
                List<PatrolRegionDataDTO> regionPatrolList = patrolDataStatistic.stream().filter(o -> RegionTypeEnum.PATH.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<PatrolRegionDataDTO> storePatrolList = patrolDataStatistic.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<PatrolRegionDataVO.PatrolRegionData> patrolRegionData = dealPatrolDTO(regionPatrolList);
                List<PatrolRegionDataVO.PatrolRegionData> patrolStoreData = dealPatrolDTO(storePatrolList);
                result = new PatrolRegionDataVO(patrolRegionData, patrolStoreData);
            }
            return result;
        } catch (ApiException e) {
           log.error(e.getErrMsg());
        }
        return null;
    }

    @Override
    public QuestionRegionDataVO getQuestionDataStatistic(AuthDataStatisticRpcRequestDTO queryParam) {
        try {
            DataSourceHelper.changeToMy();
            List<QuestionRegionDataDTO> questionDataStatistic = dataServiceApi.getQuestionDataStatistic(queryParam);
            QuestionRegionDataVO result = null;
            if(CollectionUtils.isNotEmpty(questionDataStatistic)){
                List<QuestionRegionDataDTO> questionRegionList = questionDataStatistic.stream().filter(o -> RegionTypeEnum.PATH.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<QuestionRegionDataDTO> questionStoreList = questionDataStatistic.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<QuestionRegionDataVO.QuestionRegionData> questionRegionData = dealQuestionDTO(questionRegionList);
                List<QuestionRegionDataVO.QuestionRegionData> questionStoreData = dealQuestionDTO(questionStoreList);
                result = new QuestionRegionDataVO(questionRegionData, questionStoreData);
            }
            return result;
        } catch (ApiException e) {
            log.error(e.getErrMsg());
        }
        return null;
    }

    @Override
    public TableAverageScoreVO getTableAverageScoreStatistic(AuthDataStatisticRpcRequestDTO queryParam) {
        try {
            DataSourceHelper.changeToMy();
            List<TableAverageScoreDTO> tableAverageScoreStatistic = dataServiceApi.getTableAverageScoreStatistic(queryParam);
            TableAverageScoreVO result = null;
            if(CollectionUtils.isNotEmpty(tableAverageScoreStatistic)){
                List<TableAverageScoreDTO> tableAverageRegionList = tableAverageScoreStatistic.stream().filter(o -> RegionTypeEnum.PATH.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<TableAverageScoreDTO> tableAverageStoreList = tableAverageScoreStatistic.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<TableAverageScoreVO.TableAverageScore> tableAverageRegion = dealTableAvgDTO(tableAverageRegionList);
                List<TableAverageScoreVO.TableAverageScore> tableAverageStore = dealTableAvgDTO(tableAverageStoreList);
                result = new TableAverageScoreVO(tableAverageRegion, tableAverageStore);
            }
            return result;
        } catch (ApiException e) {
            log.error(e.getErrMsg());
        }
        return null;
    }

    @Override
    public DisplayRegionDataVO getDisplayDataStatistic(AuthDataStatisticRpcRequestDTO queryParam) {
        try {
            DataSourceHelper.changeToMy();
            List<DisplayRegionDataDTO> displayDataStatistic = dataServiceApi.getDisplayDataStatistic(queryParam);
            DisplayRegionDataVO result = null;
            if(CollectionUtils.isNotEmpty(displayDataStatistic)){
                List<DisplayRegionDataDTO> displayRegionList = displayDataStatistic.stream().filter(o -> RegionTypeEnum.PATH.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<DisplayRegionDataDTO> displayStoreList = displayDataStatistic.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
                List<DisplayRegionDataVO.DisplayRegionData> displayRegionData = dealDisplayDTO(displayRegionList);
                List<DisplayRegionDataVO.DisplayRegionData> displayStoreData = dealDisplayDTO(displayStoreList);
                result = new DisplayRegionDataVO(displayRegionData, displayStoreData);
            }
            return result;
        } catch (ApiException e) {
            log.error(e.getErrMsg());
        }
        return null;
    }

    @Override
    public void dealAuthRegion(AuthDataStatisticRpcRequestDTO queryParam) {
        String enterpriseId = queryParam.getEnterpriseId();
        List<RegionDO> regionList = new ArrayList<>();
        if(queryParam.getIsAdmin()){
            //如果是管理员 获取一级区域下的门店和子区域
            regionList = regionService.getSubRegion(enterpriseId, Constants.LONG_ONE);
        }else{
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, queryParam.getUserId());
            if(CollectionUtils.isNotEmpty(userAuthMappingDOS)){
                if(userAuthMappingDOS.size() == Constants.INDEX_ONE){
                    String mappingId = userAuthMappingDOS.get(0).getMappingId();
                    regionList = regionService.getSubRegion(enterpriseId, Long.valueOf(mappingId));
                }else{
                    List<String> regionIds = userAuthMappingDOS.stream().map(o->o.getMappingId()).collect(Collectors.toList());
                    regionList = regionService.getRegionList(queryParam.getEnterpriseId(), regionIds);
                }
            }
        }
        List<AuthDataStatisticRpcRequestDTO.RegionType> regionLists = new ArrayList<>();
        for (RegionDO regionDO : regionList) {
            //神秘访客 节点 和外部用户节点忽略
            if(FixedRegionEnum.DEFAULT.getId().equals(regionDO.getId()) || FixedRegionEnum.EXTERNAL_USER.getId().equals(regionDO.getId())){
                continue;
            }
            Integer storeNum = Optional.ofNullable(regionDO.getStoreNum()).orElse(Constants.ZERO);
            AuthDataStatisticRpcRequestDTO.RegionType region = new AuthDataStatisticRpcRequestDTO.RegionType();
            region.setRegionId(regionDO.getId());
            region.setRegionType(regionDO.getRegionType());
            region.setRegionName(regionDO.getName());
            region.setStoreNum(Long.valueOf(storeNum));
            regionLists.add(region);
        }
        queryParam.setRegionLists(regionLists);
    }



    /**
     * 陈列数据处理
     * @param displayRegionList
     * @return
     */
    private List<DisplayRegionDataVO.DisplayRegionData> dealDisplayDTO(List<DisplayRegionDataDTO> displayRegionList){
        if(CollectionUtils.isEmpty(displayRegionList)){
            return null;
        }
        List<DisplayRegionDataVO.DisplayRegionData> resultList = new ArrayList<>();
        for (DisplayRegionDataDTO displayRegionDataDTO : displayRegionList) {
            DisplayRegionDataVO.DisplayRegionData result = new DisplayRegionDataVO.DisplayRegionData();
            result.setRegionId(displayRegionDataDTO.getRegionId());
            result.setRegionName(displayRegionDataDTO.getRegionName());
            result.setRegionType(displayRegionDataDTO.getRegionType());
            result.setTaskStoreNum(displayRegionDataDTO.getTaskStoreNum());
            result.setUnHandleNum(displayRegionDataDTO.getUnHandleNum());
            result.setUnApproveNum(displayRegionDataDTO.getUnApproveNum());
            result.setUnRecheckNum(displayRegionDataDTO.getUnRecheckNum());
            result.setOverDueNum(displayRegionDataDTO.getOverDueNum());
            result.setFinishNum(displayRegionDataDTO.getFinishNum());
            result.setOverDuePercent(displayRegionDataDTO.getOverDuePercent());
            DisplayRegionDataDTO.DisplayRegionData compareInfoDTO = displayRegionDataDTO.getCompareInfo();
            if(Objects.nonNull(compareInfoDTO)){
                DisplayRegionDataVO.DisplayCompareData compareData = new DisplayRegionDataVO.DisplayCompareData();
                compareData.setTaskStoreNum(compareInfoDTO.getTaskStoreNum());
                compareData.setUnHandleNum(compareInfoDTO.getUnHandleNum());
                compareData.setUnApproveNum(compareInfoDTO.getUnApproveNum());
                compareData.setUnRecheckNum(compareInfoDTO.getUnRecheckNum());
                compareData.setOverDueNum(compareInfoDTO.getOverDueNum());
                compareData.setFinishNum(compareInfoDTO.getFinishNum());
                compareData.setOverDuePercent(compareInfoDTO.getOverDuePercent());
                result.setCompareInfo(compareData);
            }
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 检查表处理
     * @param tableAvgList
     * @return
     */
    private List<TableAverageScoreVO.TableAverageScore>  dealTableAvgDTO(List<TableAverageScoreDTO> tableAvgList){
        if(CollectionUtils.isEmpty(tableAvgList)){
            return null;
        }
        List<TableAverageScoreVO.TableAverageScore> resultList = new ArrayList<>();
        for (TableAverageScoreDTO tableAverageScoreDTO : tableAvgList) {
            TableAverageScoreVO.TableAverageScore result = new TableAverageScoreVO.TableAverageScore();
            result.setRegionId(tableAverageScoreDTO.getRegionId());
            result.setRegionName(tableAverageScoreDTO.getRegionName());
            result.setRegionType(tableAverageScoreDTO.getRegionType());
            result.setMetaTableId(tableAverageScoreDTO.getMetaTableId());
            result.setTableName(tableAverageScoreDTO.getTableName());
            result.setPatrolStoreNum(tableAverageScoreDTO.getPatrolStoreNum());
            result.setPatrolNum(tableAverageScoreDTO.getPatrolNum());
            result.setFinishNum(tableAverageScoreDTO.getFinishNum());
            result.setAvgScore(tableAverageScoreDTO.getAvgScore());
            result.setScorePercent(tableAverageScoreDTO.getScorePercent());
            TableAverageScoreDTO.TableAverageScore compareInfoDTO = tableAverageScoreDTO.getCompareInfo();
            if(Objects.nonNull(compareInfoDTO)){
                TableAverageScoreVO.TableAverageScoreCompare compareInfo= new TableAverageScoreVO.TableAverageScoreCompare();
                compareInfo.setPatrolStoreNum(compareInfoDTO.getPatrolStoreNum());
                compareInfo.setPatrolNum(compareInfoDTO.getPatrolNum());
                compareInfo.setFinishNum(compareInfoDTO.getFinishNum());
                compareInfo.setAvgScore(compareInfoDTO.getAvgScore());
                compareInfo.setScorePercent(compareInfoDTO.getScorePercent());
                result.setCompareInfo(compareInfo);
            }
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 处理工单DTO
     * @param questionList
     * @return
     */
    private List<QuestionRegionDataVO.QuestionRegionData> dealQuestionDTO(List<QuestionRegionDataDTO> questionList){
        if(CollectionUtils.isEmpty(questionList)){
            return null;
        }
        List<QuestionRegionDataVO.QuestionRegionData> resultList = new ArrayList<>();
        for (QuestionRegionDataDTO questionRegionDataDTO : questionList) {
            if(Objects.isNull(questionRegionDataDTO)){
                continue;
            }
            QuestionRegionDataVO.QuestionRegionData result = new QuestionRegionDataVO.QuestionRegionData();
            result.setRegionId(questionRegionDataDTO.getRegionId());
            result.setRegionName(questionRegionDataDTO.getRegionName());
            result.setRegionType(questionRegionDataDTO.getRegionType());
            result.setQuestionNum(questionRegionDataDTO.getQuestionNum());
            result.setUnHandleNum(questionRegionDataDTO.getUnHandleNum());
            result.setUnApproveNum(questionRegionDataDTO.getUnApproveNum());
            result.setFinishNum(questionRegionDataDTO.getFinishNum());
            result.setOverDueNum(questionRegionDataDTO.getOverDueNum());
            result.setAvgUseTime(questionRegionDataDTO.getAvgUseTime());
            QuestionRegionDataDTO.QuestionRegionData compareInfoDTO = questionRegionDataDTO.getCompareInfo();
            if(Objects.nonNull(compareInfoDTO)){
                QuestionRegionDataVO.QuestionCompare compareInfo = new QuestionRegionDataVO.QuestionCompare();
                compareInfo.setQuestionNum(compareInfoDTO.getQuestionNum());
                compareInfo.setUnHandleNum(compareInfoDTO.getUnHandleNum());
                compareInfo.setUnApproveNum(compareInfoDTO.getUnApproveNum());
                compareInfo.setFinishNum(compareInfoDTO.getFinishNum());
                compareInfo.setOverDueNum(compareInfoDTO.getOverDueNum());
                compareInfo.setAvgUseTime(compareInfoDTO.getAvgUseTime());
                result.setCompareInfo(compareInfo);
            }
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 处理巡店数据
     * @param patrolList
     * @return
     */
    private List<PatrolRegionDataVO.PatrolRegionData> dealPatrolDTO(List<PatrolRegionDataDTO> patrolList){
        if(CollectionUtils.isEmpty(patrolList)){
            return null;
        }
        List<PatrolRegionDataVO.PatrolRegionData> resultList = new ArrayList<>();
        for (PatrolRegionDataDTO regionPatrol : patrolList) {
            if(Objects.isNull(regionPatrol)){
                continue;
            }
            PatrolRegionDataVO.PatrolRegionData patrolRegionData = new PatrolRegionDataVO.PatrolRegionData();
            patrolRegionData.setRegionId(regionPatrol.getRegionId());
            patrolRegionData.setRegionName(regionPatrol.getRegionName());
            patrolRegionData.setRegionType(regionPatrol.getRegionType());
            patrolRegionData.setStoreNum(regionPatrol.getStoreNum());
            patrolRegionData.setPatrolNum(regionPatrol.getPatrolNum());
            patrolRegionData.setPatrolPersonNum(regionPatrol.getPatrolPersonNum());
            patrolRegionData.setPatrolStoreNum(regionPatrol.getPatrolStoreNum());
            patrolRegionData.setPassNum(regionPatrol.getPassNum());
            patrolRegionData.setTaskNum(regionPatrol.getTaskNum());
            patrolRegionData.setOverDueNum(regionPatrol.getOverDueNum());
            patrolRegionData.setAvgScore(regionPatrol.getAvgScore());
            patrolRegionData.setScorePercent(regionPatrol.getScorePercent());
            patrolRegionData.setStoreCoverPercent(regionPatrol.getStoreCoverPercent());
            PatrolRegionDataDTO.PatrolRegionData compareInfoDTO = regionPatrol.getCompareInfo();
            if(Objects.nonNull(compareInfoDTO)){
                PatrolRegionDataVO.PatrolCompare compareInfo = new PatrolRegionDataVO.PatrolCompare();
                compareInfo.setStoreNum(compareInfoDTO.getStoreNum());
                compareInfo.setPatrolNum(compareInfoDTO.getPatrolNum());
                compareInfo.setPatrolPersonNum(compareInfoDTO.getPatrolPersonNum());
                compareInfo.setPatrolStoreNum(compareInfoDTO.getPatrolStoreNum());
                compareInfo.setPassNum(compareInfoDTO.getPassNum());
                compareInfo.setTaskNum(compareInfoDTO.getTaskNum());
                compareInfo.setOverDueNum(compareInfoDTO.getOverDueNum());
                compareInfo.setAvgScore(compareInfoDTO.getAvgScore());
                compareInfo.setScorePercent(compareInfoDTO.getScorePercent());
                compareInfo.setStoreCoverPercent(compareInfoDTO.getStoreCoverPercent());
                patrolRegionData.setCompareInfo(compareInfo);
            }
            resultList.add(patrolRegionData);
        }
        return resultList;
    }

}
