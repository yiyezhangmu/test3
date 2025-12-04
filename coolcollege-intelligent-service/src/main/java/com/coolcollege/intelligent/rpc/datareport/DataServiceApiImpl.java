package com.coolcollege.intelligent.rpc.datareport;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.cool.store.rpc.api.DataServiceApi;
import com.cool.store.rpc.constants.DataReportConstants;
import com.cool.store.rpc.model.*;
import com.coolcollege.intelligent.dto.ResultCodeDTO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: DataServiceApiImpl
 * @Description:
 * @date 2022-06-15 17:48
 */
@Service
@Slf4j
public class DataServiceApiImpl {

    @SofaReference(uniqueId ="DataServiceApi", interfaceType = DataServiceApi.class, binding = @SofaReferenceBinding(bindingType = DataReportConstants.SOFA_BINDING_TYPE, timeout = 120000))
    private DataServiceApi dataServiceApi;


    public List<PatrolRegionDataDTO> getPatrolDataStatistic(AuthDataStatisticRpcRequestDTO queryParam) throws ApiException{
        log.info("rpc getPatrolDataStatistic param :{}", JSONObject.toJSONString(queryParam));
        BaseResultDTO<List<PatrolRegionDataDTO>> patrolDataStatistic = dataServiceApi.getPatrolDataStatistic(queryParam);
        log.info("rpc getPatrolDataStatistic : {}", JSONObject.toJSONString(patrolDataStatistic));
        if (patrolDataStatistic.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(patrolDataStatistic.getResultCode()),patrolDataStatistic.getMessage());
        }
        return patrolDataStatistic.getData();
    }


    public List<QuestionRegionDataDTO> getQuestionDataStatistic(AuthDataStatisticRpcRequestDTO queryParam)  throws ApiException{
        log.info("rpc getQuestionDataStatistic param :{}", JSONObject.toJSONString(queryParam));
        BaseResultDTO<List<QuestionRegionDataDTO>> questionDataStatistic = dataServiceApi.getQuestionDataStatistic(queryParam);
        log.info("rpc getQuestionDataStatistic : {}", JSONObject.toJSONString(questionDataStatistic));
        if (questionDataStatistic.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(questionDataStatistic.getResultCode()), questionDataStatistic.getMessage());
        }
        return questionDataStatistic.getData();
    }


    public List<TableAverageScoreDTO> getTableAverageScoreStatistic(AuthDataStatisticRpcRequestDTO queryParam)  throws ApiException{
        log.info("rpc getTableAverageScoreStatistic param :{}", JSONObject.toJSONString(queryParam));
        BaseResultDTO<List<TableAverageScoreDTO>> tableAverageScoreStatistic = dataServiceApi.getTableAverageScoreStatistic(queryParam);
        log.info("rpc getTableAverageScoreStatistic : {}", JSONObject.toJSONString(tableAverageScoreStatistic));
        if (tableAverageScoreStatistic.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(tableAverageScoreStatistic.getResultCode()),tableAverageScoreStatistic.getMessage());
        }
        return tableAverageScoreStatistic.getData();
    }


    public List<DisplayRegionDataDTO> getDisplayDataStatistic(AuthDataStatisticRpcRequestDTO queryParam)  throws ApiException{
        log.info("rpc displayDataStatistic param :{}", JSONObject.toJSONString(queryParam));
        BaseResultDTO<List<DisplayRegionDataDTO>> displayDataStatistic = dataServiceApi.getDisplayDataStatistic(queryParam);
        log.info("rpc displayDataStatistic : {}", JSONObject.toJSONString(displayDataStatistic));
        if (displayDataStatistic.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(displayDataStatistic.getResultCode()),displayDataStatistic.getMessage());
        }
        return displayDataStatistic.getData();
    }
}
