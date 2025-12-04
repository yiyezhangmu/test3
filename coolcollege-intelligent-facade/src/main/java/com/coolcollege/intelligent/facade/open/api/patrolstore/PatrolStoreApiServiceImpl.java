package com.coolcollege.intelligent.facade.open.api.patrolstore;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.open.PatrolStoreDetailListDTO;
import com.coolcollege.intelligent.facade.dto.open.PatrolStoreRecordListDTO;
import com.coolcollege.intelligent.facade.enums.OpenApiResponseEnum;
import com.coolcollege.intelligent.facade.request.patrolstore.PatrolStoreDetailListRequest;
import com.coolcollege.intelligent.facade.request.patrolstore.PatrolStoreListRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaStaTableVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.TbMetaStaColumnDetailExportVO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author byd
 * @date 2022-07-11 10:48
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = PatrolStoreApiService.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class PatrolStoreApiServiceImpl implements PatrolStoreApiService {

    @Autowired
    private PatrolStoreRecordsService patrolStoreRecordsService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Override
    @ShenyuSofaClient(path = "/patrol/list")
    public OpenApiResponseVO<PageDTO<PatrolStoreRecordListDTO>> list(PatrolStoreListRequest request) {
        PageDTO<PatrolStoreRecordListDTO> pageDTO = new PageDTO<>();
        pageDTO.setPageSize(request.getPageSize());
        pageDTO.setPageNum(request.getPageNum());
        try {
            checkListRequestParams(request);
            String enterpriseId = RpcLocalHolder.getEnterpriseId();
            PatrolStoreStatisticsDataTableQuery query = new PatrolStoreStatisticsDataTableQuery();
            query.setRegionId(request.getRegionId());
            query.setPatrolType(request.getPatrolType());
            query.setStatus(request.getStatus());
            query.setBeginDate(new Date(request.getBeginTime()));
            query.setEndDate(new Date(request.getEndTime()));
            query.setLevelInfo(true);
            query.setPageNum(request.getPageNum());
            query.setPageSize(request.getPageSize());
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            PageInfo pageInfo = patrolStoreRecordsService.potralRecordList(enterpriseId, query);
            List<PatrolStoreStatisticsMetaStaTableVO> recordList = pageInfo.getList();

            List<PatrolStoreRecordListDTO> resultList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(recordList)) {
                for (PatrolStoreStatisticsMetaStaTableVO vo : recordList) {
                    PatrolStoreRecordListDTO recordListDTO = new PatrolStoreRecordListDTO();
                    recordListDTO.setActualPatrolStoreDuration(vo.getActualPatrolStoreDuration());
                    recordListDTO.setAuditOpinion(vo.getAuditOpinion());
                    recordListDTO.setAuditPicture(vo.getAuditPicture());
                    recordListDTO.setAuditRemark(vo.getAuditRemark());
                    recordListDTO.setAuditTime(vo.getAuditTime());
                    recordListDTO.setAuditUserId(vo.getAuditUserId());
                    recordListDTO.setAuditUserName(vo.getAuditUserName());
                    recordListDTO.setCheckResult(vo.getCheckResult());
                    recordListDTO.setCreateTime(vo.getCreateTime());
                    recordListDTO.setCreaterUserName(vo.getCreaterUserName());
                    recordListDTO.setFailColumnCount(vo.getFailColumnCount());
                    recordListDTO.setRecordId(vo.getId());
                    recordListDTO.setInapplicableColumnCount(vo.getInapplicableColumnCount());
                    recordListDTO.setIsOverdue(vo.getIsOverdue());
                    recordListDTO.setMetaTableId(vo.getMetaTableId());
                    recordListDTO.setMetaTableName(vo.getMetaTableName());
                    recordListDTO.setPassColumnCount(vo.getPassColumnCount());
                    recordListDTO.setPatrolType(vo.getPatrolType());
                    recordListDTO.setPercent(vo.getPercent());
                    recordListDTO.setRegionId(vo.getRegionId());
                    recordListDTO.setRegionName(vo.getRegionName());
                    recordListDTO.setRewardPenaltMoney(vo.getRewardPenaltMoney());
                    recordListDTO.setScore(vo.getScore());
                    recordListDTO.setSignEndAddress(vo.getSignEndAddress());
                    recordListDTO.setSignInStatus(vo.getSignInStatus());
                    recordListDTO.setSignOutStatus(vo.getSignOutStatus());
                    recordListDTO.setSignStartAddress(vo.getSignStartAddress());
                    recordListDTO.setSignStartTime(vo.getSignStartTime());
                    recordListDTO.setSignWay(vo.getSignWay());
                    recordListDTO.setSignEndTime(vo.getSignEndTime());
                    recordListDTO.setStatus(vo.getStatus());
                    recordListDTO.setStoreId(vo.getStoreId());
                    recordListDTO.setStoreName(vo.getStoreName());
                    recordListDTO.setSubBeginTime(vo.getSubBeginTime());
                    recordListDTO.setSubEndTime(vo.getSubEndTime());
                    recordListDTO.setSummary(vo.getSummary());
                    recordListDTO.setSummaryVideo(vo.getSummaryVideo());
                    recordListDTO.setSummaryPicture(vo.getSummaryPicture());
                    recordListDTO.setSupervisorId(vo.getSupervisorId());
                    recordListDTO.setSupervisorName(vo.getSupervisorName());
                    recordListDTO.setTaskId(vo.getTaskId());
                    recordListDTO.setTaskName(vo.getTaskName());
                    recordListDTO.setTotalScore(vo.getTotalScore());
                    recordListDTO.setTourTime(vo.getTourTime());
                    recordListDTO.setSupervisorSignature(vo.getSupervisorSignature());
                    recordListDTO.setOverdue(vo.getOverdue());
                    recordListDTO.setTaskDesc(vo.getTaskDesc());

                    resultList.add(recordListDTO);
                }
            }
            pageDTO.setList(resultList);
            pageDTO.setTotal(pageInfo.getTotal());
        }catch (ServiceException e){
            log.error("ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        }catch (Exception e){
            log.error("Exception", e);
            return OpenApiResponseVO.fail();
        }
        return OpenApiResponseVO.success(pageDTO);
    }

    @Override
    @ShenyuSofaClient(path = "/patrol/detail/list")
    public OpenApiResponseVO<PageDTO<PatrolStoreDetailListDTO>> detailList(PatrolStoreDetailListRequest request) {
        PageDTO<PatrolStoreDetailListDTO> pageDTO = new PageDTO<>();
        pageDTO.setPageSize(request.getPageSize());
        pageDTO.setPageNum(request.getPageNum());
        try{
            checkDetailRequestParams(request);
            String enterpriseId = RpcLocalHolder.getEnterpriseId();
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            PatrolStoreStatisticsDataTableQuery query = new PatrolStoreStatisticsDataTableQuery();
            query.setIsDefine(MetaTableConstant.TableTypeConstant.DEFINE.equals(request.getTableType()));
            query.setBusinessId(request.getRecordId());
            query.setRegionId(request.getRegionId());
            query.setStoreId(request.getStoreId());
            query.setCheckResult(request.getCheckResult());
            query.setBeginDate(new Date(request.getBeginTime()));
            query.setEndDate(new Date(request.getEndTime()));
            query.setIsComplete(request.getIsComplete());
            query.setPageSize(request.getPageSize());
            query.setPageNum(request.getPageNum());
            PageInfo pageInfo = patrolStoreRecordsService.potralRecordDetailList(enterpriseId, query);
            List<TbMetaStaColumnDetailExportVO> list = pageInfo.getList();
            List<PatrolStoreDetailListDTO> resultList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(list)){
                for(TbMetaStaColumnDetailExportVO detailExportVO : list){
                    PatrolStoreDetailListDTO detailListDTO = new PatrolStoreDetailListDTO();
                    detailListDTO.setId(detailExportVO.getId());
                    detailListDTO.setBusinessId(detailExportVO.getBusinessId());
                    detailListDTO.setMetaTableId(detailExportVO.getMetaTableId());
                    detailListDTO.setAwardPunish(detailExportVO.getAwardPunish());
                    detailListDTO.setAwardMoney(detailExportVO.getAwardMoney());
                    detailListDTO.setBusinessId(detailExportVO.getBusinessId());
                    detailListDTO.setCategoryName(detailExportVO.getCategoryName());
                    detailListDTO.setCheckAwardPunish(detailExportVO.getCheckAwardPunish());
                    detailListDTO.setCheckPics(detailExportVO.getCheckPics());
                    detailListDTO.setCheckResult(detailExportVO.getCheckResult());
                    detailListDTO.setCheckResultName(detailExportVO.getCheckResultName());
                    detailListDTO.setCheckScore(detailExportVO.getCheckScore());
                    detailListDTO.setCheckText(detailExportVO.getCheckText());
                    detailListDTO.setCheckVideo(detailExportVO.getCheckVideo());
                    detailListDTO.setColumnName(detailExportVO.getColumnName());
                    detailListDTO.setCreateTime(detailExportVO.getCreateTime());
                    detailListDTO.setDescription(detailExportVO.getDescription());
                    detailListDTO.setMetaTableName(detailExportVO.getMetaTableName());
                    detailListDTO.setMetaTableId(detailExportVO.getMetaTableId());
                    detailListDTO.setStandardPic(detailExportVO.getStandardPic());
                    detailListDTO.setStoreId(detailExportVO.getStoreId());
                    detailListDTO.setStoreName(detailExportVO.getStoreName());
                    detailListDTO.setSubBeginTime(detailExportVO.getSubBeginTime());
                    detailListDTO.setSubEndTime(detailExportVO.getSubEndTime());
                    detailListDTO.setSupervisorId(detailExportVO.getSupervisorId());
                    detailListDTO.setSupervisorName(detailExportVO.getSupervisorName());
                    detailListDTO.setSupportScore(detailExportVO.getSupportScore());
                    detailListDTO.setTableProperty(detailExportVO.getTableProperty());
                    detailListDTO.setTaskDesc(detailExportVO.getTaskDesc());
                    detailListDTO.setTaskName(detailExportVO.getTaskName());
                    detailListDTO.setValue(detailExportVO.getValue1());
                    detailListDTO.setFormat(detailExportVO.getFormat());
                    detailListDTO.setId(detailExportVO.getId());
                    detailListDTO.setRegionId(detailExportVO.getRegionId());
                    resultList.add(detailListDTO);
                }
            }
            pageDTO.setList(resultList);
            pageDTO.setTotal(pageInfo.getTotal());
        }catch (ServiceException e){
            log.error("ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        }catch (Exception e){
            log.error("Exception", e);
            return OpenApiResponseVO.fail();
        }
        return OpenApiResponseVO.success(pageDTO);
    }

    private void checkListRequestParams(PatrolStoreListRequest request){
        if(request.getEndTime() == null || request.getBeginTime() == null){
            throw new ServiceException(OpenApiResponseEnum.TIME_NOT_NULL.getCode(), OpenApiResponseEnum.TIME_NOT_NULL.getMessage());
        }
    }

    private void checkDetailRequestParams(PatrolStoreDetailListRequest request){
        if(request.getEndTime() == null || request.getBeginTime() == null){
            throw new ServiceException(OpenApiResponseEnum.TIME_NOT_NULL.getCode(), OpenApiResponseEnum.TIME_NOT_NULL.getMessage());
        }

        if(StringUtils.isBlank(request.getTableType())){
            throw new ServiceException(OpenApiResponseEnum.TABLE_TYPE_NOT_NULL.getCode(), OpenApiResponseEnum.TABLE_TYPE_NOT_NULL.getMessage());
        }
    }
}
